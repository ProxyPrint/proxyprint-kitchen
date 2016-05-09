package io.github.proxyprint.kitchen.controllers.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 09-05-2016.
 */
public class ConsumerPrintRequest {
    private Map<String,List<ConsumerPrintRequestDocumentInfo>> printRequest;
    private List<Long> printshops;

    public ConsumerPrintRequest() {
        printRequest = new HashMap<>();
        printshops = new ArrayList<>();
    }

    public ConsumerPrintRequest(Map<String, List<ConsumerPrintRequestDocumentInfo>> printRequest, List<Long> printshops) {
        this.printRequest = printRequest;
        this.printshops = printshops;
    }

    public Map<String, List<ConsumerPrintRequestDocumentInfo>> getPrintRequest() { return printRequest; }

    public void setPrintRequest(Map<String, List<ConsumerPrintRequestDocumentInfo>> printRequest) {
        this.printRequest = printRequest;
    }

    public List<Long> getPrintshops() { return printshops; }

    public void setPrintshops(List<Long> printshops) { this.printshops = printshops; }
}
