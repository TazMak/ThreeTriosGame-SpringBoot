# Three Trios Game Implementation

## Overview

This repository contains a web-based implementation of the Three Trios card game, a strategic two-player grid-based card game inspired by Triple Triad. Players take turns placing cards on a grid and battling adjacent cards, with the goal of controlling the most cards by the end of the game.

### Core Game Concepts
- Two players (Red and Blue) compete on a customizable grid
- Each card has directional attack values (North, South, East, West)
- Cards battle adjacent cards and can flip ownership through combat
- Winner is determined by total card ownership when grid is full

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Docker and Docker Compose
- Maven 3.6 or higher
- MySQL 8.0 or higher (if running without Docker)

### Setting Up the Development Environment

1. Clone the repository:
```bash
git clone [repository-url]
cd three-trios-game
```

2. Build the project:
```bash
./mvnw clean install
```

3. Create necessary directories:
```bash
mkdir -p src/main/resources/db/migration
mkdir -p src/main/resources/game-configs
```

4. Copy game configuration files to resources:
```bash
cp game-configs/* src/main/resources/game-configs/
```

### Running with Docker

1. Start the application using Docker Compose:
```bash
docker-compose up --build
```

This will:
- Start a MySQL database container
- Create the necessary database and tables
- Start the Spring Boot application
- Make the API available at http://localhost:8080

2. To stop the application:
```bash
docker-compose down
```

### Running Locally (Without Docker)

1. Start MySQL server and create database:
```sql
CREATE DATABASE trios_game;
```

2. Update application.properties with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/trios_game
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Run the application:
```bash
./mvnw spring-boot:run
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── io/reflectoring/TriosSpringBoot/
│   │       ├── config/
│   │       │   ├── SecurityConfig.java
│   │       │   └── ThreeTriosConfig.java
│   │       ├── controller/
│   │       │   ├── GameController.java
│   │       │   └── Features.java
│   │       ├── model/
│   │       │   ├── basic/
│   │       │   ├── board/
│   │       │   ├── battle/
│   │       │   └── player/
│   │       ├── service/
│   │       │   └── GameService.java
│   │       └── TriosSpringBootApplication.java
│   └── resources/
│       ├── db/migration/
│       │   └── V1__init.sql
│       ├── game-configs/
│       │   ├── 3x3BoardNoHoles.txt
│       │   └── CompleteCardSet.txt
│       └── application.properties
└── test/
    └── java/
        └── io/reflectoring/TriosSpringBoot/
```

## API Endpoints

### Game Management
- `POST /api/games` - Create a new game
- `GET /api/games` - List all active games
- `GET /api/games/{id}` - Get game state
- `POST /api/games/{id}/start` - Start a game
- `POST /api/games/{id}/move` - Make a move

### API Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Docs: http://localhost:8080/v3/api-docs

## Playing the Game

1. Create a new game:
```bash
curl -X POST http://localhost:8080/api/games \
  -H "Content-Type: application/json" \
  -d '{
    "gridConfig": "3x3BoardNoHoles.txt",
    "cardConfig": "TenCardSetFor3x3Board.txt",
    "redPlayerType": "HUMAN",
    "bluePlayerType": "AI"
  }'
```

2. Start the game:
```bash
curl -X POST http://localhost:8080/api/games/{gameId}/start
```

3. Make a move:
```bash
curl -X POST http://localhost:8080/api/games/{gameId}/move \
  -H "Content-Type: application/json" \
  -d '{
    "playerColor": "RED",
    "cardId": "Dragon",
    "position": {
      "row": 0,
      "col": 0
    }
  }'
```

## Database Schema

### Games Table
- id (Primary Key)
- game_state
- current_player
- board_config
- created_at
- is_active

### Players Table
- id (Primary Key)
- game_id (Foreign Key)
- color
- hand
- player_type

### Moves Table
- id (Primary Key)
- game_id (Foreign Key)
- player_id (Foreign Key)
- card_id
- position_row
- position_col
- move_number
- created_at

## Components

### Model Components
- `ThreeTriosModel`: Core game logic and state management
- `Board`: Grid representation and cell management
- `BattleHandler`: Handles card combat resolution
- `Player`: Interface for player actions and state

### View Components
- RESTful API endpoints for game interaction
- JSON responses for game state representation

### Controller Components
- `GameController`: Handles HTTP requests
- `Features`: Manages game actions and move validation

### Strategy Implementation
- `MaxFlipsStrategy`: Maximizes cards flipped in current turn
- `CornerStrategy`: Prioritizes corner positions

## Testing

To run tests:
```bash
./mvnw test
```

## Troubleshooting

1. Database Connection Issues:
    - Verify MySQL is running: `docker ps`
    - Check logs: `docker-compose logs mysql`

2. Application Startup Issues:
    - Check application logs: `docker-compose logs app`
    - Verify resource files exist in correct locations

3. API Issues:
    - Check Swagger UI for correct endpoint usage
    - Verify request/response formats

## Future Extensions

- Enhanced AI strategies
- WebSocket support for real-time updates
- User authentication and profiles
- Game history and statistics
- Tournament mode

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Create a pull request
