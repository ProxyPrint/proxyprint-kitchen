package io.github.proxyprint.kitchen.controllers.printshops.pricetable;

/**
 * Created by daniel on 02-05-2016.
 */
public class RingTableItem implements Comparable<RingTableItem> {
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
