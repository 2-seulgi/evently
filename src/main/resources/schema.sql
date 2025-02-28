CREATE TABLE IF NOT EXISTS event (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        title VARCHAR(100) NOT NULL,
        description TEXT,
        start_date TIMESTAMP NOT NULL,
        end_date TIMESTAMP NOT NULL,
        point_reward INT NOT NULL,
        is_deleted BOOLEAN NOT NULL,
        event_type VARCHAR(20) NOT NULL,
        reg_date TIMESTAMP NOT NULL,
        chg_date TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL UNIQUE,
    user_name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    points INT NOT NULL DEFAULT 0,
    reg_date TIMESTAMP NOT NULL ,
    chg_date TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS event_participation  (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_sn BIGINT,
    event_id BIGINT,
    reg_date TIMESTAMP NOT NULL ,
    chg_date TIMESTAMP NOT NULL
);