# DDD 요구사항 평가 예시

## 평가 대상 구분

요구사항 형식에 따라 평가 예시가 다릅니다:

| 요구사항 형식 | 평가 예시 |
|-------------|---------|
| **체크리스트 형식** | 체크리스트 형식 평가 예시 (아래) |
| **PRD/기능 명세 형식** | 2차원 평가 예시 (하단 참조) |

---

# 체크리스트 형식 평가 예시

## Excellent (5점) - 결제 검증 플로우

### 입력 요구사항

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

### 평가 결과

```
## 평가 결과

### 1차원: 체크리스트 요구사항 완결성 (5/5) - Excellent
✅ 단일 플로우에 집중, 백엔드+외부API 포함, 도메인별 적절성 완벽

- 도메인 식별: 1점 (payment 명확)
- YAGNI 원칙: 1점 (검증 플로우만 포함, 환불/갱신 제외)
- 백엔드 범위: 1점 (백엔드 + Apple API 포함, iOS Client 제외)
- 도메인 적절성: 1점 (검증, 상태 변경, 불변식 포함)
- 체크리스트 완결성: 1점 (7개 항목 구체적)

### 2차원: DDD 설계 품질 (0/10) - 평가 제외
체크리스트 요구사항은 DDD 설계 품질 평가 대상이 아닙니다.
DDD 설계는 구현 단계에서 별도 평가합니다.

### 종합 평가
Rich Domain Model로 즉시 구현 가능합니다.
```

---

## Fair (2점) - YAGNI 위반

### 입력 요구사항

```
### 1. 결제(payment) 요구사항
    - [ ] 영수증을 검증할 수 있다.
    - [ ] 결제 완료 시 구독을 생성한다.
    - [ ] 환불 처리를 수행할 수 있다.
    - [ ] 정기 결제 갱신을 처리한다.
```

### 평가 결과

```
## 평가 결과

### 1차원: 체크리스트 요구사항 완결성 (2/5) - Fair
❌ YAGNI 원칙 위반, 도메인 적절성 부족

- 도메인 식별: 1점 (payment 명확)
- YAGNI 원칙: 0점 (환불, 갱신은 미래 플로우)
- 백엔드 범위: 1점 (백엔드만 포함)
- 도메인 적절성: 0점 (핵심 검증 로직 누락)
- 체크리스트 완결성: 0점 (항목이 모호함)

### 개선 제안

1. **YAGNI 원칙 준수**
   - 환불 처리는 별도 플로우로 작성하세요.
   - 정기 결제 갱신은 별도 플로우로 작성하세요.
   - 현재는 "결제 검증" 플로우에만 집중하세요.

2. **백엔드 범위 명확화**
   - Apple App Store Server API 호출을 명시하세요.
   - iOS Client(StoreKit) 요구사항은 제외하세요.

3. **도메인별 필수 항목 추가**
   - 영수증 수신 (transactionId, receiptData)
   - 검증 API 호출
   - 상태 변경 (COMPLETED/FAILED)
   - 불변식 (transactionId 유일성)

### 종합 평가
환불, 갱신 요구사항을 제외하고 검증 플로우에만 집중하면 Excellent 등급입니다.
```

---

## Poor (1점) - 프론트엔드 포함

### 입력 요구사항

```
### 1. 결제(payment) 요구사항
    - [ ] StoreKit으로 영수증을 가져올 수 있다.
    - [ ] 결제 시트를 표시할 수 있다.
    - [ ] 사용자가 결제를 승인하면 영수증을 생성한다.
    - [ ] 영수증을 백엔드로 전송한다.
```

### 평가 결과

```
## 평가 결과

### 1차원: 체크리스트 요구사항 완결성 (1/5) - Poor
❌ 프론트엔드 요구사항 포함, 백엔드 범위 불명확

- 도메인 식별: 1점 (payment 명확)
- YAGNI 원칙: 1점 (결제 플로우만 포함)
- 백엔드 범위: 0점 (StoreKit, 결제 시트 등 iOS Client 요구사항 포함)
- 도메인 적절성: 0점 (백엔드 검증 로직 누락)
- 체크리스트 완결성: 0점 (항목이 프론트엔드 중심)

### 개선 제안

1. **백엔드 범위로 수정**
   - StoreKit(프론트엔드) → Apple App Store Server API(백엔드)
   - 결제 시트(프론트엔드) → 영수증 검증(백엔드)

2. **올바른 예시**
   ```
   ### 1. 결제 검증(payment) 요구사항
       - [ ] 영수증(transactionId, receiptData)을 수신할 수 있다.
       - [ ] 영수증 검증을 위해 Apple App Store Server API를 호출할 수 있다.
       - [ ] 영수증 검증 요청 시 transactionId로 거래 정보를 조회한다.
       - [ ] 검증 성공 시 결제 정보를 저장하고 COMPLETED 상태로 변경한다.
       - [ ] 검증 실패 시 결제 정보를 저장하고 FAILED 상태로 변경한다.
       - [ ] transactionId는 중복될 수 없다.
   ```

### 종합 평가
프론트엔드 요구사항을 제외하고 백엔드 검증 플로우로 재작성하세요.
```

