package io.github.proxyprint.kitchen.controllers.admin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.proxyprint.kitchen.models.Admin;
import io.github.proxyprint.kitchen.models.User;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;
import io.github.proxyprint.kitchen.models.printshops.pricetable.PaperItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.PriceItem;
import io.github.proxyprint.kitchen.models.repositories.AdminDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.RegisterRequestDAO;
import io.github.proxyprint.kitchen.models.repositories.UserDAO;
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
    private UserDAO users;
    @Autowired
    private RegisterRequestDAO registerRequests;
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

        Type listOfPShops = new TypeToken<List<PrintShop>>() {
        }.getType();
        String res = GSON.toJson(printShopsList, listOfPShops);

        response.addProperty("prinshops", res);
        response.addProperty("success", true);
        return GSON.toJson(response);
    }

    @RequestMapping(value = "/admin/register", method = RequestMethod.POST)
    public ResponseEntity<Admin> newAdmin(@RequestBody Admin admin) {
        this.admins.save(admin);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/seed", method = RequestMethod.POST)
    public ResponseEntity<String> seed() {
        JsonObject response = new JsonObject();

        Admin master = new Admin("master", "1234", "danielcaldas@sapo.pt");
        Consumer joao = new Consumer("João dos Santos", "joao", "1234", "joao@gmail.com", "69", "69");
        PrintShop printshop = new PrintShop("Video Norte", "Rua Nova de Santa Cruz", 41.5594, 8.3972, "123444378", "logo", 0);
        Manager manager = new Manager("joaquim", "1234", "Joaquim Pereira", "joaquim@gmail.com");
        Employee employee = new Employee("mafalda", "1234", "Mafalda Sofia Pinto");
        RegisterRequest registerRequest = new RegisterRequest("Ana Pinto", "danielcaldas@sapo,pt", "1234", "Rua das Cruzes n20", 43.221, 41.121, "124555321", "Printer Style", false);

        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.SIMPLEX, PaperItem.Colors.BW, 1, 20), 0.1f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.SIMPLEX, PaperItem.Colors.BW, 21, 50), 0.08f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.SIMPLEX, PaperItem.Colors.BW, 51, 100), 0.06f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.DUPLEX, PaperItem.Colors.BW, 1, 20), 0.19f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.DUPLEX, PaperItem.Colors.BW, 21, 50), 0.15f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.DUPLEX, PaperItem.Colors.BW, 51, 100), 0.11f);

        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.SIMPLEX, PaperItem.Colors.BW, 1, 20), 0.18f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.SIMPLEX, PaperItem.Colors.BW, 21, 50), 0.16f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.SIMPLEX, PaperItem.Colors.BW, 51, 100), 0.14f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.DUPLEX, PaperItem.Colors.BW, 1, 20), 0.35f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.DUPLEX, PaperItem.Colors.BW, 21, 50), 0.31f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.DUPLEX, PaperItem.Colors.BW, 51, 100), 0.27f);

        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.SIMPLEX, PaperItem.Colors.COLOR, 1, 20), 0.75f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.SIMPLEX, PaperItem.Colors.COLOR, 21, 50), 0.60f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.SIMPLEX, PaperItem.Colors.COLOR, 51, 100), 0.50f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.DUPLEX, PaperItem.Colors.COLOR, 1, 20), 1.49f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.DUPLEX, PaperItem.Colors.COLOR, 21, 50), 1.19f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A4, PaperItem.Sides.DUPLEX, PaperItem.Colors.COLOR, 51, 100), 0.99f);

        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.SIMPLEX, PaperItem.Colors.COLOR, 1, 20), 1.4f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.SIMPLEX, PaperItem.Colors.COLOR, 21, 50), 1.10f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.SIMPLEX, PaperItem.Colors.COLOR, 51, 100), 0.95f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.DUPLEX, PaperItem.Colors.COLOR, 1, 20), 2.79f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.DUPLEX, PaperItem.Colors.COLOR, 21, 50), 2.19f);
        printshop.addPriceItem(new PriceItem(PaperItem.Format.A3, PaperItem.Sides.DUPLEX, PaperItem.Colors.COLOR, 51, 100), 1.89f);
        
        users.save(master);
        users.save(joao);
        users.save(employee);
        users.save(manager);
        printShops.save(printshop);
        registerRequests.save(registerRequest);

        response.addProperty("message", "seeding completed");

        return new ResponseEntity<>(GSON.toJson(response), HttpStatus.OK);
    }
}
