package io.github.proxyprint.kitchen.models.printshops;

import io.github.proxyprint.kitchen.models.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by daniel on 09-04-2016.
 */
@Entity
@Table(name = "employees")
public class Employee extends User {

    @Column(name = "name", nullable = false)
    private String name;

    public Employee() {
        super.addRole(User.Roles.ROLE_EMPLOYEE.name());
    }

    public Employee(String username, String password, String name) {
        super(username, password);
        super.addRole(User.Roles.ROLE_EMPLOYEE.name());
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Employee{"
                + "name='" + name + '\''
                + '}';
    }
}
