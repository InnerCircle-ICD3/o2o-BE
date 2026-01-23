# DDD 요구사항 자체 평가 체크리스트

요구사항을 작성한 후 이 체크리스트로 스스로 평가해 보세요.

## 평가 대상 구분

요구사항 형식에 따라 평가 체크리스트가 다릅니다:

| 요구사항 형식 | 평가 체크리스트 |
|-------------|---------|
| **체크리스트 형식** | 체크리스트 형식 자체 평가 (아래) |
| **PRD/기능 명세 형식** | 2차원 평가 체크리스트 (하단 참조) |

---

# 체크리스트 형식 요구사항 작성 가이드

## YAGNI 원칙 (You Aren't Gonna Need It)

- **단일 플로우만 작성**: 한 번에 하나의 플로우에 집중
- **미래의 기능 제외**: 환불, 갱신 등은 별도 플로우로 작성
- **현재 필요한 것만**: MVP(Minimal Viable Product) 관점

## 백엔드 중심

- **프론트엔드 요구사항 제외**: iOS Client(StoreKit), 결제 시트, UI 등은 프론트 담당
- **외부 API 포함**: 백엔드가 소통하는 Apple App Store Server API는 포함
- **백엔드 처리**: 검증 요청, 상태 변경, DB 저장

## 도메인별 독립성

- **payment**: 영수증 검증, 상태 변경, 불변식
- **subscription**: 구독 생성, 상태 관리, 만료 처리
- **exam**: 모의고사 생성, 답안 제출, 채점

## 도메인별 필수 항목 예시

### Payment Domain (결제 검증)
필수: 영수증 수신, API 호출, 상태 변경, 불변식(transactionId 유일성)
YAGNI 위험: 환불 처리, 정기 결제 갱신

### Subscription Domain (구독 생성)
필수: 구독 생성, 상태 설정(ACTIVE), 불변식(만료 시간, transactionId 유일성)
YAGNI 위험: 구독 갱신, 구독 취소

### Exam Domain (모의고사 생성)
필수: 모의고사 생성, 문제 추가, 상태 변경(NOT_STARTED → IN_PROGRESS)
YAGNI 위험: 모의고사 재시작, 모의고사 삭제

---

# 체크리스트 형식 자체 평가 (1차원만)

체크리스트 형식 요구사항은 1차원(체크리스트 완결성)만 평가합니다.

## 필수 항목 (5점)

- [ ] **도메인 식별 (1점)**
  - 도메인이 명확히 식별되었나요?
  - 예: payment, subscription, exam
  - 주의: "결제"처럼 구체적이지 않은 표현 금지

- [ ] **YAGNI 원칙 (1점)**
  - 현재 플로우에 필요한 요구사항만 포함되었나요?
  - 예: 결제 검증 플로우만 포함, 환불/갱신 제외
  - 주의: 미래의 기능을 미리 포함하지 않기

- [ ] **백엔드 범위 명확성 (1점)**
  - 백엔드 + 백엔드가 소통하는 외부API만 포함되었나요?
  - 예: Apple App Store Server API 포함, iOS Client(StoreKit) 제외
  - 주의: 프론트엔드 요구사항은 제외

- [ ] **도메인별 적절성 (1점)**
  - 도메인별 필수 요구사항이 적절하게 포함되었나요?
  - 예: payment 도메인 - 검증, 상태 변경, 불변식
  - 주의: 핵심 요구사항 누락 금지

- [ ] **체크리스트 완결성 (1점)**
  - 핵심 항목이 구체적으로 명시되었나요?
  - 예: "영수증(transactionId, receiptData)을 수신할 수 있다"
  - 주의: "결제를 처리한다" 같은 모호한 표현 금지

## 등급 기준 (체크리스트 완결성)

| 체크된 항목 | 등급 | 설명 |
|-----------|-----|------|
| 5개 | Excellent | 바로 구현 가능 |
| 3-4개 | Good | 사소한 개선 필요 |
| 2개 | Fair | YAGNI 위반 또는 프론트엔드 포함 |
| 0-1개 | Poor | 재작성 필요 |

## 체크리스트 형식 예시

### Excellent (5점) - 결제 검증 플로우

```
### 1. 결제 검증(payment) 요구사항
    - [ ] 영수증(transactionId, receiptData)을 수신할 수 있다.
    - [ ] 영수증 검증을 위해 Apple App Store Server API를 호출할 수 있다.
    - [ ] 영수증 검증 요청 시 transactionId로 거래 정보를 조회한다.
    - [ ] 검증 성공 시 결제 정보를 저장하고 COMPLETED 상태로 변경한다.
    - [ ] 검증 실패 시 결제 정보를 저장하고 FAILED 상태로 변경한다.
    - [ ] transactionId는 중복될 수 없다.
    - [ ] 검증 완료 후 구독 생성을 수행할 수 있다.
```

