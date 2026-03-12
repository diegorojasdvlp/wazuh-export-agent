package cl.usach.wazuh.agent.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


import java.util.HashMap;
import java.util.Map;

@Component
public class WazuhIndexerConfig {
    private final Map<String, WebClient> clients = new HashMap<>();

    public WazuhIndexerConfig(Map<String, WazuhProperties> instances) throws Exception {
        for (Map.Entry<String, WazuhProperties> entry : instances.entrySet()) {
            String instanceName = entry.getKey();
            WazuhProperties props = entry.getValue();

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
                    .baseUrl(props.getIndexerUrl())
                    .defaultHeaders(h -> h.setBasicAuth(
                            props.getIndexerUser(),
                            props.getIndexerPassword()
                    ))
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();
            clients.put(instanceName, client);
        }
    }
    public Map<String, WebClient> getClients() {
        return clients;
    }
    public WebClient getClient(String host) {
        return clients.get(host);
    }
}
