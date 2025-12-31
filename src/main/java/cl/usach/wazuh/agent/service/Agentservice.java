package cl.usach.wazuh.agent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
public class Agentservice {
    private final WebClient webClient;
    private final String username;
    private final String password;

    public Agentservice(
            WebClient wazuhIndexerWebClient,
            @Value("${WAZUH_INDEXER_USERNAME:admin}") String username,
            @Value("${WAZUH_INDEXER_PASSWORD:admin}") String password) {
        this.webClient = wazuhIndexerWebClient;
        this.username = username;
        this.password = password;
    }
    public Mono<String> get(String endpoint) {
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        return webClient
                .get()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(s ->
                    log.info("GET " + endpoint + "Exitoso"))
                .doOnError(error ->
                        log.error("Error en GET {}: {}", endpoint, error.getMessage())
                );
    }
}
