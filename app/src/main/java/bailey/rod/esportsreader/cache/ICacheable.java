package bailey.rod.esportsreader.cache;

/**
 * Identifies a party capable of being stored in a client-side cache after it has been created with information
 * retrieved from online.
 * @see ESportsCache
 */
public interface ICacheable {

    /**
     * @return Uniquely identifies this object from all others in the same cache. The document at a given URL can
     * only be cached once.
     */
    public String getURL();

    /**
     * @return The timestamp associated with the cacheable object. NOT the time we are retrieving it, but the time at
     * which it was most recently updated on the origin server.
     */
    public String getCacheTimestamp();

}
