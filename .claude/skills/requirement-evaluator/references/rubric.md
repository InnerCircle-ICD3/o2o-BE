# DDD 요구사항 평가 루브릭

## 평가 대상 구분

요구사항 형식에 따라 평가 체계가 다릅니다:

| 요구사항 형식 | 평가 체계 |
|-------------|---------|
| **체크리스트 형식** | 1차원만 평가 (체크리스트 완결성 5점) |
| **PRD/기능 명세 형식** | 2차원 평가 (요구사항 완결성 5점 + DDD 설계 품질 10점) |

---

# 체크리스트 형식 요구사항 평가 (1차원만)

## 평가 대상

도메인별 체크리스트 형식의 요구사항만 평가합니다.

**체크리스트 형식 예시**:
```
### 1. 결제 검증(payment) 요구사항
    - [ ] 영수증(transactionId, receiptData)을 수신할 수 있다.
    - [ ] 영수증 검증을 위해 Apple App Store Server API를 호출할 수 있다.
    - [ ] 영수증 검증 요청 시 transactionId로 거래 정보를 조회한다.
```

**PRD 형식이 아닌 예시** (2차원 평가 대상):
```
## 기능: 모의고사 답안 제출

### Actor
- 학생 (User)

### 유스케이스
1. 학생이 모의고사에서 문제를 풀고 답안을 제출함
...
```

---

# 1차원: 체크리스트 요구사항 완결성 (5점 만점)

## 평가 항목 상세 기준

### 1. 도메인 식별 (1점)
- **0점**: 도메인이 불명확하거나 누락됨
- **1점**: 도메인이 명확히 식별됨 (예: payment, subscription, exam)

### 2. YAGNI 원칙 (1점)
- **0점**: 불필요한 미래 요구사항 포함 (환불, 갱신 등)
- **1점**: 현재 플로우에 필요한 요구사항만 포함

### 3. 백엔드 범위 명확성 (1점)
- **0점**: iOS Client(StoreKit) 등 프론트엔드 요구사항 포함
- **1점**: 백엔드 + 백엔드가 소통하는 외부API(Apple App Store Server API) 포함

### 4. 도메인별 적절성 (1점)
- **0점**: 도메인에 맞지 않는 요구사항 또는 핵심 누락
- **1점**: 도메인별 필수 요구사항이 적절하게 포함됨

### 5. 체크리스트 완결성 (1점)
- **0점**: 체크리스트 항목이 불충분하거나 모호함
- **1점**: 핵심 항목이 구체적으로 명시됨

---

## 등급 산출 예시 (체크리스트 완결성)

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

**평가**:
- 도메인 식별: 1점 (payment 명확)
- YAGNI 원칙: 1점 (검증 플로우만 포함, 환불/갱신 제외)
- 백엔드 범위: 1점 (백엔드 + Apple API 포함, iOS Client 제외)
- 도메인 적절성: 1점 (검증, 상태 변경, 불변식 포함)
- 체크리스트 완결성: 1점 (7개 항목 모두 구체적)

### Fair (2점) - YAGNI 위반

```
### 1. 결제(payment) 요구사항
    - [ ] 영수증을 검증할 수 있다.
    - [ ] 결제 완료 시 구독을 생성한다.
    - [ ] 환불 처리를 수행할 수 있다.
    - [ ] 정기 결제 갱신을 처리한다.
```

**평가**:
- 도메인 식별: 1점 (payment 명확)
- YAGNI 원칙: 0점 (환불, 갱신은 미래 플로우)
- 백엔드 범위: 1점 (백엔드만 포함)
- 도메인 적절성: 0점 (핵심 검증 로직 누락)
- 체크리스트 완결성: 0점 (항목이 모호함)

---

# 체크리스트 형식 요구사항 평가 결과

### 1차원: 체크리스트 요구사항 완결성 (5/5) - Excellent
✅ 단일 플로우에 집중, 백엔드+외부API 포함, 도메인별 적절성 완벽

- 도메인 식별: 1점 (payment 명확)
- YAGNI 원칙: 1점 (검증 플로우만 포함)
- 백엔드 범위: 1점 (백엔드 + Apple API 포함, iOS Client 제외)
- 도메인 적절성: 1점 (필수 항목 모두 포함)
- 체크리스트 완결성: 1점 (7개 항목 구체적)

