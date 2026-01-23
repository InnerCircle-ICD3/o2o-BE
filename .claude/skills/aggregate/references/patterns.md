# Aggregate Patterns Reference

## Aggregate Root Pattern

### Characteristics

- Has `@OneToMany` associations
- Extends `AbstractAggregateRoot<T>`
- No `@ManyToOne` pointing to other Aggregates
- Repository interface exists

### Correct Pattern

```java
// ✅ Aggregate Root
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam extends AbstractAggregateRoot<Exam> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private List<ExamProblem> examProblems = new ArrayList<>();

    // Domain Event
    public void complete() {
        this.status = ExamStatus.COMPLETED;
        registerEvent(new ExamCompletedEvent(this.id, this.userId));
    }

    // Business logic
    public void addExamProblem(ExamProblem examProblem) {
        if (examProblems.size() >= 50) {
            throw ExamValidationException.tooManyProblems();
        }
        this.examProblems.add(examProblem);
    }
}
```

### Wrong Pattern

```java
// ❌ Missing Aggregate Root extension
@Entity
public class Exam extends BaseEntity { // ❌ Should extend AbstractAggregateRoot
    // ...
}

// ❌ Root has @ManyToOne to other Aggregate
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // ❌ Should use Long userId
}
```

## Child Entity Pattern

### Characteristics

- Only `@ManyToOne` within same Aggregate (to Root) - BUT **should be removed**
- NO `@ManyToOne` to other Aggregates
- Extends `BaseEntity`
- No Repository interface

### Correct Pattern (No Parent Reference)

```java
// ✅ Child Entity - NO Parent reference
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamProblem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // ✅ ID reference only (NO @ManyToOne)
    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "exam_problem_order")
    private Integer examProblemOrder;

    // Business logic
    public void fixExamProblemOrder(Integer order) {
        if (this.examProblemOrder != null) {
            throw ExamValidationException(EXAM_PROBLEM_ORDER_ALREADY_ASSIGNED);
        }
        this.examProblemOrder = order;
    }

    public boolean isFirstProblem() {
        return this.examProblemOrder == 1;
    }
}
```

### Wrong Pattern

```java
// ❌ Child with Parent reference (Anti-pattern)
@Entity
public class ExamProblem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam; // ❌ REMOVE THIS - creates bidirectional

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem; // ❌ REMOVE THIS - cross-Aggregate reference
}
```

## Association Mapping Pattern

### Kokpick Rule

"애그리거트의 상위 엔티티만 연관관계 참조를 가지도록"

### Aggregate 내부 (Within Aggregate)

- **Root → Child**: `@OneToMany @JoinColumn` (단방향)
- **Child → Root**: ❌ 참조 금지

### Aggregate 간 (Between Aggregates)

- **ID 참조만**: `Long examId`, `Long problemId`
- **❌ 직접 참조 금지**: `@ManyToOne private Exam exam`

### Mapping Examples

```java
// ✅ Correct: Root owns Children
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {
    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private List<ExamProblem> examProblems = new ArrayList<>();
}

// ✅ Correct: Child uses ID only
@Entity
public class ExamProblem extends BaseEntity {
    private Long examId;  // ID reference only
    private Long problemId; // ID reference only
}

// ❌ Wrong: Bidirectional
@Entity
public class ExamProblem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam; // ❌ Creates bidirectional
}

// ❌ Wrong: Cross-Aggregate direct reference
@Entity
public class ExamProblem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem; // ❌ Should use Long problemId
}
```

## Domain Events Pattern

### Characteristics

- Root extends `AbstractAggregateRoot<T>`
- Uses `registerEvent()` to publish events
- Event classes exist in `domain/event/`
- `@EventListener` or `@TransactionalEventListener` handles events

### Publish Event (in Aggregate Root)

