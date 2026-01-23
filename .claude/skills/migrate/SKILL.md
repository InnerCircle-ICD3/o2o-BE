---
name: migrate
description: Generate Flyway migrations and sample data. Use when user asks to add tables, columns, or modify database schema. Automatically creates V{Version}__{Description}.sql files and sample data SQL.
---

# Database Migration Generator

Generates **Flyway migrations** and **sample data** for Kokpick project.

**Scope**: DDL (CREATE/ALTER), DML (INSERT sample data), Indexes, Foreign Keys
**Excludes**: Manual data manipulation (use SQL client directly)

## Usage

```
/migrate {table_name} {operation}
```

**Examples**:

- `/migrate UserHistory create` - Create new table
- `/migrate User add last_login_at` - Add column
- `/migrate ExamProblem add index on exam_id` - Add index
- `/migrate ProblemAnswer drop choice_text` - Drop column

**Module-level**:

- `/migrate kokpick-quiz` - All quiz-related migrations
- `/migrate kokpick-domains` - All domain migrations

## Workflow

**🚀 START MIGRATE SKILL**

### 1. Identify Operation Type

**CREATE TABLE** - New entity, Junction table, Association table
**ALTER TABLE** - ADD/DROP/MODIFY COLUMN, ADD INDEX, ADD FOREIGN KEY
**DROP TABLE** - Deprecated entity cleanup

### 2. Analyze Current Schema

```bash
ls -la kokpick-infrastructures/kokpick-database/src/main/resources/db/migration/
```

Determine next version: V1.22.0 → V1.23.0 (increment by 0.1.0)

### 3. Generate DDL Migration

**Filename**: `V{Version}__{Description}.sql`

**Key Rules**:

- `BIGINT AUTO_INCREMENT PRIMARY KEY` for id
- `DATETIME(6)` for timestamps (microsecond precision)
- `utf8mb4_unicode_ci` collation
- `COMMENT` for all tables/columns
- `idx_{column}` and `idx_deleted_at` indexes
- BaseEntity pattern: created_at, updated_at, deleted_at

**Templates** → `references/examples.md`

### 4. Generate Sample Data

**Filename**: `{Order}_insert_sample_{table}.sql`
**Location**: `db/sample/{domain}/{table}.sql`

**Rules**:

- Explicit PK values (id column)
- Group by related entity (user_id = 1, then user_id = 2)
- 3-5 sample records per entity
- Match foreign key values to existing sample data

**Template** → `references/examples.md`

### 5. Update CLAUDE.md

Update database table count, Flyway versions, sample data locations

### 6. Create Entity Class (if CREATE TABLE)

- Extend `BaseEntity`
- `@Getter` only (no `@Setter`)
- `@Builder` for creation
- `@Where(clause = "deleted_at IS NULL")` if soft delete needed
- Static factory methods for domain operations

**Template** → `references/entity-template.md`

### 7. Create Repository

**Interface** (domain layer) → `references/repository-template.md`
**Implementation** (infrastructure layer) → `references/repository-template.md`
**JPA Repository** (infrastructure layer) → `references/repository-template.md`

### 8. Verify Migration

```bash
docker-compose down
docker-compose up -d mysql
./gradlew :kokpick-database:flywayMigrate
docker exec -it mysql-kokpick mysql -u root -proot kokpick -e "DESCRIBE {table};"
```

### 9. Commit Strategy

Separate commits:

1. Flyway migration
2. Sample data
3. Entity
4. Repository
5. CLAUDE.md

---

**✅ END MIGRATE SKILL**

## Quick Reference

### DDL/DML Examples

**CREATE TABLE, ADD COLUMN, DROP COLUMN** → `references/examples.md`

### Common Patterns

**Audit Trail, Junction Table** → `references/patterns.md`

### Database Schema

**Current 32 tables** → `references/schema.md`

### Entity/Repository Templates

**Entity class** → `references/entity-template.md`
**Repository pattern** → `references/repository-template.md`

## Related Skills

- `aggregate` - Analyze aggregate boundaries before creating tables
- `test-generator` - Generate tests for new entities
- `review` - Review migration code
