package io.github.proxyprint.kitchen.models.consumer;

import io.github.proxyprint.kitchen.models.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by daniel on 04-04-2016.
 */
@Entity
@Table(name = "consumers")
public class Consumer extends User {

    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = true) // all true because null constraint block adition of employees
    private String email;
    @Column(name = "latitude", nullable = true)
    private String latitude;
    @Column(name = "longitude", nullable = true)
    private String longitude;
    @JoinColumn (name="consumer_id")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<PrintingSchema> printingSchemas;

    public Consumer() {
        this.printingSchemas = new HashSet<>();
    }

    public Consumer(String name, String username, String password, String email, String latitude, String longitude) {
        super(username, password);
        super.addRole(User.Roles.ROLE_USER.toString());
        this.name = name;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.printingSchemas = new HashSet<>();
    }

    public Consumer(String username, String password, String name, String email, String latitude, String longitude, Set<PrintingSchema> printingSchemas) {
        super(username, password);
        this.name = name;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.printingSchemas = printingSchemas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Set<PrintingSchema> getPrintingSchemas() { return printingSchemas; }

    public void setPrintingSchemas(Set<PrintingSchema> printingSchemas) { this.printingSchemas = printingSchemas; }

    public boolean addPrintingSchema(PrintingSchema ps) {
        return this.printingSchemas.add(ps);
    }

    public boolean deletePrintingSchema(long psID) {
        Iterator it = this.printingSchemas.iterator();
        while (it.hasNext()) {
            PrintingSchema ps = (PrintingSchema)it.next();
            if(ps.getId() == psID){
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Consumer{" + super.toString()
                + "name='" + name + '\''
                + ", email='" + email + '\''
                + ", latitude='" + latitude + '\''
                + ", longitude='" + longitude + '\''
                + '}';
    }
}
