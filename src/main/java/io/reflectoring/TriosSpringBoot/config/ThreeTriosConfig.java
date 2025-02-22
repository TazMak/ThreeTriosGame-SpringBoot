package io.reflectoring.TriosSpringBoot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.reflectoring.TriosSpringBoot.service.GameService;
import io.reflectoring.TriosSpringBoot.registry.GameControllerRegistry;
import io.reflectoring.TriosSpringBoot.repository.GameRepository;
import io.reflectoring.TriosSpringBoot.repository.PlayerRepository;

/**
 * Configuration class for Three Trios game
 */
@Configuration
public class ThreeTriosConfig {

    @Bean
    public GameControllerRegistry gameControllerRegistry() {
        return new GameControllerRegistry();
    }

    @Bean
    public GameService gameService(
            GameRepository gameRepository,
            PlayerRepository playerRepository) {
        return new GameService(gameRepository, playerRepository);
    }

    // ResourceLoader is already provided by Spring Boot
    // No need to create a custom bean for it
}