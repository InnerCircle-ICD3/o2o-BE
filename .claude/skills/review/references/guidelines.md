# CLAUDE.md Guidelines Reference

## Complete Reference

This file provides a quick reference to key CLAUDE.md guidelines for code review.

## Aggregate Structure

**Rule**: "애그리거트의 상위 엔티티만 연관관계 참조를 가지도록"

### Within Aggregate

- **Root → Child**: `@OneToMany @JoinColumn(name="xxx_id")` (unidirectional)
- **Child → Root**: ❌ No reference

### Between Aggregates

- **Reference**: ID only (`Long examId`, `Long problemId`)
- **❌ Direct**: `@ManyToOne private Exam exam`

## Repository Pattern

### YAGNI Principle

**Required Methods Only**:

- `save(Entity)` - Save entity
- `findById(Long)` - Find by ID
- `findByXxxId(Long)` - Find by FK

**NOT Needed**:

- ❌ `findAll()` - Unless used
- ❌ `delete()` - Unless used
- ❌ Search methods - Unless used

### Aggregate Root Pattern

- Fetch Join for complete Aggregate
- Single query (no N+1)
- Return full aggregate with children

## Service Layer

### Constructor Injection

```java
// ✅ Correct: Explicit constructor
@Service
public class ExamService {
    private final ExamRepository repository;

    public ExamService(ExamRepository repository) {
        this.repository = repository;
    }
}

// ❌ Wrong: @RequiredArgsConstructor
@Service
@RequiredArgsConstructor
public class ExamService {
    private final ExamRepository repository;
}
```

### Transactional

- **Command**: `@Transactional`
- **Query**: `@Transactional(readOnly = true)`

## Type Declaration

### No `var` Keyword

```java
// ✅ Correct
ExamService service = new ExamService(repository);
List<Exam> exams = repository.findByUserId(userId);

// ❌ Wrong
var service = new ExamService(repository);
var exams = repository.findByUserId(userId);
```

## DTO Pattern

### Response DTO

```java
@Getter
@Builder
public class ExamResponse {
    private Long id;
    private String examName;

    // Static factory method
    public static ExamResponse from(ExamInfo info) {
        return ExamResponse.builder()
                .id(info.id())
                .examName(info.examName())
                .build();
    }
}
```

## Exception Handling

### Custom Exceptions Only

```java
// ✅ Correct
throw ExamValidationException.examNotFound(examId);

// ❌ Wrong
throw new IllegalArgumentException("Exam not found");
```

### Exception Hierarchy

```
RuntimeException
  └── DomainException (kokpick-common)
        ├── ExamValidationException
        ├── QuizValidationException
        ├── ProblemAnswerValidationException
        └── ...
```

## CQRS Pattern

### Command (Write)

- JPA + Aggregate Root
- `@Transactional`
- Returns Entity/ID
- Business logic in Entity

### Query (Read)

- Query Repository + Native Query
- `@Transactional(readOnly = true)`
- Returns Info/DTO/Projection
- Inner record for Query/Info

## Testing

### Domain (Classist)

- No Spring Context
- No Mocks
- Real objects
- State verification

### Service (Mockist)

- `@ExtendWith(MockitoExtension.class)`
- `@Mock` dependencies
- `when().thenReturn()` setup
- `verify()` behavior check

## Quick Reference Table

| Area               | Rule                            | Example                                           |
|--------------------|---------------------------------|---------------------------------------------------|
| **Aggregate Root** | `@OneToMany @JoinColumn`        | `@OneToMany @JoinColumn(name="exam_id")`          |
| **Child Entity**   | No Parent reference             | `private Long examId;` (not `@ManyToOne`)         |
| **Repository**     | YAGNI methods                   | `save()`, `findById()`, `findByXxxId()`           |
| **Constructor**    | Explicit constructor            | `public Service(Repo repo) { this.repo = repo; }` |
| **Type**           | No `var`                        | `ExamService service = ...;`                      |
| **DTO**            | Static `from()`                 | `public static Response from(Info info)`          |
| **Exception**      | Custom only                     | `throw ExamValidationException.examNotFound(id)`  |
| **Command**        | `@Transactional`                | `@Transactional public void create()`             |
| **Query**          | `@Transactional(readOnly=true)` | `@Transactional(readOnly=true) public Info get()` |

## Related Files

For detailed guidelines, see:

- **DDD Patterns**: `references/ddd-checklist.md`
- **Coding Standards**: `references/coding-standards.md`
- **Full CLAUDE.md**: Project root `/CLAUDE.md`