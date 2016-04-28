package io.github.proxyprint.kitchen.models.printshops;
import javax.persistence.*;

/**
 * Created by MGonc on 28/04/16.
 */
@Entity
@Table(name = "printrequests")
public class PrintRequest {

    public enum Status {
        PENDING, IN_PROGRESS, FINISHED, LIFTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, name = "cost")
    private float cost;
    @Column(nullable = false, name = "arrival")
    private String arrivalTimestamp;
    @Column(nullable = true, name = "finished")
    private String finishedTimestamp;
    @Column(nullable = true, name = "delivered")
    private String deliveredTimestamp;
    @Column(nullable = true, name = "empattended")
    private String empAttended;
    @Column(nullable = true, name = "empdelivered")
    private String empDelivered;
    @Column(nullable = false, name = "customer")
    private String customerID;
    @Column(nullable = false, name = "status")
    private Status status;

    public PrintRequest(float cost, String arrivalTimestamp, String customerID, Status status) {
        this.cost = cost;
        this.arrivalTimestamp = arrivalTimestamp;
        this.customerID = customerID;
        this.status = status;
    }

    public long getId() { return id; }
    public float getCost() { return cost; }
    public String getArrivalTimestamp() { return arrivalTimestamp; }
    public String getDeliveredTimestamp() { return deliveredTimestamp; }
    public String getEmpAttended() { return empAttended; }
    public String getFinishedTimestamp() { return finishedTimestamp; }
    public String getEmpDelivered() { return empDelivered; }
    public String getCustomerID() { return customerID; }
    public Status getStatus() { return status; }

    public void setCost(float cost) { this.cost = cost; }
    public void setArrivalTimestamp(String arrivalTimestamp) { this.arrivalTimestamp = arrivalTimestamp; }
    public void setFinishedTimestamp(String finishedTimestamp) { this.finishedTimestamp = finishedTimestamp; }
    public void setEmpAttended(String empAttended) { this.empAttended = empAttended; }
    public void setDeliveredTimestamp(String deliveredTimestamp) { this.deliveredTimestamp = deliveredTimestamp; }
    public void setEmpDelivered(String empDelivered) { this.empDelivered = empDelivered; }
    public void setStatus(Status status) { this.status = status; }
    public void setCustomerID(String customerID) { this.customerID = customerID; }

}
