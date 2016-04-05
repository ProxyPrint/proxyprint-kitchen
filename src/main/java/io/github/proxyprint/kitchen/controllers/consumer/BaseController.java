package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

/**
 * Created by daniel on 04-04-2016.
 */
@RestController
public class BaseController {

    @Autowired
    private ConsumerDAO consumers;
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @RequestMapping(value = "/consumer/login", method = RequestMethod.POST)
    public String login(WebRequest request) throws IOException {
        boolean auth = false;
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        JsonObject response = new JsonObject();
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            Consumer consumer = consumers.findByUsername(username);
            if (consumer != null) {
                auth = consumer.getPassword().equals(password);
                if (auth) {
                    response.add("consumer", GSON.toJsonTree(consumer));
                }
            }
        }

        response.addProperty("success", auth);
        return GSON.toJson(response);
    }

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
            c.addRole("ROLE_USER");
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
}
