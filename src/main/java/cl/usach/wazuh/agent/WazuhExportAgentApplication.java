package cl.usach.wazuh.agent;

import cl.usach.wazuh.agent.service.VulnerabilityStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WazuhExportAgentApplication implements CommandLineRunner {

	@Autowired
	private VulnerabilityStatusService service;

	public static void main(String[] args) {
		SpringApplication.run(WazuhExportAgentApplication.class, args);
	}

	@Override
	public void run(String... args) {

		if (args.length > 0 && args[0].equals("pentest")) {
			try {
				Thread.sleep(30000); // Keep alive for 60 seconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} else {
			service.syncVulnerabilityStatus().doOnNext(status -> System.out.println("Saved status: " + status))
					.doOnError(e -> System.out.println("Error: " + e.getMessage()))
					.doOnComplete(() -> System.out.println("Sync completed"))
					.subscribe();
		}
		System.exit(0);
	}
}
