-- v2: 스토어(포인트 결제) 도메인 추가
-- - store_item : 포인트로 구매 할 수 있는 상품
-- - store_order : 주문(멱등 요청 키 request_key 포함)
-- - store_item_sales_daily: 인기 집계
-- - outbox_event:  외부 전송 대기 / 재시도 큐(payload는 TEXT로 저장: JSON 문자열)


CREATE TABLE IF NOT EXISTS store_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    cost_points INT NOT NULL CHECK (cost_points >= 0),
    stock INT NOT NULL CHECK (stock >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    limit_per_user INT NULL,                  -- 사용자별 구매 한도
    reg_date TIMESTAMP NOT NULL,
    chg_date TIMESTAMP NOT NULL
)

CREATE TABLE IF NOT EXISTS store_order(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    cost_points_at_order INT NOT NULL,
    status VARCHAR(20) NOT NULL,              -- 예: PAID, CANCELED
    request_key VARCHAR(64) NULL UNIQUE,      -- ★ 멱등 요청 키(같은 요청 중복 방지)
    reg_date TIMESTAMP NOT NULL,
    chg_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_store_order_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_store_order_item FOREIGN KEY (item_id) REFERENCES store_item(id)
)

-- 최근 7일 TOP3 조회 최적화를 위한 보조 집계
CREATE TABLE IF NOT EXISTS store_item_sales_daily(
    item_id BIGINT NOT NULL,
    day DATE NOT NULL,
    order_count INT NOT NULL,
    PRIMARY KEY (item_id, day),
    CONSTRAINT fk_sales_item FOREIGN KEY (item_id) REFERENCES store_item(id)
)

-- 주문 외부 전송을 안전하게 처리하기 위한 Outbox 테이블
CREATE TABLE IF NOT EXISTS outbox_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,   -- 예: 'StoreOrder'
    aggregate_id BIGINT NOT NULL,          -- store_order.id
    type VARCHAR(50) NOT NULL,             -- 예: 'ORDER_PAID'
    payload TEXT NOT NULL,                 -- JSON 문자열(호환성 위해 TEXT 사용)
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, SENT, FAILED
    reg_date TIMESTAMP NOT NULL,
    chg_date TIMESTAMP NOT NULL
    );

-- 조회/스케줄 성능 인덱스
CREATE INDEX IF NOT EXISTS idx_store_order_recent ON store_order (reg_date, item_id);
CREATE INDEX IF NOT EXISTS idx_outbox_pending ON outbox_event (status, reg_date);

