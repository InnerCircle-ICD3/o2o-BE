# DDD Anti-Patterns

## Anemic Domain Model

**Pattern**: Business logic in Service instead of Entity

```java
// ❌ Anemic Domain - business logic in Service
@Entity
@Getter
public class Exam {
    private Long id;
    private ExamStatus status;

    public void setStatus(ExamStatus status) {
        this.status = status;
    }
}

@Service
public class ExamCommandService {
    public void submitExam(Long examId) {
        Exam exam = repository.findById(examId);
        if (exam.getStatus() == ExamStatus.IN_PROGRESS) {
            exam.setStatus(ExamStatus.COMPLETED);
        }
    }
}

// ✅ Rich Domain Model - business logic in Entity
@Entity
@Getter
public class Exam {
    private Long id;
    private ExamStatus status;

    public void complete() {
        if (this.status != ExamStatus.IN_PROGRESS) {
            throw ExamValidationException.invalidStatus(this.status, ExamStatus.COMPLETED);
        }
        this.status = ExamStatus.COMPLETED;
    }

    public boolean isCompleted() {
        return this.status == ExamStatus.COMPLETED;
    }
}

@Service
public class ExamCommandService {
    public void submitExam(Long examId, Long userId) {
        Exam exam = validator.validateOwnershipAndGetExam(examId, userId);
        exam.complete();  // Rich domain model
        repository.save(exam);
    }
}
```

## Bidirectional Aggregates

**Pattern**: Child entity references Parent (violates Aggregate Root pattern)

```java
// ❌ Bidirectional - Child references Parent
@Entity
public class Exam extends BaseEntity {
    @OneToMany(mappedBy = "exam")
    private List<ExamProblem> examProblems;
}

@Entity
public class ExamProblem extends BaseEntity {
    @ManyToOne
    private Exam exam;  // REMOVE - creates bidirectional
}

// ✅ Unidirectional - Root owns Child
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {
    @OneToMany(cascade = ALL, orphanRemoval = true, fetch = LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private List<ExamProblem> examProblems = new ArrayList<>();
}

@Entity
public class ExamProblem extends BaseEntity {
    private Long examId;  // Simple field - no parent reference
}
```

## Repository in Facade

**Pattern**: Facade directly uses Repository (bypasses Service layer)

```java
// ❌ Facade directly uses Repository
@Service
public class ExamFacade {
    private final ExamRepository examRepository;

    public ExamResponse getExam(Long id) {
        Exam exam = examRepository.findById(id);  // Violation
        return ExamResponse.from(exam);
    }
}

// ✅ Facade uses Service
@Service
public class ExamFacade {
    private final ExamQueryService examQueryService;

    public ExamResponse getExam(Long id) {
        ExamInfo info = examQueryService.getExam(id);
        return ExamResponse.from(info);
    }
}
```

## Entity Exposure in Controller

**Pattern**: Controller returns Entity directly instead of DTO

```java
// ❌ Entity exposure
@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {
    @GetMapping("/{id}")
    public Exam getExam(@PathVariable Long id) {  // Returns Entity
        return examRepository.findById(id).orElseThrow();
    }
}

// ✅ DTO usage
@RestController
@RequestMapping("/api/v1/exams")
public class ExamController {
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamResponse>> getExam(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(facade.getExam(id)));
    }
}
```

## Missing Factory Classes

**Pattern**: Complex entity creation in Service instead of Factory

```java
// ❌ Complex creation in Service
@Service
public class ExamCommandService {
    public Exam createExam(CreateExamCommand command) {
        // 20 lines of validation and setup
        Exam exam = Exam.builder()
                .userId(command.userId())
                .examName(command.examName())
                .examType(determineExamType(command))  // Complex logic
                .problemIds(command.problemIds())
                .build();
        return repository.save(exam);
    }

    private ExamType determineExamType(CreateExamCommand command) {
        // Complex determination logic
    }
}

// ✅ Factory pattern
@Component
public class ExamFactory {
    public Exam createFromCommand(CreateExamCommand command) {
        ExamType examType = determineExamType(command);
        return Exam.builder()
                .userId(command.userId())
                .examName(command.examName())
                .examType(examType)
                .build();
    }
}
```

## Missing Domain Events

**Pattern**: State change without event publishing

```java
// ❌ State change without event
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {
    public void complete() {
        this.status = ExamStatus.COMPLETED;
    }
}

// ✅ With domain event
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {
    public void complete() {
        if (this.status != ExamStatus.IN_PROGRESS) {
            throw ExamValidationException.invalidStatus(this.status);
        }
        this.status = ExamStatus.COMPLETED;
        registerEvent(new ExamCompletedEvent(this.id, this.userId));
    }
}

@EventListener
@TransactionalEventListener
public class ExamCompletedHandler {
    public void handle(ExamCompletedEvent event) {
        // Send notification, update statistics, etc.
    }
}
```

## Violation of Aggregate Boundaries

**Pattern**: One Aggregate directly modifies another

```java
// ❌ Crossing aggregate boundaries
@Service
public class ExamService {
    public void addProblemToExam(Long examId, Long problemId) {
        Exam exam = examRepository.findById(examId);
        Problem problem = problemRepository.findById(problemId);

        // Problem should not be modified from Exam aggregate
        problem.setExamId(examId);
        problemRepository.save(problem);
    }
}

// ✅ Respect aggregate boundaries
@Service
public class ExamService {
    public void addProblemToExam(Long examId, Long problemId) {
        Exam exam = examRepository.findById(examId);

        // Exam creates its own ExamProblem (child entity)
        ExamProblem examProblem = ExamProblem.builder()
                .examId(examId)
                .problemId(problemId)
                .build();
        exam.addExamProblem(examProblem);
        examRepository.save(exam);
    }
}
```

## Common DDD Anti-Patterns

| Anti-Pattern         | Symptom                            | Solution                          |
|----------------------|------------------------------------|-----------------------------------|
| Anemic Domain        | Setter methods in Entity           | Rich domain model with behavior   |
| Bidirectional        | `@ManyToOne` in Child              | Unidirectional with `@JoinColumn` |
| Repository in Facade | Facade has `@Autowired Repository` | Use Query/Command Service         |
| Entity Exposure      | Controller returns Entity          | Use DTO                           |
| Missing Factory      | Complex creation logic             | Extract to Factory                |
| No Events            | Side effects in Service            | Domain Events                     |
| Boundary Violation   | Modifying other Aggregate          | Create child entities             |

## Refactoring Steps

1. **Identify anti-pattern** using this guide
2. **Move logic** from Service to Entity (where appropriate)
3. **Update relationships** to unidirectional
4. **Create DTOs** for API responses
5. **Add Domain Events** for state changes
6. **Test** to ensure behavior preserved
