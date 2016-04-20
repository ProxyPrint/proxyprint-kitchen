import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by daniel on 14-04-2016.
 */
public class Test {
    public static void main(String[] args) {
        String dateString = "Mon, 27 Oct 2008 08:33:29 -0700";
        DateFormat df = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss Z");
        Date parsed = null;
        try {
            parsed = df.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("parsed date: " + parsed);

        String pattern = "dd.MM.yyyy hh:mm:ss";
        GregorianCalendar newCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        newCalendar.setTime(parsed);


        // Transformar um gregorian calendar numa string
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        sdf.setCalendar(newCalendar);
        String dateFormatted = sdf.format(newCalendar.getTime());
        // return dateFormatted;

        System.out.println("\n\n"+dateFormatted);
    }
}
