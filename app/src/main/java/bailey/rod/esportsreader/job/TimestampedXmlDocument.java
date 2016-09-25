package bailey.rod.esportsreader.job;

import bailey.rod.esportsreader.util.DateUtils;
import bailey.rod.esportsreader.util.StringUtils;

/**
 * Created by rodbailey on 25/09/2016.
 */
public class TimestampedXmlDocument {

    private final String content;

    private final String etag;

    public TimestampedXmlDocument(String content, String etag) {
        this.content = content;
        this.etag = etag;
    }

    public String getEtag() {
        return etag;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("content=%s, etag=%s", StringUtils.ellipsizeNullSafe(content, 50),etag);
    }
}
