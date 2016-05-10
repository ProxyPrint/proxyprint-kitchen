package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.repositories.EmployeeDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * Created by daniel on 27-04-2016.
 */
@RestController
public class ManagerController {

    @Autowired
    PrintShopDAO printshops;
    @Autowired
    EmployeeDAO employees;
    @Autowired
    private Gson GSON;

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
            Employee e = employees.findByUsername(newEmp.getUsername());
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
    @RequestMapping(value = "/printshops/{printShopID}/employees", method = RequestMethod.PUT)
    public String editEmployee(@PathVariable(value = "printShopID") long psid, HttpServletRequest request) {
        PrintShop pshop = printshops.findOne(psid);
        JsonObject response = new JsonObject();

        String requestJSON = null;
        Employee editedEmp = null;
        try {
            requestJSON = IOUtils.toString( request.getInputStream());
            editedEmp = GSON.fromJson(requestJSON, Employee.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(pshop!=null && editedEmp!=null && editedEmp.getName()!=null && editedEmp.getUsername()!=null && editedEmp.getPassword()!=null) {
            Employee e = employees.findOne(editedEmp.getId());
            if(e==null) {
                response.addProperty("success", false);
                response.addProperty("message", "Empregado não existe");
                return GSON.toJson(response);
            } else {
                // Update fields
                e.setName(editedEmp.getName());
                e.setUsername(editedEmp.getUsername());
                e.setPassword(editedEmp.getPassword());
                employees.save(e);
                response.addProperty("success", true);
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
