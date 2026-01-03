package cl.usach.wazuh.agent.controller;

import cl.usach.wazuh.agent.entity.Agent;
import cl.usach.wazuh.agent.service.AgentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("/")
    public Flux<Agent> getAll() {
        return agentService.getAll();
    }
}
