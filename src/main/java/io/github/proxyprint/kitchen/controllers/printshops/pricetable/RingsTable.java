package io.github.proxyprint.kitchen.controllers.printshops.pricetable;

import io.github.proxyprint.kitchen.models.printshops.items.BindingItem;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by daniel on 01-05-2016.
 */
public class RingsTable {

    public class RingTableItem extends TableItem implements Comparable<RingTableItem> {
        private String ringType;
        private int infLim;
        private int supLim;
        private String price;

        public RingTableItem() {
        }

        public RingTableItem(String ringType, int infLim, int supLim, String price) {
            this.ringType = ringType;
            this.infLim = infLim;
            this.supLim = supLim;
            this.price = price;
        }

        public String getRingType() {
            return ringType;
        }

        public void setRingType(String ringType) {
            this.ringType = ringType;
        }

        public int getInfLim() {
            return infLim;
        }

        public void setInfLim(int infLim) {
            this.infLim = infLim;
        }

        public int getSupLim() {
            return supLim;
        }

        public void setSupLim(int supLim) {
            this.supLim = supLim;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        @Override
        public int compareTo(RingTableItem rti) {
            if(rti.getSupLim() > this.getSupLim()) return -1;
            else return 1;
        }
    }

    private Map<String,Set<RingTableItem>> items;
    private float staplingPrice;

    public RingsTable() {
        this.items = new HashMap<>();
        this.staplingPrice = 0;
    }

    public Map<String, Set<RingTableItem>> getItems() {
        return items;
    }

    public void setItems(Map<String, Set<RingTableItem>> items) {
        this.items = items;
    }

    public float getStaplingPrice() { return staplingPrice; }

    public void setStaplingPrice(float staplingPrice) { this.staplingPrice = staplingPrice; }

    public void addBindingItem(BindingItem bi, float price) {
        if(bi.getRingsType().equals(BindingItem.RingType.STAPLING)) {
            this.staplingPrice = price;
        }
        else {
            RingTableItem rti = new RingTableItem(bi.getRingsType().toString(), bi.getRingThicknessInfLim(), bi.getRingThicknessSupLim(), String.valueOf(price));
            if (items.containsKey(bi.getRingsType().toString())) {
                this.items.get(bi.getRingsType().toString()).add(rti);
            } else {
                Set<RingTableItem> newRingSet = new TreeSet<>();
                newRingSet.add(rti);
                this.items.put(rti.getRingType(), newRingSet);
            }
        }
    }

}
