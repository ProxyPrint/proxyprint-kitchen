package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BudgetCalculator;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.DocumentDAO;
import io.github.proxyprint.kitchen.models.repositories.DocumentSpecDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintRequestDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintingSchemaDAO;
import io.swagger.annotations.ApiOperation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;



/**
 * Created by daniel on 04-04-2016.
 */
@RestController
public class ConsumerController {

    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private PrintingSchemaDAO printingSchemas;
    @Autowired
    private DocumentDAO documents;
    @Autowired
    private DocumentSpecDAO documentsSpecs;
    @Autowired
    private PrintRequestDAO printRequests;
    @Autowired
    private PrintShopDAO printShops;
    @Autowired
    private PrintRequestDAO printrequests;
    @Autowired
    private Gson GSON;

    @ApiOperation(value = "Returns success/insuccess", notes = "This method allows consumer registration.")
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

    @Secured("ROLE_USER")
    @RequestMapping(value = "/consumer/{username}/notifications", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteAllNotifications (@PathVariable(value = "username") String username) {
        JsonObject response = new JsonObject();
        Consumer c = consumers.findByUsername(username);
        c.removeAllNotifications();
        consumers.save(c);

        response.addProperty("success", true);
        return new ResponseEntity<>(GSON.toJson(response), HttpStatus.OK);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value ="/consumer/{username}/notifications", method = RequestMethod.PUT)
    public ResponseEntity<String> readAllNotifications (@PathVariable(value = "username") String username) {

        JsonObject response = new JsonObject();
        Consumer c = consumers.findByUsername(username);
        c.readAllNotifications();
        consumers.save(c);

        response.addProperty("success", true);
        return new ResponseEntity<>(GSON.toJson(response), HttpStatus.OK);
    }

    @ApiOperation(value = "Returns pending requests.", notes = "Returns the pending requests from the user.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/requests", method = RequestMethod.GET)
    public String getRequests(Principal principal) {

        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());

        if (consumer == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }


        List<PrintRequest.Status> status = new ArrayList<>();
        status.add(PrintRequest.Status.PENDING);

        List<PrintRequest> printRequestsList = printrequests.findByStatusInAndConsumer(status, consumer);
        Type listOfPRequests = new TypeToken<List<PrintShop>>(){}.getType();

        response.add("printrequests", GSON.toJsonTree(printRequestsList,listOfPRequests));
        response.addProperty("success", true);
        return GSON.toJson(response);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/requests/cancel/{id}", method = RequestMethod.POST)
    public String cancelRequests(@PathVariable(value = "id") long id, Principal principal) {

        JsonObject response = new JsonObject();
        Consumer consumer = consumers.findByUsername(principal.getName());

        if (consumer == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        }

        PrintRequest printRequest = printrequests.findByIdInAndConsumer(id,consumer);
        System.out.println(printRequest.getId());

        if(printRequest.getStatus() == PrintRequest.Status.PENDING){
            //printrequests.delete(printRequest);
            consumer.getPrintRequests().remove(printRequest);
            consumers.save(consumer);
            response.addProperty("success", true);
        } else{
            response.addProperty("success", false);
        }

        return GSON.toJson(response);
    }
}
