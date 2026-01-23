# Domain Entity Tests (Classist Style)

## Principles

- **No Spring Context**: `@SpringBootTest`, `@DataJpaTest` prohibited
- **No Mocks**: `@Mock`, `Mockito.mock()` prohibited
- **Real Objects**: Create actual entities directly
- **State Verification**: Use `assertThat()` for state assertions
- **Pure Java**: Test framework-independent behavior

## Aggregate Root Test

```java
package com.kokpick.exam.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("Exam 도메인 테스트")
class ExamTest {

    @Test
    @DisplayName("정상적인 모의고사 생성")
    void create_Success() {
        // Given
        Long userId = 1L;
        String examName = "Java 기초 모의고사";
        ExamType examType = ExamType.MOCK;

        // When
        Exam exam = Exam.create(userId, examName, examType);

        // Then
        assertThat(exam.getUserId()).isEqualTo(userId);
        assertThat(exam.getExamName()).isEqualTo(examName);
        assertThat(exam.getExamType()).isEqualTo(examType);
        assertThat(exam.getStatus()).isEqualTo(ExamStatus.READY);
        assertThat(exam.getProblemCount()).isZero();
    }

    @Test
    @DisplayName("모의고사 문제 추가 - 성공")
    void addProblem_Success() {
        // Given
        Exam exam = Exam.create(1L, "Java 모의고사", ExamType.MOCK);
        ExamProblem problem = ExamProblem.builder()
                .problemId(100L)
                .examProblemOrder(1)
                .build();

        // When
        exam.addExamProblem(problem);

        // Then
        assertThat(exam.getProblemCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("모의고사 문제 추가 - 실패 (최대 개수 초과)")
    void addProblem_Fail_ExceedsMaxProblems() {
        // Given
        Exam exam = Exam.create(1L, "Java 모의고사", ExamType.MOCK);
        for (int i = 0; i < 50; i++) {
            exam.addExamProblem(ExamProblem.builder()
                    .problemId((long) i)
                    .examProblemOrder(i + 1)
                    .build());
        }
        ExamProblem extraProblem = ExamProblem.builder()
                .problemId(100L)
                .examProblemOrder(51)
                .build();

        // When & Then
        assertThatThrownBy(() -> exam.addExamProblem(extraProblem))
                .isInstanceOf(ExamValidationException.class)
                .hasMessageContaining("최대 문제 수 초과");
    }

    @Test
    @DisplayName("모의고사 시작 - 상태 변경")
    void start_Success() {
        // Given
        Exam exam = Exam.create(1L, "Java 모의고사", ExamType.MOCK);

        // When
        exam.start();

        // Then
        assertThat(exam.getStatus()).isEqualTo(ExamStatus.IN_PROGRESS);
        assertThat(exam.getStartedAt()).isNotNull();
    }

    @Test
    @DisplayName("모의고사 완료 - 도메인 이벤트 발행")
    void complete_Success_PublishesEvent() {
        // Given
        Exam exam = Exam.create(1L, "Java 모의고사", ExamType.MOCK);
        exam.start();

        // When
        exam.complete();

        // Then
        assertThat(exam.getStatus()).isEqualTo(ExamStatus.COMPLETED);
        assertThat(exam.getCompletedAt()).isNotNull();
        // Note: Domain events are tested in integration tests
    }
}
```

## Child Entity Test

```java
@DisplayName("ExamProblem 도메인 테스트")
class ExamProblemTest {

    @Test
    @DisplayName("ExamProblem 생성")
    void create_Success() {
        // Given
        Long examId = 1L;
        Long problemId = 100L;
        Integer order = 1;

        // When
        ExamProblem examProblem = ExamProblem.builder()
                .examId(examId)
                .problemId(problemId)
                .examProblemOrder(order)
                .build();

        // Then
        assertThat(examProblem.getExamId()).isEqualTo(examId);
        assertThat(examProblem.getProblemId()).isEqualTo(problemId);
        assertThat(examProblem.getExamProblemOrder()).isEqualTo(order);
    }

    @Test
    @DisplayName("순서 할당 - 성공")
    void fixOrder_Success() {
        // Given
        ExamProblem examProblem = ExamProblem.builder()
                .examId(1L)
                .problemId(100L)
                .build();

        // When
        examProblem.fixExamProblemOrder(1);

        // Then
        assertThat(examProblem.getExamProblemOrder()).isEqualTo(1);
    }

    @Test
    @DisplayName("순서 할당 - 실패 (이미 할당됨)")
    void fixOrder_Fail_AlreadyAssigned() {
        // Given
        ExamProblem examProblem = ExamProblem.builder()
                .examId(1L)
                .problemId(100L)
                .examProblemOrder(1)
                .build();

        // When & Then
        assertThatThrownBy(() -> examProblem.fixExamProblemOrder(2))
                .isInstanceOf(ExamValidationException.class)
                .hasMessageContaining("이미 순서가 할당됨");
    }

    @Test
    @DisplayName("첫 번째 문제 확인")
    void isFirstProblem_Success() {
        // Given
        ExamProblem first = ExamProblem.builder()
                .examId(1L)
                .problemId(100L)
                .examProblemOrder(1)
                .build();

        // When
        boolean isFirst = first.isFirstProblem();

        // Then
        assertThat(isFirst).isTrue();
    }
}
```

