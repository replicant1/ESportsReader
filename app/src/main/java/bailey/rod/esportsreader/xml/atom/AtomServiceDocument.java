package bailey.rod.esportsreader.xml.atom;

import java.util.List;

/**
 * Represents an Atom "service document" - but only those parts of it that this app is interested in. In particular,
 * we see it as having two properties:
 * <li>Title - The title that should appear over a list of the collections named in this document</li>
 * <li>collections - A list of collections in the order in which they appear in the document.</li>
 * <p>
 * Each AtomServiceCollection corresponds to an eSport.
 * </p>
 * See http://www.atomenabled.org/developers/protocol/#service
 */
public class AtomServiceDocument {

    private final String title;

    private final List<AtomServiceCollection> collections;

    public AtomServiceDocument(String title, List<AtomServiceCollection> collections) {
        this.title = title;
        this.collections = collections;
    }

    public List<AtomServiceCollection> getCollections() {
        return collections;
    }

    public String getTitle() {
        return title;
    }
}
