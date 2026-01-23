# Common Classes Tests (Classist Style)

## Principles

- **No Spring Context**: Pure Java tests
- **No Mocks**: Test actual behavior
- **Simple Verification**: Assert state/values
- **Factory Methods**: Test static factory methods

## Exception Test

```java
package com.kokpick.exam.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@DisplayName("ExamValidationException 테스트")
class ExamValidationExceptionTest {

    @Test
    @DisplayName("examNotFound 팩토리 메서드")
    void examNotFound() {
        // Given
        Long examId = 1L;

        // When
        ExamValidationException exception =
                ExamValidationException.examNotFound(examId);

        // Then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.EXAM_NOT_FOUND);
        assertThat(exception.getMessage())
                .contains("모의고사를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("invalidStatus 팩토리 메서드")
    void invalidStatus() {
        // Given
        ExamStatus current = ExamStatus.COMPLETED;
        ExamStatus expected = ExamStatus.IN_PROGRESS;

        // When
        ExamValidationException exception =
                ExamValidationException.invalidStatus(current, expected);

        // Then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.INVALID_EXAM_STATUS);
        assertThat(exception.getMessage())
                .contains("상태 변경 불가");
    }

    @Test
    @DisplayName("tooManyProblems 팩토리 메서드")
    void tooManyProblems() {
        // Given
        int currentCount = 50;
        int maxCount = 50;

        // When
        ExamValidationException exception =
                ExamValidationException.tooManyProblems(currentCount, maxCount);

        // Then
        assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.EXAM_PROBLEM_COUNT_EXCEEDED);
        assertThat(exception.getMessage())
                .contains("최대 문제 수 초과");
    }

    @Test
    @DisplayName("커스텀 메시지 포함 예외")
    void withCustomMessage() {
        // Given
        ErrorCode errorCode = ErrorCode.INVALID_EXAM_STATUS;
        String customMessage = "사용자 정의 메시지";

        // When
        ExamValidationException exception =
                new ExamValidationException(errorCode, customMessage);

        // Then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }
}
```

## Enum Test

```java
package com.kokpick.problem.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.assertj.core.api.Assertions.*;

@DisplayName("AnswerStatus 테스트")
class AnswerStatusTest {

    @Test
    @DisplayName("isNotAttempted 메서드")
    void isNotAttempted() {
        assertThat(AnswerStatus.NOT_ATTEMPTED.isNotAttempted()).isTrue();
        assertThat(AnswerStatus.CORRECT.isNotAttempted()).isFalse();
        assertThat(AnswerStatus.INCORRECT.isNotAttempted()).isFalse();
    }

    @Test
    @DisplayName("isCorrect 메서드")
    void isCorrect() {
        assertThat(AnswerStatus.CORRECT.isCorrect()).isTrue();
        assertThat(AnswerStatus.INCORRECT.isCorrect()).isFalse();
        assertThat(AnswerStatus.NOT_ATTEMPTED.isCorrect()).isFalse();
    }

    @Test
    @DisplayName("isIncorrect 메서드")
    void isIncorrect() {
        assertThat(AnswerStatus.INCORRECT.isIncorrect()).isTrue();
        assertThat(AnswerStatus.CORRECT.isIncorrect()).isFalse();
        assertThat(AnswerStatus.NOT_ATTEMPTED.isIncorrect()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(AnswerStatus.class)
    @DisplayName("모든 AnswerStatus 값은 null이 아님")
    void allValuesNotNull(AnswerStatus status) {
        assertThat(status).isNotNull();
    }

    @Test
    @DisplayName("valueOf 메서드 - 유효한 값")
    void valueOf_Valid() {
        assertThat(AnswerStatus.valueOf("CORRECT")).isEqualTo(AnswerStatus.CORRECT);
        assertThat(AnswerStatus.valueOf("INCORRECT")).isEqualTo(AnswerStatus.INCORRECT);
        assertThat(AnswerStatus.valueOf("NOT_ATTEMPTED"))
                .isEqualTo(AnswerStatus.NOT_ATTEMPTED);
    }

    @Test
    @DisplayName("valueOf 메서드 - 잘못된 값")
    void valueOf_Invalid() {
        assertThatThrownBy(() -> AnswerStatus.valueOf("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
```

## DTO Test (Request)

