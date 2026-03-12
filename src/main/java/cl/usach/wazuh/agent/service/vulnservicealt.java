package cl.usach.wazuh.agent.service;

import cl.usach.wazuh.agent.entity.Agent;
import cl.usach.wazuh.agent.entity.Vulnerability;
import cl.usach.wazuh.agent.entity.VulnerabilityStatus;
import cl.usach.wazuh.agent.repository.AgentVulnerability;
import cl.usach.wazuh.agent.repository.VulnerabilityStatusRepository;
import cl.usach.wazuh.agent.repository.Vulnerabilityrepository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class vulnservicealt {
    private final Agentservice agentService;
    private final Vulnerabilityservice vulnerabilityService;
    private final AgentVulnerability agentRepository;
    private final Vulnerabilityrepository vulnerabilityRepository;
    private final VulnerabilityStatusRepository vulnerabilityStatusRepository;

    public vulnservicealt(Agentservice agentService, Vulnerabilityservice vulnerabilityService, AgentVulnerability agentRepository, Vulnerabilityrepository vulnerabilityRepository, VulnerabilityStatusRepository vulnerabilityStatusRepository) {
        this.agentService = agentService;
        this.vulnerabilityService = vulnerabilityService;
        this.agentRepository = agentRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.vulnerabilityStatusRepository = vulnerabilityStatusRepository;
    }
    @Transactional
    public Flux<VulnerabilityStatus> syncVulnerabilityStatus() {
        Flux<Agent> agentFlux = agentService.findAgents()
                .flatMap(agentDto ->
                        agentRepository.findByName(agentDto.getName())
                                .switchIfEmpty(
                                        agentRepository.save(new Agent(
                                                agentDto.getName(),
                                                agentDto.getIp(),
                                                agentDto.isActive(),
                                                LocalDateTime.now()
                                        ))
                                )
                );
        return agentFlux.collectMap(Agent::getName).flatMapMany(agentByName ->
                vulnerabilityService.findCriticalVulnerabilities()

                        .flatMap(vulnDto -> {

                            Agent matchedAgent = agentByName.get(vulnDto.getAgentName());

                            if (matchedAgent == null) {
                                return Mono.empty();
                            }

                            Vulnerability vuln = new Vulnerability();
                            vuln.setName(vulnDto.getName());
                            vuln.setSeverity(vulnDto.getSeverity());

                            return vulnerabilityRepository.save(vuln)

                                    .flatMap(savedVuln ->
                                            vulnerabilityStatusRepository
                                                    .findByAgentAndVulnerability(
                                                            matchedAgent.getId(),
                                                            savedVuln.getId()
                                                    )
                                                    .switchIfEmpty(
                                                            vulnerabilityStatusRepository.save(
                                                                    new VulnerabilityStatus(
                                                                            matchedAgent.getId(),
                                                                            savedVuln.getId(),
                                                                            LocalDateTime.now()
                                                                    )
                                                            )
                                                    )
                                    );
                        })
        );
    }
}
