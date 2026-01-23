# Service/Facade/Controller Tests (Mockist Style)

## Principles

- **MockitoExtension**: Use `@ExtendWith(MockitoExtension.class)`
- **Mock Dependencies**: Use `@Mock` for all dependencies
- **Behavior Setup**: Use `when().thenReturn()` for mock behavior
- **Behavior Verification**: Use `verify()` for method call verification
- **Test Double**: Isolate the unit under test

## Service Test (Query/Command Service)

```java
package com.kokpick.exam.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("ExamCommandService 테스트")
@ExtendWith(MockitoExtension.class)
class ExamCommandServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private ExamValidator examValidator;

    @InjectMocks
    private ExamCommandService examCommandService;

    @Test
    @DisplayName("모의고사 생성 - 성공")
    void createExam_Success() {
        // Given
        Long userId = 1L;
        String examName = "Java 모의고사";
        CreateExamCommand command = new CreateExamCommand(userId, examName, ExamType.MOCK);

        Exam savedExam = Exam.create(userId, examName, ExamType.MOCK);
        when(examRepository.save(any(Exam.class))).thenReturn(savedExam);

        // When
        CreateExamInfo info = examCommandService.createExam(command);

        // Then
        assertThat(info.examId()).isEqualTo(savedExam.getId());
        assertThat(info.examName()).isEqualTo(examName);
        verify(examRepository).save(any(Exam.class));
    }

    @Test
    @DisplayName("모의고사 생성 - 실패 (검증 오류)")
    void createExam_Fail_ValidationError() {
        // Given
        Long userId = 1L;
        CreateExamCommand command = new CreateExamCommand(userId, "", ExamType.MOCK);

        doThrow(ExamValidationException.class)
                .when(examValidator).validateCreateExam(command);

        // When & Then
        assertThatThrownBy(() -> examCommandService.createExam(command))
                .isInstanceOf(ExamValidationException.class);
        verify(examValidator).validateCreateExam(command);
        verify(examRepository, never()).save(any(Exam.class));
    }

    @Test
    @DisplayName("모의고사 시작 - 성공")
    void startExam_Success() {
        // Given
        Long examId = 1L;
        Long userId = 1L;
        Exam exam = Exam.create(userId, "Java 모의고사", ExamType.MOCK);

        when(examValidator.validateOwnershipAndGetExam(examId, userId))
                .thenReturn(exam);
        when(examRepository.save(exam)).thenReturn(exam);

        // When
        examCommandService.startExam(examId, userId);

        // Then
        assertThat(exam.getStatus()).isEqualTo(ExamStatus.IN_PROGRESS);
        verify(examValidator).validateOwnershipAndGetExam(examId, userId);
        verify(examRepository).save(exam);
    }

    @Test
    @DisplayName("모의고사 완료 - 성공")
    void completeExam_Success() {
        // Given
        Long examId = 1L;
        Long userId = 1L;
        Exam exam = Exam.create(userId, "Java 모의고사", ExamType.MOCK);
        exam.start();

        when(examValidator.validateOwnershipAndGetExam(examId, userId))
                .thenReturn(exam);
        when(examRepository.save(exam)).thenReturn(exam);

        // When
        examCommandService.completeExam(examId, userId);

        // Then
        assertThat(exam.getStatus()).isEqualTo(ExamStatus.COMPLETED);
        verify(examValidator).validateOwnershipAndGetExam(examId, userId);
        verify(examRepository).save(exam);
    }
}
```

## Query Service Test

```java
@DisplayName("ExamQueryService 테스트")
@ExtendWith(MockitoExtension.class)
class ExamQueryServiceTest {

    @Mock
    private ExamRepository examRepository;

    @InjectMocks
    private ExamQueryService examQueryService;

    @Test
    @DisplayName("모의고사 조회 - 성공")
    void getExam_Success() {
        // Given
        Long examId = 1L;
        Exam exam = Exam.create(1L, "Java 모의고사", ExamType.MOCK);

        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        // When
        ExamInfo info = examQueryService.getExam(examId);

        // Then
        assertThat(info.id()).isEqualTo(examId);
        assertThat(info.examName()).isEqualTo("Java 모의고사");
        verify(examRepository).findById(examId);
    }

    @Test
    @DisplayName("모의고사 조회 - 실패 (찾을 수 없음)")
    void getExam_Fail_NotFound() {
        // Given
        Long examId = 999L;
        when(examRepository.findById(examId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> examQueryService.getExam(examId))
                .isInstanceOf(ExamNotFoundException.class);
        verify(examRepository).findById(examId);
    }

    @Test
    @DisplayName("사용자의 모든 모의고사 조회")
    void getExamsByUser_Success() {
        // Given
        Long userId = 1L;
        List<Exam> exams = List.of(
                Exam.create(userId, "모의고사 1", ExamType.MOCK),
                Exam.create(userId, "모의고사 2", ExamType.MOCK)
        );

        when(examRepository.findByUserId(userId)).thenReturn(exams);

        // When
        List<ExamInfo> infos = examQueryService.getExamsByUser(userId);

        // Then
        assertThat(infos).hasSize(2);
        verify(examRepository).findByUserId(userId);
    }
}
```

