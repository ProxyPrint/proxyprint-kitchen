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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;
import io.github.proxyprint.kitchen.models.repositories.RegisterRequestDAO;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<RegisterRequest> registerRequest(@RequestBody RegisterRequest registerRequest) {
        System.out.println(registerRequest);
        this.registerRequests.save(registerRequest);
        return new ResponseEntity<>(registerRequest, HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/request/accept/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> acceptRequest(@PathVariable(value = "id") long id) throws IOException {
        RegisterRequest registerRequest = registerRequests.findOne(id);
        if (registerRequest == null) {
            return new ResponseEntity<>("No Request with such ID!", HttpStatus.NOT_FOUND);
        } else {
            registerRequest.setAccepted(true);
            registerRequests.save(registerRequest);
            return new ResponseEntity<>("Request accepted!", HttpStatus.OK);
        }
    }
    
    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/request/reject/{id}", method = RequestMethod.POST)
    public String rejectRequest(@PathVariable(value ="id") long id) throws IOException {
        RegisterRequest  registerRequest = registerRequests.findOne(id);
        JsonObject response = new JsonObject();
        if (registerRequest == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
        else {
            // Send e-mail
            registerRequests.delete(id);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
    }
}
