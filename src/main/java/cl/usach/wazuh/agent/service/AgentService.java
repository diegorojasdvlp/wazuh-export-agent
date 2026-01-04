package cl.usach.wazuh.agent.service;

import cl.usach.wazuh.agent.entity.Agent;
import cl.usach.wazuh.agent.entity.Vulnerability;
import cl.usach.wazuh.agent.repository.AgentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import static reactor.netty.http.HttpConnectionLiveness.log;

@Service
public class AgentService {
    @Qualifier("wazuhServerClient")
    private final WebClient webClient;

    private final @Nonnull AgentRepository repository;

    public AgentService(
            WebClient wazuhIndexerWebClient, @Nonnull AgentRepository repository) {
        this.webClient = wazuhIndexerWebClient;
        this.repository = repository;
    }

    @Transactional
    public Mono<String> get(String endpoint) {

        return webClient
                .get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(s ->
                        log.info("GET " + endpoint + "Exitoso"))
                .doOnError(error ->
                        log.error("Error en GET {}: {}", endpoint, error.getMessage())
                );
    }

    /*
    si post no funciona -> .method(HttpMethod.GET)
     */
    public Flux<Agent> getAll() {
        return webClient
                .post()
                .uri("/agents?pretty=true&sort=-ip,name")
//                .bodyValue(Map.of(
//                        "query", Map.of("term", Map.of("vulnerability.severity", "Critical")),
//                        "_source", List.of("host.os.name", "agent.name",
//                                "vulnerability.id", "vulnerability.description", "vulnerability.severity")
//                ))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMapMany(r -> Flux.fromIterable(r.path("hits").path("hits")))
                .map(hit -> {
                    var s = hit.path("_source");
                    var a = new Agent();
//                    v.setAgentName(s.path("agent.name").asText());
//                    v.setOsName(s.path("os.name").asText());
//                    v.setSeverity(s.path("vulnerability.severity").asText());
//                    v.setVulnerabilityId(s.path("vulnerability.id").asText());
//                    v.setDescription(s.path("vulnerability.description").asText());
                    return a;
                })
                ;
    }

}
