package cl.usach.wazuh.agent.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class WazuhClientFactory {
    private final Map<String, WebClient> clients = new HashMap<>();

    public WazuhClientFactory(Map<String, WazuhProperties> instances) throws SSLException {
        for (Map.Entry<String, WazuhProperties> entry : instances.entrySet()) {
            String instanceName = entry.getKey();
            WazuhProperties props = entry.getValue();
            SslContext sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
            HttpClient httpClient = HttpClient.create()
                    .secure(t -> t.sslContext(sslContext));

            WebClient client = WebClient.builder()
                    .baseUrl(props.getManagerUrl())
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
    public WebClient getClient(String server) {
        return clients.get(server);
    }

    public Collection<WebClient> getAllClients() {
        return clients.values();
    }
}
