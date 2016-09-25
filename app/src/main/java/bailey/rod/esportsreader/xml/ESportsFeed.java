package bailey.rod.esportsreader.xml;

import java.util.LinkedList;
import java.util.List;

import bailey.rod.esportsreader.cache.AbstractCacheable;

/**
 * Represents an RSS or Atom news feed
 */
public class ESportsFeed extends AbstractCacheable {

    private final String title;

    private final List<ESportsFeedEntry> entries = new LinkedList<>();

    public ESportsFeed(String title, List<ESportsFeedEntry> entries, String url, String etag) {
        super(url, etag);
        this.title = title;
        this.entries.addAll(entries);
    }

    public List<ESportsFeedEntry> getEntries() {
        return entries;
    }

    /**
     * @return Displayable name of this news feed
     */
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return String.format("title=\"%s\"", title);
    }
}
