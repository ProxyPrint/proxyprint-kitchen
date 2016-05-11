package io.github.proxyprint.kitchen.models.consumer.printrequest;

import javax.persistence.*;

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
    @Column(name = "file_name", nullable = true)
    private String fileName;

    public Document() {}

    public Document(String fileName) {
        this.fileName = fileName;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getFileName() { return fileName; }

    public void setFileName(String fileName) { this.fileName = fileName; }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;

        Document document = (Document) o;

        if (getId() != document.getId()) return false;
        return getFileName() != null ? getFileName().equals(document.getFileName()) : document.getFileName() == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + (getFileName() != null ? getFileName().hashCode() : 0);
        return result;
    }
}