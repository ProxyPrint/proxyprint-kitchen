package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.items.RangePaperItem;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by daniel on 27-04-2016.
 */
@RestController
public class ManagerController {

    @Autowired
    private PrintShopDAO printshops;
    @Autowired
    private Gson GSON;

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable", method = RequestMethod.POST)
    public String addNewPaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            pshop.insertPaperTableItemsInPriceTable(pti);
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/deletepaperitem", method = RequestMethod.POST)
    public String deletePaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Remove price items
            List<RangePaperItem> itemsToDelete = pshop.convertPaperTableItemToPaperItems(pti);
            for(RangePaperItem pi : itemsToDelete) {
                pshop.getPriceTable().remove(pi.genKey());
            }
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }
}
