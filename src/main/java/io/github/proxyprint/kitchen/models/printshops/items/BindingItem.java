package io.github.proxyprint.kitchen.models.printshops.items;

/**
 * Created by daniel on 27-04-2016.
 */
public class BindingItem extends Item {
    public static String KEY_BASE = "BINDING";

    private RingType ringsType;
    private int ringThicknessInfLim; // in millimeters
    private int ringThicknessSupLim; // in millimeters

    public BindingItem() {
        this.ringsType = RingType.PLASTIC;
        this.ringThicknessInfLim = 0;
        this.ringThicknessInfLim = 0;
    }

    public BindingItem(RingType ringsType, int ringThicknessInfLim, int ringThicknessSupLim) {
        this.ringsType = ringsType;
        this.ringThicknessInfLim = ringThicknessInfLim;
        this.ringThicknessSupLim = ringThicknessSupLim;
    }

    public RingType getRingsType() { return ringsType; }

    public void setRingsType(RingType ringsType) { this.ringsType = ringsType; }

    public int getRingThicknessInfLim() { return ringThicknessInfLim; }

    public void setRingThicknessInfLim(int ringThicknessInfLim) { this.ringThicknessInfLim = ringThicknessInfLim; }

    public int getRingThicknessSupLim() { return ringThicknessSupLim; }

    public void setRingThicknessSupLim(int ringThicknessSupLim) { this.ringThicknessSupLim = ringThicknessSupLim; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BindingItem)) return false;

        BindingItem that = (BindingItem) o;

        if (getRingThicknessInfLim() != that.getRingThicknessInfLim()) return false;
        if (getRingThicknessSupLim() != that.getRingThicknessSupLim()) return false;
        return getRingsType() != null ? getRingsType().equals(that.getRingsType()) : that.getRingsType() == null;

    }

    @Override
    public int hashCode() {
        int result = getRingsType() != null ? getRingsType().hashCode() : 0;
        result = 31 * result + getRingThicknessInfLim();
        result = 31 * result + getRingThicknessSupLim();
        return result;
    }

    @Override
    public String genKey() {
        return String.format("%s,%s,%d,%d",KEY_BASE, this.ringsType.toString(), this.ringThicknessInfLim, this.ringThicknessSupLim);
    }
}
