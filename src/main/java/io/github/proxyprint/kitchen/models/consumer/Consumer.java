package io.github.proxyprint.kitchen.models.consumer;

import io.github.proxyprint.kitchen.models.User;
import io.github.proxyprint.kitchen.models.printshops.PrintRequest;
import io.github.proxyprint.kitchen.utils.gson.Exclude;

import javax.persistence.*;
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "consumer")
    @Exclude
    private Set<PrintRequest> printrequests;

    public Consumer() {
    }

    public Consumer(String name, String username, String password, String email, String latitude, String longitude) {
        super(username, password);
        super.addRole(User.Roles.ROLE_USER.toString());
        this.name = name;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public Set<PrintRequest> getPrintRequests() { return printrequests; }

    public void setPrintingSchemas(Set<PrintRequest> printingReq) { this.printrequests = printingReq; }

    public void addPrintRequest(PrintRequest printrequest){
        this.printrequests.add(printrequest);
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
