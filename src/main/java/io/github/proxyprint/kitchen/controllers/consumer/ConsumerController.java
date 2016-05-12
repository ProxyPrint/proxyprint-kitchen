package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.controllers.consumer.printrequest.ConsumerPrintRequest;
import io.github.proxyprint.kitchen.controllers.consumer.printrequest.ConsumerPrintRequestDocumentInfo;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.notifications.Notification;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.*;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintRequestDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintingSchemaDAO;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private PrintShopDAO printShops;
    @Autowired
    private PrintRequestDAO printrequests;
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

    /**
     * !WARNING
     * This is a temporary fix while the budget algorithm isn't implemented.
     * @param request
     * @return
     */
    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/budget", method = RequestMethod.POST)
    public String printRequest(@RequestBody List<Long> pshopIDs) {
        JsonObject response = new JsonObject();

        // Uncomment when budget request is done!
        // Map<Long,Float> budgets = calculateBudget(request);
        // List<Long> printshopsIDs = request.getPrintshops();
        Map<Long,Float> budgets = new HashMap<>();

        Random rand = new Random();
        for(long pid : pshopIDs) {
            System.out.println("Pid: "+pid);
            budgets.put(pid,rand.nextFloat() * (5 - 1) + 1);
        }

        response.add("budgets", GSON.toJsonTree(budgets));
        response.addProperty("success", true);
        return GSON.toJson(response);
    }




    /**
     *
     * @param request
     * @return
     */
    public Map<Long,Float> calculateBudget(ConsumerPrintRequest request) {
        Map<Long,Float> budgets = new HashMap<>();

        for(Long pshopID : request.getPrintshops()) {
            PrintShop pshop = printShops.findOne(pshopID);
            float cost=0;
            /*for(Map.Entry<String,List<ConsumerPrintRequestDocumentInfo>> printRequest : request.getFiles().entrySet()) {
                String documentName = printRequest.getKey();
                List<ConsumerPrintRequestDocumentInfo> docSpecs = printRequest.getValue();

                for(ConsumerPrintRequestDocumentInfo docInfo : docSpecs) {
                    List<DocumentSpec> specs = docInfo.getSpecs();
                    for(DocumentSpec spec : specs) {
                        // Calculate cost for each specified schema
                        cost += calculatePrice(spec.getFirstPage(),spec.getLastPage(),spec.getPrintingSchema(),pshop);
                    }
                }
                budgets.put(pshopID,cost);
            }*/
        }

        return budgets;
    }

    /**
     * Calculate the price for a single specification
     * @param firstPage
     * @param lastPage
     * @param pschema
     * @param pshop
     * @return -1 if the request cannot be satisfied, a value bigger than or equal to 0 representing the cost of the specification
     */
    public float calculatePrice(int firstPage, int lastPage, PrintingSchema pschema, PrintShop pshop) {
        float cost = 0;

        // Paper
        if(pschema.getPaperItem()!=null) {
            int numberOfPages = (lastPage - firstPage) + 1;
            PaperItem pi = pschema.getPaperItem();
            if(pi!=null) {
                RangePaperItem rpi = pshop.findRangePaperItem(numberOfPages,pi);
                if(rpi!=null) {
                    float res = pshop.getPriceByKey(rpi.genKey());
                    if(res!=-1) {
                        cost += res;
                    } else return -1;
                } else return -1;
            }
        }

        // Binding
        if(pschema.getBindingItem()!=null) {
            BindingItem bi = pschema.getBindingItem();

            if(bi.getRingsType().equals(Item.RingType.STAPLING.toString())) {
                bi.setRingThicknessInfLim(0);
                bi.setRingThicknessSupLim(0);
            } else {
                // TO DO: Dinamic check for rings size base on the number pages!!
                bi.setRingThicknessInfLim(6);
                bi.setRingThicknessSupLim(10);
            }
            //-------------------------------------------------------

            if(bi!=null) {
                float res = pshop.getPriceByKey(bi.genKey());
                if(res!=-1) {
                    cost += res;
                } else return -1;
            }
        }

        // Cover
        if(pschema.getCoverItem()!=null) {
            CoverItem ci = pschema.getCoverItem();
            if(ci!=null) {
                float res = pshop.getPriceByKey(ci.genKey());
                if(res!=-1) {
                    cost += res;
                } else return -1;
            }
        }

        return cost;
    }

    @Secured({"ROLE_CONSUMER"})
    @RequestMapping(value = "/requests/cancel/{id}", method = RequestMethod.POST)
    public String cancelRequests(@PathVariable(value = "id") long id, Principal principal) {

        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());

        if (consumer == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        PrintRequest printRequest = printrequests.findByIdInAndConsumer(id,consumer);

        if(printRequest.getStatus() == PrintRequest.Status.PENDING){
            printrequests.delete(printRequest);
            response.addProperty("success", true);
        } else{
            response.addProperty("success", false);
        }

        return GSON.toJson(response);
    }
}
