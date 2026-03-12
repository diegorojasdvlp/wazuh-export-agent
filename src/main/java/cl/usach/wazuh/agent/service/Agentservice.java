package cl.usach.wazuh.agent.service;

import cl.usach.wazuh.agent.config.SshTunnelManager;
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
    private final SshTunnelManager tunnelManager;

    public Agentservice(
            WazuhClientFactory clientFactory,
            WazuhTokenService tokenService,
            Map<String, WazuhProperties> instances, SshTunnelManager tunnelManager
    ) {
        this.clientFactory = clientFactory;
        this.tokenService = tokenService;
        this.instances = instances;
        this.tunnelManager = tunnelManager;
    }


    public Flux<Agent> findAgents()  {

        int limit = 1000;
        return Flux.fromIterable(instances.entrySet())
                .flatMap(entry -> {
                    WazuhProperties props = entry.getValue();
                    String instanceName = entry.getKey();
                    try {
                        tunnelManager.openTunnel(55000,instanceName+"-manager", props, extractPort(props.getManagerUrl()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    WebClient client = clientFactory.getClient(instanceName);

                    Flux<Agent> agentes = tokenService.getToken(client)
                            .flatMapMany(token ->
                                    fetchAgentsPage(client, token, limit, 0)
                            ).doFinally(signalType ->  tunnelManager.closeAll());
                    return agentes;
                });
    }
    private int extractPort(String url) {
        try {
            String[] parts = url.split(":");
            return Integer.parseInt(parts[parts.length - 1].replaceAll("/.*", ""));
        } catch (Exception e) {
            return 55000; // Wazuh manager default
        }
    }


    private Flux<Agent> fetchAgentsPage(WebClient client, String token, int limit, int offset) {

        return client.get()
                .uri(uriBuilder ->
                        uriBuilder.path("/agents")
                                .queryParam("select", "id,name,ip,status")
                                .queryParam("limit", limit)
                                .queryParam("offset", offset)
                                .build()
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)

                .flatMapMany(root -> {

                    var items = root.path("data").path("affected_items");

                    if (items.isEmpty()) {
                        return Flux.empty();
                    }

                    Flux<Agent> currentBatch =
                            Flux.fromIterable(items)
                                    .map(node -> {

                                        Agent agent = new Agent();

                                        agent.setName(node.path("name").asText(""));
                                        agent.setIp(node.path("ip").asText(""));
                                        agent.setActive(
                                                node.path("status")
                                                        .asText("not_connected")
                                                        .equals("active")
                                        );

                                        return agent;
                                    });

                    return currentBatch.concatWith(
                            fetchAgentsPage(client, token, limit, offset + limit)
                    );
                });
    }


}
