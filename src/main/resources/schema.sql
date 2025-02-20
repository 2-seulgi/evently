CREATE TABLE IF NOT EXISTS event (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(100) NOT NULL,
        description TEXT,
        start_date TIMESTAMP NOT NULL,
        end_date TIMESTAMP NOT NULL,
        point_reward INT NOT NULL,
        is_deleted BOOLEAN NOT NULL
    );
