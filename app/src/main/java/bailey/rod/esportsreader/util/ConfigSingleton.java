package bailey.rod.esportsreader.util;

import android.content.Context;
import android.util.Log;

import org.valid4j.Assertive;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * Singleton that provides access to values of run-time configuration properties as specified in the file
 * assets/config.properties.
 */
public class ConfigSingleton {

    private static final String TAG = ConfigSingleton.class.getSimpleName();

    private static final ConfigSingleton singleton = new ConfigSingleton();

    private boolean initialized = false;

    public static ConfigSingleton getInstance() {
        return singleton;
    }

    private final Properties configProperties = new Properties();


    private ConfigSingleton() {
        // Empty
    }

    public ConfigSingleton init(Context context) {
        InputStream istream;

        try {
            istream = context.getAssets().open("config.properties");
            configProperties.load(istream);
            initialized = true;
        }
        catch (IOException x) {
            Log.e(TAG, "Failed to load config.properties file from assets directory");
        }

        return this;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public String localAtomServiceDocument() {
        return getStringProperty("LocalAtomServiceDocument");
    }

    public String localAtomCollectionDocument() {
        return getStringProperty("LocalAtomCollectionDocument");
    }

    private boolean getBoolProperty(String propertyName) {
        Assertive.require(initialized);
        Assertive.require(propertyName != null);
        return Boolean.parseBoolean(configProperties.getProperty(propertyName));
    }

    private String getStringProperty(String propertyName) {
        Assertive.require(initialized);
        Assertive.require(propertyName != null);
        return configProperties.getProperty(propertyName);
    }

    private List<String> getStringListProperty(String propertyName) {
        String csvString = getStringProperty(propertyName);
        StringTokenizer tokenizer = new StringTokenizer(csvString, ",");
        List<String> result = new LinkedList<String>();
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken().trim());
        }
        return result;
    }


}
