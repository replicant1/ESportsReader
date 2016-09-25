package bailey.rod.esportsreader.cache;

/**
 * Identifies a party capable of being stored in a client-side cache after it has been created with information
 * retrieved from online.
 * @see SessionCache
 */
public interface ICacheable {

    /**
     * @return Uniquely identifies this object from all others in the same cache. The document at a given URL can
     * only be cached once.
     */
    public String getURL();

    /**
     * @return The timestamp associated with the cacheable object. NOT the time of the things retrieval,
     * but the time at which it was most recently updated on the origin server. As usual, the value is a number of
     * seconds since Jan 1, 1970 GMT.
     */
    public String getEtag();


    public long getLastModified();
}