## Facade Test

```java
@DisplayName("ExamFacade 테스트")
@ExtendWith(MockitoExtension.class)
class ExamFacadeTest {

    @Mock
    private ExamQueryService examQueryService;

    @Mock
    private ExamCommandService examCommandService;

    @InjectMocks
    private ExamFacade examFacade;

    @Test
    @DisplayName("모의고사 조회 - 성공")
    void getExam_Success() {
        // Given
        Long examId = 1L;
        ExamInfo info = new ExamInfo(examId, "Java 모의고사", "MOCK", "READY");

        when(examQueryService.getExam(examId)).thenReturn(info);

        // When
        ExamResponse response = examFacade.getExam(examId);

        // Then
        assertThat(response.getId()).isEqualTo(examId);
        assertThat(response.getExamName()).isEqualTo("Java 모의고사");
        verify(examQueryService).getExam(examId);
    }

    @Test
    @DisplayName("모의고사 생성 - 성공")
    void createExam_Success() {
        // Given
        CreateExamRequest request = new CreateExamRequest(1L, "Java 모의고사", "MOCK");
        CreateExamInfo info = new CreateExamInfo(1L, "Java 모의고사");

        when(examCommandService.createExam(any(CreateExamCommand.class)))
                .thenReturn(info);

        // When
        CreateExamResponse response = examFacade.createExam(request);

        // Then
        assertThat(response.getExamId()).isEqualTo(1L);
        verify(examCommandService).createExam(any(CreateExamCommand.class));
    }
}
```

## Controller Test

```java
@DisplayName("ExamController 테스트")
@ExtendWith(MockitoExtension.class)
class ExamControllerTest {

    @Mock
    private ExamFacade examFacade;

    @InjectMocks
    private ExamController examController;

    @Test
    @DisplayName("모의고사 조회 - 성공")
    void getExam_Success() {
        // Given
        Long examId = 1L;
        ExamResponse response = ExamResponse.builder()
                .id(examId)
                .examName("Java 모의고사")
                .status("READY")
                .build();

        when(examFacade.getExam(examId)).thenReturn(response);

        // When
        ResponseEntity<ApiResponse<ExamResponse>> result =
                examController.getExam(examId);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getData().getExamName()).isEqualTo("Java 모의고사");
        verify(examFacade).getExam(examId);
    }

    @Test
    @DisplayName("모의고사 생성 - 성공")
    void createExam_Success() {
        // Given
        CreateExamRequest request = new CreateExamRequest(1L, "Java 모의고사", "MOCK");
        CreateExamResponse response = CreateExamResponse.builder()
                .examId(1L)
                .examName("Java 모의고사")
                .build();

        when(examFacade.createExam(request)).thenReturn(response);

        // When
        ResponseEntity<ApiResponse<CreateExamResponse>> result =
                examController.createExam(request);

        // Then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody().getData().getExamId()).isEqualTo(1L);
        verify(examFacade).createExam(request);
    }
}
```

## Test Rules Summary

| Rule                    | Description                  | Example                                                       |
|-------------------------|------------------------------|---------------------------------------------------------------|
| **@ExtendWith**         | Use MockitoExtension         | `@ExtendWith(MockitoExtension.class)`                         |
| **@Mock**               | Mock all dependencies        | `@Mock private ExamRepository repository;`                    |
| **@InjectMocks**        | Inject mocks into test class | `@InjectMocks private ExamService service;`                   |
| **when().thenReturn()** | Setup mock behavior          | `when(repository.findById(1L)).thenReturn(Optional.of(exam))` |
| **verify()**            | Verify method calls          | `verify(repository).findById(1L)`                             |
| **never()**             | Verify method not called     | `verify(repository, never()).delete(any())`                   |
| **any()**               | Match any argument           | `when(repository.save(any(Exam.class)))`                      |
| **eq()**                | Match exact argument         | `when(repository.findById(eq(1L)))`                           |

## Coverage Target

**Service/Facade/Controller: 70%+**

Test all:

- ✅ Public methods
- ✅ Happy paths
- ✅ Exception handling
- ✅ Null/empty inputs
- ✅ Integration with dependencies (via mocks)