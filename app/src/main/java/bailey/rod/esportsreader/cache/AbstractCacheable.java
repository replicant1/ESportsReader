package bailey.rod.esportsreader.cache;

import java.util.UUID;

/**
 * Abstract base class for all cacheable objects.
 */
public abstract class AbstractCacheable implements ICacheable{

    private final String cacheId;

    private final String timestamp;

    public AbstractCacheable(String cacheId, String timestamp) {
        this.cacheId = cacheId;
        this.timestamp = timestamp;
    }

    @Override
    public String getCacheId() {
        return cacheId;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
