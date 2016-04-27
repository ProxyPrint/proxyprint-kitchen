package io.github.proxyprint.kitchen.models.printshops.pricetable;

/**
 * Created by daniel on 27-04-2016.
 */
public class BindingItem {

    public static String STAPLING = "STAPLING";

    public static enum RING_TYPE {
        PLASTIC, SPIRAL, WIRE, STEELMAT
    }

    private RING_TYPE ringsType;
    private int ringThicknessInfLim; // in millimeters
    private int ringThicknessSupLim; // in millimeters

    public BindingItem() {
        this.ringsType = RING_TYPE.PLASTIC;
        this.ringThicknessInfLim = 0;
        this.ringThicknessInfLim = 0;
    }

    public BindingItem(RING_TYPE ringsType, int ringThicknessInfLim, int ringThicknessSupLim) {
        this.ringsType = ringsType;
        this.ringThicknessInfLim = ringThicknessInfLim;
        this.ringThicknessSupLim = ringThicknessSupLim;
    }

    public RING_TYPE getRingsType() { return ringsType; }

    public void setRingsType(RING_TYPE ringsType) { this.ringsType = ringsType; }

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

    // RINGS_TYPE+","+INF_LIM+","+SUP_LIM
    @Override
    public String toString() {
        return String.format("%s,%d,%d", this.ringsType.toString(), this.ringThicknessInfLim, this.ringThicknessSupLim);
    }
}
