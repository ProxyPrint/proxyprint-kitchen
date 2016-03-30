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
import io.github.proxyprint.kitchen.models.User;
import io.github.proxyprint.kitchen.models.repositories.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap map) {
        return "Olá mundo!\n";
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/api/secured", method = RequestMethod.GET)
    public String secured(ModelMap map) {
        return "Se estiveres autenticado, podes ver isto!\n";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String addUser(WebRequest webRequest) {
        String user = webRequest.getParameter("user");
        String password = webRequest.getParameter("password");
        User u = new User(user, password);
        u.addRole("ROLE_USER");

        User existing = users.findByUsername(user);
        if (existing == null) {
            u.setPassword(password);
            users.save(u);
            return GSON.toJson(u);
        } else {
            return GSON.toJson("Username in use!");
        }
    }
}
