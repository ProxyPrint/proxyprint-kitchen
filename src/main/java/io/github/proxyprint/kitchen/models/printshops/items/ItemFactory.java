package io.github.proxyprint.kitchen.models.printshops.items;

import io.github.proxyprint.kitchen.controllers.printshops.PaperTableItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 28-04-2016.
 */
public class ItemFactory {

    public ItemFactory(){}

    /**
     * Create a concrete instance of an item.
     * @param item, String that represents the item specs as stored in the database
     *              or as its received from front-end applications
     * @return A concrete instance of the Item derived from the input String
     * which is parsed along the function cut in pieces and feeded to the returned object.
     */
    public Item createItem(String item) {
        PaperItem.Colors colors;
        PaperItem.Format format;
        PaperItem.Sides sides;
        int infLim, supLim;
        String itemType;

        String[] parts = item.split(",");
        itemType = parts[0];

        if(itemType.equals(RangePaperItem.KEY_BASE)) {
            colors = PaperItem.Colors.valueOf(parts[1]);
            format = PaperItem.Format.valueOf(parts[2]);
            sides = PaperItem.Sides.valueOf(parts[3]);

            if(parts[4]!=null && parts[5]!=null) {
                infLim = Integer.parseInt(parts[4]);
                supLim = Integer.parseInt(parts[5]);

                return new RangePaperItem(format, sides, colors, infLim, supLim);
            } else {
                return new PaperItem(format,sides,colors);
            }
        }
        else if(itemType.equals(BindingItem.KEY_BASE)) {
            
        }
        else if(itemType.equals(CoverItem.KEY_BASE)) {
            // ...
        }

        return null;
    }

    /**
     * Convert a PaperTableItem to its respective PriceItems
     * @param pti, a new entry in the price table
     * @return List<RangePaperItem>, List of price items which result from the conversion.
     */
    public List<RangePaperItem> fromPaperTableItemToPaperItems(PaperTableItem pti) {
        List<RangePaperItem> res = new ArrayList<>();

        if(!pti.getPriceA4SIMPLEX().equals(PaperTableItem.DEFAULT)) {
            res.add(new RangePaperItem(PaperItem.Format.A4, PaperItem.Sides.SIMPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim()));
        }
        if(!pti.getPriceA4DUPLEX().equals(PaperTableItem.DEFAULT)) {
            res.add(new RangePaperItem(PaperItem.Format.A4, PaperItem.Sides.DUPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim()));
        }
        if(!pti.getPriceA3SIMPLEX().equals(PaperTableItem.DEFAULT)) {
            res.add(new RangePaperItem(PaperItem.Format.A3, PaperItem.Sides.SIMPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim()));
        }
        if(!pti.getPriceA3DUPLEX().equals(PaperTableItem.DEFAULT)) {
            res.add(new RangePaperItem(PaperItem.Format.A3, PaperItem.Sides.DUPLEX, PaperItem.Colors.valueOf(pti.getColors()), pti.getInfLim(), pti.getSupLim()));
        }

        return res;
    }
}
