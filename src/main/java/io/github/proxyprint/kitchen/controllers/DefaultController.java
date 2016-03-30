/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
