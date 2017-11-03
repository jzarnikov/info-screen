package pi.home.screen.config;

import java.io.FileInputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pi.home.screen.config.data.Config;

@RestController
public class ConfigController {

    private final String configJson;
    private final Config config;

    public ConfigController() {
        this.configJson = loadConfig();
        this.config = parseConfig();
    }

    private String loadConfig() {
        try {
            return IOUtils.toString(new FileInputStream("config.json"));
        } catch (IOException e) {
            try {
                return IOUtils.toString(ConfigController.class.getResourceAsStream("/config.json"));
            } catch (IOException e2) {
                return "{}";
            }
        }
    }

    private Config parseConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            return mapper.readValue(configJson, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Config();
        }
    }

    @RequestMapping("/config")
    public String config() {
        return configJson;
    }

    public Config getConfig() {
        return config;
    }

}