```java
package com.kokpick.exam.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;

@DisplayName("CreateExamRequest 테스트")
class CreateExamRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("유효한 요청 생성")
    void create_ValidRequest() {
        // Given
        Long userId = 1L;
        String examName = "Java 모의고사";
        String examType = "MOCK";

        // When
        CreateExamRequest request = new CreateExamRequest(userId, examName, examType);

        // Then
        assertThat(request.getUserId()).isEqualTo(userId);
        assertThat(request.getExamName()).isEqualTo(examName);
        assertThat(request.getExamType()).isEqualTo(examType);
    }

    @Test
    @DisplayName("유효성 검사 - userId가 null")
    void validation_UserIdNull() {
        // Given
        CreateExamRequest request = new CreateExamRequest(null, "모의고사", "MOCK");

        // When
        Set<ConstraintViolation<CreateExamRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("userId"));
    }

    @Test
    @DisplayName("유효성 검사 - examName이 비어있음")
    void validation_ExamNameBlank() {
        // Given
        CreateExamRequest request = new CreateExamRequest(1L, "", "MOCK");

        // When
        Set<ConstraintViolation<CreateExamRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("examName"));
    }

    @Test
    @DisplayName("Builder 패턴으로 생성")
    void builder_Success() {
        // Given & When
        CreateExamRequest request = CreateExamRequest.builder()
                .userId(1L)
                .examName("Java 모의고사")
                .examType("MOCK")
                .build();

        // Then
        assertThat(request.getUserId()).isEqualTo(1L);
        assertThat(request.getExamName()).isEqualTo("Java 모의고사");
        assertThat(request.getExamType()).isEqualTo("MOCK");
    }
}
```

## DTO Test (Response)

```java
@DisplayName("ExamResponse 테스트")
class ExamResponseTest {

    @Test
    @DisplayName("from 메서드로 DTO 변환")
    void from_Success() {
        // Given
        ExamInfo info = new ExamInfo(1L, "Java 모의고사", "MOCK", "READY");

        // When
        ExamResponse response = ExamResponse.from(info);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getExamName()).isEqualTo("Java 모의고사");
        assertThat(response.getExamType()).isEqualTo("MOCK");
        assertThat(response.getStatus()).isEqualTo("READY");
    }

    @Test
    @DisplayName("Builder 패턴으로 생성")
    void builder_Success() {
        // Given & When
        ExamResponse response = ExamResponse.builder()
                .id(1L)
                .examName("Java 모의고사")
                .examType("MOCK")
                .status("READY")
                .problemCount(0)
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getExamName()).isEqualTo("Java 모의고사");
        assertThat(response.getExamType()).isEqualTo("MOCK");
        assertThat(response.getStatus()).isEqualTo("READY");
        assertThat(response.getProblemCount()).isZero();
    }

    @Test
    @DisplayName("중첩 DTO 변환")
    void from_WithNestedDto() {
        // Given
        SubmitAnswerInfo info = new SubmitAnswerInfo(
                1L,
                new CorrectAnswerInfo(List.of(1, 3))
        );

        // When
        SubmitAnswerResponse response = SubmitAnswerResponse.from(info);

        // Then
        assertThat(response.getUserAnswerId()).isEqualTo(1L);
        assertThat(response.getCorrectAnswer().getCorrectChoiceNumbers())
                .isEqualTo(List.of(1, 3));
    }
}
```

## Util Class Test

```java
@DisplayName("DateTimeUtil 테스트")
class DateTimeUtilTest {

    @Test
    @DisplayName("현재 시간 반환")
    void now_Success() {
        // When
        LocalDateTime now = DateTimeUtil.now();

        // Then
        assertThat(now).isNotNull();
        assertThat(now).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("날짜 범위 확인 - 범위 내")
    void isBetweenRange_Within() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);
        LocalDateTime target = LocalDateTime.of(2024, 6, 15, 12, 0);

        // When
        boolean result = DateTimeUtil.isBetween(target, start, end);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("날짜 범위 확인 - 범위 밖")
    void isBetweenRange_Outside() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);
        LocalDateTime target = LocalDateTime.of(2023, 12, 31, 23, 59);

        // When
        boolean result = DateTimeUtil.isBetween(target, start, end);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("시간 차이 계산 - 분 단위")
    void differenceInMinutes_Success() {
        // Given
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 1, 10, 30);

        // When
        long minutes = DateTimeUtil.differenceInMinutes(start, end);

        // Then
        assertThat(minutes).isEqualTo(30);
    }
}
```

## Test Rules Summary

| Type               | Focus                                | Example                             |
|--------------------|--------------------------------------|-------------------------------------|
| **Exception**      | Factory methods, ErrorCode, messages | `examNotFound()`, `invalidStatus()` |
| **Enum**           | Values, methods, valueOf             | `isCorrect()`, `valueOf("CORRECT")` |
| **DTO (Request)**  | Validation, Builder pattern          | `@Valid`, `validator.validate()`    |
| **DTO (Response)** | from() method, nested DTO            | `ExamResponse.from(info)`           |
| **Util**           | Static methods, edge cases           | `DateTimeUtil.now()`, `isBetween()` |

## Coverage Target

**Common: 70%+**

Test all:

- ✅ Public static methods
- ✅ Factory methods (Exception)
- ✅ Enum values and methods
- ✅ DTO conversions (from method)
- ✅ Validation rules (Request DTO)
- ✅ Util helper methods