package cl.usach.wazuh.agent.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class WazuhClientFactory {
    private final Map<String, WebClient> clients = new HashMap<>();

    public WazuhClientFactory(Map<String, WazuhProperties> instances) throws Exception {
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

            ;
            String userpass = props.getManagerUser() + ":" + props.getManagerPassword();
            String credentials = Base64.getEncoder().encodeToString(
                    userpass.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + credentials);
            WebClient client = WebClient.builder()
                    .defaultHeaders(h -> h.setBasicAuth(
                            String.valueOf(headers)
                    ))
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();

            String auth = String.valueOf(client.post()
                    .uri("/security/user/authenticate?raw=true")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block());
            String Autorization = "Authorization: Bearer " + auth;
            System.out.println("Auth: " + auth);
            WebClient authclient = WebClient.builder()
                    .exchangeStrategies(strategies)
                    .baseUrl(props.getManagerUrl())
                    .defaultHeader(Autorization)
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();


            clients.put(instanceName, authclient);
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
