package ch.bzz;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Cannot find configuration file 'config.properties' in the classpath.");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading configuration file 'config.properties'", ex);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
