# SOLID Violations

## SRP (Single Responsibility Principle)

**Pattern**: Service does 3+ things (Command + Query + Validation)

```java
// ❌ SRP Violation - ExamCommandService does everything
@Service
public class ExamCommandService {
    public void submitAnswer(...) { ... }      // Command
    public ExamInfo getExam(...) { ... }       // Query
    public void validate(...) { ... }          // Validation
}

// ✅ SRP Compliant - Separate classes
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

## OCP (Open/Closed Principle)

**Pattern**: Hard-coded type checks (if-else chains)

```java
// ❌ OCP Violation - must modify for new types
public class ExamProcessor {
    public void process(Exam exam) {
        if (exam instanceof MockExam) {
            processMock((MockExam) exam);
        } else if (exam instanceof PracticeExam) {
            processPractice((PracticeExam) exam);
        } else if (exam instanceof FinalExam) {
            processFinal((FinalExam) exam);
        }
        // Must add new if for new exam types
    }
}

// ✅ OCP Compliant - Polymorphism
public interface ExamProcessor {
    void process();
}

public class MockExamProcessor implements ExamProcessor {
    @Override
    public void process() { ... }
}

// New types can extend without modifying existing code
```

## LSP (Liskov Substitution Principle)

**Pattern**: Child entity breaks Parent contract

```java
// ❌ LSP Violation - Child throws unexpected exception
public class BaseExam {
    public void calculateScore() {
        // Standard scoring
    }
}

public class TimedExam extends BaseExam {
    @Override
    public void calculateScore() {
        throw new UnsupportedOperationException("Timed exams use different scoring");
    }
}

// ✅ LSP Compliant - Use Template Method or Strategy
public abstract class BaseExam {
    public void calculateScore() {
        if (requiresCustomScoring()) {
            calculateCustomScore();
        } else {
            calculateStandardScore();
        }
    }

    protected boolean requiresCustomScoring() {
        return false;
    }

    protected void calculateCustomScore() { }

    private void calculateStandardScore() { }
}
```

## ISP (Interface Segregation Principle)

**Pattern**: Fat interface (Repository with 10+ methods)

```java
// ❌ ISP Violation - Fat interface
public interface ExamRepository {
    Exam save(Exam exam);
    Optional<Exam> findById(Long id);
    List<Exam> findAll();
    List<Exam> findByUserId(Long userId);
    List<Exam> findByStatus(ExamStatus status);
    List<Exam> findByType(ExamType type);
    List<Exam> findByDateRange(LocalDateTime start, LocalDateTime end);
    void delete(Exam exam);
    void deleteAll();
    long count();
    // ... 10+ methods
}

// ✅ ISP Compliant - Segregated interfaces
public interface ExamRepository {
    Exam save(Exam exam);
    Optional<Exam> findById(Long id);
}

public interface ExamQueryRepository {
    List<Exam> findByUserId(Long userId);
    List<Exam> findByStatus(ExamStatus status);
}

public interface ExamAdminRepository extends ExamRepository {
    void delete(Exam exam);
    long count();
}
```

## DIP (Dependency Inversion Principle)

**Pattern**: Service depends on concrete classes (not interfaces)

```java
// ❌ DIP Violation - Concrete dependency
@Service
public class ExamService {
    private final ExamRepositoryImpl repository;  // Concrete class

    public ExamService() {
        this.repository = new ExamRepositoryImpl();
    }
}

// ✅ DIP Compliant - Interface dependency
@Service
public class ExamService {
    private final ExamRepository repository;  // Interface

    public ExamService(ExamRepository repository) {
        this.repository = repository;
    }
}
```

## Common SOLID Violations

| Principle | Violation                     | Solution                      |
|-----------|-------------------------------|-------------------------------|
| **SRP**   | Class does 3+ things          | Extract to separate classes   |
| **OCP**   | if-else chains for types      | Polymorphism/Strategy pattern |
| **LSP**   | Child throws unexpected error | Template Method/Strategy      |
| **ISP**   | Fat interface (10+ methods)   | Interface segregation         |
| **DIP**   | Depends on concrete class     | Depend on abstractions        |

## Detection Checklist

- [ ] **SRP**: Service has 3+ responsibilities (Command/Query/Validation)
- [ ] **OCP**: Hard-coded type checks with if-else chains
- [ ] **LSP**: Child class breaks parent contract
- [ ] **ISP**: Repository/Service with 10+ methods
- [ ] **DIP**: Direct instantiation of concrete classes

## Refactoring Steps

### SRP: Extract Class

1. Identify separate responsibilities
2. Create new classes for each responsibility
3. Move methods accordingly
4. Update dependencies

### OCP: Replace Conditional with Polymorphism

1. Identify type checks
2. Create interface/abstract class
3. Implement for each type
4. Replace if-else with polymorphic calls

### ISP: Interface Segregation

1. Identify client usage patterns
2. Split fat interface into focused interfaces
3. Implement classes can implement multiple interfaces
4. Update dependencies

### DIP: Depend on Abstractions

1. Identify concrete class dependencies
2. Extract interface
3. Update constructor injection
4. Configure implementation (Spring @Primary)
