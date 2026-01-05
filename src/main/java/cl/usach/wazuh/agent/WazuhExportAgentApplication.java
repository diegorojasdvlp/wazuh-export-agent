package cl.usach.wazuh.agent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class WazuhExportAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(WazuhExportAgentApplication.class, args);
	}
}
