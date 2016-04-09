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
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;
import io.github.proxyprint.kitchen.models.repositories.RegisterRequestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author josesousa
 */
@RestController
public class RegisterRequestController {

    @Autowired
    private RegisterRequestDAO registerRequests;
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @RequestMapping(value = "/request/register", method = RequestMethod.POST)
    public ResponseEntity<RegisterRequest> updateDisplay(@RequestBody RegisterRequest request) {
        System.out.println(request);
        this.registerRequests.save(request);
        return new ResponseEntity<>(request, HttpStatus.OK);
    }
}
