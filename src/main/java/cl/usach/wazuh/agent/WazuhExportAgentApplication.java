package cl.usach.wazuh.agent;

import cl.usach.wazuh.agent.service.VulnerabilityStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class WazuhExportAgentApplication {

	@Autowired
	private VulnerabilityStatusService service;

	public static void main(String[] args) {
		SpringApplication.run(WazuhExportAgentApplication.class, args);
	}


	public void run(String... args) {

		/*
		if (args.length > 0 && args[0].equals("pentest")) {
			try {
				Thread.sleep(30000); // Keep alive for 60 seconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		} else {

		 */
			AtomicInteger counter = new AtomicInteger();
			service.syncVulnerabilityStatus().doOnNext(v -> counter.incrementAndGet())
					.doOnComplete(() ->
							System.out.println("Inserted { "+counter.get()+" } rows" )
					)
					.doOnComplete(() -> System.out.println("Sync completed"))
					.blockLast();
	//	}

		System.exit(0);


	}
}