### 2차원: DDD 설계 품질 (0/10) - 평가 제외
체크리스트 요구사항은 DDD 설계 품질 평가 대상이 아닙니다.
DDD 설계는 구현 단계에서 별도 평가합니다.

### 종합 평가
Rich Domain Model로 즉시 구현 가능합니다.

---

## 2차원 평가 체계 (PRD/기능 명세 형식)

| 차원 | 만점 | 목적 |
|-----|-----|------|
| **요구사항 완결성** | 5점 | LLM 코드 생성 전제 조건 (최우선) |
| **DDD 설계 품질** | 10점 | Rich Domain Model 달성 |

---

# 1차원: 요구사항 완결성 (5점 만점) - PRD/기능 명세 형식

## 평가 항목 상세 기준

### 1. Actor 식별 (1점)

**0점**: Actor가 불명확하거나 누락됨
```
사용자가 모의고사를 응시함 (누구인지 불명확)
```

**1점**: 핵심 Actor가 명확히 식별됨
```
학생(User)이 모의고사(Exam)를 응시함
관리자(Admin)가 문제(Problem)를 생성함
```

### 2. 유스케이스 명시 (1점)

**0점**: 기능만 나열되고 흐름이 불명확함
```
- 모의고사 응시 기능
- 답안 제출 기능
```

**1점**: 주요 기능 흐름이 구체적으로 명시됨
```
학생이 모의고사를 생성하면 시험이 시작됨
학생이 문제를 풀고 답안을 제출하면 정답 여부가 표시됨
```

### 3. 비즈니스 불변식 구체성 (1점)

**0점**: 추상적인 불변식 ("유효해야 한다")
```
- 모의고사의 문제 수는 유효해야 함
- 시간은 적절해야 함
```

**1점**: 구체적인 불변식 ("0원 이상이어야 한다")
```
- 모의고사의 문제 수는 1개 이상 20개 이하여야 함
- 시험 시간은 30분 이상 180분 이하여야 함
- 모의고사 이름은 2자 이상 50자 이하여야 함
```

### 4. 예외/경계 조건 (1점)

**0점**: 정상 흐름만 명시되고 예외 처리가 누락됨
```
학생이 답안을 제출하면 정답 여부를 확인함
```

**1점**: 실패 시나리오, 경계값 처리가 명시됨
```
- 같은 문제를 중복 제출하면 예외 발생 (EXAM_DUPLICATE_ANSWER_SUBMISSION)
- 시험 시간 만료 후 제출하면 예외 발생 (EXAM_TIME_LIMIT_EXCEEDED)
- 문제 수가 0개이거나 20개 초과면 생성 불가
- 존재하지 않는 모의고사 ID로 조회 시 404 응답
```

### 5. 도메인 용어 일관성 (1점)

**0점**: 기술 용어 사용, 용어 불일치
```
- DB에 exam 테이블을 CRUD함
- API 엔드포인트로 문제를 가져옴
- 사용자/회원/고객 (혼용)
```

**1점**: 유비쿼터스 언어가 일관되게 사용됨
```
- 학생이 모의고사를 응시함 (Exam Taking)
- 답안을 제출함 (Answer Submission)
- 정답을 채점함 (Answer Grading)
- 사용자 (User), 문제 (Problem), 챕터 (Chapter)
```

---

## 등급 산출 예시 (요구사항 완결성)

### Excellent (5점) - 명확한 요구사항

```
## 기능: 모의고사 답안 제출

### Actor
- 학생 (User)

### 유스케이스
1. 학생이 모의고사에서 문제를 풀고 답안을 제출함
2. 시스템이 정답 여부를 확인하고 점수를 계산함

### 비즈니스 불변식
- 같은 문제는 중복 제출 불가능
- 시험 시간 만료 후 제출 불가
- 제출된 답안은 수정 불가

### 예외 처리
- 중복 제출 시: EXAM_DUPLICATE_ANSWER_SUBMISSION (409)
- 시간 만료 시: EXAM_TIME_LIMIT_EXCEEDED (422)
- 모의고사 없음: EXAM_NOT_FOUND (404)

### 도메인 용어
- 모의고사(Exam), 답안(ExamUserAnswer), 정답(Answer)
```

### Fair (2점) - 불명확한 요구사항

```
사용자가 시험을 응시하고 답안을 제출합니다.
제출하면 결과를 보여줍니다.
```

