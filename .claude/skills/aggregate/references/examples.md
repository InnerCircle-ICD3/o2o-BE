# Aggregate Examples

## Correct Pattern Examples

### Exam Aggregate (Root + Child)

```java
// Aggregate Root
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

    public void addExamProblem(ExamProblem examProblem) {
        this.examProblems.add(examProblem);
    }
}

// Child Entity
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
}
```

### Problem Aggregate (Root + Children)

```java
// Aggregate Root
@Entity
@Getter
@Table(name = "problems")
@Where(clause = "deleted_at IS NULL")
public class Problem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private List<ProblemImage> problemImages = new ArrayList<>();

    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private List<ObjectiveChoice> objectiveChoices = new ArrayList<>();

    public void addProblemImage(ProblemImage image) {
        if (problemImages.size() >= 5) {
            throw ProblemValidationException.tooManyImages();
        }
        this.problemImages.add(image);
    }
}

// Child Entity
@Entity
@Getter
@Table(name = "objective_choices")
public class ObjectiveChoice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId; // ID reference only

    @Column(name = "choice_number", nullable = false)
    private Integer choiceNumber;

    @Column(name = "choice_text", nullable = false, length = 1000)
    private String choiceText;
}
```

## Wrong Pattern Examples

### ❌ Child with Parent Reference

```java
// ❌ Child Entity with Parent reference (Anti-pattern)
@Entity
public class ExamProblem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam; // ❌ REMOVE THIS

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem; // ❌ REMOVE THIS (cross-Aggregate)

    public Exam getExam() {
        return exam; // ❌ Should not exist
    }
}
```

### ❌ Cross-Aggregate Direct Reference

```java
// ❌ Direct reference to other Aggregate
@Entity
public class ExamUserAnswer extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem; // ❌ Should use Long problemId

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam; // ❌ Should use Long examId
}

// ✅ Correct: Use ID references
@Entity
public class ExamUserAnswer extends BaseEntity {
    @Column(name = "problem_id", nullable = false)
    private Long problemId; // ID reference

    @Column(name = "exam_id", nullable = false)
    private Long examId; // ID reference
}
```

### ❌ Missing Aggregate Root Extension

```java
// ❌ Missing AbstractAggregateRoot
@Entity
public class Exam extends BaseEntity {
    // No Domain Event support
    public void complete() {
        this.status = ExamStatus.COMPLETED;
        // ❌ Cannot registerEvent()
    }
}

// ✅ Correct: Extend AbstractAggregateRoot
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {
    public void complete() {
        this.status = ExamStatus.COMPLETED;
        registerEvent(new ExamCompletedEvent(this.id)); // ✅ Domain Event
    }
}
```

## All Aggregates in Kokpick

| Domain         | Aggregate Root    | Child Entities                              | Description                            |
|----------------|-------------------|---------------------------------------------|----------------------------------------|
| **Curriculum** | Course            | -                                           | Course catalog                         |
| **Curriculum** | Chapter           | -                                           | Chapter within course                  |
| **Curriculum** | Theory            | -                                           | Theory content (OneToOne with Chapter) |
| **Problem**    | Problem           | ProblemImage, ObjectiveChoice               | Problem with images and choices        |
| **UserStudy**  | ChapterUserAnswer | UserAnswerChoice                            | User chapter answers                   |
| **Quiz**       | Quiz              | QuizProblem, QuizUserAnswer, QuizUserChoice | Quiz sessions                          |
| **Quiz**       | QuizUserAnswer    | QuizUserChoice                              | User quiz answers (Root)               |
| **Exam**       | Exam              | ExamProblem, ExamUserAnswer, ExamUserChoice | Mock exams                             |
| **Exam**       | ExamUserAnswer    | ExamUserChoice                              | User exam answers (Root)               |
| **User**       | User              | -                                           | User accounts                          |

## Aggregate Structure Examples

### Course → Chapter (ID Reference)

```java
// ✅ Correct: ID reference between Aggregates
@Entity
public class Chapter extends BaseEntity {
    @Column(name = "course_id", nullable = false)
    private Long courseId; // ID reference only (no @ManyToOne)
}

// ❌ Wrong: Direct reference
@Entity
public class Chapter extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course; // ❌ Cross-Aggregate reference
}
```

### Quiz → QuizProblem (Aggregate Root → Child)

```java
// ✅ Correct: Aggregate Root owns Children
@Entity
public class Quiz extends AbstractAggregateRoot<Quiz> {
    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private List<QuizProblem> quizProblems = new ArrayList<>();

    public void addQuizProblem(QuizProblem problem) {
        if (quizProblems.size() >= 20) {
            throw QuizValidationException.tooManyProblems();
        }
        this.quizProblems.add(problem);
    }
}

@Entity
public class QuizProblem extends BaseEntity {
    @Column(name = "quiz_id", nullable = false)
    private Long quizId; // ID reference only

    @Column(name = "problem_id", nullable = false)
    private Long problemId; // ID reference only
}
```

## Aggregate Comparison

| Aspect            | Aggregate Root                            | Child Entity                  |
|-------------------|-------------------------------------------|-------------------------------|
| **Inheritance**   | `AbstractAggregateRoot<T>` + `BaseEntity` | `BaseEntity`                  |
| **Associations**  | `@OneToMany` to Children                  | None (ID only)                |
| **Repository**    | ✅ Has Repository                          | ❌ No Repository               |
| **Domain Events** | ✅ Can publish                             | ❌ Cannot publish              |
| **Lifecycle**     | Owned by Repository                       | Owned by Root                 |
| **Example**       | `Exam`, `Problem`, `Quiz`                 | `ExamProblem`, `ProblemImage` |

## Common Issues and Fixes

| Issue                            | Severity   | Fix                                    |
|----------------------------------|------------|----------------------------------------|
| Child has `@ManyToOne` to Root   | **High**   | Remove Parent reference, use ID        |
| Cross-Aggregate direct reference | **High**   | Use ID reference (`Long problemId`)    |
| Missing `AbstractAggregateRoot`  | **High**   | Add `extends AbstractAggregateRoot<T>` |
| Bidirectional association        | **High**   | Remove `@ManyToOne` from Child         |
| Child has Repository             | **Medium** | Remove Repository, access via Root     |
| Missing Domain Events            | **Medium** | Add `registerEvent()` calls            |
| Transaction spans Aggregates     | **Medium** | Use Domain Events for coordination     |
