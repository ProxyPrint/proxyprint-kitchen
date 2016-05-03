package io.github.proxyprint.kitchen.models.printshops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.proxyprint.kitchen.controllers.printshops.PaperTableItem;
import io.github.proxyprint.kitchen.models.printshops.items.Item;
import io.github.proxyprint.kitchen.models.printshops.items.ItemFactory;
import io.github.proxyprint.kitchen.models.printshops.items.PaperItem;
import io.github.proxyprint.kitchen.models.printshops.items.RangePaperItem;
import io.github.proxyprint.kitchen.utils.gson.Exclude;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

/**
 * Created by daniel on 18-04-2016.
 */
@Entity
@Table(name = "printshops")
public class PrintShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(nullable = false, name = "address")
    private String address;
    @Column(nullable = false, name = "latitude")
    private Double latitude;
    @Column(nullable = false, name = "longitude")
    private Double longitude;
    @Column(nullable = false, name = "nif")
    private String nif;
    @Column(nullable = false, name = "logo")
    private String logo;
    @Column(nullable = false, name = "avg_rating")
    private float avgRating;
    @ElementCollection
    @JoinTable(name = "pricetables", joinColumns = @JoinColumn(name = "printshop_id"))
    @MapKeyColumn(name = "item")
    @Column(name = "price")

    @Exclude
    private Map<String, Float> priceTable;
    @JsonIgnore
    @Exclude
    @Transient
    private ItemFactory itemFactory;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "printshop")
    private Set<PrintRequest> printrequests;

    public PrintShop() {
        this.priceTable = new HashMap<>();
        itemFactory = new ItemFactory();
    }

    public PrintShop(String name, String address, Double latitude, Double longitude, String nif, String logo, float avgRating) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nif = nif;
        this.logo = logo;
        this.avgRating = avgRating;
        this.priceTable = new HashMap<>();
        this.itemFactory = new ItemFactory();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public void addPriceItem(RangePaperItem item, float price) {
        this.priceTable.put(item.genKey(), price);
    }


    public Set<PrintRequest> getPrintRequests() { return printrequests; }

    public void setPrintingSchemas(Set<PrintRequest> printingReq) { this.printrequests = printingReq; }

    public float getPrice(Item item) {
        return this.priceTable.get(item.genKey());
    }
    
    public Map<String, Float> getPriceTable(){
        return this.priceTable;
    }

    public void addPrintRequest(PrintRequest printrequest){
        this.printrequests.add(printrequest);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrintShop)) return false;

        PrintShop printShop = (PrintShop) o;

        if (getId() != printShop.getId()) return false;
        if (Float.compare(printShop.getAvgRating(), getAvgRating()) != 0) return false;
        if (getName() != null ? !getName().equals(printShop.getName()) : printShop.getName() != null) return false;
        if (getAddress() != null ? !getAddress().equals(printShop.getAddress()) : printShop.getAddress() != null)
            return false;
        if (getLatitude() != null ? !getLatitude().equals(printShop.getLatitude()) : printShop.getLatitude() != null)
            return false;
        if (getLongitude() != null ? !getLongitude().equals(printShop.getLongitude()) : printShop.getLongitude() != null)
            return false;
        if (getNif() != null ? !getNif().equals(printShop.getNif()) : printShop.getNif() != null) return false;
        if (getLogo() != null ? !getLogo().equals(printShop.getLogo()) : printShop.getLogo() != null) return false;
        return getPriceTable() != null ? getPriceTable().equals(printShop.getPriceTable()) : printShop.getPriceTable() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getLatitude() != null ? getLatitude().hashCode() : 0);
        result = 31 * result + (getLongitude() != null ? getLongitude().hashCode() : 0);
        result = 31 * result + (getNif() != null ? getNif().hashCode() : 0);
        result = 31 * result + (getAvgRating() != +0.0f ? Float.floatToIntBits(getAvgRating()) : 0);
        return result;
    }
}
