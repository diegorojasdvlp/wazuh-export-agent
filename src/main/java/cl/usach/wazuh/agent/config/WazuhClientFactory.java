package cl.usach.wazuh.agent.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class WazuhClientFactory {
    private final Map<String, WebClient> clients = new HashMap<>();

    public WazuhClientFactory(Map<String, WazuhProperties> instances, SshTunnelManager tunnelManager) throws Exception {
        for (Map.Entry<String, WazuhProperties> entry : instances.entrySet()) {
            String instanceName = entry.getKey();
            WazuhProperties props = entry.getValue();
            int localPort = tunnelManager.openTunnel(30001,instanceName + "-manager", props, extractPort(props.getManagerUrl()));
            String tunnelUrl = "https://localhost:" + localPort;

            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            HttpClient httpClient = HttpClient.create()
                    .secure(t -> t.sslContext(sslContext));

            ExchangeStrategies strategies = ExchangeStrategies.builder()
                    .codecs(configurer ->
                            configurer.defaultCodecs()
                                    .maxInMemorySize(50 * 1024 * 1024)
                    )
                    .build();

            WebClient client = WebClient.builder()
                    .exchangeStrategies(strategies)
                    .baseUrl(tunnelUrl)
                    .defaultHeaders(headers ->
                            headers.setBasicAuth(
                                    props.getManagerUser(),
                                    props.getManagerPassword()
                            )
                    )
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();

            clients.put(instanceName, client);
        }
    }

    private int extractPort(String url) {
        // e.g. https://localhost:55000 → 55000
        try {
            String[] parts = url.split(":");
            return Integer.parseInt(parts[parts.length - 1].replaceAll("/.*", ""));
        } catch (Exception e) {
            return 55000; // Wazuh default
        }
    }
    public WebClient getClient(String server) { return clients.get(server); }
    public Collection<WebClient> getAllClients() { return clients.values(); }
}
