package io.github.proxyprint.kitchen.controllers.printshop.employee;

/**
 * Created by daniel on 09-04-2016.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.printshop.employee.Employee;
import io.github.proxyprint.kitchen.models.repositories.EmployeeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

/**
 * Created by daniel on 09-04-2016.
 */
@RestController
public class EBaseController {

    @Autowired
    private EmployeeDAO employees;
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @RequestMapping(value = "/employee/login", method = RequestMethod.POST)
    public String login(WebRequest request) throws IOException {
        boolean auth = false;
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        JsonObject response = new JsonObject();
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            Employee employee = employees.findByUsername(username);
            if (employee != null) {
                auth = employee.getPassword().equals(password);
                if (auth) {
                    response.add("employee", GSON.toJsonTree(employee));
                }
            }
        }

        response.addProperty("success", auth);
        return GSON.toJson(response);
    }

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
