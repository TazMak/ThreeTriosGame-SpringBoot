package io.reflectoring.TriosSpringBoot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.reflectoring.TriosSpringBoot.service.GameService;
import lombok.extern.slf4j.Slf4j;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.flywaydb.core.Flyway;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@Slf4j
public class TriosSpringBootApplication {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private GameService gameService;

	public static void main(String[] args) {
		SpringApplication.run(TriosSpringBootApplication.class, args);
	}

	@PostConstruct
	public void initDatabase() {
		// Initialize Flyway
		Flyway flyway = Flyway.configure()
				.dataSource(dataSource)
				.locations("classpath:db/migration")
				.load();

		// Clean the database (be careful with this in production!)
		// flyway.clean();

		// Run the migrations
		flyway.migrate();
	}

	@EventListener(ApplicationReadyEvent.class)
	public void initializeGameResources() {
		try {
			// Copy game configuration files to a temporary location
			copyGameConfigFiles();

			log.info("Game configuration files initialized successfully");
			log.info("Server is ready to accept game requests at http://localhost:8080/api/games");

			// Print available endpoints
			printAvailableEndpoints();

		} catch (IOException e) {
			log.error("Failed to initialize game resources", e);
			throw new RuntimeException("Game initialization failed", e);
		}
	}

	private void copyGameConfigFiles() throws IOException {
		// Define the configuration files to copy
		String[] configFiles = {
				"3x3BoardNoHoles.txt",
				"4x4BoardNoHoles.txt",
				"5x5BoardConnectedCardCells.txt",
				"6x6Board.txt",
				"CompleteCardSet.txt",
				"TenCardSetFor3x3Board.txt"
		};

		// Create temp directory if it doesn't exist
		Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "trios-game");
		Files.createDirectories(tempDir);

		// Copy each config file
		for (String fileName : configFiles) {
			Resource resource = new ClassPathResource("game-configs/" + fileName);
			Path destinationPath = tempDir.resolve(fileName);

			try {
				Files.copy(resource.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
				log.info("Copied configuration file: {}", fileName);
			} catch (IOException e) {
				log.error("Failed to copy configuration file: {}", fileName, e);
				throw e;
			}
		}
	}

	private void printAvailableEndpoints() {
		log.info("\nAvailable Game Endpoints:");
		log.info("-------------------------");
		log.info("POST   /api/games              - Create a new game");
		log.info("GET    /api/games              - List all active games");
		log.info("GET    /api/games/{id}         - Get game state");
		log.info("POST   /api/games/{id}/start   - Start a game");
		log.info("POST   /api/games/{id}/move    - Make a move");
		log.info("-------------------------\n");
	}
}