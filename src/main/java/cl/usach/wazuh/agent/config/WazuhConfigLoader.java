package cl.usach.wazuh.agent.config;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

@Component
public class WazuhConfigLoader {

        public Map<String, WazuhProperties> load(String path) throws Exception {

            Map<String, WazuhProperties> instances = new HashMap<>();
            WazuhProperties currentConfig = null;
            String currentInstance = null;


            try (BufferedReader reader = new BufferedReader(new FileReader(path))) {



                String line;

                while ((line = reader.readLine()) != null) {

                    line = line.trim();
                    System.out.println("Line: " + line);

                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    if (line.startsWith("[") && line.endsWith("]")) {

                        currentInstance = line.substring(1, line.length() - 1);
                        currentConfig = new WazuhProperties();

                        instances.put(currentInstance, currentConfig);
                        continue;
                    }

                    // key=value
                    String[] parts = line.split("=", 2);
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    switch (key) {

                        case "manager_url":
                            currentConfig.setManagerUrl(value);
                            break;

                        case "manager_user":
                            currentConfig.setManagerUser(value);
                            break;

                        case "manager_password":
                            currentConfig.setManagerPassword(value);
                            break;

                        case "indexer_url":
                            currentConfig.setIndexerUrl(value);
                            break;

                        case "indexer_user":
                            currentConfig.setIndexerUser(value);
                            break;

                        case "indexer_password":
                            currentConfig.setIndexerPassword(value);
                            break;
                    }
                }
            }

            return instances;
        }
}
