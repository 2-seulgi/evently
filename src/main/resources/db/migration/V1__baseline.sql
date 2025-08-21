-- V1: 기존 테이블 그대로(베이스라인). 새로운 제약/인덱스 안 건드림.
-- 이유: Flyway가 "현재 상태"를 기준으로 이후 변경 이력을 안전하게 쌓기 위함.

CREATE TABLE IF NOT EXISTS event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    point_reward INT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    event_type VARCHAR(20) NOT NULL,
    reward_type VARCHAR(20) ,
    max_participants INT,
    current_participants INT NOT NULL,
    reg_date TIMESTAMP NOT NULL,
    chg_date TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id  VARCHAR(100) NOT NULL UNIQUE,
    user_name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    points INT NOT NULL DEFAULT 0,
    user_role VARCHAR(100) NOT NULL,
    user_status VARCHAR(100) NOT NULL,
    is_use BOOLEAN NOT NULL,
    reg_date TIMESTAMP NOT NULL ,
    chg_date TIMESTAMP NOT NULL,
    withdrawal_dt  NULL DEFAULT NULL
    );

CREATE TABLE IF NOT EXISTS event_participation  (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    event_id BIGINT,
    reg_date TIMESTAMP NOT NULL ,
    chg_date TIMESTAMP NOT NULL
);


CREATE TABLE IF NOT EXISTS point_history  (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    event_id BIGINT,
    event_type VARCHAR(20) NOT NULL,
    points INT NOT NULL DEFAULT 0,
    reg_date TIMESTAMP NOT NULL,
    chg_date TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS event_reward_histories  (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    reward_type VARCHAR(20) NOT NULL,      -- 예: FIRST_COME, DRAW, INSTANT
    reward_name VARCHAR(100),              -- 예: "CU 3천원 쿠폰", "에어팟", "500포인트"
    reward_status VARCHAR(30) NOT NULL,    -- 예: WIN, LOSE, PENDING
    reg_date TIMESTAMP NOT NULL,
    chg_date TIMESTAMP NOT NULL
    );

CREATE TABLE IF NOT EXISTS  event_reward_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    reward_name VARCHAR(100) NOT NULL,       -- 예: "에어팟", "CU 3천원 쿠폰"
    quantity INT NOT NULL,                   -- 보상 개수
    probability FLOAT,                       -- 확률 (optional)
    reward_type VARCHAR(20) NOT NULL         -- 예: POINT, ITEM
);
