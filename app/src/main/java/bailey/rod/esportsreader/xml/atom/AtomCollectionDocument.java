package bailey.rod.esportsreader.xml.atom;

import java.util.List;

/**
 * Created by rodbailey on 19/09/2016.
 */
public class AtomCollectionDocument {

    private final String title;

    private final List<AtomCollectionEntry> entries;

    public AtomCollectionDocument(String title, List<AtomCollectionEntry> entries) {
        this.title = title;
        this.entries = entries;
    }

    public List<AtomCollectionEntry> getEntries() {
        return entries;
    }

    public String getTitle() {
        return title;
    }
}
