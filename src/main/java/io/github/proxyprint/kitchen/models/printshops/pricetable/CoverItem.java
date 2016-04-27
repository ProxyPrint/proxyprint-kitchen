package io.github.proxyprint.kitchen.models.printshops.pricetable;

/**
 * Created by daniel on 27-04-2016.
 */
public class CoverItem {

    public static enum COVER_TYPE {
        CRISTAL_ACETATE, PVC_TRANSPARENT, PVC_OPAQUE
    }

    private COVER_TYPE coverType;
    private PaperItem.Format format;

    public CoverItem() {
        this.coverType = COVER_TYPE.CRISTAL_ACETATE;
        this.format = PaperItem.Format.A4;
    }

    public CoverItem(COVER_TYPE coverType, PaperItem.Format format) {
        this.coverType = coverType;
        this.format = format;
    }

    public COVER_TYPE getCoverType() { return coverType; }

    public void setCoverType(COVER_TYPE coverType) { this.coverType = coverType; }

    public PaperItem.Format getFormat() { return format; }

    public void setFormat(PaperItem.Format format) { this.format = format; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoverItem)) return false;

        CoverItem coverItem = (CoverItem) o;

        if (getCoverType() != coverItem.getCoverType()) return false;
        return getFormat() == coverItem.getFormat();

    }

    @Override
    public int hashCode() {
        int result = getCoverType() != null ? getCoverType().hashCode() : 0;
        result = 31 * result + (getFormat() != null ? getFormat().hashCode() : 0);
        return result;
    }

    // COVER_TYPE+","+FORMAT
    @Override
    public String toString() {
        return this.coverType.toString()+","+this.format.toString();
    }
}
