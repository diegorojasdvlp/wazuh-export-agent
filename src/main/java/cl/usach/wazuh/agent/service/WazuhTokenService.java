package cl.usach.wazuh.agent.service;

import cl.usach.wazuh.agent.config.WazuhProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WazuhTokenService {

    private final Map<String, Mono<String>> tokenCache = new ConcurrentHashMap<>();

    public Mono<String> getToken(WebClient client) {

        String base = client.toString();

        return tokenCache.computeIfAbsent(base, k ->

                client.post()
                        .uri("/security/user/authenticate?raw=true")
                        .retrieve()
                        .bodyToMono(String.class)
        );
    }
}