### Fair (2점) - YAGNI 위반

```
### 1. 결제(payment) 요구사항
    - [ ] 영수증을 검증할 수 있다.
    - [ ] 결제 완료 시 구독을 생성한다.
    - [ ] 환불 처리를 수행할 수 있다.
    - [ ] 정기 결제 갱신을 처리한다.
```

### Poor (1점) - 프론트엔드 포함

```
### 1. 결제(payment) 요구사항
    - [ ] StoreKit으로 영수증을 가져올 수 있다.
    - [ ] 결제 시트를 표시할 수 있다.
    - [ ] 사용자가 결제를 승인하면 영수증을 생성한다.
    - [ ] 영수증을 백엔드로 전송한다.
```

---

## 2차원 평가 체계 (PRD/기능 명세 형식)

| 차원 | 만점 | 목적 |
|-----|-----|------|
| **요구사항 완결성** | 5점 | LLM 코드 생성 전제 조건 (최우선) |
| **DDD 설계 품질** | 10점 | Rich Domain Model 달성 |

---

# 1차원: 요구사항 완결성 체크리스트 (5점 만점)

LLM이 코드 생성 시 누락을 방지하기 위해 요구사항이 얼마나 명확한지 확인하세요.

## 필수 항목 (5점)

- [ ] **Actor 식별 (1점)**
  - 핵심 Actor(사용자, 시스템)가 명확히 식별되었나요?
  - 예: 학생(User), 관리자(Admin), 강사(Instructor)
  - 주의: "사용자"처럼 모호한 표현 금지

- [ ] **유스케이스 명시 (1점)**
  - 주요 기능 흐름이 구체적으로 명시되었나요?
  - 예: "학생이 모의고사를 생성하면 시험이 시작됨"
  - 주의: "시스템이 처리함" 같은 모호한 표현 금지

- [ ] **비즈니스 불변식 구체성 (1점)**
  - 불변식이 구체적인 수치로 명시되었나요?
  - 예: "문제 수는 1개 이상 20개 이하여야 함"
  - 주의: "유효해야 한다", "적절해야 한다" 같은 추상적 표현 금지

- [ ] **예외/경계 조건 (1점)**
  - 실패 시나리오, 경계값 처리가 명시되었나요?
  - 예: "중복 제출 시 EXAM_DUPLICATE_ANSWER_SUBMISSION (409)"
  - 주의: 정상 흐름만 명시하고 예외 처리 누락 금지

- [ ] **도메인 용어 일관성 (1점)**
  - 유비쿼터스 언어가 일관되게 사용되었나요?
  - 예: 모의고사(Exam), 문제(Problem), 챕터(Chapter)
  - 주의: "시험/테스트/모의고사" 혼용 금지

## 등급 기준 (요구사항 완결성)

| 체크된 항목 | 등급 | 설명 |
|-----------|-----|------|
| 5개 | Excellent | 불명확한 지점 없음, 바로 코드 생성 가능 |
| 4개 | Good | 사소한 불명확한 지점 있음 |
| 2-3개 | Fair | 핵심 불명확한 지점 있음, 추가 질문 필요 |
| 0-1개 | Poor | 요구사항 부족, 재작성 필요 |

## 불명확한 지점 자가 진단

체크리스트에 응답하지 못한 항목이 있다면 다음 질문으로 불명확한 지점을 식별하세요:

### Actor 식별이 안 된 경우
```
Q: 누가 이 기능을 사용하나요?
   - 학생(User)?
   - 관리자(Admin)?
   - 강사(Instructor)?
   - 시스템 자체?
```

### 유스케이스가 불명확한 경우
```
Q: 사용자가 수행하는 구체적인 동작은 무엇인가요?
   - 생성(Create)?
   - 조회(Read)?
   - 수정(Update)?
   - 삭제(Delete)?
   - 제출(Submit)?

Q: 전체 흐름은 어떻게 되나요?
   - A 단계 → B 단계 → C 단계
```

### 비즈니스 불변식이 추상적인 경우
```
Q: 구체적인 제한 조건은 무엇인가요?
   - 최소값/최대값?
   - 길이 제한?
   - 허용되는 값의 범위?

❌ Bad: "가격은 유효해야 한다"
✅ Good: "가격은 0원 이상 100만원 이하여야 한다"
```

