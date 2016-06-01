package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.config.NgrokConfig;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BudgetCalculator;
import io.github.proxyprint.kitchen.models.repositories.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by daniel on 20-05-2016.
 */
@RestController
public class PrintRequestController {

    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private PrintingSchemaDAO printingSchemas;
    @Autowired
    private DocumentDAO documents;
    @Autowired
    private DocumentSpecDAO documentsSpecs;
    @Autowired
    private PrintRequestDAO printRequests;
    @Autowired
    private PrintShopDAO printShops;
    @Autowired
    private PrintRequestDAO printrequests;
    @Autowired
    private Gson GSON;

    @ApiOperation(value = "Returns a set of budgets", notes = "This method calculates budgets for a given and already specified print request. The budgets are calculated for specific printshops also passed along as parameters.")
    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/budget", method = RequestMethod.POST)
    public String calcBudgetForPrintRequest(HttpServletRequest request, Principal principal, @RequestPart("printRequest") String requestJSON) throws IOException {
        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());

        PrintRequest printRequest = new PrintRequest();
        printRequest.setConsumer(consumer);
        printRequest = printRequests.save(printRequest);

        List<Long> pshopIDs = null;

        Map prequest = new Gson().fromJson(requestJSON, Map.class);

        // PrintShops
        List<Double> tmpPshopIDs = (List<Double>) prequest.get("printshops");
        pshopIDs = new ArrayList<>();
        for (double doubleID : tmpPshopIDs) {
            pshopIDs.add((long) Double.valueOf((double) doubleID).intValue());
        }

        // Process files
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, Long> documentsIds = new HashMap<String, Long>();
        for (Collection<MultipartFile> files : multipartRequest.getMultiFileMap().values()) {
            for (MultipartFile file : files) {
                singleFileHandle(file, printRequest, documentsIds);
            }
        }

        // Store Documents and respective Specifications
        Map<String, Map> mdocuments = (Map) prequest.get("files");
        for (Map.Entry<String, Map> documentSpecs : mdocuments.entrySet()) {
            String fileName = documentSpecs.getKey();
            List<Map<String, String>> specs = (List) documentSpecs.getValue().get("specs");

            for (Map<String, String> entry : specs) {
                Object tmpid = entry.get("id");
                Object tmpinfLim = entry.get("from");
                Object tmpsupLim = entry.get("to");

                long id = (long) Double.valueOf((double) tmpid).intValue();

                int infLim = 0;
                if (tmpinfLim != null) infLim = Double.valueOf((double) tmpinfLim).intValue();

                int supLim = 0;
                if (tmpsupLim != null) supLim = Double.valueOf((double) tmpsupLim).intValue();

                // Get printing schema by its id
                PrintingSchema tmpschema = printingSchemas.findOne(id);

                // Create DocumentSpec and associate it with respective Document
                DocumentSpec tmpdc = new DocumentSpec(infLim, supLim, tmpschema);
                documentsSpecs.save(tmpdc);
                long did = documentsIds.get(fileName);
                Document tmpdoc = documents.findOne(did);
                tmpdoc.addSpecification(tmpdc);
                documents.save(tmpdoc);
            }
        }

        // Finally calculate the budgets :D
        Map<Long, String> budgets = calcBudgetsForPrintShops(pshopIDs, printRequest);

        response.addProperty("success", true);
        response.add("budgets", GSON.toJsonTree(budgets));
        response.addProperty("printRequestID", printRequest.getId());
        response.addProperty("externalURL", NgrokConfig.getExternalUrl());
        return GSON.toJson(response);
    }

    private void singleFileHandle(MultipartFile file, PrintRequest printRequest, Map<String, Long> documentsIds) {
        String filetype = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!filetype.equals("pdf")) {
            return;
        }
        String name = FilenameUtils.removeExtension(file.getOriginalFilename());

        if (!file.isEmpty()) {
            try {
                PDDocument pdf = PDDocument.load(file.getInputStream());
                int count = pdf.getNumberOfPages();
                pdf.close();

                Document doc = new Document(name, count, printRequest);
                doc = this.documents.save(doc);
                printRequest.addDocument(doc);
                documentsIds.put(doc.getName() + ".pdf", doc.getId());

                FileOutputStream fos = new FileOutputStream(new File(Document.DIRECTORY_PATH + doc.getId() + ".pdf"));
                IOUtils.copy(file.getInputStream(), fos);
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(ConsumerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

        }
    }

    private Map<Long, String> calcBudgetsForPrintShops(List<Long> pshopIDs, PrintRequest printRequest) {
        Map<Long, String> budgets = new HashMap<>();

        Set<Document> prDocs = printRequest.getDocuments();
        for (long pshopID : pshopIDs) {
            PrintShop printShop = printShops.findOne(pshopID);
            BudgetCalculator budgetCalculator = new BudgetCalculator(printShop);
            float totalCost = 0; // In the future we may specifie the budget by file its easy!
            for (Document document : prDocs) {
                for (DocumentSpec documentSpec : document.getSpecs()) {
                    float specCost = 0;
                    if (documentSpec.getFirstPage() != 0 && documentSpec.getLastPage() != 0) {
                        // Partial calculation
                        specCost = budgetCalculator.calculatePrice(documentSpec.getFirstPage(), documentSpec.getLastPage(), documentSpec.getPrintingSchema());
                    } else {
                        // Total calculation
                        specCost = budgetCalculator.calculatePrice(1, document.getTotalPages(), documentSpec.getPrintingSchema());
                    }
                    if (specCost != -1) totalCost += specCost;
                    else {
                        budgets.put(pshopID, "Esta reprografia não pode satisfazer o pedido.");
                    }
                }
            }
            if (totalCost > 0) budgets.put(pshopID, String.valueOf(totalCost)); // add to budgets
        }

        return budgets;
    }

    @ApiOperation(value = "Returns success/insuccess", notes = "This method allow clients to POST a print request and associate it to a given printshop with a given budget.")
    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/printrequest/{printRequestID}/submit", method = RequestMethod.POST)
    public String finishAndSubmitPrintRequest(@PathVariable(value = "printRequestID") long prid, HttpServletRequest request, Principal principal) {
        JsonObject response = new JsonObject();
        PrintRequest printRequest = printRequests.findOne(prid);
        Consumer consumer = consumers.findByUsername(principal.getName());

        String requestJSON = null;
        try {
            requestJSON = IOUtils.toString(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map mrequest = new Gson().fromJson(requestJSON, Map.class);

        long pshopID = (long) Double.valueOf((double) mrequest.get("printshopID")).intValue();
        double cost = round((Double) mrequest.get("budget"), 2);

        if (printRequest != null && consumer != null) {
            PrintShop pshop = printShops.findOne(pshopID);

            if (pshop != null) {
                // Final attributes for given print request
                printRequest.setArrivalTimestamp(new Date());
                printRequest.setStatus(PrintRequest.Status.NOT_PAYED);
                printRequest.setCost(cost);

                printRequests.save(printRequest);

                pshop.addPrintRequest(printRequest);

                printShops.save(pshop);
                response.addProperty("success", true);
                return GSON.toJson(response);
            }
        }

        response.addProperty("success", false);
        return GSON.toJson(response);
    }

    protected double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET)
    protected String testFilesConfig() throws java.io.IOException {
        return Document.DIRECTORY_PATH;
    }

}
