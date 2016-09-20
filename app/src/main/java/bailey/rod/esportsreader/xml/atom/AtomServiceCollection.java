package bailey.rod.esportsreader.xml.atom;

/**
 * Represents a "collection" in so far as it is specified in an Atom Service Document. Corresponds to an eSport. The
 * "title" is the name of the eSport and the "collectionDocumentHref" is the location of an Atom Collection Document
 * that specifies the available feeds pertaining to that eSport.
 *
 * See http://www.atomenabled.org/developers/protocol/#collection
 */
public final class AtomServiceCollection {

    private final String title;

    private final String collectionDocumentHref;

    /**
     * Constructs an immutable AtomServiceCollection.
     *
     * @param title Title of the collection, which corresponds to the name of an eSport
     * @param collectionDocumentHref URL to the Atom Collection Document
     */
    public AtomServiceCollection(String title, String collectionDocumentHref) {
        this.title = title;
        this.collectionDocumentHref = collectionDocumentHref;
    }

    public String getCollectionDocumentHref() {
        return collectionDocumentHref;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return String.format("title=\"%s\", collectionDocumentHref=\"%s\"", title, collectionDocumentHref);
    }
}
