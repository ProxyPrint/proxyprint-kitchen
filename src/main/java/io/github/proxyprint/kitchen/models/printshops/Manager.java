package io.github.proxyprint.kitchen.models.printshops;

import io.github.proxyprint.kitchen.models.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by daniel on 18-04-2016.
 */
@Entity
@Table(name = "managers")
public class Manager extends User {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "print_shop_id", nullable = false)
    private String printShopID;

    public Manager() {}

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    @Override
    public String getPassword() { return password; }

    @Override
    public void setPassword(String password) { this.password = password; }

    public String getPrintShopID() { return printShopID; }

    public void setPrintShopID(String printShopID) { this.printShopID = printShopID; }

    @Override
    public String toString() {
        return "Manager{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", printShopID='" + printShopID + '\'' +
                '}';
    }
}
