package io.github.proxyprint.kitchen.models.consumer.printrequest;

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.utils.gson.Exclude;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by MGonc on 28/04/16.
 */
@Entity
@Table(name = "print_requests")
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
    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @Exclude private PrintShop printshop;
    @ManyToOne
    private Consumer consumer;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "print_request_id")
    private List<Document> documents;

    public PrintRequest() {
        this.documents = new ArrayList<>();
    }

    public PrintRequest(float cost, Date arrivalTimestamp, Consumer consumer, Status status) {
        this.cost = cost;
        this.arrivalTimestamp = arrivalTimestamp;
        this.consumer = consumer;
        this.status = status;
        this.documents = new ArrayList<>();
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

    public Consumer getConsumer() {
        return consumer;
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

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public PrintShop getPrintshop() {
        return printshop;
    }

    public List<Document> getDocuments() { return documents; }

    public void setDocuments(List<Document> documents) { this.documents = documents; }

    public void addDocument(Document doc) { this.documents.add(doc); }
}
