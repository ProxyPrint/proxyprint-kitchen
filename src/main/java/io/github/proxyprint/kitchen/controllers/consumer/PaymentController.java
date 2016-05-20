package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.core.LoggingManager;
import com.paypal.ipn.IPNMessage;
import io.github.proxyprint.kitchen.Configuration;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.EmployeeDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintRequestDAO;
import io.github.proxyprint.kitchen.models.repositories.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 20-05-2016.
 */
@RestController
public class PaymentController {
    @Autowired
    private UserDAO users;
    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private EmployeeDAO employees;
    @Autowired
    private PrintRequestDAO printRequests;
    @Autowired
    private Gson GSON;

    @RequestMapping(value = "/paypal/getaccesstoken", method = RequestMethod.GET)
    public String testPayPal() throws PayPalRESTException {
        JsonObject response = new JsonObject();
        // Load configurations
        OAuthTokenCredential tokenCredential = Payment.initConfig(new File("src/main/resources/paypal.properties"));
        // Create access token
        // String accessToken = tokenCredential.getAccessToken();

        Map<String, String> map = new HashMap<String, String>();
        map.put("mode", "sandbox");
        String accessToken = new OAuthTokenCredential("AXIKcgDWuFinKkX2WdRa5cOPJIbSEJZ-carWw_nYB5bOii8EK1phZQp8rOKN0b9WGMGb639hh_EboCrd", "EL0hjjAsRH0sVtRKNg1HUi6JM-paicXwpPG38neEMJD1GqRblX7rkvbM8IjGx0IYtDCGUkXGaY2gjz-Y", map).getAccessToken();
        response.addProperty("token", accessToken);

        return GSON.toJson(response);
    }


    // eCheck - complete
    @RequestMapping(value = "/paypal/ipn/{printRequestID}", method = RequestMethod.POST)
    protected void doPost(@PathVariable(value = "printRequestID") long prid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // For a full list of configuration parameters refer in wiki page.
        // (https://github.com/paypal/sdk-core-java/wiki/SDK-Configuration-Parameters)
        Map<String,String> configurationMap =  Configuration.getConfig();
        IPNMessage ipnlistener = new IPNMessage(request,configurationMap);
        boolean isIpnVerified = ipnlistener.validate();
        String transactionType = ipnlistener.getTransactionType();
        Map<String,String> map = ipnlistener.getIpnMap();

        // Parse content of interest from the IPN notification JSON Body -- :D Todalooo
        String payerEmail = map.get("payer_email");
        Double quantity = Double.valueOf(map.get("quantity"));
        String paymentStatus = map.get("payment_status");


        if(payerEmail!=null && quantity!=null && paymentStatus!=null) {
            Consumer c = consumers.findByEmail(payerEmail);
            if (c != null) {
                LoggingManager.info(this.getClass(), "******* IPN (name:value) pair : " + map + "  " +
                        "######### TransactionType : " + transactionType + "  ======== IPN verified : " + isIpnVerified);

                PrintRequest pr = printRequests.findOne(prid);

                // Divine Condition for secure request background check
                if(pr!=null && c.getPrintRequests().contains(pr) && c.getEmail().equals(payerEmail)
                        /*&& pr.getCost()==quantity*/ && paymentStatus.equals(PrintRequest.PAYPAL_COMPLETED_PAYMENT)) {
                    // The print request may now go to the printshop!
                    pr.setStatus(PrintRequest.Status.PENDING);
                    printRequests.save(pr);
                    return;
                }
            } else {
                LoggingManager.info(this.getClass(), "PayPal transaction ERROR: consumer with email <" + payerEmail + "> is not registred in ProxyPrint.");
                return;
            }
        } else {
            LoggingManager.info(this.getClass(), "PayPal transaction ERROR: bad IPN JSON body for values payerEmail, quantity, paymentStatus, paymentData");
        }
    }
}
