-- Create games table
CREATE TABLE IF NOT EXISTS games (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     game_state VARCHAR(50) NOT NULL,
                                     current_player VARCHAR(20) NOT NULL,
                                     board_config TEXT NOT NULL,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Create players table
CREATE TABLE IF NOT EXISTS players (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       game_id BIGINT NOT NULL,
                                       color VARCHAR(20) NOT NULL,
                                       hand TEXT NOT NULL,
                                       player_type VARCHAR(20) NOT NULL,
                                       FOREIGN KEY (game_id) REFERENCES games(id),
                                       CONSTRAINT unique_player_color_per_game UNIQUE (game_id, color)
);

-- Create cards table
CREATE TABLE IF NOT EXISTS cards (
                                     id VARCHAR(50) PRIMARY KEY,
                                     name VARCHAR(100) NOT NULL,
                                     north_value INT NOT NULL,
                                     east_value INT NOT NULL,
                                     south_value INT NOT NULL,
                                     west_value INT NOT NULL
);

-- Create moves table
CREATE TABLE IF NOT EXISTS moves (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     game_id BIGINT NOT NULL,
                                     player_id BIGINT NOT NULL,
                                     card_id VARCHAR(50) NOT NULL,
                                     position_row INT NOT NULL,
                                     position_col INT NOT NULL,
                                     move_number INT NOT NULL,
                                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (game_id) REFERENCES games(id),
                                     FOREIGN KEY (player_id) REFERENCES players(id),
                                     FOREIGN KEY (card_id) REFERENCES cards(id)
);

-- Create indexes
CREATE INDEX idx_game_state ON games(game_state);
CREATE INDEX idx_current_player ON games(current_player);
CREATE INDEX idx_is_active ON games(is_active);
CREATE INDEX idx_game_player ON players(game_id, color);
CREATE INDEX idx_moves_game ON moves(game_id);