### 예외 처리가 누락된 경우
```
Q: 실패할 수 있는 시나리오는 무엇인가요?
   - 리소스를 찾을 수 없음 (404)
   - 권한 없음 (403)
   - 중복 리소스 (409)
   - 시간 만료 (422)
   - 입력값 검증 실패 (400)

Q: 각 실패 시 어떻게 처리되나요?
   - 예외 발생?
   - 기본값 사용?
   - 재시도?
```

### 도메인 용어가 불일치하는 경우
```
Q: 이 기능에서 사용하는 핵심 용어는 무엇인가요?
   - 용어를 3~5개 추출
   - 전체 요구사항에서 일관되게 사용 확인

❌ Bad: 시험/테스트/모의고사 혼용
✅ Good: 모의고사(Exam) 일관 사용
```

---

# 2차원: DDD 설계 품질 체크리스트 (10점 만점)

주어진 요구사항을 DDD 관점에서 얼마나 잘 설계했는지 확인하세요.

## 필수 항목 (7점)

- [ ] **Aggregate 식별 (1점)**
  - Aggregate Root + Child Entity가 명확히 식별되었나요?
  - 예: Exam (Root) → ExamProblem (Child)

- [ ] **경계 명확성 (1점)**
  - Aggregate 간 ID 참조만 사용하나요? (직접 객체 참조 금지)
  - 예: Exam.userId (Long), ExamProblem.problemId (Long)

- [ ] **도메인 이벤트 (1점)**
  - 상태 변경 시 도메인 이벤트 발행을 고려했나요?
  - 예: ExamAnswerSubmittedEvent

- [ ] **불변성 보장 (1점)**
  - Enum/Value Object로 불변성을 보장하나요?
  - 예: ExamType (Enum), ExamStatus (Enum)

- [ ] **유비쿼터스 언어 (1점)**
  - 도메인 전문가 용어를 사용하나요? (기술 용어 금지)
  - 예: 모의고사 응시(Exam Taking), 답안 제출(Answer Submission)

- [ ] **비즈니스 불변식 (1점)**
  - 도메인 모델에서 Business Invariant를 명시했나요?
  - 예: Exam.validateExamProblemsCount()

- [ ] **CQRS 분리 (1점)**
  - Command와 Query가 명확히 분리되었나요?
  - 예: ExamCommandService, ExamQueryService

## 선택 항목 (2점)

- [ ] **도메인 서비스 분리 (1점)**
  - Entity에 속하지 않는 로직만 Domain Service로 분리했나요?
  - 예: ExamValidator (도메인 서비스)

- [ ] **리포지토리 추상화 (1점)**
  - 기술 구현(JPA)을 감추고 Repository 인터페이스만 노출하나요?
  - 예: interface ExamRepository extends Repository<Exam, Long>

## 고급 항목 (1점)

- [ ] **컨텍스트 매핑 (1점)**
  - Bounded Context 간 관계를 명시했나요?
  - 예: Exam Context (Core), Problem Context (Shared Kernel)

## 등급 기준 (DDD 설계 품질)

| 체크된 항목 | 등급 | DDD 품질 |
|-----------|-----|----------|
| 9-10개 | Excellent | Rich Domain Model |
| 7-8개 | Good | Healthy Domain |
| 5-6개 | Fair | Anemic Domain Model 위험 |
| 3-4개 | Poor | Transaction Script |
| 0-2개 | Incomplete | 설계 부족 |

---

# 2차원 종합 평가

## 평가 결과 매트릭스

| DDD \ 요구사항 | Excellent (5) | Good (4) | Fair (2-3) | Poor (0-1) |
|---------------|--------------|----------|------------|------------|
| **Excellent (9-10)** | Rich Domain Model 완성 | Rich Domain Model 가능 | 요구사항 명확화 후 구현 | 요구사항 재작성 필수 |
| **Good (7-8)** | Rich Domain Model 가능 | Healthy Domain | 요구사항 명확화 필요 | 요구사항 재작성 권장 |
| **Fair (5-6)** | 요구사험 명확화 후 구현 | 요구사항 명확화 필요 | Anemic Domain Model 위험 | 설계 재고 필요 |
| **Poor (3-4)** | 요구사항 재작성 필수 | 요구사항 재작성 권장 | 설계 재고 필요 | 전면 재작성 |
| **Incomplete (0-2)** | PRD 작성 필수 | PRD 작성 필수 | PRD 작성 필수 | PRD 작성 필수 |

## 권장 사항

