package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.CoverTableItem;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.PaperTableItem;
import io.github.proxyprint.kitchen.controllers.printshops.pricetable.RingTableItem;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.BindingItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.CoverItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.Item;
import io.github.proxyprint.kitchen.models.printshops.pricetable.RangePaperItem;
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

    /*------------------------------------------
    Paper
    ----------------------------------------*/
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/papers", method = RequestMethod.POST)
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
    @RequestMapping(value = "/printshops/{id}/pricetable/papers", method = RequestMethod.PUT)
    public String editPaperItem(@PathVariable(value = "id") long id, @RequestBody PaperTableItem pti) {
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
    @RequestMapping(value = "/printshops/{id}/pricetable/deletepaper", method = RequestMethod.POST)
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


    /*------------------------------------------
    Rings
    ----------------------------------------*/

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/rings", method = RequestMethod.POST)
    public String addNewRingsItem(@PathVariable(value = "id") long id, @RequestBody RingTableItem rti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            BindingItem newBi = new BindingItem(Item.RingType.valueOf(rti.getRingType()), rti.getInfLim(), rti.getSupLim());
            pshop.addItemPriceTable(newBi.genKey(),Float.parseFloat(rti.getPrice()));
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
    @RequestMapping(value = "/printshops/{id}/pricetable/rings", method = RequestMethod.PUT)
    public String editRingsItem(@PathVariable(value = "id") long id, @RequestBody RingTableItem rti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            BindingItem newBi = new BindingItem(Item.RingType.valueOf(rti.getRingType()), rti.getInfLim(), rti.getSupLim());
            pshop.getPriceTable().remove(newBi.genKey());
            pshop.addItemPriceTable(newBi.genKey(),Float.parseFloat(rti.getPrice()));
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
    @RequestMapping(value = "/printshops/{id}/pricetable/deletering", method = RequestMethod.POST)
    public String deleteRingItem(@PathVariable(value = "id") long id, @RequestBody RingTableItem rti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Remove price items
            BindingItem newBi = new BindingItem(RingTableItem.getRingTypeForPresentationString(rti.getRingType()), rti.getInfLim(), rti.getSupLim());
            pshop.getPriceTable().remove(newBi.genKey());
            printshops.save(pshop);
            response.addProperty("success", true);
            return GSON.toJson(response);
        }
        else{
            response.addProperty("success", false);
            return GSON.toJson(response);
        }
    }


    /*------------------------------------------
    Cover
    ----------------------------------------*/
    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/covers", method = RequestMethod.POST)
    public String addNewCoverItem(@PathVariable(value = "id") long id, @RequestBody CoverTableItem cti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            List<CoverItem> newCoverItems = cti.convertToCoverItems();
            for(CoverItem newCi : newCoverItems) {
                if(newCi.getFormat() == Item.Format.A4) {
                    pshop.addItemPriceTable(newCi.genKey(), Float.parseFloat(cti.getPriceA4()));
                } else {
                    pshop.addItemPriceTable(newCi.genKey(), Float.parseFloat(cti.getPriceA3()));
                }
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

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/covers", method = RequestMethod.PUT)
    public String editCoverItem(@PathVariable(value = "id") long id, @RequestBody CoverTableItem cti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            List<CoverItem> newCoverItems = cti.convertToCoverItems();
            for(CoverItem newCi : newCoverItems) {
                pshop.getPriceTable().remove(newCi.genKey());
                if(newCi.getFormat() == Item.Format.A4) {
                    pshop.addItemPriceTable(newCi.genKey(), Float.parseFloat(cti.getPriceA4()));
                } else {
                    pshop.addItemPriceTable(newCi.genKey(), Float.parseFloat(cti.getPriceA3()));
                }
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

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{id}/pricetable/deletecover", method = RequestMethod.POST)
    public String deleteCoverItem(@PathVariable(value = "id") long id, @RequestBody CoverTableItem cti) {
        PrintShop pshop = printshops.findOne(id);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Remove price items
            List<CoverItem> toRemoveCoverItems = cti.convertToCoverItems();
            for(CoverItem oldCi : toRemoveCoverItems) {
                pshop.getPriceTable().remove(oldCi.genKey());
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

    /*------------------------------------------
    Stapling
    ----------------------------------------*/

    @Secured("ROLE_MANAGER")
    @RequestMapping(value = "/printshops/{printShopID}/pricetable/editstapling", method = RequestMethod.PUT)
    public String editStaplingPrice(@PathVariable(value = "printShopID") long psid, @RequestBody String newStaplingPrice) {
        PrintShop pshop = printshops.findOne(psid);
        JsonObject response = new JsonObject();

        if(pshop!=null) {
            // Edit stapling price
            pshop.getPriceTable().put("BINDING,STAPLING,0,0", Float.parseFloat(newStaplingPrice));
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
