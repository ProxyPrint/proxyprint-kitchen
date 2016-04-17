package io.github.proxyprint.kitchen.models;

import io.github.proxyprint.kitchen.models.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by daniel on 13-04-2016.
 */
@Entity
@Table(name = "admin")
public class Admin extends User {
    @Column(name = "email", nullable = true)
    private String email;

    public Admin() {}

    public Admin(String username, String password, String email) {
        super(username, password);
        this.email = email;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Admin{" + super.toString() +
                "email='" + email + '\'' +
                '}';
    }
}