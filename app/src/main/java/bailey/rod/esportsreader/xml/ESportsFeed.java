package bailey.rod.esportsreader.xml;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents an RSS or Atom news feed
 */
public class ESportsFeed {

    private final String title;

    private final List<ESportsFeedEntry> entries = new LinkedList<>();

    public ESportsFeed(String title, List<ESportsFeedEntry> entries) {
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
