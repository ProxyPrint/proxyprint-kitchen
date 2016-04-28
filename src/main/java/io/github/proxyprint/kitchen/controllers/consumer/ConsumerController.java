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
    private PrintingSchemaDAO printingSchemas;
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
     * Get all the consumer PrintingSchemas.
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

    /**
     * Add a new PrintingSchema to user's printing schemas collection.
     * Test
     * curl --data "name=MyFancySchema&bindingSpecs=SPIRAL&coverSpecs=CRISTAL_ACETATE&paperSpecs=COLOR,A4,SIMPLEX" -u joao:1234 localhost:8080/consumer/1001/printingschemas
     * @param id, the id of the consumer.
     * @param ps, the PrintingSchema created by the consumer.
     * @return HttpStatus.OK if everything went well.
     */
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{id}/printingschemas", method = RequestMethod.POST)
    public ResponseEntity<String> addNewConsumerPrintingSchema(@PathVariable(value = "id") long id, PrintingSchema ps) {
        Consumer c = consumers.findOne(id);
        boolean res = c.addPrintingSchema(ps);
        if(res) {
            consumers.save(c);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a new PrintingSchema to user's printing schemas collection.
     * Test
     * curl -u joao:1234 -X DELETE localhost:8080/consumer/1001/printingschemas/{printingSchemaID}
     * @param cid, the id of the consumer.
     * @param psid, the id of the printing schema to delete.
     * @return HttpStatus.OK if everything went well.
     */
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{consumerID}/printingschemas/{printingSchemaID}", method = RequestMethod.DELETE)
    public ResponseEntity<String> addNewConsumerPrintingSchema(@PathVariable(value = "consumerID") long cid, @PathVariable(value = "printingSchemaID") long psid) {
        printingSchemas.delete(psid);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

}
