package io.github.proxyprint.kitchen.controllers.admin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.proxyprint.kitchen.models.Admin;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;
import io.github.proxyprint.kitchen.models.printshops.pricetable.*;
import io.github.proxyprint.kitchen.models.repositories.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
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
    private PrintingSchemaDAO printingSchemas;
    @Autowired
    private RegisterRequestDAO registerRequests;
    @Autowired
    private Gson GSON;

    @ApiOperation(value = "Returns the list of all printshops.", notes = "This method allows the admin entity to access all printshop's information registered in the platform.")
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

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows the register of an admin entity.")
    @RequestMapping(value = "/admin/register", method = RequestMethod.POST)
    public ResponseEntity<Admin> newAdmin(@RequestBody Admin admin) {
        this.admins.save(admin);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @ApiOperation(value = "Returns nothing.", notes = "This method allows developers to fill in the database with fake but consistent data.")
    @RequestMapping(value = "/admin/seed", method = RequestMethod.POST)
    public ResponseEntity<String> seed() {
        JsonObject response = new JsonObject();

        Admin master = new Admin("master", "1234", "proxyprint@gmail.pt");
        Consumer joao = new Consumer("João dos Santos", "joao", "1234", "joao@gmail.com", "69", "69");
        Consumer rui = new Consumer("Rui Moreira Campos", "rui", "1234", "rui@gmail.com", "69", "69");
        Consumer ana = new Consumer("Ana Monteiro", "anam", "1234", "anamonteiro@gmail.com", "69", "69");
        Consumer rita = new Consumer("Rita Maria Costinha", "ritinha", "1234", "rita@gmail.com", "69", "69");

        // Printshops
        PrintShop printshop = new PrintShop("Copy Scan", "Rua Quinta dos Órfãos 16 S. Vitor (Junto à Universidade do Minho), Braga", 41.557973, -8.398398, "123555378", "logo_1", 0);
        printShops.save(printshop);
        PrintShop printshop2 = new PrintShop("SmartPrint", "Rua dos Peões, Braga", 41.557973, -8.398398, "123555378", "logo_2", 0);
        printshop = new PrintShop("Flash Vidius - Gomes & Santana, Lda.", "R. Manuel Silva, 20 - R/C. C.P. 4490000, Povoa de Varzim, Porto", 41.379392, -8.761458, "199111333", "logo_3", 0);
        printShops.save(printshop);
        printshop = new PrintShop("CopyGraphe", "R. Ramalho Ortigão C Com Pescador - lj 12. C.P. 4490678, Povoa de Varzim, Porto", 41.383314, -8.761942, "123098333", "logo_4", 0);
        printShops.save(printshop);
        printshop = new PrintShop("Nelson Costa Santos", "Tv. Senra, 22 - Póvoa de Varzim. C.P. 4490000, Povoa de Varzim, Porto.", 41.382070, -8.763069, "153655378", "logo_5", 0);
        printShops.save(printshop);
        printshop = new PrintShop("Gráfica Editora Poveira, Lda.", "R. Manuel Silva, 18. C.P. 4490657, Povoa de Varzim, Porto.", 41.379605, -8.761047, "133555378", "logo_6", 0);
        printShops.save(printshop);
        printshop = new PrintShop("Staples Vila do Conde", "Avenida General Humberto Delgado 2, Vila do Conde", 41.370674, -8.744176, "133555378", "logo_7", 0);
        printShops.save(printshop);
        printshop = new PrintShop("Staples Vila da Maia", "Rua Comendador Valentim dos Santos Dinis 570, Maia", 41.246159, -8.625225, "133555378", "logo_7", 0);
        printShops.save(printshop);


        printshop = new PrintShop("Video Norte", "Rua Nova de Santa Cruz", 41.5594, -8.3972, "123444378", "logo_8", 0);

        Manager manager = new Manager("joaquim", "1234", "Joaquim Pereira", "joaquim@gmail.com");
        Manager manager2 = new Manager("laura", "1234", "Laura Afonso", "lauraafonso@hotmail.com");
        RegisterRequest registerRequest = new RegisterRequest("Jorge Caldas", "jcaldas", "danielcaldas@sapo,pt", "1234", "Rua das Cruzes n20", 43.221, 41.121, "124555321", "Printer Style", false);

        users.save(master);

        // joao
        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), joao, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), joao, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), joao, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(35, Date.from(Instant.now()), joao, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), joao, PrintRequest.Status.IN_PROGRESS));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), joao, PrintRequest.Status.LIFTED));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), joao, PrintRequest.Status.FINISHED));

        // rui
        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), rui, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), rui, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), rui, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(35, Date.from(Instant.now()), rui, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), rui, PrintRequest.Status.IN_PROGRESS));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), rui, PrintRequest.Status.LIFTED));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), rui, PrintRequest.Status.FINISHED));

        // ana
        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), ana, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), ana, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), ana, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(35, Date.from(Instant.now()), ana, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), ana, PrintRequest.Status.IN_PROGRESS));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), ana, PrintRequest.Status.LIFTED));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), ana, PrintRequest.Status.FINISHED));

        // rita
        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), rita, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), rita, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), rita, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(35, Date.from(Instant.now()), rita, PrintRequest.Status.PENDING));
        printshop.addPrintRequest(new PrintRequest(20, Date.from(Instant.now()), rita, PrintRequest.Status.IN_PROGRESS));
        printshop.addPrintRequest(new PrintRequest(25, Date.from(Instant.now()), rita, PrintRequest.Status.LIFTED));
        printshop.addPrintRequest(new PrintRequest(30, Date.from(Instant.now()), rita, PrintRequest.Status.FINISHED));


        registerRequests.save(registerRequest);

        /*-------------------------------------
            PriceTable
        ------------------------------------*/
        // Paper
        PaperItem p1 = new PaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW);

        // Black & White
        RangePaperItem rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.1);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.19);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.18);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.35);

        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.08);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.15);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.16);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.31);

        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.06);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.11);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.14);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.27);

        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.05);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.09);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.12);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.23);

        // Color

        PaperItem p2 = new PaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR);

        RangePaperItem rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.75);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.49);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.40);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 2.79);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.60);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.19);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.10);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 2.19);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.50);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.99);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.95);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.89);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.40);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.79);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.75);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.49);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.30);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.59);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.55);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.19);

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
        BindingItem bs = new BindingItem(BindingItem.RingType.STAPLING, 0, 0);
        printshop.addItemPriceTable(bs.genKey(), (float) 0.1);

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

        // PrintingSchemas
        // PrintingSchema printingSchema1 = new PrintingSchema("A4+2LAD+PB+Agrafar",p1.genKey(),bs.genKey(),"");
        PrintingSchema printingSchema1 = new PrintingSchema("A4+2LAD+PB+Agrafar",p1.genKey(),"BINDING,STAPLING,0,0","");
        // PrintingSchema printingSchema2 = new PrintingSchema("A4+2LAD+CORES+Encaderna",p2.genKey(),b.genKey(),c.genKey());
        PrintingSchema printingSchema2 = new PrintingSchema("A4+2LAD+CORES+Encaderna",p2.genKey(),"BINDING,SPIRAL",c.genKey());
        printingSchemas.save(printingSchema1);
        printingSchemas.save(printingSchema2);

        // Add printing schemas to joao
        joao.addPrintingSchema(printingSchema1);
        joao.addPrintingSchema(printingSchema2);
        users.save(joao);
        users.save(rui);
        users.save(ana);
        users.save(rita);

        // Save printshops
        printShops.save(printshop);
        printShops.save(printshop2);

        // Set printshops to managers and save
        manager.setPrintShop(printshop);
        users.save(manager);

        manager2.setPrintShop(printshop2);
        users.save(manager2);

        /*--------------------- Employees ---------------------*/
        Employee employee = new Employee("mafalda", "1234", "Mafalda Sofia Pinto", printshop);
        employee.setPrintShop(printshop);
        users.save(employee);
        employee = new Employee("miguel", "1234", "Miguel Santos", printshop);
        employee.setPrintShop(printshop);
        users.save(employee);
        employee = new Employee("ana", "1234", "Ana Ferreira", printshop);
        employee.setPrintShop(printshop);
        users.save(employee);
        employee = new Employee("joana", "1234", "Joana Sofia", printshop);
        employee.setPrintShop(printshop);
        users.save(employee);
        employee = new Employee("rita", "1234", "Rita Semedo", printshop);
        employee.setPrintShop(printshop);
        users.save(employee);
        employee = new Employee("rafaela", "1234", "Rafaela Martins", printshop);
        employee.setPrintShop(printshop);
        users.save(employee);
        employee = new Employee("cristiano", "1234", "Cristiano Costa", printshop);
        employee.setPrintShop(printshop2);
        users.save(employee);
        employee = new Employee("marco", "1234", "Marco Pinheiro", printshop);
        employee.setPrintShop(printshop2);
        users.save(employee);
        employee = new Employee("daniel", "1234", "Daniel Caldas", printshop);
        employee.setPrintShop(printshop2);
        users.save(employee);
        employee = new Employee("carlos", "1234", "Carlos do Mar", printshop);
        employee.setPrintShop(printshop2);
        users.save(employee);
        employee = new Employee("mario", "1234", "Mário Pereira", printshop);
        employee.setPrintShop(printshop2);
        users.save(employee);
        /*--------------------- Employees ---------------------*/

        response.addProperty("message", "seeding completed");

        return new ResponseEntity<>(GSON.toJson(response), HttpStatus.OK);
    }

}
