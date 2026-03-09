package cl.usach.wazuh.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

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
    private Date date;
}
