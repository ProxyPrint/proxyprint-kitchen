package io.github.proxyprint.kitchen.models.consumer;

import io.github.proxyprint.kitchen.models.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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

    public Consumer() {
        super.addRole(User.Roles.ROLE_USER.name());
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
