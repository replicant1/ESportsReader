package bailey.rod.esportsreader.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * A cache for storing anything that implements ICacheable. In practice, we use this to store news feed info to avoid
 * needless retrievals of it over the network.
 */
public class ESportsCache {

    private static final ESportsCache singleton = new ESportsCache();

    private Map<String, ICacheable> cache = new HashMap<String, ICacheable>();

    public static synchronized ESportsCache getInstance() {
        return singleton;
    }

    public boolean contains(String cacheId) {
        return cache.containsKey(cacheId);
    }

    public ICacheable get(String cacheId) {
        return cache.get(cacheId);
    }

    private boolean isNewer(ICacheable thisOne, ICacheable otherOne) {
        String thisTimestamp = thisOne.getCacheTimestamp();
        String otherTimestamp = otherOne.getCacheTimestamp();

        // TODO: Replace this with a proper date comparison.
        return (thisTimestamp.compareTo(otherTimestamp) > 0);
    }

    /**
     * @param cacheable Object to be placed unconditionally in the cache. If there is already an object with the same
     *                  cache id in the cache, it will be overwritten with this one.
     */
    public void put(ICacheable cacheable) {
        cache.put(cacheable.getCacheId(), cacheable);
    }

    /**
     * @param cacheable Object to placed in the cache, provided there is not already a more recent version of it in
     *                  the cache.
     * @return True if a) The given object was not already in the cache, OR b) The given object was already in the
     * cache but was replaced with the given one because it was newer.
     */
    public boolean putIfNewer(ICacheable cacheable) {
        boolean isGivenObjectInCacheAtExit = false;

        if (contains(cacheable.getCacheId())) {
            ICacheable alreadyCachedObj = get(cacheable.getCacheId());

            if (isNewer(alreadyCachedObj, cacheable)) {
                // Do nothing, the object already in the cache is newer than the given object
            } else {
                // Replace the existing cache object with the given object
                put(cacheable);

            }
        } else {
            cache.put(cacheable.getCacheId(), cacheable);
            isGivenObjectInCacheAtExit = true;
        }

        return isGivenObjectInCacheAtExit;
    }
}