---

# 2차원 평가 체계 (PRD/기능 명세 형식)

- **1차원: 요구사항 완결성 (5점)** - LLM 코드 생성의 전제 조건
- **2차원: DDD 설계 품질 (10점)** - Rich Domain Model 달성도

---

# 불명확한 요구사항 예시와 질문 생성 (PRD/기능 명세 형식)

## Example 1: 모의고사 기능 (불명확)

### 입력 요구사항

```
사용자가 시험을 응시하고 답안을 제출합니다.
제출하면 결과를 보여줍니다.
```

### 평가 결과

```
## 평가 결과

### 1차원: 요구사항 완결성 (1/5) - Poor
❌ 심각한 불명확한 지점 있음 → 재작성 필요 또는 추가 질문 필수

Q1: Actor 식별 필요
   - "사용자"가 학생(User)인가요, 관리자(Admin)인가요?
   - 다른 Actor가 involved 되나요?

Q2: 유스케이스 구체화 필요
   - 시험 응시 전체 흐름이 어떻게 되나요?
     - 시험 생성 → 시작 → 문제 풀이 → 제출 → 결과 확인
   - 각 단계에서 사용자가 수행하는 동작은 무엇인가요?

Q3: 비즈니스 불변식 구체화 필요
   - 시험에 포함될 문제 수의 제한이 있나요? (최소/최대)
   - 시험 시간 제한이 있나요?
   - 같은 문제를 중복 제출할 수 있나요?
   - 제출 후 답안 수정이 가능한가요?

Q4: 예외/경계 조건 명시 필요
   - 시험 시간 만료 후 어떻게 되나요?
   - 존재하지 않는 시험 ID로 접근하면 어떻게 되나요?
   - 중복 제출 시 어떻게 처리하나요?

Q5: 도메인 용어 확인
   - "시험" = 모의고사(Exam)인가요, 퀴즈(Quiz)인가요?
   - 다른 도메인 용어를 사용하시나요?

### 2차원: DDD 설계 품질 (0/10) - Incomplete
❌ 요구사항 부족으로 DDD 설계 불가능

### 종합 평가
요구사항이 너무 추상적입니다. 위 질문에 답변하시면
구체적인 요구사항을 재작성해 드립니다.
```

---

## Example 2: 북마크 기능 (불명확)

### 입력 요구사항

```
사용자가 문제를 북마크할 수 있는 기능을 만들어주세요.
```

### 평가 결과

```
## 평가 결과

### 1차원: 요구사항 완결성 (1/5) - Poor
❌ 심각한 불명확한 지점 있음 → 추가 질문 필수

Q1: 유스케이스 구체화
   - 북마크 생성: 사용자가 문제를 북마크하는 방법은?
   - 북마크 조회: 북마크 목록을 어떻게 보여주나요?
   - 북마크 삭제: 북마크를 취소할 수 있나요?

Q2: 비즈니스 불변식
   - 같은 문제를 중복 북마크할 수 있나요?
   - 북마크 수에 제한이 있나요?
   - 북마크 순서는 어떻게 결정되나요? (최신순, 생성순)

Q3: 예외/경계 조건
   - 존재하지 않는 문제 ID를 북마크하면 어떻게 되나요?
   - 삭제된 문제의 북마크는 어떻게 처리되나요?

Q4: 도메인 관계
   - 북마크는 다른 기능과 연동되나요? (학습 추천, 문제 집중 등)

### 2차원: DDD 설계 품질 (0/10) - Incomplete
❌ 요구사항 부족으로 DDD 설계 불가능

### 종합 평가
PRD 작성을 권장합니다. 위 질문에 답변하시면
구체적인 요구사항을 작성해 드립니다.
```

