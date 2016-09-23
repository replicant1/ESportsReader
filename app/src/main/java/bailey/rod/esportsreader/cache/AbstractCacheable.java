package bailey.rod.esportsreader.cache;

/**
 * Abstract base class for all cacheable objects.
 */
public abstract class AbstractCacheable implements ICacheable{

    private final String url;

    private final String timestamp;

    public AbstractCacheable(String url, String timestamp) {
        this.url = url;
        this.timestamp = timestamp;
    }

    @Override
    public String getURL() {
        return url;
    }

    public String getCacheTimestamp() {
        return timestamp;
    }
}
