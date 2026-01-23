# YAGNI Violation Patterns

## Unused Repository Methods

**Pattern**: Repository with methods that are never called

```java
// ❌ YAGNI Violation
public interface ProblemRepository {
    Problem save(Problem problem);
    Problem findById(Long id);
    List<Problem> findByChapterId(Long chapterId);
    List<Problem> findAll();              // REMOVE - never called
    void delete(Problem problem);         // REMOVE - never called
    List<Problem> findByDifficulty(String difficulty);  // REMOVE
    Problem findByTitle(String title);    // REMOVE - never called
}

// ✅ YAGNI Compliant
public interface ProblemRepository {
    Problem save(Problem problem);
    Optional<Problem> findById(Long id);
    List<Problem> findByChapterId(Long chapterId);
    List<Long> findProblemIdsByChapterId(Long chapterId);
}
```

**Detection**:

```bash
grep -r "findAll(" kokpick-domains/
grep -r "delete(" kokpick-domains/
```

## Unused Service Methods

**Pattern**: Service with methods that are never invoked

```java
// ❌ YAGNI Violation
@Service
public class ExamQueryService {
    public ExamInfo getExam(Long id) { ... }
    public List<ExamInfo> getExamsByUserId(Long userId) { ... }
    public ExamStatistics getStatistics() { ... }  // REMOVE
    public ExamExport exportToExcel() { ... }      // REMOVE
}

// ✅ YAGNI Compliant
@Service
public class ExamQueryService {
    public ExamInfo getExam(Long id) { ... }
    public List<ExamInfo> getExamsByUserId(Long userId) { ... }
}
```

**Detection**: Use IDE "Find Usages" or `grep -r "getStatistics("`

## Unused DTO Fields

**Pattern**: DTO with fields that are never populated or displayed

```java
// ❌ YAGNI Violation
@Getter @Builder
public class ExamResponse {
    private Long id;
    private String examName;
    private ExamStatus status;
    private LocalDateTime createdAt;
    private String createdBy;        // REMOVE - never populated
    private String description;      // REMOVE - never displayed
    private Integer totalProblems;   // REMOVE - always null
}

// ✅ YAGNI Compliant
@Getter @Builder
public class ExamResponse {
    private Long id;
    private String examName;
    private ExamStatus status;
    private LocalDateTime createdAt;
}
```

**Detection**:

- Search for field assignments in codebase
- Check Response builders

## Unused Entity Fields

**Pattern**: Entity fields mapped but never used

```java
// ❌ YAGNI Violation
@Entity
public class Exam {
    @Id
    private Long id;
    private String examName;
    private String internalCode;      // REMOVE - never used
    private Integer legacyId;         // REMOVE - migration artifact
}

// ✅ YAGNI Compliant
@Entity
public class Exam {
    @Id
    private Long id;
    private String examName;
}
```

## Unused Utility Methods

**Pattern**: Static helper methods never called

```java
// ❌ YAGNI Violation
public class StringUtils {
    public static boolean isEmpty(String str) { ... }        // Used
    public static String reverse(String str) { ... }        // REMOVE
    public static String capitalize(String str) { ... }     // REMOVE
}

// ✅ YAGNI Compliant
public class StringUtils {
    public static boolean isEmpty(String str) { ... }
}
```

## Unused Exception Classes

**Pattern**: Custom exceptions never thrown

```java
// ❌ YAGNI Violation
public class ExamValidationException extends DomainException {
    public static ExamValidationException examNotFound(Long id) { ... }
    public static ExamValidationException invalidStatus() { ... }
    public static ExamValidationException duplicateSubmission() { ... }  // REMOVE
}

// ✅ YAGNI Compliant
public class ExamValidationException extends DomainException {
    public static ExamValidationException examNotFound(Long id) { ... }
    public static ExamValidationException invalidStatus() { ... }
}
```

## Common YAGNI Violations

| Type       | Example                 | Action                   |
|------------|-------------------------|--------------------------|
| Repository | `findAll()`, `delete()` | Remove unused methods    |
| Service    | Unexported methods      | Remove or document usage |
| DTO        | Never populated fields  | Remove fields            |
| Entity     | Legacy/migration fields | Remove columns           |
| Utility    | Unused static methods   | Remove methods           |
| Exception  | Never thrown            | Remove factory methods   |

## Detection Strategy

```bash
# 1. Find potentially unused methods
grep -r "findAll(" kokpick-domains/
grep -r "delete(" kokpick-domains/

# 2. Check DTO usage
grep -r "createdBy" kokpick-applications/
grep -r "totalProblems" kokpick-applications/

# 3. Verify with IDE
# IntelliJ: Find Usages (Alt+F7)
# VS Code: Search (Ctrl+Shift+F)
```

## Refactoring Steps

1. **Identify**: Use grep or IDE to find unused code
2. **Verify**: Confirm no tests or production code uses it
3. **Remove**: Delete unused code
4. **Test**: Ensure tests still pass
5. **Commit**: Separate commit per category