---

## Example 3: 모의고사 재시작 기능 (일부 명확)

### 입력 요구사항

```
## 기능: 모의고사 재시작

학생(User)이 진행 중인 모의고사(Exam)를 다시 시작할 수 있습니다.
```

### 평가 결과

```
## 평가 결과

### 1차원: 요구사항 완결성 (2/5) - Fair
⚠️ 핵심 불명확한 지점 있음 → 추가 질문 필요

✅ 잘된 부분
- Actor: 학생(User) 명확
- 도메인 용어: 모의고사(Exam) 사용

❌ 추가 질문 필요

Q1: 재시작 시 기존 답안은 어떻게 처리하나요? (중요!)
   - A. 기존 답안 모두 초기화 (처음부터 다시 시작)
   - B. 기존 답안 보존 (이어서 시작)
   - C. 사용자에게 선택권 제공 (초기화/보존 선택)

Q2: 재시작 가능 조건이 있나요?
   - 시험 시간 만료 후에도 재시작 가능한가요?
   - 이미 제출(SUBMITTED)한 모의고사도 재시작 가능한가요?

Q3: 재시작 횟수 제한이 있나요?
   - 무제한 재시작 가능한가요?
   - 재시작 횟수를 제한하나요?

Q4: 재시작 후 문제 순서
   - 문제 순서가 유지되나요, 아니면 섞이나요?

### 2차원: DDD 설계 품질 (3/10) - Poor
⚠️ 기본적인 Aggregate는 식별되나 상세 설계 부족

### 종합 평가
Q1(기존 답안 처리)이 가장 중요합니다. 이에 따라
도메인 로직이 완전히 달라집니다.
```

---

## Example 4: 명확한 요구사항 (요구사항 완결성 Excellent)

### 입력 요구사항

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
- 객관식 선택지(ObjectiveChoice)
```

### 평가 결과

```
## 평가 결과

### 1차원: 요구사항 완결성 (5/5) - Excellent
✅ 불명확한 지점 없음, 바로 코드 생성 가능
- Actor: 학생(User) 명확
- 유스케이스: 구체적인 흐름 (제출 → 채점 → 조회)
- 불변식: 구체적인 수치와 조건 명시
- 예외 처리: ErrorCode와 HTTP 상태 코드까지 명시
- 도메인 용어: 일관된 용어 사용

### 2차원: DDD 설계 품질 (8/10) - Good
✅ 핵심 DDD 패턴 적용됨
- Aggregate: Exam Root + ExamProblem Child 명확
- 경계: ID 참조 사용
- CQRS: 제출(Command)/조회(Query) 분리
- 불변식: 구체적인 Business Invariant 명시

⚠️ 개선 제안
- 도메인 이벤트 고려: ExamAnswerSubmittedEvent 발행
- 불변성 보장: ExamStatus Enum 활용

### 종합 평가
Rich Domain Model로 즉시 구현 가능합니다.
도메인 이벤트만 추가하면 Excellent 등급입니다.
```

---

# Excellent (요구사항 5점 + DDD 10점)

## 요구사항: 모의고사 답안 제출

```
## 기능 개요
학생(User)이 모의고사(Exam)에서 문제를 풀고 답안을 제출하며, 정답 여부를 확인한다.

## Actor
- 학생 (User)

## 유스케이스
1. 학생이 모의고사를 생성하면 시험이 시작됨 (NOT_STARTED → IN_PROGRESS)
2. 학생이 문제를 풀고 답안을 제출함
3. 시스템이 정답 여부를 확인하고 점수를 계산함
4. 학생이 제출 내역을 조회함

## 비즈니스 불변식
- 같은 문제는 중복 제출 불가능
- 시험 시간 만료 후 제출 불가
- 제출된 답안은 수정 불가
- 모의고사 문제 수는 1개 이상 20개 이하여야 함
- 모의고사 시간은 30분 이상 180분 이하여야 함

## 예외 처리
- 중복 제출: EXAM_DUPLICATE_ANSWER_SUBMISSION (409)
- 시간 만료: EXAM_TIME_LIMIT_EXCEEDED (422)
- 모의고사 없음: EXAM_NOT_FOUND (404)
- 문제 수 초과: EXAM_PROBLEMS_COUNT_OUT_OF_RANGE (400)
- 소유자 아님: EXAM_NOT_OWNER (403)

