-- 🧑‍💻 유저 비밀번호 (dnflxla123$)
INSERT INTO users (user_id, user_name, password, points, user_role, user_status, is_use, reg_date, chg_date)
VALUES
    ('testuser1@example.com', '테스트유저1', '$2a$10$9uCz5sk7biUOtDwEoPXQM.PhO4Mr14svGeP4gSAlGOhN3aomltf1S',0, 'USER', 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('bien@example.com', '본인', '$2a$10$9uCz5sk7biUOtDwEoPXQM.PhO4Mr14svGeP4gSAlGOhN3aomltf1S',0, 'USER', 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('admin@example.com', '관리자', '$2a$10$9uCz5sk7biUOtDwEoPXQM.PhO4Mr14svGeP4gSAlGOhN3aomltf1S',0, 'ADMIN' , 'ACTIVE', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 📅 이벤트
INSERT INTO event (title, description, start_date, end_date, point_reward, max_participants, current_participants, event_type, is_deleted, reg_date, chg_date)
VALUES
    ('출석체크 4월', '4월 한달간 매일 출석체크 이벤트입니다.', '2025-04-18 07:24:48', '2025-05-18 07:24:48', 2, null, 0, 'CHECKIN', false, '2025-04-01 07:24:48', '2025-04-01 07:24:48'),
    ('봄맞이 설문조사', '간단한 설문조사에 참여하고 포인트를 받아보세요!', '2025-04-18 07:24:48', '2025-04-28 07:24:48', 200, 500, 0, 'SURVEY', false, '2025-04-01 07:24:48', '2025-04-01 07:24:48');

-- ✅ 이벤트 참여
INSERT INTO event_participation (user_sn, event_id, reg_date, chg_date)
VALUES
    (1, 1, '2025-04-17 07:24:48', '2025-04-17 07:24:48');

-- 💰 포인트 이력
INSERT INTO point_history (user_sn, event_id, points, reason, created_at)
VALUES
    (1, 1, 2, '이벤트 참여: 출석체크 4월', '2025-04-17 07:24:48');
