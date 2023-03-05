package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    private static Properties properties = new Properties();

    public String getPropValue(String key)  {
        try {
            properties.load(new FileInputStream("src/test/java/resources/config.properties"));
        } catch (IOException e) {
            e.getMessage();
        }
        return properties.getProperty(key);
    }
}
