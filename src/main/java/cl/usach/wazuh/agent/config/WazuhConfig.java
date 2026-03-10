package cl.usach.wazuh.agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class WazuhConfig {
    @Bean
    public Map<String, WazuhProperties> wazuhInstances(
            WazuhConfigLoader loader
    ) throws Exception {

        return loader.load("config.txt");
    }
}
