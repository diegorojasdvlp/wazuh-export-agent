package cl.usach.wazuh.agent.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

public class Agent {
    @Id
    private String id;

    @JsonProperty("agent.name")
    private String agentName;

    public Agent(String id, String agentName) {
        this.id = id;
        this.agentName = agentName;
    }

    public Agent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }
}