**누락된 내용**:
- Actor: "사용자"가 학생인지 관리자인지 불명확
- 유스케이스: 구체적인 흐름 부족
- 불변식: 중복 제출, 시간 제한 등 경계 조건 누락
- 예외 처리: 실패 시나리오 전혀 없음

---

# 2차원: DDD 설계 품질 (10점 만점)

## 평가 항목 상세 기준

### 1. Aggregate 식별 (1점)

**0점**: Entity/Aggregate 구분 없이 데이터 모델만 나열
```
- exam 테이블 생성
- exam_problem 테이블 생성
- exam_user_answer 테이블 생성
```

**1점**: Aggregate Root + Child Entity 명확히 식별
```
Exam Aggregate (Root):
  - Exam (Aggregate Root): id, userId, examType, status
  - ExamProblem (Child): problemId, examProblemOrder
  - ExamUserAnswer (Root): id, userId, examId, status
```

### 2. 경계 명확성 (1점)

**0점**: Aggregate 간 직접 참조 사용
```
Exam -> User (직접 참조)
Exam -> Problem (직접 참조)
```

**1점**: Aggregate 간 ID 참조만 사용
```
Exam.userId (Long)
ExamProblem.problemId (Long)
// Aggregate 간 객체 참조 금지
```

### 3. 도메인 이벤트 (1점)

**0점**: 상태 변경만 있고 이벤트 고려 없음
```
사용자가 답안을 제출하면 상태가 변경됨
```

**1점**: 상태 변경 시 도메인 이벤트 발행 고려
```
Exam.submitAnswer() 실행 시:
  - 상태 변경: IN_PROGRESS -> SUBMITTED
  - 이벤트 발행: ExamAnswerSubmittedEvent
```

### 4. 불변성 보장 (1점)

**0점**: 모든 데이터를 가변 Entity로 설계
```
Exam:
  - String examType (변경 가능)
  - String status (변경 가능)
```

**1점**: Value Object 활용하여 불변성 보장
```
Exam:
  - ExamType examType (Enum/Value Object)
  - ExamStatus status (Enum/Value Object)
  - Duration duration (Value Object)

ExamType (Enum):
  PUBLISHED_EXAM, CUSTOM_EXAM
```

### 5. 유비쿼터스 언어 (1점)

**0점**: 기술 중심 용어 사용
```
- CRUD 작업
- DB 테이블 조인
- API 엔드포인트
```

**1점**: 도메인 전문가 용어 사용
```
- 모의고사 응시 (Exam Taking)
- 답안 제출 (Answer Submission)
- 정답 채점 (Answer Grading)
```

### 6. 비즈니스 불변식 (1점)

**0점**: 데이터베이스 제약조건만 고려
```
- NOT NULL 제약조건
- FK 제약조건
```

**1점**: 도메인 모델에서 Business Invariant 명시
```
Exam:
  - validateExamProblems(): 문제 수는 1~20개
  - validateDuplicateSubmission(): 같은 문제 중복 제출 금지
  - validateExamTimeLimit(): 제출 시간 만료 체크
```

### 7. CQRS 분리 (1점)

**0점**: Command/Query 구분 없이 하나의 메서드
```
ExamService.process(request) // 조회/명령 구분 없음
```

**1점**: Command/Query 명확히 구분
```
Command:
  - ExamCommandService.createExam()
  - ExamCommandService.submitAnswer()

Query:
  - ExamQueryService.getExamById()
  - ExamQueryService.getExamProblems()
```

### 8. 도메인 서비스 분리 (1점)

**0점**: 모든 로직을 Service에 위임
```
ExamService.submitAnswer() {
  // 중복 제출 검증
  // 정답 채점
  // 점수 계산
  // 저장
}
```

**1점**: Entity에 속하지 않는 로직만 Domain Service로 분리
```
Exam.submitAnswer() {
  validateDuplicateSubmission(); // 도메인 로직
  fixExamProblemOrder();         // 도메인 로직
  registerEvent(...);            // 이벤트 발행
}

ExamValidator.validateExamProblemsCount() // 도메인 서비스
```

### 9. 리포지토리 추상화 (1점)

**0점**: 기술 구현(JPA/Hibernate) 노출
```
EntityManager를 사용하여 CRUD
JpaRepository 직접 의존
```