### Rich Domain Model 완성 (요구사항 5점 + DDD 9-10점)
- 즉시 구현 가능
- 도메인 로직을 Entity에 위치
- 도메인 이벤트로 상태 변경 처리

### 요구사항 명확화 필요 (요구사항 2-4점)
- 불명확한 지점 식별 후 추가 질문 생성
- Actor, 유스케이스, 불변식, 예외 처리 구체화

### DDD 개선 필요 (DDD 5-8점)
- 도메인 이벤트 고려
- 불변성 보장 (String → Enum)
- Rich Domain Model 패턴 적용

### PRD 재작성 (요구사항 0-1점)
- 체크리스트 기반으로 요구사항 재작성
- Actor, 유스케이스, 불변식, 예외 처리 전체 누락

---

# 개선 제안 템플릿

## 요구사항 완결성 개선

### Actor 식별 부족
**문제**: "사용자"처럼 모호한 표현
**해결**:
- 학생(User), 관리자(Admin), 강사(Instructor)로 구체화
- 각 Actor의 권한과 역할 명시

### 유스케이스 불명확
**문제**: "시스템이 처리함" 같은 모호한 표현
**해결**:
- 구체적인 동작 순서 명시
- A 단계 → B 단계 → C 단계 흐름 작성

### 비즈니스 불변식 추상적
**문제**: "유효해야 한다" 같은 추상적 표현
**해결**:
- 최소값/최대값 명시
- "0원 이상", "1개 이상 20개 이하" 수준으로 구체화

### 예외 처리 누락
**문제**: 정상 흐름만 명시
**해결**:
- 실패 시나리오 식별
- ErrorCode와 HTTP 상태 코드 명시

### 도메인 용어 불일치
**문제**: "시험/테스트/모의고사" 혼용
**해결**:
- 핵심 용어 3~5개 추출
- 전체 요구사항에서 일관되게 사용

---

## DDD 설계 품질 개선

### 1. Aggregate 식별 부족
**문제**: 데이터 모델만 나열되어 있고 Aggregate Root가 식별되지 않음
**해결**:
- 핵심 엔티티를 Aggregate Root로 지정
- 연관된 엔티티를 Child Entity로 구성
- `@OneToMany` + `@JoinColumn`으로 단방향 연관관계 설정

### 2. 경계 명확성 부족
**문제**: Aggregate 간 직접 객체 참조 사용
**해결**:
- Aggregate 간 참조는 ID(Long)로만 유지
- `@ManyToOne` 사용 금지
- Lazy Loading 대신 ID로 필요시 조회

### 3. 도메인 이벤트 누락
**문제**: 상태 변경만 있고 이벤트 고려 없음
**해결**:
- `AbstractAggregateRoot<T>` 상속
- 상태 변경 시 `registerEvent(new DomainEvent(...))` 호출
- `@EventListener`로 후속 처리

### 4. 불변성 보장 부족
**문제**: 가변 String으로 상태 표현
**해결**:
- Enum으로 상태/타입 정의 (ExamType, ExamStatus)
- 복합 Value Object 추출 (Duration, Money)
- `@Getter`만 사용, `@Setter` 제거

### 5. 유비쿼터스 언어 부족
**문제**: 기술 중심 용어 사용 (CRUD, DB 테이블, API)
**해결**:
- 도메인 전문가 용어 사용 (모의고사 응시, 답안 제출, 정답 채점)
- 기술 용어 최소화
- 기능 명세에서 비즈니스 용어 우선

### 6. 비즈니스 불변식 미명시
**문제**: DB 제약조건만 고려
**해결**:
- 도메인 모델에서 `validateXxx()` 메서드로 불변식 검증
- 커스텀 예외로 비즈니스 위반 표현
- 상태 변경 전 불변식 검증

### 7. CQRS 분리 미흡
**문제**: Command/Query가 하나의 Service에 혼재
**해결**:
- `XxxCommandService` (`@Transactional`)
- `XxxQueryService` (`@Transactional(readOnly = true)`)
- Command → Info 반환, Query → Info 반환

### 8. 도메인 서비스 분리 부족
**문제**: 모든 로직이 Service에 위치 (Anemic Domain Model)
**해결**:
- 핵심 비즈니스 로직을 Entity로 이동
- Entity에 속하지 않는 로직만 Domain Service로 분리
- Rich Domain Model 패턴 적용

### 9. 리포지토리 추상화 부족
**문제**: JPA 구현체가 도메인에 노출
**해결**:
- 도메인에는 Repository 인터페이스만 정의
- JPA 구현체는 인프라 계층에 위치
- 기술 구현 감춤

