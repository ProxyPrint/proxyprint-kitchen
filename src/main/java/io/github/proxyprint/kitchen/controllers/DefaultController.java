/* 
 * Copyright 2016 Jorge Caldas, José Cortez
 * José Francisco, Marcelo Gonçalves
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
package io.github.proxyprint.kitchen.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.User;
import io.github.proxyprint.kitchen.models.repositories.UserDAO;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author josesousa
 */
@RestController
public class DefaultController {

    @Autowired
    private UserDAO users;
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @RequestMapping(method = RequestMethod.OPTIONS, value = "/*")
    @ResponseBody
    public ResponseEntity handleOptions() {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap map) {
        JsonObject response = new JsonObject();
        response.addProperty("message", "Olá Mundo!");
        return GSON.toJson(response);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/api/secured", method = RequestMethod.GET)
    public String secured(ModelMap map) {
        JsonObject response = new JsonObject();
        response.addProperty("message", "Se estiveres autenticado, podes ver isto!");
        return GSON.toJson(response);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(WebRequest request) throws IOException {
        boolean auth;
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        JsonObject response = new JsonObject();
        if (username == null || password == null) {
            auth = false;
        } else {
            User user = users.findByUsername(username);
            if (user == null) {
                auth = false;
            } else {
                auth = user.getPassword().equals(password);
                response.add("user", GSON.toJsonTree(user));
            }
        }

        response.addProperty("success", auth);
        return GSON.toJson(response);
    }
}
