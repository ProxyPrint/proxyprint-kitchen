package io.github.proxyprint.kitchen.controllers.printshops.pricetable;

/**
 * Created by daniel on 02-05-2016.
 */
public class CoverTableItem {
    public static String DEFAULT = "-";
    public String coverType;
    public String priceA4, priceA3;

    public CoverTableItem() {
        this.coverType = DEFAULT;
        this.priceA4 = DEFAULT;
        this.priceA3 = DEFAULT;
    }

    public CoverTableItem(String coverType, String priceA4, String priceA3) {
        this.coverType = coverType;
        this.priceA4 = priceA4;
        this.priceA3 = priceA3;
    }

    public String getCoverType() { return coverType; }

    public void setCoverType(String coverType) {this.coverType = coverType; }

    public String getPriceA4() { return priceA4; }

    public void setPriceA4(String priceA4) { this.priceA4 = priceA4; }

    public String getPriceA3() { return priceA3; }

    public void setPriceA3(String priceA3) { this.priceA3 = priceA3; }
}
