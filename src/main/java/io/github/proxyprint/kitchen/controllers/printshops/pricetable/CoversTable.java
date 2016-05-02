package io.github.proxyprint.kitchen.controllers.printshops.pricetable;

import io.github.proxyprint.kitchen.models.printshops.items.CoverItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by daniel on 02-05-2016.
 */
public class CoversTable {
    public static String DEFAULT = "-";

    public class CoverTableItem {
        public String format;
        public String coverType;

        public CoverTableItem() {
            this.format = DEFAULT;
            this.coverType = DEFAULT;
        }

        public CoverTableItem(String format, String coverType) {
            this.format = format;
            this.coverType = coverType;
        }

        public String getFormat() { return format; }

        public void setFormat(String format) { this.format = format; }

        public String getCoverType() { return coverType; }

        public void setCoverType(String coverType) {this.coverType = coverType; }

    }

    private Map<String,Set<CoverTableItem>> items;

    public CoversTable() {
        this.items = new HashMap<>();
    }

    public void addCoverItem(CoverItem ci) {
        CoverTableItem cti = new CoverTableItem(ci.getFormat().toString(),ci.getCoverType().toString());
        if(this.items.containsKey(cti.getCoverType())) {
            this.items.get(cti.getCoverType()).add(cti);
        }
        else {
            Set<CoverTableItem> newSet = new HashSet<>();
            newSet.add(cti);
            this.items.put(cti.getCoverType(),newSet);
        }
    }
}
