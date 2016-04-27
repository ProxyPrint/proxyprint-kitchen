package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.PaperTableItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.PriceItem;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by daniel on 27-04-2016.
 */
@RestController
public class ManagerController {

    @Autowired
    private PrintShopDAO printshops;
    private final static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @RequestMapping(value = "/printshops/{id}/pricetable/newpaperitem", method = RequestMethod.POST)
    public String addNewPaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            pshop.insertPaperItemsInPriceTable(pti);
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }


    @RequestMapping(value = "/printshops/{id}/pricetable/deletepaperitem", method = RequestMethod.POST)
    public String deletePaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Remove price items
            List<PriceItem> itemsToDelete = pshop.convertPaperTableItemToPaperItems(pti);
            for(PriceItem pi : itemsToDelete) {
                pshop.getPriceTable().remove(pi.toString());
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
