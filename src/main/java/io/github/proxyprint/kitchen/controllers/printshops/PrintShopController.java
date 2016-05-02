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
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.PaperTableItem;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.RingsTable;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.items.BindingItem;
import io.github.proxyprint.kitchen.models.printshops.items.CoverItem;
import io.github.proxyprint.kitchen.models.printshops.items.Item;
import io.github.proxyprint.kitchen.models.printshops.items.RangePaperItem;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.utils.DistanceCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

/**
 *
 * @author josesousa
 */
@RestController
public class PrintShopController {

    @Autowired
    private PrintShopDAO printshops;
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

        for (PrintShop p: printshops.findAll()){
            double distance = DistanceCalculator.distance(latitude, longitude, p.getLatitude(), p.getLongitude());
            pshops.put(distance,p);
        }
        response.add("printshops", GSON.toJsonTree(new LinkedList(pshops.values())));

        return GSON.toJson(response);
    }

    @RequestMapping(value = "/seed", method = RequestMethod.GET)
    public String populatePriceTable() {
        long id = 100;
        PrintShop pshop = printshops.findOne(id);

        // Bindings
        BindingItem b = new BindingItem(BindingItem.RingType.PLASTIC,6,10);
        pshop.addItemPriceTable(b.genKey(),(float)1.15);
        b = new BindingItem(BindingItem.RingType.PLASTIC,12,20);
        pshop.addItemPriceTable(b.genKey(),(float)1.4);
        b = new BindingItem(BindingItem.RingType.PLASTIC,22,28);
        pshop.addItemPriceTable(b.genKey(),(float)1.75);
        b = new BindingItem(BindingItem.RingType.PLASTIC,32,38);
        pshop.addItemPriceTable(b.genKey(),(float)2.00);
        b = new BindingItem(BindingItem.RingType.PLASTIC,45,52);
        pshop.addItemPriceTable(b.genKey(),(float)2.5);

        b = new BindingItem(BindingItem.RingType.SPIRAL,6,10);
        pshop.addItemPriceTable(b.genKey(),(float)1.55);
        b = new BindingItem(BindingItem.RingType.SPIRAL,12,20);
        pshop.addItemPriceTable(b.genKey(),(float)1.90);
        b = new BindingItem(BindingItem.RingType.SPIRAL,24,32);
        pshop.addItemPriceTable(b.genKey(),(float)2.55);
        b = new BindingItem(BindingItem.RingType.SPIRAL,36,40);
        pshop.addItemPriceTable(b.genKey(),(float)2.95);
        b = new BindingItem(BindingItem.RingType.SPIRAL,44,50);
        pshop.addItemPriceTable(b.genKey(),(float)3.35);
        b = new BindingItem(BindingItem.RingType.STAPLING,0,0);
        pshop.addItemPriceTable(b.genKey(),(float)0.1);

        // Covers
        CoverItem c = new CoverItem(Item.CoverType.CRISTAL_ACETATE, Item.Format.A4);
        pshop.addItemPriceTable(c.genKey(),(float)0.5);

        c = new CoverItem(Item.CoverType.PVC_TRANSPARENT, Item.Format.A4);
        pshop.addItemPriceTable(c.genKey(),(float)0.7);
        c = new CoverItem(Item.CoverType.PVC_TRANSPARENT, Item.Format.A3);
        pshop.addItemPriceTable(c.genKey(),(float)1.5);

        c = new CoverItem(Item.CoverType.PVC_OPAQUE, Item.Format.A4);
        pshop.addItemPriceTable(c.genKey(),(float)0.7);
        c = new CoverItem(Item.CoverType.PVC_OPAQUE, Item.Format.A3);
        pshop.addItemPriceTable(c.genKey(),(float)1.5);


        printshops.save(pshop);

        return "OK";
    }

    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/printshops/{id}/pricetable", method = RequestMethod.GET)
    public String getPrintShopPriceTable(@PathVariable(value = "id") long id) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        Map<String,Set<PaperTableItem>> finalTable = new HashMap<>();


        Map<String,Map<String,PaperTableItem>> table = new HashMap<>();
        RingsTable ringsTable = new RingsTable();

        if (pshop == null) {
            response.addProperty("success", false);
        } else {
            for(String key : pshop.getPriceTable().keySet()) {
                String type = Item.checkItemType(key);
                if (type.equals(Item.PAPER)) {
                    RangePaperItem pi = (RangePaperItem) pshop.loadPriceItem(key);
                    processPaperItem(table, pi, pshop);
                } else if (type.equals(Item.BINDING)) {
                    BindingItem bi = (BindingItem) pshop.loadPriceItem(key);
                    ringsTable.addBindingItem(bi, pshop.getPrice(bi));

                } else if (type.equals(Item.COVER)) {
                    // CODE...
                }
            }
            // Covert Map<String,PaperTableItem> to Set<PaperTableItem>
            for(String color : table.keySet()) {
                Map<String,PaperTableItem> map = table.get(color);
                Set<PaperTableItem> set = new TreeSet<>();
                for(PaperTableItem pti : map.values()) {
                    set.add(pti);
                }
                finalTable.put(color,set);
            }
            response.add("printcopy", GSON.toJsonTree(finalTable));
            response.add("rings", GSON.toJsonTree(ringsTable.getItems()));
            response.addProperty("stapling", ringsTable.getStaplingPrice());
            response.addProperty("success", true);
        }

        return GSON.toJson(response);
    }

    /**
     * Insert a (Range)PaperItem in a pricetable.
     * @param table, current table being builded.
     * @param pi, the PaperItem to be inserted.
     * @param pshop, the current print shop instance.
     * @return the updated price table.
     */
    public Map<String,Map<String,PaperTableItem>> processPaperItem(Map<String,Map<String,PaperTableItem>> table, RangePaperItem pi, PrintShop pshop) {
        if (!table.containsKey(pi.getColors().toString())) { // The color is new
            // Create new PaperTableItem
            PaperTableItem pti = new PaperTableItem(pi.getInfLim(), pi.getSupLim());
            pti.addPriceToPaperTableItem(pi, pshop.getPrice(pi));
            pti.setColors(pi.getColors().toString());

            // Add new range and associated PaperTableItem instance
            Map<String, PaperTableItem> map = new HashMap<>();
            map.put(pti.genKey(), pti);

            // Add to table
            table.put(pi.getColors().toString(), map);

        } else { // The color already exists
            String ptiKey = pi.getInfLim() + ";" + pi.getSupLim();
            Map<String, PaperTableItem> aux = table.get(pi.getColors().toString());
            PaperTableItem pti = aux.get(ptiKey);

            if (pti != null) {
                // PriceTableItem instance already exists add price
                pti.addPriceToPaperTableItem(pi, pshop.getPrice(pi));
                pti.setColors(pi.getColors().toString());
                table.get(pi.getColors().toString()).put(pti.genKey(), pti);
            } else {
                // Create new PaperTableItem
                pti = new PaperTableItem(pi.getInfLim(), pi.getSupLim());
                pti.setColors(pi.getColors().toString());
                pti.addPriceToPaperTableItem(pi, pshop.getPrice(pi));

                // Add new range and associated PaperTableItem instance
                Map<String, PaperTableItem> map = table.get(pi.getColors().toString());
                map.put(pti.genKey(), pti);

                // Add to table
                table.put(pi.getColors().toString(), map);
            }
        }

        return table;
    }

}
