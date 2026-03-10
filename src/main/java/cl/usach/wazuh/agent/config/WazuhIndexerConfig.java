package cl.usach.wazuh.agent.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Component
public class WazuhIndexerConfig {
    private final Map<String, WebClient> clients = new HashMap<>();

    public WazuhIndexerConfig(Map<String, WazuhProperties> instances, SshTunnelManager tunnelManager) throws Exception {
        for (Map.Entry<String, WazuhProperties> entry : instances.entrySet()) {
            String instanceName = entry.getKey();
            WazuhProperties props = entry.getValue();

            int localPort = tunnelManager.openTunnel(instanceName + "-indexer", props, extractPort(props.getIndexerUrl()));
            String tunnelUrl = "https://localhost:" + localPort;

            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.create()
                    .secure(t -> t.sslContext(sslContext));
            WebClient client = WebClient.builder()
                    .baseUrl(tunnelUrl)
                    .defaultHeaders(h -> h.setBasicAuth(
                            props.getIndexerUser(),
                            props.getIndexerPassword()
                    ))
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();
            clients.put(instanceName, client);
        }
    }
    private int extractPort(String url) {
        try {
            String[] parts = url.split(":");
            return Integer.parseInt(parts[parts.length - 1].replaceAll("/.*", ""));
        } catch (Exception e) {
            return 9200; // Wazuh indexer default
        }
    }
    public Map<String, WebClient> getClients() {
        return clients;
    }
    public WebClient getClient(String host) {
        return clients.get(host);
    }
}
