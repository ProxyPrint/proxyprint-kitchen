package io.github.proxyprint.kitchen.models.printshops;

import io.github.proxyprint.kitchen.utils.gson.Exclude;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 * Created by MGonc on 28/04/16.
 */
@Entity
@Table(name = "printrequests")
public class PrintRequest implements Serializable {

    public enum Status {
        PENDING, IN_PROGRESS, FINISHED, LIFTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, name = "cost")
    private float cost;
    @Column(nullable = false, name = "arrival")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date arrivalTimestamp;
    @Column(nullable = true, name = "finished")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedTimestamp;
    @Column(nullable = true, name = "delivered")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date deliveredTimestamp;
    @Column(nullable = true, name = "empattended")
    private String empAttended;
    @Column(nullable = true, name = "empdelivered")
    private String empDelivered;
    @Column(nullable = false, name = "customer")
    private String customerID;
    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @Exclude private PrintShop printshop;

    public PrintRequest() {
    }

    public PrintRequest(float cost, Date arrivalTimestamp, String customerID, Status status) {
        this.cost = cost;
        this.arrivalTimestamp = arrivalTimestamp;
        this.customerID = customerID;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public float getCost() {
        return cost;
    }

    public Date getArrivalTimestamp() {
        return arrivalTimestamp;
    }

    public Date getDeliveredTimestamp() {
        return deliveredTimestamp;
    }

    public String getEmpAttended() {
        return empAttended;
    }

    public Date getFinishedTimestamp() {
        return finishedTimestamp;
    }

    public String getEmpDelivered() {
        return empDelivered;
    }

    public String getCustomerID() {
        return customerID;
    }

    public Status getStatus() {
        return status;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setArrivalTimestamp(Date arrivalTimestamp) {
        this.arrivalTimestamp = arrivalTimestamp;
    }

    public void setFinishedTimestamp(Date finishedTimestamp) {
        this.finishedTimestamp = finishedTimestamp;
    }

    public void setEmpAttended(String empAttended) {
        this.empAttended = empAttended;
    }

    public void setDeliveredTimestamp(Date deliveredTimestamp) {
        this.deliveredTimestamp = deliveredTimestamp;
    }

    public void setEmpDelivered(String empDelivered) {
        this.empDelivered = empDelivered;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public PrintShop getPrintshop() {
        return printshop;
    }

}
