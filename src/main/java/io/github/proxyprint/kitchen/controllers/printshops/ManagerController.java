package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.PaperTableItem;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.RingTableItem;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BindingItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;
import io.github.proxyprint.kitchen.models.printshops.pricetable.RangePaperItem;
import io.github.proxyprint.kitchen.models.repositories.EmployeeDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by daniel on 27-04-2016.
 */
@RestController
public class ManagerController {

    @Autowired
    private PrintShopDAO printshops;
    @Autowired
    private EmployeeDAO employees;
    @Autowired
    private Gson GSON;

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/papers", method = RequestMethod.POST)
    public String addNewPaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            pshop.insertPaperTableItemsInPriceTable(pti);
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/deletepaperitem", method = RequestMethod.POST)
    public String deletePaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Remove price items
            List<RangePaperItem> itemsToDelete = pshop.convertPaperTableItemToPaperItems(pti);
            for(RangePaperItem pi : itemsToDelete) {
                pshop.getPriceTable().remove(pi.genKey());
            }
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/rings", method = RequestMethod.POST)
    public String addNewPaperItem(@PathVariable(value = "id") long id, @RequestBody RingTableItem rti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            BindingItem newBi = new BindingItem(Item.RingType.valueOf(rti.getRingType()), rti.getInfLim(), rti.getSupLim());
            pshop.addItemPriceTable(newBi.genKey(),Float.parseFloat(rti.getPrice()));
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/deleteringitem", method = RequestMethod.POST)
    public String deleteRingItem(@PathVariable(value = "id") long id, @RequestBody RingTableItem rti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Remove price items
            BindingItem newBi = new BindingItem(RingTableItem.getRingTypeForPresentationString(rti.getRingType()), rti.getInfLim(), rti.getSupLim());
            pshop.getPriceTable().remove(newBi.genKey());
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/pricetable/editstapling", method = RequestMethod.PUT)
    public String editStaplingPrice(@PathVariable(value = "printShopID") long psid, @RequestBody String newStaplingPrice) {
        PrintShop pshop = printshops.findOne(psid);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Edit stapling price
            pshop.getPriceTable().put("BINDING,STAPLING,0,0", Float.parseFloat(newStaplingPrice));
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    /*---------------------
        Employees
     ---------------------*/
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/employees", method = RequestMethod.GET)
    public String getEmployees(@PathVariable(value = "printShopID") long psid) {
        PrintShop pshop = printshops.findOne(psid);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            List<Employee> employeesList = employees.findByPrintShop(pshop);
            response.add("employees", GSON.toJsonTree(employeesList));
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/employees", method = RequestMethod.POST)
    public String addEmployee(@PathVariable(value = "printShopID") long psid, HttpServletRequest request) {
        PrintShop pshop = printshops.findOne(psid);
        JsonObject response = new JsonObject();

        String requestJSON = null;
        Employee newEmp = null;
        try {
            requestJSON = IOUtils.toString( request.getInputStream());
            newEmp = GSON.fromJson(requestJSON, Employee.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(pshop!=null && newEmp!=null && newEmp.getName()!=null && newEmp.getUsername()!=null && newEmp.getPassword()!=null) {
            Employee e = employees.findByUsername(newEmp.getName());
            if(e==null) {
                e = new Employee(newEmp.getUsername(), newEmp.getPassword(), newEmp.getName(), pshop);
                employees.save(e);
                e = employees.findByUsername(e.getUsername());
                response.addProperty("success", true);
                response.addProperty("id", e.getId());
                return GSON.toJson(response);
            } else {
                response.addProperty("success", false);
                response.addProperty("message", "Empregado já existe");
                return GSON.toJson(response);
            }
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/employees/{employeeID}", method = RequestMethod.DELETE)
    public String deleteEmployee(@PathVariable(value = "printShopID") long psid, @PathVariable(value = "employeeID") long eid) {
        Employee emp = employees.findOne(eid);
        JsonObject response = new JsonObject();

        if(emp!=null) {
            employees.delete(eid);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }
}
