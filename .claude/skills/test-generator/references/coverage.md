# Test Coverage Targets

## Overall Targets

| Layer          | Target | Current | Status               |
|----------------|--------|---------|----------------------|
| **Domain**     | 85%+   | ~60%    | ⚠️ Needs improvement |
| **Service**    | 70%+   | ~50%    | ⚠️ Needs improvement |
| **Facade**     | 70%+   | ~15%    | ❌ Critical           |
| **Controller** | 70%+   | ~15%    | ❌ Critical           |
| **Common**     | 70%+   | 0%      | ❌ Not started        |
| **Overall**    | 80%+   | ~50%    | ❌ Critical           |

## Module Breakdown

### kokpick-domains

| Module             | Domain                            | Current | Target | Gap  |
|--------------------|-----------------------------------|---------|--------|------|
| kokpick-exam       | Exam, ExamProblem, ExamUserAnswer | ~55%    | 85%    | -30% |
| kokpick-quiz       | Quiz, QuizProblem, QuizUserAnswer | ~60%    | 85%    | -25% |
| kokpick-problem    | Problem, Answer, Choice           | ~65%    | 85%    | -20% |
| kokpick-user       | User, UserHistory                 | ~50%    | 85%    | -35% |
| kokpick-curriculum | Course, Chapter, Theory           | ~70%    | 85%    | -15% |

### kokpick-applications

| Module              | Service/Facade/Controller | Current | Target | Gap  |
|---------------------|---------------------------|---------|--------|------|
| kokpick-service-api | All layers                | ~15%    | 70%    | -55% |
| kokpick-admin-api   | All layers                | ~20%    | 70%    | -50% |

### kokpick-common

| Component | Current | Target | Gap  |
|-----------|---------|--------|------|
| Exception | 0%      | 70%    | -70% |
| Enum      | 0%      | 70%    | -70% |
| DTO       | 0%      | 70%    | -70% |
| Util      | 0%      | 70%    | -70% |

## Priority Areas

### P0 (Critical - Immediate Action)

1. **Exception Coverage** - All custom exceptions need tests
    - ExamValidationException
    - QuizValidationException
    - ProblemAnswerValidationException
    - AuthException
    - StorageValidationException

2. **Facade Coverage** - Thin layer, quick to test
    - ExamFacade
    - QuizFacade
    - ProblemFacade
    - UserFacade

3. **Controller Coverage** - API endpoints need verification
    - ExamController
    - QuizController
    - ProblemController
    - UserController

### P1 (High - This Sprint)

4. **Enum Coverage** - All enums with business logic
    - AnswerStatus
    - ExamStatus
    - QuizStatus
    - UserStatus
    - ProblemType

5. **Service Coverage** - Query/Command services
    - ExamQueryService, ExamCommandService
    - QuizQueryService, QuizCommandService
    - ProblemQueryService, ProblemCommandService

### P2 (Medium - Next Sprint)

6. **Domain Entity Coverage** - Core business logic
    - Exam aggregate (currently 55%)
    - Quiz aggregate (currently 60%)
    - User aggregate (currently 50%)

7. **DTO Coverage** - Request/Response conversions
    - All Request DTOs (validation)
    - All Response DTOs (from() methods)

## Coverage Verification

### Run Tests

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :kokpick-exam:test
./gradlew :kokpick-service-api:test

# Run with coverage
./gradlew jacocoTestReport
```

### View Coverage Report

```bash
# HTML report
open build/reports/jacoco/test/html/index.html

# CLI summary
./gradlew jacocoTestReport --console=plain
```

## Coverage Best Practices

### What to Test

✅ **DO Test**:

- Public methods
- Business logic
- Validation rules
- State transitions
- Exception conditions
- Edge cases (null, empty, boundaries)

❌ **DON'T Test**:

- Private methods (test via public methods)
- Lombok-generated methods (getters, setters, builders)
- Third-party libraries
- Java/Kotlin standard library
- Framework behavior (Spring, JPA)

### Coverage Quality

> **Note**: 100% coverage ≠ 100% quality

Focus on:

- **Meaningful tests** that verify behavior
- **Business logic** correctness
- **Error handling** completeness
- **Edge cases** coverage

Avoid:

- Tests that only pass to increase coverage
- Tests that don't verify meaningful behavior
- Redundant tests that verify the same thing

## Incremental Improvement Strategy

### Week 1: Common Tests

- Day 1-2: All Exception tests (70%+)
- Day 3-4: All Enum tests (70%+)
- Day 5: Util class tests (70%+)

### Week 2-3: Application Tests

- Facade tests (70%+)
- Controller tests (70%+)
- Service tests (70%+)

### Week 4-6: Domain Tests

- Exam aggregate (85%+)
- Quiz aggregate (85%+)
- Problem aggregate (85%+)
- User aggregate (85%+)

## Sub-Agent Strategy for Parallel Processing

For `kokpick-domains` testing:

**Level 1 (Parallel)**:

- Agent 1: kokpick-exam tests
- Agent 2: kokpick-quiz tests
- Agent 3: kokpick-problem tests
- Agent 4: kokpick-user tests
- Agent 5: kokpick-curriculum tests

**Level 2 (Sequential)**:

- Agent 6: kokpick-user-study tests (after dependencies complete)

Each agent:

1. Generates tests for its module
2. Runs tests locally
3. Commits independently to avoid conflicts
4. Reports coverage status