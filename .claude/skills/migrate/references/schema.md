# Database Schema Reference

## Current Tables (32)

### Core (10 tables)

- `user` - User accounts
- `admin_users` - Administrator accounts
- `apple_users` - Apple OAuth
- `google_users` - Google OAuth
- `kakao_users` - Kakao OAuth
- `user_roles` - User role assignments
- `user_logins` - Login history
- `avatars` - User avatar images
- `temporary_nicknames` - Temporary nickname storage
- `sms_verifications` - SMS verification codes

### Curriculum (3 tables)

- `courses` - Course catalog
- `chapters` - Chapter within course
- `theories` - Theory content for chapter

### Problem (7 tables)

- `problems` - Problem catalog
- `objective_choices` - Multiple choice options
- `problem_images` - Problem image references
- `answers` - Correct answers
- `objective_answer_choices` - Correct choice mapping
- `subjective_answers` - Subjective answer key
- `problem_recommendations` - Problem recommendation metadata

### UserStudy (2 tables)

- `chapter_user_answers` - User chapter answers
- `user_answer_choices` - User choice selections

### Quiz (5 tables)

- `quiz` - Quiz sessions
- `quiz_problems` - Quiz problem assignments
- `quiz_user_answers` - User quiz answers
- `quiz_user_choices` - User choice selections
- `quiz_scheduler_failure_logs` - Quiz scheduler failures

### Exam (4 tables)

- `exams` - Mock exam sessions
- `exam_problems` - Exam problem assignments
- `exam_user_answers` - User exam answers
- `exam_user_choices` - User choice selections

### User (1 table)

- `bookmarked_problems` - User bookmarked problems

## Column Types

| Java Type     | MySQL Type  | Example                      |
|---------------|-------------|------------------------------|
| Long          | BIGINT      | id, user_id, problem_id      |
| String        | VARCHAR     | VARCHAR(500), VARCHAR(100)   |
| String (text) | TEXT        | content, description         |
| String (JSON) | JSON        | action_details, metadata     |
| LocalDateTime | DATETIME(6) | created_at, updated_at       |
| LocalDate     | DATE        | birth_date, exam_date        |
| Boolean       | TINYINT(1)  | is_correct, is_public        |
| Integer       | INT         | display_order, problem_count |
| BigDecimal    | DECIMAL     | score, accuracy_rate         |

## Index Naming

| Type            | Pattern                 | Example                     |
|-----------------|-------------------------|-----------------------------|
| PK              | PRIMARY KEY             | id                          |
| FK              | fk_{table}_{column}     | fk_user_history_user_id     |
| Index           | idx_{column}            | idx_user_id, idx_created_at |
| Composite Index | idx_{column1}_{column2} | idx_exam_id_problem_order   |
| Unique Index    | uk_{table}_{column}     | uk_user_email               |

## Foreign Key Naming

| Pattern                          | Example                  |
|----------------------------------|--------------------------|
| fk_{child_table}_{parent_column} | fk_user_history_user_id  |
| fk_{table}_{ref_column}          | fk_exam_problems_exam_id |

## Table Naming Conventions

| Pattern                     | Example                           |
|-----------------------------|-----------------------------------|
| Singular noun               | user, course, chapter             |
| Junction table              | {table1}_{table2} (exam_problems) |
| Prefix for specific purpose | {entity}_{purpose} (user_history) |

## Column Naming Conventions

| Pattern          | Example                                  |
|------------------|------------------------------------------|
| snake_case       | user_id, created_at, exam_problem_order  |
| FK reference     | {entity}_id (user_id, exam_id)           |
| Boolean prefix   | is_{condition} (is_correct, is_public)   |
| Timestamp suffix | _at (created_at, updated_at, deleted_at) |
