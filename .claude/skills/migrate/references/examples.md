# DDL/DML Operation Examples

## CREATE TABLE

```sql
-- V1.23.0__Create_user_history_table.sql

CREATE TABLE user_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL COMMENT 'LOGIN, LOGOUT, UPDATE_PROFILE, etc.',
    action_details JSON COMMENT 'Action metadata',
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6) NULL,

    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='User action history for audit trail';

-- Foreign Key
ALTER TABLE user_history
ADD CONSTRAINT fk_user_history_user_id
FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE;
```

## ADD COLUMN

```sql
-- V1.24.0__Add_last_login_at_to_user.sql

ALTER TABLE user
ADD COLUMN last_login_at DATETIME(6) NULL COMMENT 'Last login timestamp'
AFTER updated_at;

ADD INDEX idx_last_login_at (last_login_at);
```

## DROP COLUMN

```sql
-- V1.25.0__Remove_legacy_field_from_user.sql

ALTER TABLE user
DROP COLUMN legacy_field;
```

## MODIFY COLUMN

```sql
-- V1.26.0__Extend_user_email_length.sql

ALTER TABLE user
MODIFY COLUMN email VARCHAR(255) NOT NULL COMMENT 'User email address';
```

## ADD INDEX

```sql
-- V1.27.0__Add_index_to_exam_problems.sql

ALTER TABLE exam_problems
ADD INDEX idx_exam_id_problem_order (exam_id, exam_problem_order);
```

## ADD FOREIGN KEY

```sql
-- V1.28.0__Add_foreign_key_to_exam_problems.sql

ALTER TABLE exam_problems
ADD CONSTRAINT fk_exam_problems_exam_id
FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE;
```

## Sample Data Template

```sql
-- 001_insert_sample_user_history.sql

-- User history for user_id = 1
INSERT INTO user_history (id, user_id, action, action_details, ip_address, user_agent, created_at, updated_at)
VALUES
    (1, 1, 'LOGIN', '{"success": true, "method": "KAKAO"}', '127.0.0.1', 'Mozilla/5.0...', '2024-01-01 10:00:00.000000', '2024-01-01 10:00:00.000000'),
    (2, 1, 'UPDATE_PROFILE', '{"field": "nickname", "old": "old_nick", "new": "new_nick"}', '127.0.0.1', 'Mozilla/5.0...', '2024-01-02 14:30:00.000000', '2024-01-02 14:30:00.000000'),
    (3, 1, 'LOGOUT', '{"success": true}', '127.0.0.1', 'Mozilla/5.0...', '2024-01-02 18:00:00.000000', '2024-01-02 18:00:00.000000');

-- User history for user_id = 2
INSERT INTO user_history (id, user_id, action, action_details, ip_address, user_agent, created_at, updated_at)
VALUES
    (4, 2, 'LOGIN', '{"success": true, "method": "APPLE"}', '192.168.1.100', 'Mozilla/5.0...', '2024-01-03 09:15:00.000000', '2024-01-03 09:15:00.000000');
```

## Rules

### DDL Rules

- Use `BIGINT AUTO_INCREMENT PRIMARY KEY` for id
- Use `DATETIME(6)` for timestamps (microsecond precision)
- Use `utf8mb4_unicode_ci` collation
- Use `COMMENT` for all tables/columns
- Add `idx_{column}` and `idx_deleted_at` indexes
- Add `fk_{table}_{column}` foreign keys
- Follow CLAUDE.md BaseEntity pattern (created_at, updated_at, deleted_at)

### DML Rules

- Explicit PK values (id column)
- Use `NOW()` or hardcoded timestamps for consistency
- Group by related entity (user_id = 1, then user_id = 2)
- 3-5 sample records per entity
- Match foreign key values to existing sample data
- Order: 001, 002, 003, ...
