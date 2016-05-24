package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.core.LoggingManager;
import com.paypal.ipn.IPNMessage;
import io.github.proxyprint.kitchen.Configuration;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.repositories.*;
import io.swagger.annotations.ApiOperation;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private PrintShopDAO printShops;
    @Autowired
    private ManagerDAO managers;
    @Autowired
    private Gson GSON;

    public String generatePayPalAccessToken() throws PayPalRESTException {
        JsonObject response = new JsonObject();
        // Load configurations
        OAuthTokenCredential tokenCredential = Payment.initConfig(new File("src/main/resources/paypal.properties"));
        // Create access token
        String accessToken = tokenCredential.getAccessToken();

        // Alternative to generate access token
        /*
        Map<String, String> map = new HashMap<String, String>();
        map.put("mode", "sandbox");
        String accessToken = new OAuthTokenCredential("AXIKcgDWuFinKkX2WdRa5cOPJIbSEJZ-carWw_nYB5bOii8EK1phZQp8rOKN0b9WGMGb639hh_EboCrd", "EL0hjjAsRH0sVtRKNg1HUi6JM-paicXwpPG38neEMJD1GqRblX7rkvbM8IjGx0IYtDCGUkXGaY2gjz-Y", map).getAccessToken();
        response.addProperty("token", accessToken);*/

        return accessToken;
    }

    @ApiOperation(value = "Returns nothing", notes = "This method implements the payment check mechanism given by PayPal. This method acts as callback, it reacts to the change of status of a transaction to Completed (eCheck - complete).")
    @RequestMapping(value = "/paypal/ipn/{printRequestID}", method = RequestMethod.POST)
    protected void someRequestPaymentConfirmationFromPaylPal(@PathVariable(value = "printRequestID") long prid, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String,String> configurationMap =  Configuration.getConfig();
        IPNMessage ipnlistener = new IPNMessage(request,configurationMap);
        boolean isIpnVerified = ipnlistener.validate();
        String transactionType = ipnlistener.getTransactionType();
        Map<String,String> map = ipnlistener.getIpnMap();

        // Parse content of interest from the IPN notification JSON Body -- :D
        String payerEmail = map.get("payer_email");
        Double quantity = Double.valueOf(map.get("mc_gross"));
        String paymentStatus = map.get("payment_status");


        if(payerEmail!=null && quantity!=null && paymentStatus!=null) {
            Consumer c = consumers.findByEmail(payerEmail);
            if (c != null) {
                LoggingManager.info(this.getClass(), "******* IPN (name:value) pair : " + map + "  " +
                        "######### TransactionType : " + transactionType + "  ======== IPN verified : " + isIpnVerified);

                PrintRequest pr = printRequests.findOne(prid);

                // Divine Condition for secure request background check
                if(pr!=null && c.getPrintRequests().contains(pr) && c.getEmail().equals(payerEmail)
                        && pr.getCost()==quantity && paymentStatus.equals(PrintRequest.PAYPAL_COMPLETED_PAYMENT)) {
                    // The print request may now go to the printshop
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


    // ONLY FOR TESTING!!
    @RequestMapping(value = "/paypal/testpaypshop", method = RequestMethod.POST)
    public String testPayShareToPrintShop() throws PayPalRESTException {
        PrintRequest pr = printRequests.findOne((long)38);
        PrintShop pshop = printShops.findOne((long)8);
        Manager m = managers.findOne((long)7);
        return payShareToPrintShop(pr,m,pshop);
    }

    /**
     * Pay to a printshop its share of a print request.
     * @param prequest the print request.
     * @param manager the manager of the printshop.
     * @param pshop the prinshop where the prequest was sent.
     * @return true/false pending on paypal operation success/insuccess.
     * @throws PayPalRESTException
     */
    public String payShareToPrintShop(PrintRequest prequest, Manager manager, PrintShop pshop) throws PayPalRESTException {
        JsonObject response = new JsonObject();

        if(prequest!=null && manager!=null && pshop!=null) {
            Payout payout = new Payout();
            PayoutBatch batch = null;

            // Get an access token
            String accessToken = generatePayPalAccessToken();
            PayoutSenderBatchHeader senderBatchHeader = new PayoutSenderBatchHeader();

            // Batch
            Random random = new Random();
            String batchId = prequest.getArrivalTimestamp().toString();
            String emailSubject = "ProxyPrint - Pagamento relativo ao pedido ";
            // UNMARK getFinishedTimestamp() on full integration
            emailSubject += prequest.getArrivalTimestamp().toString() + "::" /*+ prequest.getFinishedTimestamp()*/ + " .";
            senderBatchHeader.setSenderBatchId(batchId).setEmailSubject(emailSubject);

            // Currency (90% of the print request value)
            double amountValue = prequest.getCost() * PrintShop.PRINTSHOPS_PERCENTILS_REVENUE;
            Currency amount = new Currency();
            amount.setValue(String.format("%.2f", amountValue)).setCurrency("EUR");

            // Sender Item
            String senderItemID = "PEDIDO::"+prequest.getFinishedTimestamp();
            PayoutItem senderItem = new PayoutItem();
            senderItem.setRecipientType("Email")
                    .setNote("Obrigado por usar o ProxyPrint!")
                    .setReceiver(manager.getEmail())
                    .setSenderItemId(senderItemID).setAmount(amount);

            List<PayoutItem> items = new ArrayList<PayoutItem>();
            items.add(senderItem);

            payout.setSenderBatchHeader(senderBatchHeader).setItems(items);

            APIContext apiContext = new APIContext(accessToken);
            batch = payout.createSynchronous(apiContext);

            LoggingManager.info(this.getClass(), "Payout Batch Processed with ID: " + batch.getBatchHeader().getPayoutBatchId());

            if (batch != null) {
                response.addProperty("success", true);
                response.addProperty("batch", batch.getBatchHeader().toString());
            } else {
                response.addProperty("success", false);
            }
        } else {
            response.addProperty("success", false);
        }

        return GSON.toJson(response);
    }

}
