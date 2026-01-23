# Common Migration Patterns

## Audit Trail Table

Pattern for tracking entity changes (create, update, delete).

```sql
CREATE TABLE {entity}_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    {entity}_id BIGINT NOT NULL COMMENT 'Reference to original entity',
    action VARCHAR(100) NOT NULL COMMENT 'CREATE, UPDATE, DELETE',
    changes JSON COMMENT 'Before/after values',
    performed_by BIGINT COMMENT 'User who performed the action',
    performed_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    INDEX idx_{entity}_id ({entity}_id),
    INDEX idx_performed_by (performed_by),
    INDEX idx_performed_at (performed_at),
    FOREIGN KEY (fk_{entity}_id) REFERENCES {entity}(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_performed_by) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Audit trail for {entity}';
```

**Example usage**:

- `user_history` - Track user actions (login, logout, profile updates)
- `exam_history` - Track exam modifications
- `problem_history` - Track problem changes

## Junction Table (Many-to-Many)

Pattern for many-to-many relationships between two entities.

```sql
CREATE TABLE {table1}_{table2} (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    {table1}_id BIGINT NOT NULL,
    {table2}_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    UNIQUE KEY uk_{table1}_{table2} ({table1}_id, {table2}_id),
    INDEX idx_{table1}_id ({table1}_id),
    INDEX idx_{table2}_id ({table2}_id),
    FOREIGN KEY (fk_{table1}_id) REFERENCES {table1}(id) ON DELETE CASCADE,
    FOREIGN KEY (fk_{table2}_id) REFERENCES {table2}(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Junction table for {table1} and {table2}';
```

**Example usage**:

- `exam_problems` - Link exams to problems (already exists)
- `course_tags` - Link courses to tags
- `problem_categories` - Link problems to categories

## Soft Delete Pattern

Already implemented in BaseEntity, but can be added to existing tables:

```sql
-- V1.29.0__Add_soft_delete_to_entity.sql

ALTER TABLE {entity}
ADD COLUMN deleted_at DATETIME(6) NULL COMMENT 'Soft delete timestamp'
AFTER updated_at;

ADD INDEX idx_deleted_at (deleted_at);
```

## Timestamp Pattern

All tables should have these audit timestamps:

```sql
created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'Creation timestamp',
updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT 'Last update timestamp',
deleted_at DATETIME(6) NULL COMMENT 'Soft delete timestamp'
```

## Pattern Selection Guide

| Pattern            | When to Use                   | Example                         |
|--------------------|-------------------------------|---------------------------------|
| **Audit Trail**    | Need to track entity changes  | user_history, exam_history      |
| **Junction Table** | Many-to-many relationship     | course_tags, problem_categories |
| **Soft Delete**    | Need to preserve deleted data | All entities (via BaseEntity)   |
| **Timestamp**      | Track creation/modification   | All entities (via BaseEntity)   |
