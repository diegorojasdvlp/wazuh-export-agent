package cl.usach.wazuh.agent.repository;

import cl.usach.wazuh.agent.entity.Agent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends ReactiveCrudRepository<Agent, String> {
}
