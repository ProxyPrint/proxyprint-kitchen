package io.github.proxyprint.kitchen.models.printshops;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.proxyprint.kitchen.models.User;
import io.github.proxyprint.kitchen.utils.gson.Exclude;

import javax.persistence.*;

/**
 * Created by daniel on 09-04-2016.
 */
@Entity
@Table(name = "employees")
public class Employee extends User {

    @Column(name = "name", nullable = false)
    private String name;
    @JsonIgnore
    @Exclude
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "printshop_id")
    private PrintShop printShop;

    public Employee() {
        super.addRole(User.Roles.ROLE_EMPLOYEE.name());
    }

    public Employee(String username, String password, String name, PrintShop pshop) {
        super(username, password);
        super.addRole(User.Roles.ROLE_EMPLOYEE.name());
        this.name = name;
        this.printShop = pshop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PrintShop getPrintShop() { return printShop; }

    public void setPrintShop(PrintShop printShop) { this.printShop = printShop; }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", printShop=" + printShop.getName() +
                '}';
    }
}
