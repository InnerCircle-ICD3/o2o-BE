# Query Patterns Reference

## N+1 Query Detection

### Common Patterns

#### 1. Default Lazy Loading + Loop

```java
// ❌ N+1 발생 (1 + N queries)
List<Exam> exams = examRepository.findAll(); // 1 query
for (Exam exam : exams) {
    exam.getExamProblems().size(); // N queries (lazy loading)
}
```

**Detection**:

- `@OneToMany` without `@EntityGraph`
- Lazy collection access in loops
- `.size()`, `.stream()` on lazy collections

#### 2. Missing Fetch Join

```java
// ❌ Child Entity 로드 시 N+1
@Query("SELECT e FROM Exam e WHERE e.userId = :userId")
List<Exam> findByUserId(@Param("userId") Long userId);

// When accessing exam.getExamProblems()
// → N+1 queries occur
```

**Detection**:

- `@Query` without explicit `FETCH` keyword
- `@OneToMany` associations not fetched

### Fix: Fetch Join

```java
// ✅ Fetch Join으로 한 번에 로드
@Query("SELECT e FROM Exam e " +
       "LEFT JOIN FETCH e.examProblems " +
       "WHERE e.userId = :userId")
List<Exam> findByUserIdWithProblems(@Param("userId") Long userId);
```

### Fix: EntityGraph

```java
// ✅ @EntityGraph 사용
@EntityGraph(attributePaths = {"examProblems"})
List<Exam> findByUserId(Long userId);
```

### Fix: QueryDSL

```java
// ✅ QueryDSL Fetch Join
List<Exam> exams = queryFactory
        .selectFrom(exam)
        .leftJoin(exam.examProblems).fetchJoin()
        .where(exam.userId.eq(userId))
        .fetch();
```

## Fetch Join Patterns

### Single @OneToMany (Safe)

```java
// ✅ Single collection - OK
@Query("SELECT e FROM Exam e " +
       "LEFT JOIN FETCH e.examProblems " +
       "WHERE e.id = :id")
Optional<Exam> findById(@Param("id") Long id);
```

### Multiple @OneToMany (Requires Care)

```java
// ❌ Two collections in same entity - MultipleBagFetchException
@Query("SELECT p FROM Problem p " +
       "LEFT JOIN FETCH p.images " +
       "LEFT JOIN FETCH p.objectiveChoices") // ❌ MultipleBagFetchException
List<Problem> findAll();
```

**Solutions**:

#### Option 1: Separate Queries

```java
// ✅ Query 1: Fetch images
@Query("SELECT p FROM Problem p LEFT JOIN FETCH p.images")
List<Problem> findAllWithImages();

// ✅ Query 2: Fetch choices separately
@Query("SELECT oc FROM ObjectiveChoice oc WHERE oc.problem.id IN :problemIds")
List<ObjectiveChoice> findByProblemIds(@Param("problemIds") List<Long> problemIds);
```

#### Option 2: @BatchSize

```java
@OneToMany(cascade = ALL, orphanRemoval = true)
@JoinColumn(name = "problem_id")
@BatchSize(size = 100) // Load in batches instead of N+1
private List<ObjectiveChoice> objectiveChoices = new ArrayList<>();
```

#### Option 3: Only Fetch IDs

```java
// ✅ Fetch only IDs (avoid collections)
@Query("SELECT p.id FROM Problem p WHERE p.chapterId = :chapterId")
List<Long> findProblemIdsByChapter(@Param("chapterId") Long chapterId);
```

## MultipleBagFetchException

### Issue

Hibernate cannot fetch multiple `@OneToMany` (Bag) collections in a single query.

```java
// ❌ MultipleBagFetchException
@Query("SELECT e FROM Exam e " +
       "LEFT JOIN FETCH e.examProblems " +              // @OneToMany
       "LEFT JOIN FETCH e.examUserAnswers")             // @OneToMany
List<Exam> findAll();
```

### Detection Checklist

- [ ] Two or more `@OneToMany` in same `@Query`
- [ ] Nested `@OneToMany` (e.g., `answer.objectiveChoices`)
- [ ] `@EntityGraph` with multiple `@OneToMany` paths

### Solutions

#### 1. Separate Queries

```java
// ✅ Query 1: Fetch problems
@Query("SELECT e FROM Exam e LEFT JOIN FETCH e.examProblems")
List<Exam> findAllWithProblems();

// ✅ Query 2: Fetch answers separately (by exam IDs)
@Query("SELECT eua FROM ExamUserAnswer eua WHERE eua.exam.id IN :examIds")
List<ExamUserAnswer> findByExamIds(@Param("examIds") List<Long> examIds);
```

#### 2. @EntityGraph (Single Collection)

```java
@EntityGraph(attributePaths = {"examProblems"})
List<Exam> findAll(); // Fetch only examProblems
```

#### 3. Redesign (Avoid Multiple Collections)

