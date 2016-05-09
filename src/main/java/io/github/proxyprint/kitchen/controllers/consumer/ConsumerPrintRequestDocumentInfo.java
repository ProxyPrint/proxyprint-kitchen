package io.github.proxyprint.kitchen.controllers.consumer;

import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;

import java.util.List;

/**
 * Created by daniel on 09-05-2016.
 */
public class ConsumerPrintRequestDocumentInfo {
    private int pages;
    private List<DocumentSpec> specs;

    public ConsumerPrintRequestDocumentInfo(){}

    public ConsumerPrintRequestDocumentInfo(int pages, List<DocumentSpec> specs) {
        this.pages = pages;
        this.specs = specs;
    }

    public int getPages() { return pages; }

    public void setPages(int pages) { this.pages = pages; }

    public List<DocumentSpec> getSpecs() { return specs; }

    public void setSpecs(List<DocumentSpec> specs) { this.specs = specs; }

    @Override
    public String toString() {
        return "ConsumerPrintRequestDocumentInfo{" +
                "pages=" + pages +
                ", specs=" + specs +
                '}';
    }
}
