package cl.usach.wazuh.agent.config;

import com.jcraft.jsch.JSch;

import com.jcraft.jsch.Session;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SshTunnelManager {
    private final Map<String, Session> sessions = new HashMap<>();

    public int openTunnel(String tunnelKey, WazuhProperties props, int remotePort) throws Exception {
        return this.openTunnel(0, tunnelKey, props, remotePort);
    }

    public int openTunnel(int lport, String tunnelKey, WazuhProperties props, int remotePort) throws Exception {
        JSch jsch = new JSch();

        Session session = jsch.getSession(props.getSshUser(), props.getSshHost(), props.getSshPort());
        session.setPassword(props.getSshPassword());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10_000);

        int localPort = session.setPortForwardingL(lport, "localhost", remotePort);
        sessions.put(tunnelKey, session);

        System.out.println("SSH tunnel opened [" + tunnelKey + "] localhost:" + localPort + " -> " + props.getSshHost()
                + ":" + remotePort + " (via SSH " + props.getSshHost() + ":" + props.getSshPort() + ")");
        return localPort;
    }

    public void closeAll() {
        sessions.values().forEach(Session::disconnect);
        sessions.clear();
    }

}
