package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BudgetCalculator;
import io.github.proxyprint.kitchen.models.repositories.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

/**
 * Created by daniel on 04-04-2016.
 */
@RestController
public class ConsumerController {

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
    private Gson GSON;

    @RequestMapping(value = "/consumer/register", method = RequestMethod.POST)
    public String addUser(WebRequest request) {
        boolean success = false;

        JsonObject response = new JsonObject();
        String username = request.getParameter("username");
        Consumer c = consumers.findByUsername(username);

        if (c == null) {
            String password = request.getParameter("password");
            String email = request.getParameter("email");
            String name = request.getParameter("name");
            String lat = request.getParameter("latitude");
            String lon = request.getParameter("longitude");
            c = new Consumer(name, username, password, email, lat, lon);
            consumers.save(c);
            response.add("consumer", GSON.toJsonTree(c));
            success = true;
        } else {
            response.addProperty("error", "O username já se encontra em uso.");
            success = false;
        }

        response.addProperty("success", success);
        return GSON.toJson(response);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/upload", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file, WebRequest req) {
        JsonObject response = new JsonObject();

        String filetype = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!filetype.equals("pdf")) {
            throw new RuntimeException("Error! Not a pdf");
        }
        String name = FilenameUtils.removeExtension(file.getOriginalFilename());

        if (!file.isEmpty()) {
            try {
                PDDocument pdf = PDDocument.load(file.getInputStream());
                int count = pdf.getNumberOfPages();

                System.out.println("Este pdf tem " + count + " páginas!!");
                response.addProperty("success", true);
                response.addProperty("message", "Este pdf tem " + count + " páginas!!");
            } catch (Exception e) {
                response.addProperty("success", false);
                response.addProperty("message", "You failed to upload " + name + " => " + e.getMessage());
            }
        } else {
            response.addProperty("success", false);
            response.addProperty("message", "You failed to upload because the file was empty");
        }
        return GSON.toJson(response);
    }


    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/budget", method = RequestMethod.POST)
    public String calcBudgetForPrintRequest(HttpServletRequest request, Principal principal) {
        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());

        PrintRequest printRequest = new PrintRequest();
        printRequest.setConsumer(consumer);

        String requestJSON = null;
        List<Long> pshopIDs = null;
        try {
            requestJSON = IOUtils.toString(request.getInputStream());
            Map prequest = new Gson().fromJson(requestJSON, Map.class);

            // PrintShops
            List<Double> tmpPshopIDs = (List<Double>) prequest.get("printshops");
            pshopIDs = new ArrayList<>();
            for(double doubleID : tmpPshopIDs) {
                pshopIDs.add((long)Double.valueOf((double)doubleID).intValue());
            }

            /*--------------------------------------------------------
                    Number of pages for each submited file
            --------------------------------------------------------*/
            Map<String,Long> documentsIds = new HashMap<>(); // This map stays

            Document d = new Document("Hack For Good Manual PT.pdf",4);
            d = documents.save(d);
            documentsIds.put("Hack For Good Manual PT.pdf", d.getId());
            printRequest.addDocument(d);

            d = new Document("fatura_janeiro_2016_gas.pdf", 4);
            d = documents.save(d);
            documentsIds.put("fatura_janeiro_2016_gas.pdf", d.getId());
            printRequest.addDocument(d);

            d = new Document("fatura_janeiro_2016_agua.pdf", 2);
            d = documents.save(d);
            documentsIds.put("fatura_janeiro_2016_agua.pdf", d.getId());
            printRequest.addDocument(d);
            /*--------------------------------------------------------*/
            /*--------------------------------------------------------*/

            // Store Documents and respective Specifications
            Map<String,Map> mdocuments = (Map) prequest.get("files");
            for(Map.Entry<String,Map> documentSpecs : mdocuments.entrySet()) {
                String fileName = documentSpecs.getKey();
                List<Map<String,String>> specs = (List)documentSpecs.getValue().get("specs");

                for(Map<String,String> entry : specs) {
                    Object tmpid = entry.get("id");
                    Object tmpinfLim = entry.get("from");
                    Object tmpsupLim = entry.get("to");

                    long id = (long)Double.valueOf((double)tmpid).intValue();

                    int infLim=0;
                    if(tmpinfLim!=null) infLim = Double.valueOf((double)tmpinfLim).intValue();

                    int supLim=0;
                    if(tmpsupLim!=null) supLim = Double.valueOf((double)tmpsupLim).intValue();

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

            printRequest = printRequests.save(printRequest);

            // Finally calculate the budgets :D
            Map<Long,String> budgets = new HashMap<>();
            List<Document> prDocs = printRequest.getDocuments();
            for(long pshopID : pshopIDs) {
                PrintShop printShop = printShops.findOne(pshopID);
                BudgetCalculator budgetCalculator = new BudgetCalculator(printShop);
                float totalCost = 0; // In the future we may specifie the budget by file its easy!
                for(Document document : prDocs) {
                    for(DocumentSpec documentSpec : document.getSpecs()) {
                        float specCost=0;
                        if(documentSpec.getFirstPage()!=0 && documentSpec.getLastPage()!=0) {
                            // Partial calculation
                            specCost = budgetCalculator.calculatePrice(documentSpec.getFirstPage(), documentSpec.getLastPage(), documentSpec.getPrintingSchema());
                        } else {
                            // Total calculation
                            specCost = budgetCalculator.calculatePrice(1, document.getTotalPages(), documentSpec.getPrintingSchema());
                        }
                        if(specCost!=-1) totalCost += specCost;
                        else {
                            budgets.put(pshopID,"Esta reprografia não pode satisfazer o pedido.");
                        }
                    }
                }
                if (totalCost > 0) budgets.put(pshopID,String.valueOf(totalCost)); // add to budgets
            }
            response.addProperty("success", true);
            response.add("budgets",GSON.toJsonTree(budgets));
            response.addProperty("printRequestID", printRequest.getId());
            return GSON.toJson(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.addProperty("success", false);
        return GSON.toJson(response);
    }

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

        long pshopID = (long)Double.valueOf((double)mrequest.get("printshopID")).intValue();
        double cost = (Double)mrequest.get("budget");

        if(printRequest!=null && consumer!=null) {
            PrintShop pshop = printShops.findOne(pshopID);

            if(pshop!=null) {
                // Final attributes for given print request
                printRequest.setArrivalTimestamp(new Date());
                printRequest.setStatus(PrintRequest.Status.PENDING);
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

}