## 도메인 용어
- 모의고사(Exam), 답안(ExamUserAnswer), 정답(Answer)
- 객관식 선택지(ObjectiveChoice), 주관식 답안(SubjectiveAnswer)

## DDD 설계
### Exam Aggregate (Root)
```
Exam (Aggregate Root)
├── id: Long
├── userId: Long (ID 참조)
├── examType: ExamType (Value Object/Enum)
│   ├── PUBLISHED_EXAM (출제된 모의고사)
│   └── CUSTOM_EXAM (사용자 정의 모의고사)
├── examName: String
├── status: ExamStatus (Value Object/Enum)
│   ├── NOT_STARTED
│   ├── IN_PROGRESS
│   └── SUBMITTED
├── duration: Duration (Value Object)
├── startedAt: LocalDateTime
├── submittedAt: LocalDateTime (nullable)
└── examProblems: List<ExamProblem> (Child Entity)

ExamProblem (Child Entity)
├── examProblemOrder: Integer
├── problemId: Long (ID 참조)
└── chapterId: Long (ID 참조)
```

### ExamUserAnswer Aggregate (Root)
```
ExamUserAnswer (Aggregate Root)
├── id: Long
├── userId: Long (ID 참조)
├── examId: Long (ID 참조)
├── examProblemId: Long (ID 참조)
├── status: AnswerStatus (Value Object/Enum)
├── examUserChoices: List<ExamUserChoice> (Child Entity)

ExamUserChoice (Child Entity)
├── choiceNumber: Integer
```

## 도메인 로직
- Exam.submitAnswer(): 중복 제출 검증, 문제 순서 확정, 이벤트 발행
- Exam.validateExamProblemsCount(): 문제 수 1~20개 검증
- Exam.validateExamTimeLimit(): 시간 만료 검증
```

**점수**: 5/5 (요구사항 완결성), 10/10 (DDD 설계 품질)

---

# Good (요구사항 4점 + DDD 7점)

## 요구사항: 퀴즈 생성

```
## 기능 개요
사용자가 퀴즈(Quiz)를 생성하고 문제를 추가한다.

## Actor
- 사용자 (User)

## 유스케이스
1. 사용자가 퀴즈를 생성함
2. 사용자가 퀴즈에 문제를 추가함
3. 사용자가 퀴즈를 시작함

## 비즈니스 불변식
- 퀴즈 문제 수는 5개 이상이어야 함
- 퀴즈 이름은 필수

## 예외 처리
- 퀴즈 없음: QUIZ_NOT_FOUND (404)
- 문제 수 부족: QUIZ_PROBLEMS_COUNT_INSUFFICIENT (400)
```

**점수**: 4/5 (요구사항 완결성), 7/10 (DDD 설계 품질)

**개선 제안**:
- 요구사항: 시간 제한, 예외 처리 추가
- DDD: 도메인 이벤트(QuizCreatedEvent), 컨텍스트 매핑 추가

---

# Fair (요구사항 2점 + DDD 5점)

## 요구사항: 문제 생성

```
## 기능 개요
관리자가 객관식 문제(Problem)를 생성한다.

## 데이터 모델

Problem
├── id: Long
├── chapterId: Long
├── title: String
├── content: String
└── difficulty: String

ObjectiveChoice
├── id: Long
├── problemId: Long
├── choiceNumber: Integer
└── content: String
```

**점수**: 2/5 (요구사항 완결성), 5/10 (DDD 설계 품질)

**누락된 내용**:
- Actor: "관리자" 시스템 권한 명시 필요
- 예외 처리: 실패 시나리오 전혀 없음
- DDD: 불변성(Difficulty Enum), 도메인 이벤트, 컨텍스트 매핑 누락

---

# Poor (요구사항 1점 + DDD 3점)

## 요구사항: 사용자 학습 관리

```
사용자가 챕터를 학습하고 답안을 제출합니다.
```

**점수**: 1/5 (요구사항 완결성), 3/10 (DDD 설계 품질)

**개선 제안**:
- PRD 재작성 필수
- Actor, 유스케이스, 불변식, 예외 처리 전체 누락
- DDD: Transaction Script 패턴

---

# Incomplete (요구사항 0점 + DDD 0점)

## 요구사항: 북마크 기능

```
사용자가 문제를 북마크할 수 있는 기능을 만들어주세요.
```

**점수**: 0/5 (요구사항 완결성), 0/10 (DDD 설계 품질)

**개선 제안**: PRD 작성부터 시작하세요.
