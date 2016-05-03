package io.github.proxyprint.kitchen.controllers.admin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.proxyprint.kitchen.models.Admin;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BindingItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.CoverItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;
import io.github.proxyprint.kitchen.models.repositories.AdminDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.RegisterRequestDAO;
import io.github.proxyprint.kitchen.models.repositories.UserDAO;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
        for (PrintShop printshop : printShops.findAll()) {
            printShopsList.add(printshop);
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

        // Bindings
        BindingItem b = new BindingItem(BindingItem.RingType.PLASTIC, 6, 10);
        printshop.addItemPriceTable(b.genKey(), (float) 1.15);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 12, 20);
        printshop.addItemPriceTable(b.genKey(), (float) 1.4);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 22, 28);
        printshop.addItemPriceTable(b.genKey(), (float) 1.75);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 32, 38);
        printshop.addItemPriceTable(b.genKey(), (float) 2.00);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 45, 52);
        printshop.addItemPriceTable(b.genKey(), (float) 2.5);

        b = new BindingItem(BindingItem.RingType.SPIRAL, 6, 10);
        printshop.addItemPriceTable(b.genKey(), (float) 1.55);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 12, 20);
        printshop.addItemPriceTable(b.genKey(), (float) 1.90);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 24, 32);
        printshop.addItemPriceTable(b.genKey(), (float) 2.55);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 36, 40);
        printshop.addItemPriceTable(b.genKey(), (float) 2.95);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 44, 50);
        printshop.addItemPriceTable(b.genKey(), (float) 3.35);
        b = new BindingItem(BindingItem.RingType.STAPLING, 0, 0);
        printshop.addItemPriceTable(b.genKey(), (float) 0.1);

        // Covers
        CoverItem c = new CoverItem(Item.CoverType.CRISTAL_ACETATE, Item.Format.A4);
        printshop.addItemPriceTable(c.genKey(), (float) 0.5);

        c = new CoverItem(Item.CoverType.PVC_TRANSPARENT, Item.Format.A4);
        printshop.addItemPriceTable(c.genKey(), (float) 0.7);
        c = new CoverItem(Item.CoverType.PVC_TRANSPARENT, Item.Format.A3);
        printshop.addItemPriceTable(c.genKey(), (float) 1.5);

        c = new CoverItem(Item.CoverType.PVC_OPAQUE, Item.Format.A4);
        printshop.addItemPriceTable(c.genKey(), (float) 0.7);
        c = new CoverItem(Item.CoverType.PVC_OPAQUE, Item.Format.A3);
        printshop.addItemPriceTable(c.genKey(), (float) 1.5);

        users.save(master);
        users.save(joao);
        users.save(employee);
        users.save(manager);

        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), joao, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), joao, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), joao, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(35, Date.from(Instant.now()), joao, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), joao, PrintRequest.Status.IN_PROGRESS));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), joao, PrintRequest.Status.LIFTED));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), joao, PrintRequest.Status.FINISHED));

        printShops.save(printshop);
        registerRequests.save(registerRequest);

        response.addProperty("message", "seeding completed");

        return new ResponseEntity<>(GSON.toJson(response), HttpStatus.OK);
    }

}