## Value Object Test (Record)

```java
@DisplayName("CorrectAnswerInfo 테스트")
class CorrectAnswerInfoTest {

    @Test
    @DisplayName("정답 정보 생성")
    void create_Success() {
        // Given
        List<Integer> correctChoices = List.of(1, 3);

        // When
        CorrectAnswerInfo info = new CorrectAnswerInfo(correctChoices);

        // Then
        assertThat(info.correctChoiceNumbers()).isEqualTo(correctChoices);
    }

    @Test
    @DisplayName("단일 정답인지 확인")
    void isSingleChoice_Success() {
        // Given
        CorrectAnswerInfo single = new CorrectAnswerInfo(List.of(2));
        CorrectAnswerInfo multiple = new CorrectAnswerInfo(List.of(1, 3));

        // When & Then
        assertThat(single.isSingleChoice()).isTrue();
        assertThat(multiple.isSingleChoice()).isFalse();
    }

    @Test
    @DisplayName("정답 포함 여부 확인")
    void contains_Success() {
        // Given
        CorrectAnswerInfo info = new CorrectAnswerInfo(List.of(1, 3, 5));

        // When & Then
        assertThat(info.contains(1)).isTrue();
        assertThat(info.contains(2)).isFalse();
        assertThat(info.contains(3)).isTrue();
    }
}
```

## Test Rules Summary

| Rule                    | Description                          | Example                                         |
|-------------------------|--------------------------------------|-------------------------------------------------|
| **No Spring**           | Don't use `@SpringBootTest`          | Use `new Exam()` directly                       |
| **No Mocks**            | Don't use `@Mock`, `Mockito.mock()`  | Create real objects                             |
| **No Setter**           | Don't add test-only setters          | Use test constructors                           |
| **No Reflection**       | Don't use reflection for field setup | Use test constructors                           |
| **State Verification**  | Use `assertThat()`                   | `assertThat(exam.getStatus()).isEqualTo(READY)` |
| **Given-When-Then**     | Structure test clearly               | Comment each section                            |
| **Descriptive Names**   | Method names explain behavior        | `create_Success`, `addProblem_Fail_ExceedsMax`  |
| **Korean @DisplayName** | Korean display names                 | `@DisplayName("모의고사 생성 성공")`                    |

## Test Constructor Pattern

When entities use `@GeneratedValue` for ID (cannot be set via Builder):

**Entity** (package-private test constructor):

```java
@Entity
public class ExamProblem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder
    public ExamProblem(Long problemId, Integer examProblemOrder) {
        this.problemId = problemId;
        this.examProblemOrder = examProblemOrder;
    }

    // 테스트용 생성자 (ID 포함)
    ExamProblem(Long id, Long problemId, Integer examProblemOrder) {
        this.id = id;
        this.problemId = problemId;
        this.examProblemOrder = examProblemOrder;
    }
}
```

**Test**:

```java
@Test
@DisplayName("ID 기준 정렬 테스트")
void testWithId() {
    // Given: 테스트용 생성자로 ID 설정
    ExamProblem ep1 = new ExamProblem(1L, 100L, null);
    ExamProblem ep2 = new ExamProblem(2L, 200L, 1);

    // When & Then
    // ... test logic
}
```

## Coverage Target

**Domain: 85%+**

Test all:

- ✅ Public methods
- ✅ Business logic branches
- ✅ Validation rules
- ✅ State transitions
- ✅ Exception conditions
- ✅ Edge cases (null, empty, boundaries)