```java
// ✅ Use separate queries per use case
// Use case 1: Get exam with problems
Optional<Exam> findByIdWithProblems(Long id);

// Use case 2: Get exam with answers
Optional<Exam> findByIdWithAnswers(Long id);
```

## CQRS Pattern

### Command (Write)

```java
// ✅ Command: JPA + Aggregate Root
@Service
@Transactional
public class ExamCommandService {

    public CreateExamInfo createExam(CreateExamCommand command) {
        Exam exam = Exam.create(command.userId(), command.examName());
        Exam saved = examRepository.save(exam); // Returns complete Aggregate
        return new CreateExamInfo(saved.getId());
    }

    public void submitAnswer(Long examId, SubmitAnswerCommand command) {
        Exam exam = examRepository.findById(examId).orElseThrow();
        exam.submitAnswer(command); // Business logic in Entity
        examRepository.save(exam);
    }
}
```

**Characteristics**:

- `@Transactional` (read-write)
- Returns Entity/ID
- Complete Aggregate loading
- Business logic in Entity

### Query (Read)

```java
// ✅ Query: Native Query + DTO/Info
@Service
@Transactional(readOnly = true)
public class ExamQueryService {

    public record GetExamsQuery(Long userId) {}
    public record ExamInfo(Long id, String examName, String status) {}

    public List<ExamInfo> getExamsByUser(GetExamsQuery query) {
        return examRepository.findExamInfosByUserId(query.userId());
    }

    public ExamDetailInfo getExamDetail(Long examId) {
        return examRepository.findExamDetailInfo(examId);
    }
}
```

**Characteristics**:

- `@Transactional(readOnly = true)`
- Returns Info/DTO/Projection
- Optimized queries (Fetch Join, Native Query)
- No business logic

### Repository Separation

```java
// Command Repository (JPA)
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Exam save(Exam exam);
    Optional<Exam> findById(Long id);
}

// Query Repository (Custom)
public interface ExamQueryRepository {
    List<ExamInfo> findExamInfosByUserId(Long userId);
    ExamDetailInfo findExamDetailInfo(Long examId);
}
```

## Query Optimization

### Index Usage

```java
// ✅ Use indexed columns in WHERE
@Query("SELECT e FROM Exam e WHERE e.userId = :userId")
// Ensure user_id has index: idx_user_id

// ✅ Composite index for multiple columns
@Query("SELECT e FROM Exam e WHERE e.userId = :userId AND e.status = :status")
// Ensure index: idx_user_id_status (userId, status)
```

### Pagination

```java
// ✅ Large result sets with pagination
@Override
Page<Exam> findAll(Pageable pageable);

// Usage
Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
Page<Exam> exams = examRepository.findAll(pageable);
```

### Batch Operations

```java
// ✅ Batch update
@Modifying
@Query("UPDATE Exam e SET e.status = :status WHERE e.id IN :ids")
int bulkUpdateStatus(@Param("ids") List<Long> ids, @Param("status") ExamStatus status);

// ✅ Batch delete
@Modifying
@Query("UPDATE Exam e SET e.deletedAt = :deletedAt WHERE e.id IN :ids")
int bulkSoftDelete(@Param("ids") List<Long> ids, @Param("deletedAt") LocalDateTime deletedAt);
```

### QueryDSL Optimization

```java
// ✅ Fetch Join
List<Exam> exams = queryFactory
        .selectFrom(exam)
        .leftJoin(exam.examProblems).fetchJoin()
        .where(exam.userId.eq(userId))
        .fetch();

// ✅ Where clause optimization
List<Exam> exams = queryFactory
        .selectFrom(exam)
        .where(
            exam.userId.eq(userId),
            exam.status.eq(ExamStatus.IN_PROGRESS),
            exam.deletedAt.isNull()
        )
        .fetch();

// ✅ Projection (DTO)
List<ExamInfo> infos = queryFactory
        .select(Projections.bean(ExamInfo.class,
            exam.id,
            exam.examName,
            exam.status.name.as("status")
        ))
        .from(exam)
        .where(exam.userId.eq(userId))
        .fetch();
```

## Detection Checklist Summary

| Issue                         | Detection                                            | Fix                                            |
|-------------------------------|------------------------------------------------------|------------------------------------------------|
| **N+1 Query**                 | Lazy collection in loops, `@OneToMany` without fetch | Add `LEFT JOIN FETCH` or `@EntityGraph`        |
| **MultipleBagFetchException** | Two `@OneToMany` in same query                       | Separate queries, `@BatchSize`                 |
| **CQRS Violation**            | Query without `readOnly`, Entity in Query Service    | Add `@Transactional(readOnly = true)`, use DTO |
| **Missing Index**             | WHERE on non-indexed columns                         | Add index or use indexed column                |
| **No Pagination**             | Large result sets without `Pageable`                 | Add `Pageable` parameter                       |