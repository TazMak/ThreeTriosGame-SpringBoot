package io.reflectoring.TriosSpringBoot.registry;

import io.reflectoring.TriosSpringBoot.model.PlayerColor;
import org.springframework.stereotype.Component;
import io.reflectoring.TriosSpringBoot.controller.ThreeTriosController;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameControllerRegistry {
    private final Map<PlayerColor, ThreeTriosController> controllers = new ConcurrentHashMap<>();

    public void register(PlayerColor color, ThreeTriosController controller) {
        if (controllers.containsKey(color)) {
            throw new IllegalStateException("Controller already registered for " + color);
        }
        controllers.put(color, controller);
    }

    public ThreeTriosController getController(PlayerColor color) {
        return controllers.get(color);
    }

    public void clearRegistry() {
        controllers.clear();
    }
}