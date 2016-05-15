package io.github.proxyprint.kitchen.models.consumer.printrequest;

import io.github.proxyprint.kitchen.utils.gson.Exclude;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 09-05-2016.
 */
@Entity
@Table(name = "documents")
public class Document {
    public static String FILES_PATH = "./documents/";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "file_name", nullable = false)
    private String name;
    @Column(name = "total_pages", nullable = false)
    private int totalPages;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    private List<DocumentSpec> specs;
    @ManyToOne
    @Exclude
    private PrintRequest printRequest;

    public Document() {
        specs = new ArrayList<>();
    }

    public Document(String name, int totalPages) {
        this.name = name;
        this.totalPages = totalPages;
        specs = new ArrayList<>();
    }

    public Document(String name, int totalPages, PrintRequest printRequest) {
        this.name = name;
        this.totalPages = totalPages;
        this.specs = new ArrayList<>();
        this.printRequest = printRequest;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getTotalPages() { return totalPages; }

    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public List<DocumentSpec> getSpecs() { return specs; }

    public void setSpecs(List<DocumentSpec> specs) { this.specs = specs; }

    public void addSpecification(DocumentSpec ds) { this.specs.add(ds); }

    public PrintRequest getPrintRequest() { return printRequest; }

    public void setPrintRequest(PrintRequest printRequest) { this.printRequest = printRequest; }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", totalPages=" + totalPages +
                '}';
    }

    public File getFile(){
        return new File(Document.FILES_PATH+this.id+".pdf");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document document = (Document) o;

        if (getId() != document.getId()) return false;
        if (getTotalPages() != document.getTotalPages()) return false;
        if (getName() != null ? !getName().equals(document.getName()) : document.getName() != null)
            return false;
        return getSpecs() != null ? getSpecs().equals(document.getSpecs()) : document.getSpecs() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        return result;
    }

    public String getPresentationString() {
        StringBuilder sb = new StringBuilder();

        for(DocumentSpec docSpecs : this.specs) {
            sb.append(docSpecs.getPresentationString()).append("\n");
        }

        return sb.toString();
    }

}
