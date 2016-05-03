package io.github.proxyprint.kitchen.models.printshops.items;

/**
 * Created by daniel on 28-04-2016.
 */
public abstract class Item {

    public static String PAPER = "PAPER";
    public static String COVER = "COVER";
    public static String BINDING = "BINDING";

    public static String checkItemType(String item) {
        String[] chunks = item.split(",");
        if (chunks[0] != null && (chunks[0].equals(PAPER) || chunks[0].equals(COVER) || chunks[0].equals(BINDING))) {
            return chunks[0];
        }
        return "";
    }

    // Paper
    public static enum Format {
        A4, A3
    }

    public static enum Sides {
        SIMPLEX, DUPLEX
    }

    public static enum Colors {
        BW, GREY_TONES, COLOR
    }

    // Cover
    public static enum CoverType {
        CRISTAL_ACETATE, PVC_TRANSPARENT, PVC_OPAQUE
    }

    // Binding
    public static enum RingType {
        PLASTIC, SPIRAL, WIRE, STEELMAT, STAPLING
    }

    public Item() {
    }

    /**
     * Generates a key which identifies (and describes) a certain item.
     *
     * @return the key which identifies the item by the
     * concatenation of its characteristics.
     */
    public abstract String genKey();

    public static String getPresentationStringForCover(CoverType ct) {
        if(ct.equals(CoverType.CRISTAL_ACETATE)) {
            return "Acetato Cristal";
        } else if(ct.equals(CoverType.PVC_TRANSPARENT)) {
            return "PVC Transparente";
        } else if(ct.equals(CoverType.PVC_OPAQUE)) {
            return "PVC Opaco";
        }
        return "";
    }

    public static String getPresentationStringForRings(RingType rt) {
        if(rt.equals(RingType.PLASTIC)) {
            return "Argolas de Plástico";
        } else if(rt.equals(RingType.SPIRAL)) {
            return "Argolas Espiral";
        } else if(rt.equals(RingType.WIRE)) {
            return "Argolas de Arame";
        }
        return "";
    }

    public static RingType getRingTypeForPresentationString(String rt) {
        if(rt.equals("Argolas de Plástico")) {
            return RingType.PLASTIC;
        } else if(rt.equals("Argolas Espiral")) {
            return RingType.SPIRAL;
        } else if(rt.equals("Argolas de Arame")) {
            return RingType.WIRE;
        }
        return RingType.PLASTIC;
    }

}
