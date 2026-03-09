package cl.usach.wazuh.agent.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WazuhProperties {
    private String managerUrl;
    private String managerUser;
    private String managerPassword;

    private String indexerUrl;
    private String indexerUser;
    private String indexerPassword;
}
