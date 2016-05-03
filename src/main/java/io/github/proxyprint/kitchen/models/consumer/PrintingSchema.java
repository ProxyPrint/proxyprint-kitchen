package io.github.proxyprint.kitchen.models.consumer;

import io.github.proxyprint.kitchen.models.printshops.pricetable.BindingItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.CoverItem;
import io.github.proxyprint.kitchen.models.printshops.pricetable.PaperItem;

import javax.persistence.*;

/**
 * Created by daniel on 27-04-2016.
 */
@Entity
@Table(name = "printing_schemas")
public class PrintingSchema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "pschema_name", nullable = false)
    private String name;
    @Column(name = "paper_specs", nullable = false)
    private String paperSpecs;
    @Column(name = "binding_specs", nullable = true)
    private String bindingSpecs;
    @Column(name = "cover_specs", nullable = true)
    private String coverSpecs;

    public PrintingSchema() {}

    public PrintingSchema(String name , PaperItem psi, BindingItem bi, CoverItem ci) {
        this.paperSpecs = psi.toString();
        if(bi!=null) {
            this.bindingSpecs = bi.genKey();
            if(ci!=null) {
                this.coverSpecs = ci.genKey();
            }
        } else {
            this.bindingSpecs = BindingItem.RingType.STAPLING.toString();
        }
    }

    public PrintingSchema(String name, String paperSpecs, String bindingSpecs, String coverSpecs) {
        this.paperSpecs = paperSpecs;
        this.bindingSpecs = bindingSpecs;
        this.coverSpecs = coverSpecs;
    }

    public long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPaperSpecs() { return paperSpecs; }

    public void setPaperSpecs(String paperSpecs) { this.paperSpecs = paperSpecs; }

    public String getBindingSpecs() { return bindingSpecs; }

    public void setBindingSpecs(String bindingSpecs) { this.bindingSpecs = bindingSpecs; }

    public String getCoverSpecs() { return coverSpecs; }

    public void setCoverSpecs(String coverSpecs) { this.coverSpecs = coverSpecs; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrintingSchema)) return false;

        PrintingSchema that = (PrintingSchema) o;

        if (getId() != that.getId()) return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        if (getPaperSpecs() != null ? !getPaperSpecs().equals(that.getPaperSpecs()) : that.getPaperSpecs() != null)
            return false;
        if (getBindingSpecs() != null ? !getBindingSpecs().equals(that.getBindingSpecs()) : that.getBindingSpecs() != null)
            return false;
        return getCoverSpecs() != null ? getCoverSpecs().equals(that.getCoverSpecs()) : that.getCoverSpecs() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPaperSpecs() != null ? getPaperSpecs().hashCode() : 0);
        result = 31 * result + (getBindingSpecs() != null ? getBindingSpecs().hashCode() : 0);
        result = 31 * result + (getCoverSpecs() != null ? getCoverSpecs().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrintingSchema{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", paperSpecs='" + paperSpecs + '\'' +
                ", bindingSpecs='" + bindingSpecs + '\'' +
                ", coverSpecs='" + coverSpecs + '\'' +
                '}';
    }
}
