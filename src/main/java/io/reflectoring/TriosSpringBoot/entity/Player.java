package io.reflectoring.TriosSpringBoot.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String color;

    @Column(columnDefinition = "TEXT")
    private String hand;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false)
    private String playerType; // "HUMAN" or "AI"

    public boolean isAI() {
        return "AI".equals(playerType);
    }
}