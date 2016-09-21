package bailey.rod.esportsreader.xml;

import bailey.rod.esportsreader.util.StringUtils;

/**
 * Represents a single news item in an ESportsFeed. Will have been parsed out of an RSS or Atom feed. Some attributes
 * may be null because either a) the feed format doesn't support the attribute, or b) the particular publisher has
 * chosen not to supply a value for the attribute (presumably optional as per feed format definition).
 */
public class ESportsFeedEntry {
    private final String content;

    private final String lastUpdated;

    private final String link;

    private final String published;

    private final String synopsis;

    private final String title;

    /**
     * Constructs an immutable ESportsFeedEntry
     *
     * @param content     May be null
     * @param lastUpdated May be null
     * @param link
     * @param published
     * @param synopsis    May be null
     * @param title
     */
    public ESportsFeedEntry(String content, String lastUpdated, String link, String published, String synopsis,
                            String title) {
        this.content = content;
        this.lastUpdated = lastUpdated;
        this.link = link;
        this.published = published;
        this.synopsis = synopsis;
        this.title = title;
    }

    /**
     * @return May be null
     */
    public String getContent() {
        return content;
    }


    /**
     * @return May be null
     */
    public String getLastUpdated() {
        return lastUpdated;
    }


    public String getLink() {
        return link;
    }


    public String getPublished() {
        return published;
    }

    /**
     * @return May be null
     */
    public String getSynopsis() {
        return synopsis;
    }


    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(String.format("title=\"%s\"", StringUtils.ellipsizeNullSafe(title, 40)));
        buf.append(String.format(",synopsis=\"%s\"", StringUtils.ellipsizeNullSafe(synopsis, 60)));
        buf.append(String.format(",lastUpdated=\"%s\"", lastUpdated));
        buf.append(String.format(",link=\"%s\"", link));
        buf.append(String.format(",published=\"%s\"", published));
        buf.append(String.format(",content=\"%s\"", StringUtils.ellipsizeNullSafe(content, 60)));
        return buf.toString();
    }
}
