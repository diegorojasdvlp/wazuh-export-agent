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

    private String sshHost;
    private int sshPort = 22;
    private String sshUser;
    private String sshPassword;
}
