-- V3: point_history를 '원장'으로 확장
-- - tx_type: 거래 구분(EARN: 적립, SPEND: 차감, ADJUST: 운영 보정)
-- - source_type: 거래 출처(EVENT / STORE / ADMIN)
-- - source_id: 출처의 식별자(event.id 또는 store_order.id)
-- - idempotency_key: 멱등 키(중복 요청 방지) - UNIQUE 인덱스 부여
-- - balance_after: 거래 후 잔액(감사/추적 편의)

ALTER TABLE point_history
    ADD COLUMN tx_type VARCHAR(10) NOT NULL DEFAULT 'EARN',
    ADD COLUMN source_type VARCHAR(20) NOT NULL DEFAULT 'EVENT',
    ADD COLUMN source_id BIGINT NULL,
    ADD COLUMN idempotency_key VARCHAR(64) NULL,
    ADD COLUMN balance_after INT NULL;

-- 멱등 키는 요청당 1회만 허용
CREATE UNIQUE INDEX IF NOT EXISTS uq_point_history_idempotency_key
    ON point_history (idempotency_key);

-- 조회 성능 인덱스(사용자 타임라인 / 출처 조회)
CREATE INDEX IF NOT EXISTS idx_point_history_user_time
    ON point_history (user_id, reg_date);

CREATE INDEX IF NOT EXISTS idx_point_history_source
    ON point_history (user_id, source_type, source_id);
