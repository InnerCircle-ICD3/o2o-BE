# DDD Principles Checklist

## Aggregate Structure

### Aggregate Root Pattern

**Requirements**:

- [ ] Has `@OneToMany` associations to children
- [ ] Uses `@JoinColumn(name = "{child_table}_id")` (unidirectional)
- [ ] Extends `AbstractAggregateRoot<{Entity}>`
- [ ] Has Repository interface
- [ ] Publishes Domain Events via `registerEvent()`

**Correct Example**:

```java
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {
    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private List<ExamProblem> examProblems = new ArrayList<>();

    public void complete() {
        this.status = ExamStatus.COMPLETED;
        registerEvent(new ExamCompletedEvent(this.id));
    }
}
```

**Common Issues**:

- ❌ Missing `AbstractAggregateRoot` extension
- ❌ Bidirectional `@ManyToOne` in child
- ❌ Missing `@JoinColumn` (uses default join table)
- ❌ No Domain Events for state changes

### Child Entity Pattern

**Requirements**:

- [ ] Extends `BaseEntity`
- [ ] NO `@ManyToOne` to parent (aggregate root)
- [ ] Uses ID references only (`Long examId`)
- [ ] No Repository interface
- [ ] Business logic in entity methods

**Correct Example**:

```java
@Entity
public class ExamProblem extends BaseEntity {
    // ✅ ID reference only (NO @ManyToOne)
    @Column(name = "exam_id", nullable = false)
    private Long examId;

    // Business logic
    public void fixOrder(Integer order) {
        if (this.examProblemOrder != null) {
            throw ExamValidationException.ALREADY_ASSIGNED;
        }
        this.examProblemOrder = order;
    }
}
```

**Common Issues**:

- ❌ `@ManyToOne private Exam exam` (bidirectional)
- ❌ Cross-aggregate direct reference
- ❌ Business logic in Service (anemic domain)

### Entity Inheritance

**Requirements**:

- [ ] All entities extend `BaseEntity`
- [ ] `@Where(clause = "deleted_at IS NULL")` for soft delete
- [ ] `@EntityListeners(AuditingEntityListener.class)` enabled
- [ ] Inherited: `createdAt`, `updatedAt`, `deletedAt`
- [ ] Inherited methods: `softDelete()`, `isActive()`

**Correct Example**:

```java
@Entity
@Where(clause = "deleted_at IS NULL")
public class Exam extends BaseEntity {
    // Inherited from BaseEntity:
    // - createdAt, updatedAt, deletedAt
    // - softDelete(), isActive()
}
```

**Common Issues**:

- ❌ Missing `@Where` clause
- ❌ Direct `setDeletedAt()` instead of `softDelete()`
- ❌ Missing `@EnableJpaAuditing` in config

## Repository Pattern

### Domain Layer (Interface)

**Requirements**:

- [ ] Located in domain module (`{domain}/repository/`)
- [ ] Only required methods (YAGNI principle)
- [ ] Returns domain entities or value objects

**Correct Example**:

```java
package com.kokpick.exam.repository;

public interface ExamRepository {
    Exam save(Exam exam);
    Optional<Exam> findById(Long id);
    List<Exam> findByUserId(Long userId);
}
```

**Common Issues**:

- ❌ Unused methods (`findAll()`, `delete()`)
- ❌ Implementation leakage (returns JPA types)
- ❌ Too many methods (>10)

### Infrastructure Layer (Implementation)

**Requirements**:

