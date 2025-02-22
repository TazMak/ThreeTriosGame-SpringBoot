package io.reflectoring.TriosSpringBoot.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            // First check if cards are already loaded
            int count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM cards", Integer.class);

            if (count == 0) {
                // Load cards from CompleteCardSet.txt
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new ClassPathResource("game-configs/CompleteCardSet.txt")
                                        .getInputStream()))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(" ");
                        if (parts.length == 5) {
                            String name = parts[0];
                            int north = parseValue(parts[1]);
                            int east = parseValue(parts[2]);
                            int south = parseValue(parts[3]);
                            int west = parseValue(parts[4]);

                            jdbcTemplate.update(
                                    "INSERT INTO cards (id, name, north_value, east_value, south_value, west_value) " +
                                            "VALUES (?, ?, ?, ?, ?, ?)",
                                    name, name, north, east, south, west
                            );
                        }
                    }
                }
            }
        };
    }

    private int parseValue(String value) {
        return value.equals("A") ? 10 : Integer.parseInt(value);
    }
}