package bailey.rod.esportsreader.cache;

/**
 * Abstract base class for all cacheable objects.
 */
public abstract class AbstractCacheable implements ICacheable{

    private final String url;

    private final String etag;

    public AbstractCacheable(String url, String etag) {
        this.url = url;
        this.etag = etag;
    }

    @Override
    public String getURL() {
        return url;
    }

    public String getEtag() {
        return etag;
    }
}
