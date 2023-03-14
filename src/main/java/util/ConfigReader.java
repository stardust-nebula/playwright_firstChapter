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

    private static final String CONFIG_PROPERTIES_PATH = "src/test/resources/config.properties";
    public static String getPropValue(String key) {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PROPERTIES_PATH)){
            properties.load(fileInputStream);
        } catch (IOException e) {
            log.error("Error while reading config properties file: " + CONFIG_PROPERTIES_PATH);
        }
        return properties.getProperty(key);
    }
}
