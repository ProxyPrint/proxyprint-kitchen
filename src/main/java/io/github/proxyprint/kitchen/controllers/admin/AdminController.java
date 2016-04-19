package io.github.proxyprint.kitchen.controllers.admin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.controllers.MailBox;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;
import io.github.proxyprint.kitchen.models.repositories.ManagerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.RegisterRequestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by daniel on 19-04-2016.
 */
@RestController
public class AdminController {

    @Autowired
    private PrintShopDAO printShops;
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/prinshops", method = RequestMethod.GET)
    public ResponseEntity<List<PrintShop>> registerRequest(@RequestBody RegisterRequest registerRequest) {
        List<PrintShop> printShopsList = new ArrayList<>();
        for (PrintShop pshop : printShops.findAll()) {
            printShopsList.add(pshop);
        }
        return new ResponseEntity<List<PrintShop>>(printShopsList, HttpStatus.OK);
    }
}

