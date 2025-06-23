-- 🧑‍💻 유저 비밀번호 (dnflxla123$)
INSERT INTO users (login_id, user_name, password, points, user_role, user_status, is_use, reg_date, chg_date)
VALUES
    ('testuser1@example.com', '테스트유저1', '$2a$10$9uCz5sk7biUOtDwEoPXQM.PhO4Mr14svGeP4gSAlGOhN3aomltf1S',0, 'USER', 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('bien@example.com', '본인', '$2a$10$9uCz5sk7biUOtDwEoPXQM.PhO4Mr14svGeP4gSAlGOhN3aomltf1S',0, 'USER', 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('admin@example.com', '관리자', '$2a$10$9uCz5sk7biUOtDwEoPXQM.PhO4Mr14svGeP4gSAlGOhN3aomltf1S',0, 'ADMIN' , 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 📅 이벤트
INSERT INTO event (title, description, start_date, end_date, point_reward, max_participants, current_participants, event_type, reward_type,  reg_date, chg_date)
VALUES
    ('출석체크 4월', '4월 한달간 매일 출석체크 이벤트입니다.', '2025-04-18 07:24:48', '2025-05-18 07:24:48', 2, null, 0, 'CHECKIN', null, false, '2025-04-01 07:24:48', '2025-04-01 07:24:48'),
    ('봄맞이 설문조사', '간단한 설문조사에 참여하고 포인트를 받아보세요!', '2025-04-18 07:24:48', '2025-04-28 07:24:48', 200, 500, 0, 'SURVEY', null, false, '2025-04-01 07:24:48', '2025-04-01 07:24:48'),
    ('에어팟 추첨 이벤트', '이벤트 참여를 누르면 에어팟 추첨 기회가 주어집니다.', '2025-06-18 07:24:48', '2025-08-28 07:24:48', 10, null, 0, 'GIVEAWAY', DRAW, '2025-06-01 07:24:48', '2025-06-01 07:24:48');

-- ✅ 이벤트 참여
INSERT INTO event_participation (user_id, event_id, reg_date, chg_date)
VALUES
    (1, 1, NOW(), NOW());

-- 💰 포인트 이력
INSERT INTO point_history (user_id, event_id, event_type, points, reg_dat, chg_date)
VALUES
    (1, 1, CHECKIN, 2, NOW(),NOW()),
    (1, 3, GIVEAWAY, 10, NOW(), NOW());
