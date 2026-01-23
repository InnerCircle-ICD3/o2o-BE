# Code Duplication (DRY Violations)

## Duplicated Validation Logic

**Pattern**: Same validation repeated across multiple classes

```java
// ❌ DRY Violation - repeated in 3 places
@Service
public class ExamCommandService {
    public void submitAnswer(SubmitAnswerCommand command) {
        if (command.examId() == null) {
            throw new IllegalArgumentException("examId must not be null");
        }
        if (command.userId() == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
    }
}

@Service
public class QuizCommandService {
    public void submitAnswer(QuizAnswerCommand command) {
        if (command.quizId() == null) {
            throw new IllegalArgumentException("quizId must not be null");
        }
        if (command.userId() == null) {
            throw new IllegalArgumentException("userId must not be null");
        }
    }
}

// ✅ DRY Compliant - extract to Validator
@Component
public class CommonValidator {
    public void validateNotNull(Object obj, String message) {
        Objects.requireNonNull(obj, message);
    }
}

// Usage
@Service
public class ExamCommandService {
    private final CommonValidator commonValidator;

    public void submitAnswer(SubmitAnswerCommand command) {
        commonValidator.validateNotNull(command.examId(), "examId must not be null");
        commonValidator.validateNotNull(command.userId(), "userId must not be null");
    }
}
```

## Duplicated Conversion Logic

**Pattern**: Same DTO conversion in multiple Facades

```java
// ❌ DRY Violation - repeated in 3 Facades
@Service
public class ExamFacade {
    private ExamResponse toResponse(Exam exam) {
        return ExamResponse.builder()
                .id(exam.getId())
                .examName(exam.getExamName())
                .status(exam.getStatus().name())
                .build();
    }
}

@Service
public class QuizFacade {
    private QuizResponse toResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .quizName(quiz.getQuizName())
                .status(quiz.getStatus().name())
                .build();
    }
}

// ✅ DRY Compliant - static factory in Response
@Getter @Builder
public class ExamResponse {
    private Long id;
    private String examName;
    private String status;

    public static ExamResponse from(ExamInfo info) {
        return ExamResponse.builder()
                .id(info.id())
                .examName(info.examName())
                .status(info.status().name())
                .build();
    }
}
```

## Duplicated Error Handling

**Pattern**: Same exception handling in Controllers

```java
// ❌ DRY Violation
@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponse>> getExam(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(facade.getExam(id)));
        } catch (ExamValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

@RestController
@RequestMapping("/api/v1/quizzes")
public class QuizController {
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuiz(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(facade.getQuiz(id)));
        } catch (QuizValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

// ✅ DRY Compliant - Global Exception Handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiResponse<Void>> handleDomainException(DomainException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
    }
}
```

## Duplicated Query Logic

**Pattern**: Same query patterns across Repositories

```java
// ❌ DRY Violation
public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Query("SELECT e FROM Exam e WHERE e.userId = :userId AND e.deletedAt IS NULL")
    List<Exam> findByUserId(@Param("userId") Long userId);
}

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    @Query("SELECT q FROM Quiz q WHERE q.userId = :userId AND q.deletedAt IS NULL")
    List<Quiz> findByUserId(@Param("userId") Long userId);
}

// ✅ DRY Compliant - Base Repository or QueryDSL
// Option 1: Base Repository
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
    @Query("SELECT e FROM #{#entityName} e WHERE e.userId = :userId AND e.deletedAt IS NULL")
    List<T> findByUserId(@Param("userId") Long userId);
}

// Option 2: QueryDSL
public class ExamQueryRepository {
    public List<Exam> findByUserId(Long userId) {
        return queryFactory
                .selectFrom(exam)
                .where(exam.userId.eq(userId))
                .where(exam.deletedAt.isNull())
                .fetch();
    }
}
```

## Duplicated Null Checks

**Pattern**: Repeated null validation

```java
// ❌ DRY Violation
if (exam != null && exam.getStatus() != null) {
    // ...
}
if (quiz != null && quiz.getStatus() != null) {
    // ...
}

// ✅ DRY Compliant - use Optional
Optional.ofNullable(exam)
    .map(Exam::getStatus)
    .ifPresent(status -> { ... });

// Or custom method
public boolean isValidWithStatus(Exam exam) {
    return exam != null && exam.getStatus() != null;
}
```

## Duplicated Entity Constructors

**Pattern**: Similar constructors can use Builder

```java
// ❌ DRY Violation
public class Exam {
    public Exam(Long id, String name, ExamStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Exam(Long id, String name) {
        this(id, name, ExamStatus.READY);
    }
}

// ✅ DRY Compliant - Builder pattern
@Entity
@Getter
@Builder
public class Exam {
    private Long id;
    private String name;
    @Builder.Default
    private ExamStatus status = ExamStatus.READY;
}
```

## Common Duplication Patterns

| Pattern        | Detection                       | Solution                    |
|----------------|---------------------------------|-----------------------------|
| Validation     | Same null checks across classes | Extract to Validator        |
| Conversion     | Same toResponse() in Facades    | Static factory in Response  |
| Error handling | Same try-catch in Controllers   | Global exception handler    |
| Query          | Same @Query in Repositories     | Base repository or QueryDSL |
| Null checks    | Repeated null validation        | Optional or utility method  |
| Constructors   | Similar constructors            | Builder pattern             |

## Refactoring Steps

1. **Identify**: Search for repeated patterns
   ```bash
   grep -r "must not be null" kokpick-*/src/main/java/
   grep -r "toResponse(" kokpick-applications/
   ```

2. **Extract**: Create shared component
    - Validation → `CommonValidator`
    - Conversion → Static factory in Response
    - Error handling → `@RestControllerAdvice`

3. **Replace**: Update all usages
4. **Test**: Verify functionality preserved
5. **Commit**: Separate commit per extraction type
