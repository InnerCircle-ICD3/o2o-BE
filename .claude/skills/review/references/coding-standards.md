# Coding Standards Checklist

## Type Declaration

### No `var` Keyword

**Requirements**:

- [ ] All variables use explicit strong types
- [ ] No `var` keyword anywhere
- [ ] Generic types explicitly declared

**Correct Example**:

```java
// ✅ Explicit types
ExamService examService = new ExamService(examRepository);
List<Exam> exams = examRepository.findByUserId(userId);
Map<Long, Exam> examMap = exams.stream()
        .collect(Collectors.toMap(Exam::getId, Function.identity()));
```

**Wrong Example**:

```java
// ❌ var keyword
var examService = new ExamService(examRepository);
var exams = examRepository.findByUserId(userId);
var examMap = new HashMap<Long, Exam>();
```

## Service Layer

### Constructor Injection

**Requirements**:

- [ ] Constructor injection (no `@RequiredArgsConstructor`)
- [ ] Explicit constructor with all dependencies
- [ ] No field injection
- [ ] No setter injection

**Correct Example**:

```java
@Service
@Transactional(readOnly = true)
public class ExamQueryService {
    private final ExamRepository examRepository;
    private final ExamValidator examValidator;

    // ✅ Explicit constructor
    public ExamQueryService(ExamRepository examRepository, ExamValidator examValidator) {
        this.examRepository = examRepository;
        this.examValidator = examValidator;
    }
}
```

**Wrong Example**:

```java
@Service
@RequiredArgsConstructor // ❌ Don't use
public class ExamQueryService {
    private final ExamRepository examRepository;
}
```

### Transactional Annotation

**Requirements**:

- [ ] Command Service: `@Transactional`
- [ ] Query Service: `@Transactional(readOnly = true)`
- [ ] No `@Transactional` on methods without database access

**Correct Example**:

```java
// Command (Write)
@Service
@Transactional
public class ExamCommandService {
    public Exam createExam(CreateExamCommand command) { ... }
}

// Query (Read)
@Service
@Transactional(readOnly = true)
public class ExamQueryService {
    public List<ExamInfo> getExams(Long userId) { ... }
}
```

### Service Naming

**Requirements**:

- [ ] `{Domain}QueryService` for read operations
- [ ] `{Domain}CommandService` for write operations
- [ ] Or `{Domain}{Function}Service` (e.g., `ExamCreationService`)

## DTO Pattern

### Request DTO

**Requirements**:

- [ ] Lombok: `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- [ ] `@Schema` for API documentation
- [ ] Validation annotations (`@NotNull`, `@NotBlank`, `@Size`)
- [ ] Explicit constructor or builder pattern

**Correct Example**:

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExamRequest {
    @Schema(description = "사용자 ID", example = "1")
    @NotNull(message = "userId는 필수입니다.")
    private Long userId;

    @Schema(description = "모의고사 이름", example = "Java 기초 모의고사")
    @NotBlank(message = "examName은 필수입니다.")
    @Size(max = 100, message = "examName은 100자 이하여야 합니다.")
    private String examName;

    @Schema(description = "모의고사 유형", example = "MOCK")
    @NotBlank(message = "examType은 필수입니다.")
    private String examType;
}
```

### Response DTO

**Requirements**:

- [ ] Lombok: `@Getter`, `@Builder`
- [ ] `@Schema` for API documentation
- [ ] `public static from()` method for conversion
- [ ] Nested DTOs as static inner classes

**Correct Example**:

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResponse {
    @Schema(description = "모의고사 ID", example = "1")
    private Long id;

    @Schema(description = "모의고사 이름", example = "Java 기초 모의고사")
    private String examName;

    @Schema(description = "상태", example = "READY")
    private String status;

    // Static factory method
    public static ExamResponse from(ExamInfo info) {
        return ExamResponse.builder()
                .id(info.id())
                .examName(info.examName())
                .status(info.status())
                .build();
    }

    // Nested DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProblemSummary {
        private Long problemId;
        private String title;
    }
}
```

## Controller Pattern

### REST Controller

**Requirements**:

- [ ] `@RestController` annotation
- [ ] `@RequestMapping("/api/v1/{domain}")` with versioning
- [ ] `@Tag` for OpenAPI documentation
- [ ] `@Operation` for each endpoint
- [ ] `@ApiResponses` for response documentation
- [ ] Facade injection only (no Repository/Service directly)
- [ ] `ApiResponse<T>` wrapper

**Correct Example**:

```java
@RestController
@RequestMapping("/api/v1/exams")
@Tag(name = "모의고사", description = "모의고사 관리 API")
public class ExamController {
    private final ExamFacade examFacade;

    public ExamController(ExamFacade examFacade) {
        this.examFacade = examFacade;
    }

    @GetMapping("/{id}")
    @Operation(summary = "모의고사 조회", description = "ID로 모의고사를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "모의고사 없음")
    })
    public ResponseEntity<ApiResponse<ExamResponse>> getExam(
            @Parameter(description = "모의고사 ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(examFacade.getExam(id)));
    }
}
```

## Exception Handling

### Custom Exceptions

**Requirements**:

- [ ] Custom exceptions only
- [ ] No direct `IllegalArgumentException`/`IllegalStateException`
- [ ] Extend `DomainException` or appropriate base
- [ ] Factory methods for common cases
- [ ] `ErrorCode` mapping

**Correct Example**:

```java
public class ExamValidationException extends DomainException {
    public ExamValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ExamValidationException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    // Factory methods
    public static ExamValidationException examNotFound(Long examId) {
        return new ExamValidationException(ErrorCode.EXAM_NOT_FOUND,
                String.format("모의고사를 찾을 수 없습니다: %d", examId));
    }

    public static ExamValidationException invalidStatus(ExamStatus current, ExamStatus expected) {
        return new ExamValidationException(ErrorCode.INVALID_EXAM_STATUS,
                String.format("상태 변경 불가: %s → %s", current, expected));
    }
}
```

### HTTP Status Code Mapping

**Requirements**:

- [ ] 400: Input validation errors
- [ ] 403: Forbidden (ownership check)
- [ ] 404: Resource not found
- [ ] 409: Conflict (duplicate, state conflict)
- [ ] 422: Unprocessable entity (business logic violation)
- [ ] 500: Internal server error (unexpected)

## Common Issues Summary

| Area                 | Issue                             | Fix                        |
|----------------------|-----------------------------------|----------------------------|
| **Type Declaration** | `var` keyword                     | Use explicit types         |
| **Service**          | `@RequiredArgsConstructor`        | Write explicit constructor |
| **Service**          | Missing `@Transactional`          | Add for Command Service    |
| **Service**          | Missing `readOnly = true`         | Add for Query Service      |
| **DTO**              | Missing `@Schema`                 | Add for API documentation  |
| **DTO**              | No `from()` method                | Add static factory method  |
| **Controller**       | Direct Repository injection       | Inject Facade only         |
| **Controller**       | No `@Operation`                   | Add for each endpoint      |
| **Exception**        | Direct `IllegalArgumentException` | Use custom exception       |
| **Exception**        | No `ErrorCode` mapping            | Add ErrorCode              |