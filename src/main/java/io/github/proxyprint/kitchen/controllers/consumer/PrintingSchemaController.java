package io.github.proxyprint.kitchen.controllers.consumer;

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintingSchemaDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by daniel on 28-04-2016.
 */
@RestController
public class PrintingSchemaController {
    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private PrintingSchemaDAO printingSchemas;

    /**
     * Get all the consumer's PrintingSchemas.
     * @param id, the id of the consumer.
     * @return set of the printing schemas belonging to the consumer matched by the id.
     */
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{consumerID}/printingschemas", method = RequestMethod.GET)
    public ResponseEntity<Set<PrintingSchema>> getConsumerPrintingSchemas(@PathVariable(value = "consumerID") long id) {
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
    @RequestMapping(value = "/consumer/{consumerID}/printingschemas", method = RequestMethod.POST)
    public ResponseEntity<String> addNewConsumerPrintingSchema(@PathVariable(value = "consumerID") long id, @RequestBody PrintingSchema ps) {
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
     * Delete a PrintingSchema.
     * Test
     * curl -u joao:1234 -X DELETE localhost:8080/consumer/1001/printingschemas/{printingSchemaID}
     * @param cid, the id of the consumer.
     * @param psid, the id of the printing schema to delete.
     * @return HttpStatus.OK if everything went well.
     */
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{consumerID}/printingschemas/{printingSchemaID}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteConsumerPrintingSchema(@PathVariable(value = "consumerID") long cid, @PathVariable(value = "printingSchemaID") long psid) {
        printingSchemas.delete(psid);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    /**
     * Edit an existing PrintingSchema.
     * Test
     * curl -X PUT --data "name=MyFancyEditedSchema&bindingSpecs=STAPLING&paperSpecs=COLOR,A4,SIMPLEX" -u joao:1234 localhost:8080/consumer/1001/printingschemas/{printingSchemaID}
     * @param cid, the id of the consumer.
     * @param psid, the PrintingSchema id.
     * @return HttpStatus.OK if everything went well.
     */
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/{consumerID}/printingschemas/{printingSchemaID}", method = RequestMethod.PUT)
    public ResponseEntity<String> editConsumerPrintingSchema(@PathVariable(value = "consumerID") long cid, @PathVariable(value = "printingSchemaID") long psid, @RequestBody PrintingSchema pschema) {
        printingSchemas.delete(psid);

        Consumer c = consumers.findOne(cid);
        boolean res = c.addPrintingSchema(pschema);
        if(res) {
            consumers.save(c);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}