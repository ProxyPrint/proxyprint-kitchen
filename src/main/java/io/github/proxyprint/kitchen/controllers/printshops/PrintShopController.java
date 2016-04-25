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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.utils.DistanceCalculator;
import io.github.proxyprint.kitchen.utils.gson.AnnotationExclusionStrategy;
import java.util.LinkedList;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author josesousa
 */
@RestController
public class PrintShopController {

    @Autowired
    private PrintShopDAO printshops;
    private final static Gson GSON = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();

    @RequestMapping(value = "/printshops/nearest", method = RequestMethod.GET)
    public String getNearestPrintShops(WebRequest request) {
        System.out.format("Latitude: %s Longitude: %s\n", request.getParameter("latitude"), request.getParameter("longitude"));
        Double latitude = Double.parseDouble(request.getParameter("latitude"));
        Double longitude = Double.parseDouble(request.getParameter("longitude"));

        System.out.format("Latitude: %s Longitude: %s\n", latitude, longitude);

        TreeMap<Double, PrintShop> pshops = new TreeMap<>();
        JsonObject response = new JsonObject();
        //em vez de register requests, meter reprografias
        for (PrintShop p: printshops.findAll()){
            double distance = DistanceCalculator.distance(latitude, longitude, p.getLatitude(), p.getLongitude());
            pshops.put(distance,p);
        }
        response.add("printshops", GSON.toJsonTree(new LinkedList(pshops.values())));

        return GSON.toJson(response);
    }
}