**1점**: 기술 구현 감춤, Repository 인터페이스만
```
interface ExamRepository extends Repository<Exam, Long> {
  Exam findById(Long id);
  List<ExamProblem> findExamProblemsByExamId(Long examId);
}

// JPA 구현체는 인프라 계층에 위임
```

### 10. 컨텍스트 매핑 (1점)

**0점**: 전체 시스템을 하나의 컨텍스트로 설계
```
모든 기능을 하나의 Bounded Context에 포함
```

**1점**: Bounded Context 간 관계 명시
```
Exam Context (Core):
  - Exam, ExamProblem, ExamUserAnswer
  - ExamType, ExamStatus

User Context (Generic):
  - User, UserAuth
  - 참관자/열위 패턴으로 User 참조

Problem Context (Shared Kernel):
  - Problem, Answer
  - Problem 공유

Curriculum Context (Separate):
  - Course, Chapter
  - ACL 패턴으로 Chapter 참조
```

---

## 등급 산출 예시 (DDD 설계 품질)

### Excellent (9-10점) - Rich Domain Model
- 모든 항목 만족
- Rich Domain Model 패턴 적용
- 도메인 로직이 Entity에 위치
- 도메인 이벤트로 상태 변경 처리

### Good (7-8점) - Healthy Domain
- 핵심 항목 만족 (Aggregate, 경계, CQRS, Repository)
- 일부 항목 미흡 (이벤트, Value Object)
- 건강한 도메인 모델

### Fair (5-6점) - Anemic Domain Model 위험
- 기본 구조는 있으나 로직이 Service로 치우침
- Entity가 데이터 그릇 역할만
- 도메인 서비스 과다 사용

### Poor (3-4점) - Transaction Script
- Aggregate 식별 미흡
- Service에 모든 로직 위치
- 비즈니스 불변식 미명시

### Incomplete (0-2점) - 설계 부족
- 데이터 모델만 존재
- 도메인 용어 부재
- DDD 패턴 미적용

---

## 2차원 종합 평가 예시

### Excellent + Excellent (요구사항 5점 + DDD 10점)

```
## 평가 결과

### 1차원: 요구사항 완결성 (5/5) - Excellent
✅ 불명확한 지점 없음, 바로 코드 생성 가능
- Actor: 학생(User) 명확
- 유스케이스: 답안 제출 흐름 구체적
- 불변식: 중복 제출 금지, 시간 제한 명시
- 예외 처리: 실패 시나리오 포함
- 도메인 용어: 일관된 용어 사용

### 2차원: DDD 설계 품질 (10/10) - Excellent
✅ Rich Domain Model 완성
- Aggregate: Exam Root + ExamProblem Child 명확
- 경계: ID 참조만 사용
- 이벤트: ExamAnswerSubmittedEvent 발행
- 불변성: ExamType/ExamStatus Enum
- 유비쿼터스 언어: 모의고사 응시, 답안 제출
- 불변식: validateExamProblemsCount() 등
- CQRS: CommandService/QueryService 분리
- 도메인 서비스: ExamValidator 적절 분리
- Repository: 인터페이스 추상화
- 컨텍스트: Exam/Problem/User/Curriculum 분리

### 종합 평가
Rich Domain Model로 즉시 구현 가능합니다.
```

### Fair + Good (요구사항 2점 + DDD 7점)

```
## 평가 결과

### 1차원: 요구사항 완결성 (2/5) - Fair
❌ 불명확한 지점 있음 → 추가 질문 필요

Q1: 모의고사 재시작 시 기존 답안은 어떻게 처리하나요?
    - A. 기존 답안 초기화
    - B. 기존 답안 보존
    - C. 사용자에게 선택권 제공

Q2: 시험 시간 만료 후 자동 제출되나요, 아니면 수동 제출만 가능한가요?

Q3: 모의고사 생성 시 같은 문제가 중복으로 포함될 수 있나요?

### 2차원: DDD 설계 품질 (7/10) - Good
✅ 핵심 DDD 패턴 적용됨
- Aggregate: Exam Root 식별
- 경계: ID 참조 사용
- CQRS: Command/Query 분리
- Repository: 추상화 적용

⚠️ 개선 필요
- 도메인 이벤트 고려 (0점)
- 불변성 보장 (String status → Enum)
- 유비쿼터스 언어 강화

### 종합 평가
요구사항을 명확화한 후 DDD 구현을 권장합니다.
추가 질문에 답변하면 Rich Domain Model 구현 가능합니다.
```
