package io.github.proxyprint.kitchen.controllers.printshops;

/**
 * Created by daniel on 09-04-2016.
 */

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.repositories.EmployeeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by daniel on 09-04-2016.
 */
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeDAO employees;
    @Autowired
    private Gson GSON;

    /*The employee doesn't have resgiter method. A printshop manager should register its employees*/
    /*ONLY FOR TESTING!!*/
    @RequestMapping(value = "/employee/register", method = RequestMethod.POST)
    public String addEmployee(WebRequest request) {
        boolean success = false;

        JsonObject response = new JsonObject();
        String username = request.getParameter("username");
        Employee e = employees.findByUsername(username);

        if (e == null) {
            String password = request.getParameter("password");
            String name = request.getParameter("name");
            e = new Employee(username, password, name);
            //e.addRole(User.Roles.ROLE_EMPLOYEE.toString());

            employees.save(e);
            response.add("employee", GSON.toJsonTree(e));
            success = true;
        } else {
            response.addProperty("error", "O username já se encontra em uso.");
            success = false;
        }

        response.addProperty("success", success);
        return GSON.toJson(response);
    }

}