```java
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {

    public void complete() {
        if (this.status != ExamStatus.IN_PROGRESS) {
            throw ExamValidationException.invalidStatus(this.status, ExamStatus.COMPLETED);
        }
        this.status = ExamStatus.COMPLETED;
        registerEvent(new ExamCompletedEvent(this.id, this.userId));
    }

    public void submitAnswer(Long problemId, List<Integer> choices) {
        ExamProblem problem = findExamProblem(problemId);
        problem.validateSubmission();
        registerEvent(new ExamAnswerSubmittedEvent(this.id, this.userId, problemId, choices));
    }
}
```

### Event Class

```java
package com.kokpick.exam.domain.event;

import lombok.Getter;

@Getter
public class ExamCompletedEvent {
    private final Long examId;
    private final Long userId;
    private final LocalDateTime completedAt;

    public ExamCompletedEvent(Long examId, Long userId) {
        this.examId = examId;
        this.userId = userId;
        this.completedAt = LocalDateTime.now();
    }
}
```

### Event Handler

```java
@Component
public class ExamEventHandler {

    @TransactionalEventListener
    public void handleExamCompleted(ExamCompletedEvent event) {
        // Update user statistics
        // Send notification
        // Update exam history
        // Publish to external systems
    }

    @TransactionalEventListener
    public void handleAnswerSubmitted(ExamAnswerSubmittedEvent event) {
        // Update answer statistics
        // Trigger recommendation engine
        // Update user progress
    }
}
```

## Aggregate Boundaries Pattern

### Characteristics

- Each Root has its own Repository
- Transactions don't span multiple Aggregates
- Consistency boundary is clear

### Correct Pattern

```java
// ✅ Correct: Single Aggregate transaction
@Transactional
public void submitExam(Long examId, SubmitAnswerCommand command) {
    Exam exam = examRepository.findById(examId)
        .orElseThrow(() -> ExamValidationException.examNotFound(examId));
    exam.submitAnswer(command); // All consistency within Exam
    examRepository.save(exam);
}

// ✅ Correct: Use Domain Events for cross-Aggregate coordination
@Transactional
public void submitExam(Long examId, SubmitAnswerCommand command) {
    Exam exam = examRepository.findById(examId).orElseThrow();
    exam.submitAnswer(command); // Publishes ExamCompletedEvent
    examRepository.save(exam);
    // Event handler updates User aggregate separately
}
```

### Wrong Pattern

```java
// ❌ Wrong: Multiple Aggregates in one transaction
@Transactional
public void submitExamAndUpdateUser(Long examId, SubmitAnswerCommand command) {
    Exam exam = examRepository.findById(examId).orElseThrow();
    User user = userRepository.findById(userId).orElseThrow();
    exam.submitAnswer(command);
    user.updateExamCount(); // ❌ Separate Aggregate - violates boundaries
    examRepository.save(exam);
    userRepository.save(user); // ❌ Two Aggregates in one transaction
}
```

## Entity Inheritance Pattern

### BaseEntity (All Entities)

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    protected LocalDateTime deletedAt;

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return deletedAt == null;
    }
}
```

### Entity Usage

```java
@Entity
@Getter
@Where(clause = "deleted_at IS NULL") // Soft delete filter
public class Exam extends BaseEntity {
    // Inherited: createdAt, updatedAt, deletedAt
    // Inherited methods: softDelete(), isActive()

    public void delete() {
        this.softDelete(); // Uses BaseEntity method
    }
}
```

## Checklist Summary

| Pattern            | Checklist                                                                     |
|--------------------|-------------------------------------------------------------------------------|
| **Aggregate Root** | `@OneToMany`, `AbstractAggregateRoot`, no `@ManyToOne` out, Repository exists |
| **Child Entity**   | ID only, no `@ManyToOne`, extends `BaseEntity`, no Repository                 |
| **Association**    | Root→Child `@JoinColumn`, Child→Root ❌, Cross-Aggregate ID only               |
| **Domain Events**  | `AbstractAggregateRoot`, `registerEvent()`, Event handler                     |
| **Boundaries**     | One Repository per Root, one Aggregate per transaction                        |
| **Inheritance**    | `BaseEntity`, `@Where`, `@EntityListeners`                                    |
