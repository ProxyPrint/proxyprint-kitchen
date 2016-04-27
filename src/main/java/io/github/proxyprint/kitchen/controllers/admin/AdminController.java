package io.github.proxyprint.kitchen.controllers.admin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.proxyprint.kitchen.models.Admin;
import io.github.proxyprint.kitchen.models.User;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.repositories.AdminDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 19-04-2016.
 */
@RestController
public class AdminController {

    @Autowired
    private PrintShopDAO printShops;
    @Autowired
    private AdminDAO admins;
    @Autowired
    private Gson GSON;

    // NOT WORKING YET!
    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/printshops", method = RequestMethod.GET)
    public String getPrinShopsList() {
        JsonObject response = new JsonObject();

        List<PrintShop> printShopsList = new ArrayList<>();
        for (PrintShop pshop : printShops.findAll()) {
            printShopsList.add(pshop);
        }

        Type listOfPShops = new TypeToken<List<PrintShop>>(){}.getType();
        String res = GSON.toJson(printShopsList, listOfPShops);

        response.addProperty("prinshops", res);
        response.addProperty("success", true);
        return GSON.toJson(response);
    }
    
    @RequestMapping(value = "/admin/register", method = RequestMethod.POST)
    public ResponseEntity<Admin> newAdmin(@RequestBody Admin admin) {
        admin.addRole(User.Roles.ROLE_ADMIN.toString());
        this.admins.save(admin);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }
}

