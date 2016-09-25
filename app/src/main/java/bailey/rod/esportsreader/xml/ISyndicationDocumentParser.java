package bailey.rod.esportsreader.xml;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import bailey.rod.esportsreader.cache.ICacheable;

/**
 * Created by rodbailey on 25/09/2016.
 */
public interface ISyndicationDocumentParser {

    public ICacheable parse(InputStream inputStream, String url, String etag, long lastModififed) throws
            XmlPullParserException,
            IOException;
}
