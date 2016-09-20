package bailey.rod.esportsreader.xml.atom;

import java.util.Date;

import bailey.rod.esportsreader.util.DateUtils;

/**
 * Represents an "entry" as it is specified in an Atom Collection Document. Corresponds to a
 * blog i.e. a source of a single news feed
 */
public class AtomCollectionEntry {

    private final String title;

    // TODO: Change this back to Date when I know more about parsing ISO 8601 and variations thereon
    private final String updated;

    private final String summary;

    private final String linkViaHref;

    public AtomCollectionEntry(String title, String updated, String summary, String linkViaHref) {
        this.title = title;
        this.updated = updated;
        this.summary = summary;
        this.linkViaHref = linkViaHref;
    }

    public String getLinkViaHref() {
        return linkViaHref;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    public String getUpdated() {
        return updated;
    }

    @Override
    public String toString() {
        return String.format("title=\"%s\", updated=%s, summary=\"%s\", linkViaHref=\"%s\"",
                             title, updated, summary, linkViaHref) ;
    }
}
