package io.github.proxyprint.kitchen.models.printshops;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by daniel on 18-04-2016.
 */
@Entity
@Table(name = "printshops")
public class PrintShop {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(nullable = false, name = "address")
    private String address;
    @Column(nullable = false, name = "latitude")
    private String latitude;
    @Column(nullable = false, name = "longitude")
    private String longitude;
    @Column(nullable = false, name = "nif")
    private String nif;
    @Column(nullable = false, name = "logo")
    private String logo;
    @Column(nullable = false, name = "avg_rating")
    private float avgRating;
    // Missing priceTable : <PriceItem,price:float>


    public PrintShop() {}

    public PrintShop(String name, String address, String latitude, String longitude, String nif, String logo, float avgRating) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.nif = nif;
        this.logo = logo;
        this.avgRating = avgRating;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }

   public void setAddress(String address) { this.address = address; }

    public String getLatitude() { return latitude; }

    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }

    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getNif() { return nif; }

    public void setNif(String nif) { this.nif = nif; }

    public String getLogo() { return logo; }

    public void setLogo(String logo) { this.logo = logo; }

    public float getAvgRating() { return avgRating; }

    public void setAvgRating(float avgRating) { this.avgRating = avgRating; }

    @Override
    public String toString() {
        return "PrintShop{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", nif='" + nif + '\'' +
                ", logo='" + logo + '\'' +
                ", avgRating=" + avgRating +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrintShop)) return false;

        PrintShop printShop = (PrintShop) o;

        if (Float.compare(printShop.getAvgRating(), getAvgRating()) != 0) return false;
        if (getName() != null ? !getName().equals(printShop.getName()) : printShop.getName() != null) return false;
        if (getAddress() != null ? !getAddress().equals(printShop.getAddress()) : printShop.getAddress() != null)
            return false;
        if (getLatitude() != null ? !getLatitude().equals(printShop.getLatitude()) : printShop.getLatitude() != null)
            return false;
        if (getLongitude() != null ? !getLongitude().equals(printShop.getLongitude()) : printShop.getLongitude() != null)
            return false;
        if (getNif() != null ? !getNif().equals(printShop.getNif()) : printShop.getNif() != null) return false;
        return getLogo() != null ? getLogo().equals(printShop.getLogo()) : printShop.getLogo() == null;

    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getAddress() != null ? getAddress().hashCode() : 0);
        result = 31 * result + (getLatitude() != null ? getLatitude().hashCode() : 0);
        result = 31 * result + (getLongitude() != null ? getLongitude().hashCode() : 0);
        result = 31 * result + (getNif() != null ? getNif().hashCode() : 0);
        return result;
    }
}
