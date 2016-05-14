package io.github.proxyprint.kitchen.models.consumer.printrequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by daniel on 09-05-2016.
 */
@Entity
@Table(name = "documents")
public class Document {
    public static String FILES_PATH = ""; // Where do we temporarily store files

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Column(name = "total_pages", nullable = false)
    private int totalPages;
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    private List<DocumentSpec> specs;

    public Document() {
        specs = new ArrayList<>();
    }

    public Document(String fileName, int totalPages) {
        this.fileName = fileName;
        this.totalPages = totalPages;
        specs = new ArrayList<>();
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    public int getTotalPages() { return totalPages; }

    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public List<DocumentSpec> getSpecs() { return specs; }

    public void setSpecs(List<DocumentSpec> specs) { this.specs = specs; }

    public void addSpecification(DocumentSpec ds) { this.specs.add(ds); }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", totalPages=" + totalPages +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document document = (Document) o;

        if (getId() != document.getId()) return false;
        if (getTotalPages() != document.getTotalPages()) return false;
        if (getFileName() != null ? !getFileName().equals(document.getFileName()) : document.getFileName() != null)
            return false;
        return getSpecs() != null ? getSpecs().equals(document.getSpecs()) : document.getSpecs() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getFileName() != null ? getFileName().hashCode() : 0);
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