- [ ] Located in infrastructure module
- [ `ExamRepositoryImpl implements ExamRepository`
- [ ] Uses JPA repository internally

**Correct Example**:

```java
@RequiredArgsConstructor
public class ExamRepositoryImpl implements ExamRepository {
    private final ExamJpaRepository jpaRepository;

    @Override
    public Exam save(Exam exam) {
        return jpaRepository.save(exam);
    }
}
```

### Aggregate Root Pattern

**Requirements**:

- [ ] Fetch Joins for complete aggregate
- [ ] Returns full aggregate with children
- [ ] Single query (no N+1)

**Correct Example**:

```java
@Query("SELECT e FROM Exam e " +
       "LEFT JOIN FETCH e.examProblems " +
       "WHERE e.id = :id")
Optional<Exam> findByIdWithProblems(@Param("id") Long id);
```

**Common Issues**:

- ❌ N+1 queries (lazy loading in loop)
- ❌ Missing Fetch Join
- ❌ Returns incomplete aggregate

## Domain Events

### Event Publishing

**Requirements**:

- [ ] Aggregate Root extends `AbstractAggregateRoot<T>`
- [ ] Uses `registerEvent()` for state changes
- [ ] Event classes in `domain/event/`
- [ ] `@TransactionalEventListener` handles events

**Correct Example**:

```java
// Publish
public void complete() {
    this.status = ExamStatus.COMPLETED;
    registerEvent(new ExamCompletedEvent(this.id, this.userId));
}

// Handle
@Component
public class ExamEventHandler {
    @TransactionalEventListener
    public void handle(ExamCompletedEvent event) {
        // Update statistics, send notifications
    }
}
```

**Common Issues**:

- ❌ Missing event registration
- ❌ Side effects in Service (should use events)
- ❌ No event handlers

## Rich Domain Model

### Business Logic in Entity

**Requirements**:

- [ ] Business logic in domain entity
- [ ] Validation in entity methods
- [ ] State transitions in entity
- [ ] Domain-specific behavior

**Correct Example**:

```java
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {
    public void start() {
        if (this.status != ExamStatus.READY) {
            throw ExamValidationException.invalidStatus(this.status, ExamStatus.IN_PROGRESS);
        }
        this.status = ExamStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void addProblem(ExamProblem problem) {
        if (examProblems.size() >= 50) {
            throw ExamValidationException.tooManyProblems();
        }
        this.examProblems.add(problem);
    }
}
```

**Common Issues**:

- ❌ Anemic domain (only getters/setters)
- ❌ Business logic in Service
- ❌ Validation in Controller

## CQRS Pattern

### Command (Write)

**Requirements**:

- [ ] `{Domain}CommandService`
- [ ] `@Transactional` (read-write)
- [ ] Returns Entity or ID
- [ ] Complete aggregate loading
- [ ] Business logic in Entity

**Correct Example**:

```java
@Service
@Transactional
public class ExamCommandService {
    public CreateExamInfo createExam(CreateExamCommand command) {
        Exam exam = Exam.create(command.userId(), command.examName());
        Exam saved = examRepository.save(exam);
        return new CreateExamInfo(saved.getId());
    }
}
```

### Query (Read)

**Requirements**:

- [ ] `{Domain}QueryService`
- [ ] `@Transactional(readOnly = true)`
- [ ] Returns Info/DTO/Projection
- [ ] Optimized queries (Fetch Join, Native)
- [ ] Inner record for Query/Info

**Correct Example**:

```java
@Service
@Transactional(readOnly = true)
public class ExamQueryService {
    public record GetExamsQuery(Long userId) {}
    public record ExamInfo(Long id, String examName, String status) {}

    public List<ExamInfo> getExamsByUser(GetExamsQuery query) {
        return examRepository.findExamInfosByUserId(query.userId());
    }
}
```

## Checklist Summary

| Area               | Key Checks                                                          |
|--------------------|---------------------------------------------------------------------|
| **Aggregate Root** | `@OneToMany` + `@JoinColumn`, `AbstractAggregateRoot`, Repository   |
| **Child Entity**   | ID reference only, no `@ManyToOne`, extends `BaseEntity`            |
| **Repository**     | Interface in domain, impl in infra, YAGNI methods                   |
| **Domain Events**  | `registerEvent()`, event handlers, side effects via events          |
| **Rich Domain**    | Business logic in Entity, validation in Entity                      |
| **CQRS**           | Command: `@Transactional`, Query: `@Transactional(readOnly = true)` |
