package bailey.rod.esportsreader.util;

/**
 * Created by rodbailey on 23/09/2016.
 */
public class HttpUtils {

    public static String mungeURLforCDN(String rawUrl) {
        return String.format("%s?v=11",rawUrl);
    }
}
