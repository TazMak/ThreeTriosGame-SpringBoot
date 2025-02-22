package io.reflectoring.TriosSpringBoot.repository;

import io.reflectoring.TriosSpringBoot.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findByGameId(Long gameId);
}