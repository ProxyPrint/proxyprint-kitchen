package io.github.proxyprint.kitchen.models.printshops.pricetable;

/**
 * Created by daniel on 27-04-2016.
 */
public class PaperSpecItem extends PaperItem {

    public PaperSpecItem(Format format, Sides sides, Colors colors) {
        super(format, sides, colors);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s", this.colors, this.format, this.sides);
    }
}
