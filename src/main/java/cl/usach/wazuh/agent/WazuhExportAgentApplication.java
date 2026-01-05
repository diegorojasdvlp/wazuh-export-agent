package cl.usach.wazuh.agent;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class WazuhExportAgentApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WazuhExportAgentApplication.class, args);
	}

	@Override
	public void run(String... args) {
		Scanner scanner = new Scanner(System.in);
		boolean running = true;

		System.out.println("\n--- Wazuh Export Agent ---");

		while (running) {
			System.out.println("\nSelecciona una opción:");
			System.out.println("1. Recuperar Datos");
			System.out.println("2. Ver ultimo registro");
			System.out.println("3. Ver todos los registros");
			System.out.println("4. Exit");
			System.out.print("Escribe el numero de tu opcion: ");

			String input = scanner.nextLine();

			switch (input) {
				case "1":
					System.out.println("Placeholder 1");
					break;
				case "2":
					System.out.println("Placeholder 2");
					break;
				case "3":
					System.out.println("Placeholder 3");
					break;
				case "4":
					System.out.println("Saliendo...");
					running = false;
					System.exit(0);
					break;
				default:
					System.out.println("Opcion invalida, intenta nuevamente");
			}
		}
	}
}
