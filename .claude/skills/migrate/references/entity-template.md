# Entity Class Template

## Standard Entity

```java
package com.kokpick.{domain}.domain;

import jakarta.persistence.*;
import kokpick.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "{table_name}")
@Where(clause = "deleted_at IS NULL")
public class {EntityName} extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "action_details", columnDefinition = "JSON")
    private String actionDetails;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Builder
    public {EntityName}(Long userId, String action, String actionDetails,
                        String ipAddress, String userAgent) {
        this.userId = userId;
        this.action = action;
        this.actionDetails = actionDetails;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    // Static factory methods for domain operations
    public static {EntityName} ofLogin(Long userId, String ipAddress, String userAgent) {
        return {EntityName}.builder()
                .userId(userId)
                .action("LOGIN")
                .actionDetails("{\"success\": true}")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
    }

    // Business logic methods (Rich Domain Model)
    public boolean isLoginAction() {
        return "LOGIN".equals(this.action);
    }
}
```

## Entity with Relationships

### Aggregate Root with OneToMany

```java
@Entity
@Getter
@NoArgsConstructor
@Table(name = "exams")
@Where(clause = "deleted_at IS NULL")
public class Exam extends AbstractAggregateRoot<Exam> {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private List<ExamProblem> examProblems = new ArrayList<>();

    @Builder
    public Exam(Long userId, List<ExamProblem> examProblems) {
        this.userId = userId;
        this.examProblems = examProblems != null ? examProblems : new ArrayList<>();
    }

    public void addExamProblem(ExamProblem examProblem) {
        this.examProblems.add(examProblem);
    }

    public int getProblemCount() {
        return this.examProblems.size();
    }
}
```

### Child Entity (No Parent Reference)

```java
@Entity
@Getter
@NoArgsConstructor
@Table(name = "exam_problems")
public class ExamProblem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "exam_problem_order", nullable = false)
    private Integer examProblemOrder;

    @Builder
    public ExamProblem(Long examId, Long problemId, Integer examProblemOrder) {
        this.examId = examId;
        this.problemId = problemId;
        this.examProblemOrder = examProblemOrder;
    }

    // Business logic
    public boolean isFirstProblem() {
        return this.examProblemOrder == 1;
    }
}
```

## Entity Rules

### Annotations

- `@Entity` - JPA entity
- `@Getter` - Generate getters (no `@Setter`)
- `@NoArgsConstructor` - Default constructor for JPA
- `@Builder` - Builder pattern for creation
- `@Table(name = "{table_name}")` - Table name mapping
- `@Where(clause = "deleted_at IS NULL")` - Soft delete filter

### Field Mapping

- `@Id` - Primary key
- `@GeneratedValue(strategy = IDENTITY)` - Auto-increment
- `@Column(name = "{column}", nullable = false)` - Column mapping
- `@Column(columnDefinition = "JSON")` - JSON column type
- `@OneToMany` - One-to-many relationship (Aggregate Root only)
- `@JoinColumn` - Foreign key column

### Best Practices

1. **Extend BaseEntity** - CreatedAt, UpdatedAt, DeletedAt
2. **No Setter methods** - Use `@Builder` for creation
3. **Rich Domain Model** - Business logic in entity methods
4. **Static factory methods** - For domain operations (e.g., `ofLogin()`)
5. **Validation in entity** - Throw domain exceptions for invalid state
6. **Unidirectional relationships** - Child does not reference parent
