---
name: test-generator
description: Generate unit tests for all layers (Domain, Service, Facade, Controller, Common). Use when user asks to write tests, mentions test coverage, or references /test command. Automatically detects target type and generates appropriate tests.
---

# Test Generator

Generates unit tests for **all Kokpick layers** with appropriate coverage targets.

**Scope**: Domain, Service, Facade, Controller, Common (Exception, Enum, DTO, Util)
**Excludes**: E2E tests (use `cucumber-test-implementation` agent)

## Usage

```
/test {target}
```

**Examples**:

- `/test Exam` - Domain entity
- `/test ExamValidator` - Service class
- `/test ExamFacade` - Facade class
- `/test ExamController` - Controller class
- `/test ExamValidationException` - Exception class
- `/test AnswerStatus` - Enum class
- `/test ExamResponse` - DTO class

**Module-level**:

- `/test kokpick-exam` - single domain module
- `/test kokpick-service-api` - application module
- `/test kokpick-common` - common module
- `/test kokpick-domains` - all domains (parallel)

## Workflow

**🚀 START TEST-GENERATOR SKILL**

### 1. Identify Target Type & Test Style

**Domain Entity** (Classist): Aggregate Root, Child Entity, Value Object
**Service Class** (Mockist): Query Service, Command Service, Validator
**Facade Class** (Mockist): DTO conversion + Service orchestration
**Controller Class** (Mockist): REST API endpoints, Request validation
**Common Class** (Classist): Exception, Enum, DTO, Util

**Skip**: E2E test → Use `cucumber-test-implementation` agent

### 2. Dependency Analysis

**Single module/class**: Proceed to step 3

**All domains**: Analyze module dependencies

- **Level 1 (parallel)**: exam, problem, user, curriculum, quiz
- **Level 2 (sequential)**: user-study (depends on exam, problem, user)

Use **sub-agents** for parallel processing.

### 3. Generate Checklist

For each target, create test cases in:

- ✅ Core functionality (Happy Path)
- ⚠️ Exception cases
- 🔍 Edge cases
- 🎯 Layer-specific cases

### 4. Write Tests

Follow CLAUDE.md test strategy:

#### Domain Entity (Classist)

- JUnit 5 + AssertJ
- No Spring Context (`@SpringBootTest`, `@DataJpaTest` prohibited)
- No mocks (`@Mock`, `Mockito.mock()` prohibited)
- Create actual objects directly
- State verification with `assertThat()`
- **No setter** (테스트용 setter 추가 금지)
- **No reflection** (리플렉션으로 필드 설정 금지)
- **Use test constructors** (테스트용 생성자로 ID 등 필드 설정)

**Examples** → `references/domain-tests.md`

**Coverage target**: 85%+

#### Service/Facade/Controller (Mockist)

- `@ExtendWith(MockitoExtension.class)`
- `@Mock` for dependencies
- `when().thenReturn()` for behavior setup
- `verify().method()` for behavior verification

**Examples** → `references/service-tests.md`

**Coverage target**: 70%+

#### Common Classes (Classist)

- JUnit 5 + AssertJ
- No Spring Context
- No mocks
- Pure Java tests

**Examples** → `references/common-tests.md`

**Coverage target**: 70%+

### 5. Test Style (All Layers)

- **Given-When-Then** structure
- **@DisplayName** in Korean
- Descriptive test method names
- AssertJ assertions

### 6. Commit Strategy

Commit per feature (all layers together):

```
test: {feature} 테스트

examples:
test: 모의고사 채점 기능 테스트
test: 사용자 인증 기능 테스트
test: 문제 추천 기능 테스트
```

When using sub-agents for parallel processing, each agent commits independently.

### 7. Verify

```bash
./gradlew test
./gradlew jacocoTestReport
```

**Coverage targets** → `references/coverage.md`

---

**✅ END TEST-GENERATOR SKILL**

## Quick Reference

### Domain Tests

**Entity patterns, examples** → `references/domain-tests.md`

### Service/Facade/Controller Tests

**Mock patterns, examples** → `references/service-tests.md`

### Common Tests

**Exception, Enum, DTO, Util** → `references/common-tests.md`

### Coverage Targets

**Layer-specific targets, current status** → `references/coverage.md`

## Related Agents

- `cucumber-test-implementation` - E2E tests (Cucumber + RestAssured + TestContainers)
