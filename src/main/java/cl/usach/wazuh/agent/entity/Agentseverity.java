package cl.usach.wazuh.agent.entity;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "agent_severity")
public class Agentseverity {

    @Id
    private Long id;

    @Nonnull
    private Integer quantity;

    @Nonnull
    private String severity;

    @Nonnull
    @Column("agent_name")
    private String agentName;

    @Nonnull
    @Column("agent_ip")
    private String agentIp;

    @Nonnull
    private Timestamp date;

}
