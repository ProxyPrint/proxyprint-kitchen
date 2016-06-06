package models;

import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import io.github.proxyprint.kitchen.models.consumer.printrequest.PrintRequest;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.pricetable.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * Created by daniel on 04-06-2016.
 */
public class PrintRequestTests extends TestCase {
    PrintRequest pr;
    PrintingSchema ps;
    PrintShop printshop;
    Consumer consumer = new Consumer("Test", "test", "1234", "test@gmail.com", "0", "0");

    @Before
    public void setUp(){
        // Printshops
        printshop = new PrintShop("Video Norte", "Rua Nova de Santa Cruz", 41.5594, -8.3972, "123444378", "logo_8", 0);
        printshop.setId(8);

        /*-------------------------------------
            PriceTable
        ------------------------------------*/
        // Paper
        PaperItem p1 = new PaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW);

        // Black & White
        RangePaperItem rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.1);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.19);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.18);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 1, 20);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.35);

        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.08);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.15);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.16);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 21, 50);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.31);

        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.06);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.11);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.14);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 51, 100);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.27);

        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.05);
        rp1 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.09);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.12);
        rp1 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.BW, 101, 500);
        printshop.addItemPriceTable(rp1.genKey(),(float) 0.23);

        // Color

        PaperItem p2 = new PaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR);

        RangePaperItem rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.75);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.49);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.40);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 1, 5);
        printshop.addItemPriceTable(rp2.genKey(),(float) 2.79);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.60);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.19);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.10);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 6, 20);
        printshop.addItemPriceTable(rp2.genKey(),(float) 2.19);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.50);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.99);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.95);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 21, 50);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.89);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.40);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.79);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.75);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 51, 100);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.49);

        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.SIMPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.30);
        rp2 = new RangePaperItem(Item.Format.A4, Item.Sides.DUPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.59);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.SIMPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 0.55);
        rp2 = new RangePaperItem(Item.Format.A3, Item.Sides.DUPLEX, Item.Colors.COLOR, 101, 500);
        printshop.addItemPriceTable(rp2.genKey(),(float) 1.19);

        // Bindings
        BindingItem b = new BindingItem(BindingItem.RingType.PLASTIC, 6, 10);
        printshop.addItemPriceTable(b.genKey(), (float) 1.15);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 12, 20);
        printshop.addItemPriceTable(b.genKey(), (float) 1.4);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 22, 28);
        printshop.addItemPriceTable(b.genKey(), (float) 1.75);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 32, 38);
        printshop.addItemPriceTable(b.genKey(), (float) 2.00);
        b = new BindingItem(BindingItem.RingType.PLASTIC, 45, 52);
        printshop.addItemPriceTable(b.genKey(), (float) 2.5);

        b = new BindingItem(BindingItem.RingType.SPIRAL, 6, 10);
        printshop.addItemPriceTable(b.genKey(), (float) 1.55);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 12, 20);
        printshop.addItemPriceTable(b.genKey(), (float) 1.90);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 24, 32);
        printshop.addItemPriceTable(b.genKey(), (float) 2.55);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 36, 40);
        printshop.addItemPriceTable(b.genKey(), (float) 2.95);
        b = new BindingItem(BindingItem.RingType.SPIRAL, 44, 50);
        printshop.addItemPriceTable(b.genKey(), (float) 3.35);
        BindingItem bs = new BindingItem(BindingItem.RingType.STAPLING, 0, 0);
        printshop.addItemPriceTable(bs.genKey(), (float) 0.1);

        // Covers
        CoverItem c = new CoverItem(Item.CoverType.CRISTAL_ACETATE, Item.Format.A4);
        printshop.addItemPriceTable(c.genKey(), (float) 0.5);

        c = new CoverItem(Item.CoverType.PVC_TRANSPARENT, Item.Format.A4);
        printshop.addItemPriceTable(c.genKey(), (float) 0.7);
        c = new CoverItem(Item.CoverType.PVC_TRANSPARENT, Item.Format.A3);
        printshop.addItemPriceTable(c.genKey(), (float) 1.5);

        c = new CoverItem(Item.CoverType.PVC_OPAQUE, Item.Format.A4);
        printshop.addItemPriceTable(c.genKey(), (float) 0.7);
        c = new CoverItem(Item.CoverType.PVC_OPAQUE, Item.Format.A3);
        printshop.addItemPriceTable(c.genKey(), (float) 1.5);

        ps = new PrintingSchema("Cores+A3+Frente+Agrafar", "PAPER,COLOR,A4,SIMPLEX", "BINDING,STAPLING,0,0", "");
        pr = new PrintRequest();
        pr.setArrivalTimestamp(new Date());
        pr.setConsumer(consumer);
        pr.setPaymentType(PrintRequest.PROXY_PAYMENT);

        Document doc = new Document("doc.pdf", 1);
        DocumentSpec docSpc = new DocumentSpec(1,1,ps);
        Set<DocumentSpec> docSpcs = new HashSet<>();
        docSpcs.add(docSpc);
        doc.setSpecs(docSpcs);

        Set<Document> docs = new HashSet<>();
        docs.add(doc);
        pr.setDocuments(docs);

    }

    @Test
    public void testBudget() throws Exception {
        List<PrintShop> pshops = new ArrayList<>();
        pshops.add(printshop);

        Map<Long,String> map = pr.calcBudgetsForPrintShops(pshops);
        assertTrue(map.get((long)8).equals("0.75"));

        assertTrue(printshop.getName().equals("Video Norte"));
    }
}
