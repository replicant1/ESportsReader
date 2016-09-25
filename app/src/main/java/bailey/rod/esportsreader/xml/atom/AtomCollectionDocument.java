package bailey.rod.esportsreader.xml.atom;

import java.util.List;

import bailey.rod.esportsreader.cache.AbstractCacheable;

/**
 * Created by rodbailey on 19/09/2016.
 */
public class AtomCollectionDocument extends AbstractCacheable {

    private final String title;

    private final List<AtomCollectionEntry> entries;

    public AtomCollectionDocument(String title, List<AtomCollectionEntry> entries,  String url, String etag) {
        super(url, etag);
        this.title = title;
        this.entries = entries;
    }

    public List<AtomCollectionEntry> getEntries() {
        return entries;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        StringBuffer  buf = new StringBuffer();
        buf.append("title=" + title);
        buf.append(",url=" + getURL());
        buf.append(",etag=" + getEtag());
        buf.append(",entries=[");
        for (AtomCollectionEntry entry : entries) {
            buf.append("[" + entry.toString() + "],");
        }
        buf.append("]");
        return buf.toString();
    }
}
