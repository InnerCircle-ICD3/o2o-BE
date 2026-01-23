# Repository Pattern Template

## Repository Interface (Domain Layer)

```java
package com.kokpick.{domain}.repository;

import com.kokpick.{domain}.domain.{EntityName};
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface {EntityName}Repository {
    {EntityName} save({EntityName} entity);
    Optional<{EntityName}> findById(Long id);
    List<{EntityName}> findByUserId(Long userId);
    List<{EntityName}> findByUserIdBetweenDates(Long userId, LocalDateTime start, LocalDateTime end);
    boolean existsByUserIdAndAction(Long userId, String action);
}
```

## Repository Implementation (Infrastructure Layer)

```java
package com.kokpick.database.persistence.{domain};

import com.kokpick.database.jpa.{EntityName}JpaRepository;
import com.kokpick.{domain}.domain.{EntityName};
import com.kokpick.{domain}.repository.{EntityName}Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class {EntityName}RepositoryImpl implements {EntityName}Repository {

    private final {EntityName}JpaRepository jpaRepository;

    @Override
    public {EntityName} save({EntityName} entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<{EntityName}> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<{EntityName}> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public List<{EntityName}> findByUserIdBetweenDates(Long userId, LocalDateTime start, LocalDateTime end) {
        return jpaRepository.findByUserIdAndCreatedAtBetween(userId, start, end);
    }

    @Override
    public boolean existsByUserIdAndAction(Long userId, String action) {
        return jpaRepository.existsByUserIdAndAction(userId, action);
    }
}
```

## JPA Repository (Infrastructure Layer)

```java
package com.kokpick.database.jpa;

import com.kokpick.{domain}.domain.{EntityName};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface {EntityName}JpaRepository extends JpaRepository<{EntityName}, Long> {

    // Standard query methods
    List<{EntityName}> findByUserId(Long userId);

    // Between query
    List<{EntityName}> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    // Exists query
    boolean existsByUserIdAndAction(Long userId, String action);

    // Custom @Query
    @Query("SELECT e FROM {EntityName} e WHERE e.userId = :userId AND e.action = :action ORDER BY e.createdAt DESC")
    List<{EntityName}> findByUserIdAndActionOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("action") String action);

    // Native query (if needed)
    @Query(value = "SELECT * FROM {table_name} WHERE user_id = :userId AND action = :action LIMIT 1", nativeQuery = true)
    Optional<{EntityName}> findFirstByUserIdAndActionNative(@Param("userId") Long userId, @Param("action") String action);
}
```

## Query Repository (CQRS - Read Side)

```java
package com.kokpick.database.query.{domain};

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kokpick.{domain}.domain.Q{EntityName}.{entityName};

@RequiredArgsConstructor
@Transactional(readOnly = true)
public class {EntityName}QueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<{EntityName}> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(entityName)
                .where(
                    userIdEq(userId),
                    deletedAtIsNull()
                )
                .fetch();
    }

    public List<{EntityName}> findByUserIdBetweenDates(Long userId, LocalDateTime start, LocalDateTime end) {
        return queryFactory
                .selectFrom(entityName)
                .where(
                    userIdEq(userId),
                    createdAtBetween(start, end),
                    deletedAtIsNull()
                )
                .fetch();
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? entityName.userId.eq(userId) : null;
    }

    private BooleanExpression createdAtBetween(LocalDateTime start, LocalDateTime end) {
        return start != null && end != null ? entityName.createdAt.between(start, end) : null;
    }

    private BooleanExpression deletedAtIsNull() {
        return entityName.deletedAt.isNull();
    }
}
```

## Repository Rules

### Layer Separation

1. **Domain Layer** - Repository interface (contracts)
2. **Infrastructure Layer** - JPA implementation (details)
3. **Infrastructure Layer** - Query repository (CQRS)

### Naming Conventions

- Repository Interface: `{EntityName}Repository`
- Implementation: `{EntityName}RepositoryImpl`
- JPA Repository: `{EntityName}JpaRepository`
- Query Repository: `{EntityName}QueryRepository`

### Method Patterns

| Pattern                 | Method Name                 | Example                                   |
|-------------------------|-----------------------------|-------------------------------------------|
| Find by single field    | `findBy{Field}`             | `findByUserId(Long userId)`               |
| Find by multiple fields | `findBy{Field1}And{Field2}` | `findByUserIdAndAction()`                 |
| Find between            | `findBy{Field}Between`      | `findByCreatedAtBetween()`                |
| Exists                  | `existsBy{Field}`           | `existsByEmail(String email)`             |
| Count                   | `countBy{Field}`            | `countByUserId(Long userId)`              |
| First/Top               | `findFirstBy{Field}`        | `findFirstByUserIdOrderByCreatedAtDesc()` |

### CQRS Pattern

- **Command** - Use standard JPA Repository (write)
- **Query** - Use Query Repository with QueryDSL (read)
- Separate repositories for read and write operations

### YAGNI Principle

Only implement methods that are actually used:

- ❌ Don't add `findAll()` if not needed
- ❌ Don't add `delete()` if not needed
- ✅ Add `findByXxx()` only when used in Service
