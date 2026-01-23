# Refactoring Patterns

## Extract Method

**When**: Method is too long or does multiple things

```java
// Before
public void submitAnswer(SubmitAnswerCommand command) {
    if (command.examId() == null || command.userId() == null ||
        command.problemId() == null || command.choices() == null) {
        throw new IllegalArgumentException("Invalid command");
    }
    // ... rest of logic
}

// After
public void submitAnswer(SubmitAnswerCommand command) {
    validateSubmitAnswerCommand(command);
    // ... rest of logic
}

private void validateSubmitAnswerCommand(SubmitAnswerCommand command) {
    Objects.requireNonNull(command.examId(), "examId must not be null");
    Objects.requireNonNull(command.userId(), "userId must not be null");
    Objects.requireNonNull(command.problemId(), "problemId must not be null");
    Objects.requireNonNull(command.choices(), "choices must not be null");
}
```

## Extract Class

**When**: Class has multiple responsibilities

```java
// Before: ExamCommandService does 3 things
@Service
public class ExamCommandService {
    public void submitAnswer(...) { ... }      // Command
    public ExamInfo getExam(...) { ... }       // Query
    public void validate(...) { ... }          // Validation
}

// After: Single Responsibility
@Service
public class ExamCommandService {
    public void submitAnswer(...) { ... }
}

@Service
public class ExamQueryService {
    public ExamInfo getExam(...) { ... }
}

@Component
public class ExamValidator {
    public void validate(...) { ... }
}
```

## Replace Conditional with Polymorphism

**When**: Hard-coded type checks

```java
// Before: Hard-coded type checks
public void processExam(Object exam) {
    if (exam instanceof MockExam) {
        // ... mock exam logic
    } else if (exam instanceof PracticeExam) {
        // ... practice exam logic
    }
}

// After: Polymorphism
public interface ExamProcessor {
    void process(Exam exam);
}

public class MockExamProcessor implements ExamProcessor {
    @Override
    public void process(Exam exam) {
        // ... mock exam logic
    }
}

public class PracticeExamProcessor implements ExamProcessor {
    @Override
    public void process(Exam exam) {
        // ... practice exam logic
    }
}
```

## Introduce Parameter Object

**When**: Method has too many parameters

```java
// Before
public void createExam(Long userId, String name, ExamType type,
                       List<Long> problemIds, LocalDateTime startTime,
                       LocalDateTime endTime, Integer duration) { ... }

// After
public void createExam(CreateExamCommand command) { ... }

@Getter @Builder
public class CreateExamCommand {
    private Long userId;
    private String name;
    private ExamType type;
    private List<Long> problemIds;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration;
}
```

## Replace Magic Number with Constant

**When**: Numbers have special meaning

```java
// Before
if (exam.getProblemCount() > 50) {
    throw new IllegalArgumentException("Too many problems");
}

// After
private static final int MAX_PROBLEM_COUNT = 50;

if (exam.getProblemCount() > MAX_PROBLEM_COUNT) {
    throw new IllegalArgumentException("Too many problems");
}
```

## Decompose Conditional

**When**: Complex condition in if statement

```java
// Before
if (exam.getStatus() == ExamStatus.IN_PROGRESS &&
    exam.getStartTime().isBefore(LocalDateTime.now()) &&
    exam.getEndTime().isAfter(LocalDateTime.now())) {
    // ...
}

// After
if (exam.isActive()) {
    // ...
}

// In Entity
public boolean isActive() {
    return this.status == ExamStatus.IN_PROGRESS &&
           startTime.isBefore(LocalDateTime.now()) &&
           endTime.isAfter(LocalDateTime.now());
}
```

## Extract Interface

**When**: Class has multiple roles

```java
// Before
@Service
public class ExamService {
    public void submitAnswer(...) { ... }
    public ExamInfo getExam(...) { ... }
    public void validate(...) { ... }
}

// After
public interface ExamCommandService {
    void submitAnswer(...);
}

public interface ExamQueryService {
    ExamInfo getExam(...);
}

public interface ExamValidator {
    void validate(...);
}
```

## Common Patterns

| Pattern               | When              | Benefit                  |
|-----------------------|-------------------|--------------------------|
| Extract Method        | Long method       | Readability, reusability |
| Extract Class         | SRP violation     | Single responsibility    |
| Polymorphism          | Type checks       | Extensibility            |
| Parameter Object      | Many parameters   | Cleaner API              |
| Constant              | Magic numbers     | Self-documenting         |
| Decompose Conditional | Complex condition | Readability              |
| Extract Interface     | Multiple roles    | Flexibility              |
