package cl.usach.wazuh.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "agents")
public class Agent {
    @Id
    private Long id;
    private String name;
    private String ip;
    private boolean active;
    private LocalDateTime date;

    public Agent(String name, String ip, boolean active, LocalDateTime date) {
        this.name = name;
        this.ip = ip;
        this.active = active;
        this.date = date;
    }
}
