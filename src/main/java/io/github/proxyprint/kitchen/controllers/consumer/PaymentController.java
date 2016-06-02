package io.github.proxyprint.kitchen.controllers.consumer;

import com.google.gson.Gson;
import com.paypal.base.rest.PayPalRESTException;
import com.paypal.core.LoggingManager;
import com.paypal.ipn.IPNMessage;
import io.github.proxyprint.kitchen.Configuration;
import io.github.proxyprint.kitchen.config.NgrokConfig;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.repositories.*;
import io.github.proxyprint.kitchen.utils.PayPalWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    private PrintShopDAO printShops;
    @Autowired
    private ManagerDAO managers;
    @Autowired
    private Gson GSON;

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
        String transactionID = map.get("txn_id");


        if(payerEmail!=null && quantity!=null && paymentStatus!=null) {
            Consumer c = consumers.findByEmail(payerEmail);
            if (c != null) {
                LoggingManager.info(this.getClass(), "******* IPN (name:value) pair : " + map + "  " +
                        "######### TransactionType : " + transactionType + "  ======== IPN verified : " + isIpnVerified);

                PrintRequest pr = printRequests.findOne(prid);

                // Divine Condition for secure request background check
                if(pr!=null && c.getPrintRequests().contains(pr) && pr.getCost()==quantity && paymentStatus.equals(PayPalWrapper.PAYPAL_COMPLETED_PAYMENT)) {
                    // The print request may now go to the printshop
                    pr.setStatus(PrintRequest.Status.PENDING);
                    pr.setPayPalSaleID(transactionID);
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
        PayPalWrapper ppw = new PayPalWrapper();
        return String.valueOf(ppw.payShareToPrintShop(pr,m,pshop));
    }

    // ONLY FOR TESTING!!
    @RequestMapping(value = "/paypal/testrefund", method = RequestMethod.POST)
    public String testRefund() throws PayPalRESTException {
        Consumer c = consumers.findOne((long)2);
        PrintRequest pr = printRequests.findOne((long)29);
        PayPalWrapper ppw = new PayPalWrapper();
        return String.valueOf(ppw.refundConsumerCancelledPrintRequest(c,pr));
    }

}
