/*
 * Copyright 2016 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.CoversTable;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.PapersTable;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.RingsTable;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest.Status;
import io.github.proxyprint.kitchen.models.notifications.Notification;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BindingItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.CoverItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;
import io.github.proxyprint.kitchen.models.printshops.pricetable.RangePaperItem;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.EmployeeDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintRequestDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.utils.DistanceCalculator;
import io.github.proxyprint.kitchen.utils.NotificationManager;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.dao.SystemWideSaltSource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author josesousa
 */
@RestController
public class PrintShopController {

    @Autowired
    private PrintShopDAO printshops;
    @Autowired
    private PrintRequestDAO printrequests;
    @Autowired
    private EmployeeDAO employees;
    @Autowired
    private NotificationManager notificationManager;

    @Autowired
    private ConsumerDAO consumers;

    @Autowired
    private Gson GSON;

    @RequestMapping(value = "/printshops/nearest", method = RequestMethod.GET)
    public String getNearestPrintShops(WebRequest request) {
        String lat = request.getParameter("latitude");
        String lon = request.getParameter("longitude");

        JsonObject response = new JsonObject();

        if(lat!=null && lon!=null) {
            Double latitude = Double.parseDouble(lat);
            Double longitude = Double.parseDouble(lon);
            System.out.format("Latitude: %s Longitude: %s\n", latitude, longitude);

            TreeMap<Double, PrintShop> pshops = new TreeMap<>();

            for (PrintShop p : printshops.findAll()) {
                double distance = DistanceCalculator.distance(latitude, longitude, p.getLatitude(), p.getLongitude());
                pshops.put(distance, p);
            }
            response.addProperty("success", true);
            response.add("printshops", GSON.toJsonTree((pshops)));
        } else {
            response.addProperty("success", false);
        }
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns a pricetable", notes = "This method returns a pricetable of a specific printshop.")
    @Secured({"ROLE_MANAGER","ROLE_USER"})
    @RequestMapping(value = "/printshops/{id}/pricetable", method = RequestMethod.GET)
    public String getPrintShopPriceTable(@PathVariable(value = "id") long id) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        PapersTable papersTable = new PapersTable();
        RingsTable ringsTable = new RingsTable();
        CoversTable coversTable = new CoversTable();

        if (pshop == null) {
            response.addProperty("success", false);
        } else {
            for(String key : pshop.getPriceTable().keySet()) {
                String type = Item.checkItemType(key);
                if (type.equals(Item.PAPER)) {
                    RangePaperItem rpi = (RangePaperItem) pshop.loadPriceItem(key);
                    papersTable.addRangePaperItem(rpi,pshop);
                } else if (type.equals(Item.RingType.BINDING.toString())) {
                    BindingItem bi = (BindingItem) pshop.loadPriceItem(key);
                    ringsTable.addBindingItem(bi, pshop.getPrice(bi));

                } else if (type.equals(Item.COVER)) {
                    CoverItem ci = (CoverItem) pshop.loadPriceItem(key);
                    coversTable.addCoverItem(ci,pshop.getPrice(ci));
                }
            }

            response.add("printcopy", GSON.toJsonTree(papersTable.getFinalTable()));
            response.add("rings", GSON.toJsonTree(ringsTable.getItems()));
            response.addProperty("stapling", ringsTable.getStaplingPrice());
            response.add("covers", GSON.toJsonTree(coversTable.getItems()));
            response.addProperty("success", true);
        }

        return GSON.toJson(response);
    }


