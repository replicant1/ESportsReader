package bailey.rod.esportsreader.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * A cache for storing anything that implements ICacheable. In practice, we use this to store news feed info to avoid
 * needless retrievals of it over the network.
 */
public class SessionCache {

    private static final SessionCache singleton = new SessionCache();

    private Map<String, ICacheable> cache = new HashMap<String, ICacheable>();

    public static synchronized SessionCache getInstance() {
        return singleton;
    }

    public void clear() {
        cache.clear();
    }

    public boolean contains(String cacheId) {
        return cache.containsKey(cacheId);
    }

    /**
     * @return True if the cache contains a copy of a thing with the given cacheId AND that copy has an etag that
     * is different from the one provided.
     */
    public boolean containsDifferentVersion(String cacheId, String etag) {
        boolean result = true; // Having no version at all is considered a "different" version

        if (cache.containsKey(cacheId)) {
            ICacheable inCacheCopy = cache.get(cacheId);

            if (cacheId.equals(inCacheCopy.getURL())) {
                result = false;
            }
            else {
                result = true;
            }
        }

        return result;
    }

    public String dump() {
        StringBuffer buf = new StringBuffer();
        buf.append(String.format("SessionCache has %d records. ", cache.keySet().size()));

        for (Map.Entry<String, ICacheable> cacheEntry : cache.entrySet()) {
            buf.append(String.format("[%s -> %s]", cacheEntry.getKey(), cacheEntry.getValue()));
        }

        return buf.toString();
    }

    public ICacheable get(String cacheId) {
        return cache.get(cacheId);
    }

    /**
     * Determines which of two ICacheable's is the most recent.
     *
     * @return True if thisOne is more newer (more recent, later, after) the otherOne
     */
    private boolean isDifferentVersion(ICacheable thisOne, ICacheable otherOne) {
        String thisTimestamp = thisOne.getEtag();
        String otherTimestamp = otherOne.getEtag();
        return (!thisTimestamp.equals(otherTimestamp));
    }

    /**
     * @param cacheable Object to placed in the cache, provided there is not already a more recent version of it in
     *                  the cache.
     * @return True if a) The given object was not already in the cache, OR b) The given object was already in the
     * cache but was replaced with the given one because it was newer.
     */
    public boolean put(ICacheable cacheable) {
        boolean isGivenObjectInCacheAtExit = false;

        if (contains(cacheable.getURL())) {
            ICacheable alreadyCachedObj = get(cacheable.getURL());

            if (isDifferentVersion(alreadyCachedObj, cacheable)) {
                // Replace the existing cache object with the given object.
                // Having different etags probably means the given one is newer (assumption!)
                cache.put(cacheable.getURL(), cacheable);
            } else {
                // Do nothing, the object already in the cache is newer than the given object
            }
        } else {
            // Add to cache for the first time
            cache.put(cacheable.getURL(), cacheable);
            isGivenObjectInCacheAtExit = true;
        }

        return isGivenObjectInCacheAtExit;
    }
}
