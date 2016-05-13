package io.github.proxyprint.kitchen.models.printshops.pricetable;

import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;

import java.util.Map;

/**
 * Created by daniel on 14-05-2016.
 */
public class BudgetCalculator {

    private PrintShop pshop;
    private ItemFactory itemFactory;

    public BudgetCalculator(PrintShop printShop) {
        this.pshop = printShop;
        this.itemFactory = new ItemFactory();
    }

    /**
     * Calculate the price for a single specification
     *
     * @param firstPage
     * @param lastPage
     * @param pschema
     * @return -1 if the request cannot be satisfied, a value bigger than or equal to 0 representing the cost of the specification.
     */
    public float calculatePrice(int firstPage, int lastPage, PrintingSchema pschema) {
        float cost = 0;

        // Paper
        if (pschema.getPaperItem() != null) {
            int numberOfPages = (lastPage - firstPage) + 1;
            PaperItem pi = pschema.getPaperItem();
            if (pi != null) {
                RangePaperItem rpi = findIdealRangePaperItem(numberOfPages, pi);
                if (rpi != null) {
                    float res = pshop.getPriceByKey(rpi.genKey());
                    if (res != -1) {
                        cost += res;
                    } else return -1;
                } else return -1;
            }
        }

        // Binding
        if (pschema.getBindingItem() != null) {
            BindingItem bi = pschema.getBindingItem();

            if (bi.getRingsType().equals(Item.RingType.STAPLING.toString())) {
                bi.setRingThicknessInfLim(0);
                bi.setRingThicknessSupLim(0);
            } else {
                // TO DO: Dinamic check for rings size base on the number pages!!
                bi.setRingThicknessInfLim(6);
                bi.setRingThicknessSupLim(10);
            }
            //-------------------------------------------------------

            if (bi != null) {
                float res = pshop.getPriceByKey(bi.genKey());
                if (res != -1) {
                    cost += res;
                } else return -1;
            }
        }

        // Cover
        if (pschema.getCoverItem() != null) {
            CoverItem ci = pschema.getCoverItem();
            if (ci != null) {
                float res = pshop.getPriceByKey(ci.genKey());
                if (res != -1) {
                    cost += res;
                } else return -1;
            }
        }

        return cost;
    }

    /**
     * Find a range paper item given the number of pages and
     * a PaperItem
     * @param nPages number of pages
     * @param pi a paper item to match in the pricetable
     * @return The suitable RangePagerItem for the given parameters
     */
    private RangePaperItem findIdealRangePaperItem(int nPages, PaperItem pi) {
        RangePaperItem idealRpi = null;
        int maxSupLim = 0;

        for(Map.Entry<String,Float> entry : pshop.getPriceTable().entrySet()) {
            String key = entry.getKey();
            Float price = entry.getValue();

            String[] parts = key.split(",");
            if(parts[0].equals(PaperItem.KEY_BASE)) {
                // Its a paper item and I want it!
                RangePaperItem rpi = (RangePaperItem) itemFactory.createItem(key);
                PaperItem tpi = new PaperItem(rpi.getFormat(),rpi.getSides(), rpi.getColors());

                if(tpi.genKey().equals(pi.genKey())) {
                    // Check for range compatibility
                    if(nPages >= rpi.getInfLim() && nPages <= rpi.getSupLim()) {
                        // Perfect match
                        idealRpi = rpi;
                        return idealRpi;
                    }
                    if(nPages > rpi.getSupLim()) {
                        if(rpi.getSupLim() > maxSupLim) {
                            // Assure that we hold the max sup limit of the interval
                            // in case of a non perfect match
                            idealRpi = rpi;
                            maxSupLim = rpi.getSupLim();
                        }
                    }
                }
            }
        }
        return idealRpi;
    }

    /**
     * Find a range paper item given the number of pages and
     * a PaperItem
     * @param nPages number of pages
     * @param bi a binding item to match in the pricetable
     * @return The suitable RangePagerItem for the given parameters
     */
    private BindingItem findIdealBindingItem(int nPages, BindingItem bi) {
        RangePaperItem idealRpi = null;
        int maxSupLim = 0;

        for(Map.Entry<String,Float> entry : pshop.getPriceTable().entrySet()) {
            String key = entry.getKey();
            Float price = entry.getValue();

            String[] parts = key.split(",");
            if(parts[0].equals(PaperItem.KEY_BASE)) {
                // Its a paper item and I want it!
                RangePaperItem rpi = (RangePaperItem) itemFactory.createItem(key);
                PaperItem tpi = new PaperItem(rpi.getFormat(),rpi.getSides(), rpi.getColors());

                if(tpi.genKey().equals(pi.genKey())) {
                    // Check for range compatibility
                    if(nPages >= rpi.getInfLim() && nPages <= rpi.getSupLim()) {
                        // Perfect match
                        idealRpi = rpi;
                        return idealRpi;
                    }
                    if(nPages > rpi.getSupLim()) {
                        if(rpi.getSupLim() > maxSupLim) {
                            // Assure that we hold the max sup limit of the interval
                            // in case of a non perfect match
                            idealRpi = rpi;
                            maxSupLim = rpi.getSupLim();
                        }
                    }
                }
            }
        }
        return idealRpi;
    }
}
