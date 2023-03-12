package util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ConfigReader {

    private static final String CONFIG_PROPERTIES_PATH = "src/test/java/resources/config.properties";
    public static String getPropValue(String key) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(CONFIG_PROPERTIES_PATH));
        } catch (IOException e) {
            log.info("Error while reading config properties file: " + CONFIG_PROPERTIES_PATH);
        }
        return properties.getProperty(key);
    }
}
