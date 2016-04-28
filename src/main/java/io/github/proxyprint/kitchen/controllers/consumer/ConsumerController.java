package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintingSchemaDAO;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by daniel on 04-04-2016.
 */
@RestController
public class ConsumerController {

    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private Gson GSON;

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

    @RequestMapping(value = "/consumer/upload", method = RequestMethod.POST)
    public String handleFileUpload(@RequestParam("file") MultipartFile file, WebRequest req) {
        JsonObject response = new JsonObject();

        String filetype = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!filetype.equals("pdf")) {
            throw new RuntimeException("Error! Not a pdf");
        }
        String name = FilenameUtils.removeExtension(file.getOriginalFilename());

        if (!file.isEmpty()) {
            try {
                PDDocument pdf = PDDocument.load(file.getInputStream());
                int count = pdf.getNumberOfPages();

                System.out.println("Este pdf tem " + count + " páginas!!");
                response.addProperty("success", true);
                response.addProperty("message", "Este pdf tem " + count + " páginas!!");
            } catch (Exception e) {
                response.addProperty("success", false);
                response.addProperty("message", "You failed to upload " + name + " => " + e.getMessage());
            }
        } else {
            response.addProperty("success", false);
            response.addProperty("message", "You failed to upload because the file was empty");
        }
        return GSON.toJson(response);
    }

    /**
     *
     * @param id, the id of the consumer.
     * @return set of the printing schemas belonging to the consumer matched by the id.
     */
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{id}/printingschemas", method = RequestMethod.GET)
    public ResponseEntity<Set<PrintingSchema>> getConsumerPrintingSchemas(@PathVariable(value = "id") long id) {
        Set<PrintingSchema> consumerSchemas = consumers.findOne(id).getPrintingSchemas();
        if(consumerSchemas!=null) {
            return new ResponseEntity<>(consumerSchemas, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Secured({"ROLE_USER"})

    /*
    *
    *     @RequestMapping(value = "/request/register", method = RequestMethod.POST)
    public ResponseEntity<RegisterRequest> registerRequest(@RequestBody RegisterRequest registerRequest) {
        registerRequests.save(registerRequest);
        return new ResponseEntity<>(registerRequest, HttpStatus.OK);
    }
    *
    * */

    // public ResponseEntity<Map<String,Set<PaperTableItem>>> getPrintShopPriceTable(@PathVariable(value = "id") long id)

    // TESTING
    @RequestMapping(value = "/schema", method = RequestMethod.GET)
    public String testPrintinSchema(WebRequest request) {
        JsonObject response = new JsonObject();
        List<PrintingSchema> list = new ArrayList<>();
        long id = 1001;
        Consumer c = consumers.findOne(id);

        for(PrintingSchema ps : c.getPrintingSchemas()) {
            list.add(ps);
        }

        return GSON.toJsonTree(list).toString();
    }
}
