package bailey.rod.esportsreader.cache;

/**
 * Abstract base class for all cacheable objects.
 */
public abstract class AbstractCacheable implements ICacheable{

    private final String url;

    private final String etag;

    private final long lastModified;

    public AbstractCacheable(String url, String etag, long lastModified) {
        this.url = url;
        this.etag = etag;
        this.lastModified = lastModified;
    }

    @Override
    public String getURL() {
        return url;
    }

    public String getEtag() {
        return etag;
    }

    @Override
    public long getLastModified() {
        return lastModified;
    }
}
