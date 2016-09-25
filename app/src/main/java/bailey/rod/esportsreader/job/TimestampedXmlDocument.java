package bailey.rod.esportsreader.job;

import bailey.rod.esportsreader.util.DateUtils;
import bailey.rod.esportsreader.util.StringUtils;

/**
 * Created by rodbailey on 25/09/2016.
 */
public class TimestampedXmlDocument {

    private final String content;

    private final String etag;

    private final long lastModified;

    public TimestampedXmlDocument(String content, String etag, long lastModified) {
        this.content = content;
        this.etag = etag;
        this.lastModified = lastModified;
    }

    public String getContent() {
        return content;
    }

    public String getEtag() {
        return etag;
    }

    public long getLastModified() {
        return lastModified;
    }

    @Override
    public String toString() {
        return String.format("content=%s, etag=%s, lastModified=%s", StringUtils.ellipsizeNullSafe(content, 50), etag,
                             DateUtils.timeSinceEpochToString(lastModified));
    }
}
