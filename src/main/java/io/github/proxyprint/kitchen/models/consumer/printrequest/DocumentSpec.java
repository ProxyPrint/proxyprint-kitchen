package io.github.proxyprint.kitchen.models.consumer.printrequest;

import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;

/**
 * Created by daniel on 09-05-2016.
 */
public class DocumentSpec {
    private int firstPage;
    private int lastPage;
    private PrintingSchema printingSchema;

    public DocumentSpec() {}

    public DocumentSpec(int firstPage, int lastPage, PrintingSchema printingSchema) {
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.printingSchema = printingSchema;
    }

    public int getFirstPage() { return firstPage; }

    public void setFirstPage(int firstPage) { this.firstPage = firstPage; }

    public int getLastPage() { return lastPage; }

    public void setLastPage(int lastPage) { this.lastPage = lastPage; }

    public PrintingSchema getPrintingSchema() { return printingSchema; }

    public void setPrintingSchema(PrintingSchema printingSchema) { this.printingSchema = printingSchema; }

    @Override
    public String toString() {
        return "DocumentSpec{" +
                "firstPage=" + firstPage +
                ", lastPage=" + lastPage +
                ", printingSchema=" + printingSchema.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentSpec)) return false;

        DocumentSpec that = (DocumentSpec) o;

        if (getFirstPage() != that.getFirstPage()) return false;
        if (getLastPage() != that.getLastPage()) return false;
        return getPrintingSchema() != null ? getPrintingSchema().equals(that.getPrintingSchema()) : that.getPrintingSchema() == null;

    }

    @Override
    public int hashCode() {
        int result = getFirstPage();
        result = 31 * result + getLastPage();
        result = 31 * result + (getPrintingSchema() != null ? getPrintingSchema().hashCode() : 0);
        return result;
    }
}
