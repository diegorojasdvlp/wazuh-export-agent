package cl.usach.wazuh.agent.service;

import cl.usach.wazuh.agent.config.WazuhClientFactory;
import cl.usach.wazuh.agent.config.WazuhProperties;
import cl.usach.wazuh.agent.entity.Agent;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Map;


@Service
public class Agentservice {
    @Qualifier("wazuhServerClient")
    private final WazuhClientFactory clientFactory;
    private final WazuhTokenService tokenService;
    private final Map<String, WazuhProperties> instances;

    public Agentservice(
            WazuhClientFactory clientFactory,
            WazuhTokenService tokenService,
            Map<String, WazuhProperties> instances
    ) {
        this.clientFactory = clientFactory;
        this.tokenService = tokenService;
        this.instances = instances;
    }


    public Flux<Agent> findAgents() {

        return Flux.fromIterable(instances.entrySet())

                .flatMap(entry -> {

                    String instanceName = entry.getKey();
                    WazuhProperties props = entry.getValue();

                    WebClient client = clientFactory.getClient(instanceName);

                    return tokenService.getToken(client)

                            .flatMapMany(token ->
                                    client.get()
                                            .uri("/agents?select=id,name,ip,status")
                                            .header("Authorization", "Bearer " + token)
                                            .retrieve()
                                            .bodyToMono(JsonNode.class)
                            )

                            .flatMap(root ->
                                    Flux.fromIterable(
                                            root.path("data").path("affected_items")
                                    )
                            )

                            .map(node -> {

                                Agent agent = new Agent();

                                agent.setName(node.path("name").asText(""));
                                agent.setIp(node.path("ip").asText(""));
                                agent.setActive(node.path("status").asText("not_connected").equals("active"));
                                return agent;
                            });

                });
    }

}
