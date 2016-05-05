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
import io.github.proxyprint.kitchen.models.printshops.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintRequest.Status;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BindingItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.CoverItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;
import io.github.proxyprint.kitchen.models.printshops.pricetable.RangePaperItem;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintRequestDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.utils.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author josesousa
 */
@RestController
public class PrintShopController {

    //remover isto no pull request
    @Autowired
    private ConsumerDAO consumers;

    @Autowired
    private PrintShopDAO printshops;
    @Autowired
    private PrintRequestDAO printrequests;
    @Autowired
    private Gson GSON;

    @RequestMapping(value = "/printshops/nearest", method = RequestMethod.GET)
    public String getNearestPrintShops(WebRequest request) {
        System.out.format("Latitude: %s Longitude: %s\n", request.getParameter("latitude"), request.getParameter("longitude"));
        Double latitude = Double.parseDouble(request.getParameter("latitude"));
        Double longitude = Double.parseDouble(request.getParameter("longitude"));

        System.out.format("Latitude: %s Longitude: %s\n", latitude, longitude);

        TreeMap<Double, PrintShop> pshops = new TreeMap<>();
        JsonObject response = new JsonObject();

        for (PrintShop p : printshops.findAll()) {
            double distance = DistanceCalculator.distance(latitude, longitude, p.getLatitude(), p.getLongitude());
            pshops.put(distance, p);
        }
        response.add("printshops", GSON.toJsonTree(new LinkedList(pshops.values())));

        return GSON.toJson(response);
    }

    @Secured({"ROLE_MANAGER", "ROLE_EMPLOYEE"})
    @RequestMapping(value = "/printshops/requests", method = RequestMethod.GET)
    public String getPrintShopRequests() {
        JsonObject response = new JsonObject();
        //PrintShop printshop = printshops.findOne(id);
        PrintShop printshop = printshops.findAll().iterator().next();

        if (printshop == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        List<Status> status = new ArrayList<>();
        status.add(Status.PENDING);
        status.add(Status.IN_PROGRESS);

        List<PrintRequest> printRequestsList = printrequests.findByStatusInAndPrintshop(status, printshop);
        Type listOfPRequests = new TypeToken<List<PrintShop>>() {
        }.getType();
        response.add("printrequest", GSON.toJsonTree(printRequestsList, listOfPRequests));
        response.addProperty("success", true);
        return GSON.toJson(response);
    }

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
                } else if (type.equals(Item.BINDING)) {
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

    @Secured({"ROLE_MANAGER", "ROLE_EMPLOYEE"})
    @RequestMapping(value = "/printshops/requests/{id}", method = RequestMethod.POST)
    public String changeStatusPrintShopRequests(@PathVariable(value = "id") long id) {
        JsonObject response = new JsonObject();
        //PrintShop printshop = printshops.findOne(id);
        PrintShop printshop = printshops.findAll().iterator().next();

        if (printshop == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        PrintRequest printRequest = printrequests.findOne(id);

        if (printRequest.getStatus() == Status.PENDING) {
            printRequest.setStatus(Status.IN_PROGRESS);
        } else if (printRequest.getStatus() == Status.IN_PROGRESS) {
            printRequest.setStatus(Status.FINISHED);
        } else if (printRequest.getStatus() == Status.FINISHED) {
            printRequest.setStatus(Status.LIFTED);
        }
        printrequests.save(printRequest);
        response.addProperty("success", true);
        return GSON.toJson(response);
    }
}