    /*-------------------------
        PrintRequests
    -------------------------*/
    @ApiOperation(value = "Returns the new status.", notes = "Returns the new status of the corresponding print request.")
    @Secured({"ROLE_MANAGER", "ROLE_EMPLOYEE"})
    @RequestMapping(value = "/printshops/requests/{id}", method = RequestMethod.POST)
    public String changeStatusPrintShopRequests(@PathVariable(value = "id") long id, Principal principal) throws IOException {
        JsonObject response = new JsonObject();
        Employee e = employees.findByUsername(principal.getName());
        PrintShop printshop = e.getPrintShop();

        if (printshop == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        String not;
        PrintRequest printRequest = printrequests.findByIdInAndPrintshop(id,printshop);
        String user = printRequest.getConsumer().getUsername();

        if (printRequest == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        } else if (printRequest.getStatus() == Status.PENDING) {
            printRequest.setStatus(Status.IN_PROGRESS);
            printRequest.setEmpAttended(principal.getName());
            response.addProperty("newStatus", Status.IN_PROGRESS.toString());
            not = "O pedido número " + printRequest.getId() + " está a ser processado!";
            notificationManager.sendNotification(user, new Notification(not));
        } else if (printRequest.getStatus() == Status.IN_PROGRESS) {
            printRequest.setStatus(Status.FINISHED);
            printRequest.setFinishedTimestamp(new Date());
            response.addProperty("newStatus", Status.FINISHED.toString());
            not = "O pedido número " + printRequest.getId() +
                    " está completo! Pode deslocar-se á reprografia para proceder ao levantamento.";
            notificationManager.sendNotification(user, new Notification(not));
        } else if (printRequest.getStatus() == Status.FINISHED) {
            printRequest.setStatus(Status.LIFTED);
            printRequest.setDeliveredTimestamp(new Date());
            printRequest.setEmpDelivered(principal.getName());
            response.addProperty("newStatus", Status.LIFTED.toString());
        } else {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        printrequests.save(printRequest);
        response.addProperty("success", true);
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns a request.", notes = "Returns the corresponding request from a printshop.")
    @Secured({"ROLE_MANAGER", "ROLE_EMPLOYEE"})
    @RequestMapping(value = "/printshops/requests/{id}", method = RequestMethod.GET)
    public String getPrintShopRequest(@PathVariable(value = "id") long id, Principal principal) {

        JsonObject response = new JsonObject();
        Employee e = employees.findByUsername(principal.getName());
        PrintShop printshop = e.getPrintShop();

        if (printshop == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        PrintRequest printRequest = printrequests.findByIdInAndPrintshop(id,printshop);

        for (Document d : printRequest.getDocuments()){
            for (DocumentSpec s :  d.getSpecs()){
                s.setSpecsToString();
            }
        }
        response.add("printrequest", GSON.toJsonTree(printRequest));

        response.addProperty("success", true);
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns pending requests.", notes = "Returns the pending and in progress requests from a printshop.")
    @Secured({"ROLE_MANAGER", "ROLE_EMPLOYEE"})
    @RequestMapping(value = "/printshops/requests", method = RequestMethod.GET)
    public String getPrintShopRequests(Principal principal) {
        JsonObject response = new JsonObject();
        Employee e = employees.findByUsername(principal.getName());
        PrintShop printshop = e.getPrintShop();

        if (printshop == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        List<Status> status = new ArrayList<>();
        status.add(Status.PENDING);
        status.add(Status.IN_PROGRESS);

        List<PrintRequest> printRequestsList = printrequests.findByStatusInAndPrintshop(status, printshop);
        Type listOfPRequests = new TypeToken<List<PrintShop>>(){}.getType();

        response.add("printrequest", GSON.toJsonTree(printRequestsList,listOfPRequests));
        response.addProperty("success", true);
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns satisfied requests.", notes = "Returns the satisfied requests from a printshop.")
    @Secured({"ROLE_MANAGER", "ROLE_EMPLOYEE"})
    @RequestMapping(value = "/printshops/satisfied", method = RequestMethod.GET)
    public String getPrintShopSatisfiedRequests(Principal principal) {
        JsonObject response = new JsonObject();

        Employee e = employees.findByUsername(principal.getName());

        PrintShop printshop = e.getPrintShop();

        if (printshop == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        List<Status> status = new ArrayList<>();
        status.add(Status.FINISHED);

        List<PrintRequest> printRequestsList = printrequests.findByStatusInAndPrintshop(status, printshop);
        Type listOfPRequests = new TypeToken<List<PrintShop>>(){}.getType();

        response.add("satisfiedrequests", GSON.toJsonTree(printRequestsList,listOfPRequests));
        response.addProperty("success", true);
        return GSON.toJson(response);
    }

    @Secured({"ROLE_MANAGER", "ROLE_EMPLOYEE"})
    @RequestMapping(value = "/printshops/requests/cancel/{id}", method = RequestMethod.DELETE)
    public String cancelPrintShopRequests(@PathVariable(value = "id") long id, Principal principal, @RequestBody String motive)
            throws IOException {

        JsonObject response = new JsonObject();
        Employee e = employees.findByUsername(principal.getName());
        PrintShop printshop = e.getPrintShop();

        if (printshop == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        String not;
        PrintRequest printRequest = printrequests.findByIdInAndPrintshop(id,printshop);
        Consumer user = printRequest.getConsumer();

        if(printRequest.getStatus() == Status.PENDING){

            long requestid = printRequest.getId();

            printshop.getPrintRequests().remove(printRequest);
            printshops.save(printshop);

            user.getPrintRequests().remove(printRequest);
            consumers.save(user);

            not = "O pedido número " + requestid + " foi cancelado! Motivo: " + motive;
            notificationManager.sendNotification(user.getUsername(), new Notification(not));

            response.addProperty("success", true);
        } else{
            response.addProperty("success", false);
        }

        return GSON.toJson(response);
    }
}