### 10. 컨텍스트 매핑 부족
**문제**: 전체 시스템을 하나의 컨텍스트로 설계
**해결**:
- Bounded Context 식별 (Exam, Problem, User, Curriculum)
- Context 간 관계 정의 (Shared Kernel, ACL, OHS)
- ID로 참조하거나 공유 커널 사용

---

# 예시: Poor → Excellent 개선

## Before (Poor - 요구사항 1점 + DDD 3점)

```java
@Service
public class ExamService {
    public void submitAnswer(SubmitAnswerRequest request) {
        // 1) 중복 체크
        if (answerRepository.existsByExamIdAndProblemId(
            request.getExamId(), request.getProblemId())) {
            throw new RuntimeException("Already submitted");
        }

        // 2) 정답 비교
        boolean isCorrect = checkAnswer(request);

        // 3) 저장
        saveAnswer(request, isCorrect);
    }
}
```

**요구사항 문제점**:
- Actor: "사용자" 불명확
- 예외 처리: RuntimeException만 사용
- 비즈니스 불변식: 구체적 제한 조건 없음

**DDD 문제점**:
- Aggregate 식별 안 됨
- 도메인 로직이 Service에 위치
- 불변성 보장 안 됨 (String status)
- 도메인 이벤트 없음

---

## After (Excellent - 요구사항 5점 + DDD 10점)

### 요구사항 (완결성 5점)

```
## 기능: 모의고사 답안 제출

### Actor
- 학생 (User)

### 유스케이스
1. 학생이 모의고사에서 문제를 풀고 답안을 제출함
2. 시스템이 정답 여부를 확인하고 점수를 계산함
3. 학생이 제출 내역을 조회함

### 비즈니스 불변식
- 같은 문제는 중복 제출 불가능
- 시험 시간 만료 후 제출 불가
- 제출된 답안은 수정 불가
- 모의고사 문제 수는 1개 이상 20개 이하여야 함

### 예외 처리
- 중복 제출 시: EXAM_DUPLICATE_ANSWER_SUBMISSION (409)
- 시간 만료 시: EXAM_TIME_LIMIT_EXCEEDED (422)
- 모의고사 없음: EXAM_NOT_FOUND (404)
- 문제 수 범위 초과: EXAM_PROBLEMS_COUNT_OUT_OF_RANGE (400)

### 도메인 용어
- 모의고사(Exam), 답안(ExamUserAnswer), 정답(Answer)
```

### DDD 설계 (품질 10점)

```java
// Entity: Rich Domain Model
@Entity
public class Exam extends AbstractAggregateRoot<Exam> {
    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "exam_id")
    private List<ExamProblem> examProblems;

    // 비즈니스 불변식
    public void submitAnswer(Long userId, Long examProblemId, List<Integer> choices) {
        validateDuplicateSubmission(examProblemId);
        fixExamProblemOrder();

        // 도메인 이벤트 발행
        registerEvent(new ExamAnswerSubmittedEvent(
            this.id, userId, examProblemId, choices
        ));
    }

    private void validateDuplicateSubmission(Long examProblemId) {
        boolean alreadySubmitted = examProblems.stream()
            .anyMatch(ep -> ep.getId().equals(examProblemId) && ep.isSubmitted());
        if (alreadySubmitted) {
            throw new ExamDuplicateSubmissionException(
                ErrorCode.EXAM_DUPLICATE_ANSWER_SUBMISSION
            );
        }
    }
}

// Value Object: 불변성 보장
public enum ExamType {
    PUBLISHED_EXAM, CUSTOM_EXAM
}

// CQRS: Command Service
@Service
@Transactional
public class ExamCommandService {
    public void submitAnswer(Long examId, Long examProblemId, List<Integer> choices) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new ExamNotFoundException(ErrorCode.EXAM_NOT_FOUND));
        exam.submitAnswer(getCurrentUserId(), examProblemId, choices);
        examRepository.save(exam);
    }
}

// CQRS: Query Service
@Service
@Transactional(readOnly = true)
public class ExamQueryService {
    public ExamInfo getExam(Long examId) {
        return queryRepository.getExamById(examId);
    }
}

// 컨텍스트 매핑
// Exam Context (Core) --OHS--> User Context
// Exam Context (Core) --ACL--> Problem Context
// Exam Context (Core) --ACL--> Curriculum Context
```

**개선 사항**:
- 요구사항: Actor, 유스케이스, 불변식, 예외 처리 완전 명시
- DDD: Aggregate 식별, 도메인 로직 Entity 위치, 불변성 보장, 도메인 이벤트 발행, CQRS 분리, 컨텍스트 매핑
