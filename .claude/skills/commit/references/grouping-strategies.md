# Grouping Strategies

## File Grouping Algorithm

### Priority Order

```
Priority 1: Domain/Module Boundary
  ├── kokpick-exam
  ├── kokpick-quiz
  ├── kokpick-problem
  ├── kokpick-user
  ├── kokpick-curriculum
  ├── kokpick-user-study
  ├── kokpick-database
  ├── kokpick-external
  ├── kokpick-service-api
  ├── kokpick-admin-api
  └── kokpick-common

Priority 2: Layer (within module)
  ├── domain (entities, repositories, services)
  ├── infrastructure (persistence, external)
  ├── application (controllers, facades, dtos)
  └── common (shared utilities)

Priority 3: Change Type
  ├── New feature (add files)
  ├── Refactoring (modify structure)
  ├── Bug fix (modify behavior)
  ├── Test (test files)
  ├── Documentation (docs)
  └── Configuration (config)

Priority 4: File Coherence
  └── Files changed together for single purpose
```

## Grouping Rules

### Size Limits

| Metric              | Limit      | Action            |
|---------------------|------------|-------------------|
| Files per commit    | 15 (soft)  | Split if exceeded |
| Lines changed       | 500 (soft) | Split if exceeded |
| Domains per commit  | 1 (hard)   | Split if multiple |
| Features per commit | 1 (hard)   | Split if multiple |

### When to Split

**Multiple Domains**:

```
Files:
  kokpick-exam/... (10 files)
  kokpick-quiz/... (8 files)

→ Split into 2 commits
```

**Mixed Change Types**:

```
Files:
  kokpick-exam/domain/Exam.java (refactor)
  kokpick-exam/service/ExamTest.java (test)

→ Split into 2 commits
```

**Cross-Cutting Changes**:

```
Files:
  kokpick-common/exception/ErrorCode.java
  kokpick-exam/exception/ExamValidationException.java
  kokpick-quiz/exception/QuizValidationException.java

→ Single commit: "refactor: 도메인 예외 클래스 추가"
  (if all related to single change)
```

### When to Merge

**Single Feature, Multiple Layers**:

```
Files:
  kokpick-exam/domain/Exam.java
  kokpick-exam/service/ExamCommandService.java
  kokpick-exam/application/ExamController.java

→ Single commit: "feat(exam): 모의고사 생성 기능 구현"
```

**Single Domain, Multiple Files**:

```
Files:
  kokpick-exam/domain/Exam.java
  kokpick-exam/domain/ExamProblem.java
  kokpick-exam/service/ExamCommandService.java
  kokpick-exam/service/ExamQueryService.java

→ Single commit: "refactor(exam): Exam 도메인 리팩토링"
```

## Grouping Examples

### Example 1: Single Domain, Single Feature

```
Files:
  kokpick-exam/domain/Exam.java (modified)
  kokpick-exam/domain/ExamProblem.java (modified)
  kokpick-exam/service/ExamCommandService.java (modified)

Analysis:
  - Single domain: exam
  - Single feature: 연관관계 변경
  - Related files: 3
  - Lines changed: ~150

→ Single commit: "refactor(exam): Exam-ExamProblem 단방향 연관관계 적용"
```

### Example 2: Multiple Domains

```
Files:
  kokpick-exam/.../Exam.java (modified)
  kokpick-exam/.../ExamProblem.java (modified)
  kokpick-exam/.../ExamService.java (modified)
  kokpick-quiz/.../Quiz.java (modified)
  kokpick-quiz/.../QuizProblem.java (modified)
  kokpick-quiz/.../QuizService.java (modified)

Analysis:
  - Multiple domains: exam, quiz
  - Similar change: 연관관계 재구성
  - Files per domain: 3 each

→ Split into 2 commits:
  1. "refactor(exam): Exam-ExamProblem 단방향 연관관계 적용"
  2. "refactor(quiz): Quiz-QuizProblem 단방향 연간관계 적용"
```

### Example 3: Cross-Domain Common Change

```
Files:
  kokpick-common/exception/ErrorCode.java (modified)
  kokpick-common/exception/DomainException.java (modified)
  kokpick-exam/exception/ExamValidationException.java (added)
  kokpick-quiz/exception/QuizValidationException.java (added)
  kokpick-problem/exception/ProblemValidationException.java (added)

Analysis:
  - Multiple domains: common, exam, quiz, problem
  - Single feature: 도메인 예외 클래스 체계화
  - Related change: All exception-related

→ Single commit: "refactor: 도메인 예외 클래스 추가 및 ErrorCode 재분배"
  (Cross-domain but single cohesive change)
```

### Example 4: Mixed Changes in Single Domain

```
Files:
  kokpick-exam/domain/Exam.java (modified - refactor)
  kokpick-exam/service/ExamValidator.java (modified - bug fix)
  kokpick-exam/service/ExamServiceTest.java (added - test)

Analysis:
  - Single domain: exam
  - Mixed change types: refactor, fix, test
  - Different features: 연관관계, 검증, 테스트

→ Split into 3 commits:
  1. "refactor(exam): Exam-ExamProblem 연관관계 재구성"
  2. "fix(exam): ExamValidator 검증 로직 수정"
  3. "test(exam): ExamValidator 테스트 추가"
```

### Example 5: Documentation Changes

```
Files:
  .claude/skills/aggregate/SKILL.md (modified)
  .claude/skills/aggregate/references/patterns.md (added)
  .claude/skills/query/SKILL.md (modified)
  .claude/skills/query/references/patterns.md (added)
  CLAUDE.md (modified)

Analysis:
  - Multiple skills: aggregate, query
  - Single feature: skills 구조 개선
  - Related change: All documentation

→ Single commit: "docs: Skills 구조 개선 및 Progressive Disclosure 적용"
```

## Special Cases

### .claude Files

```
Files:
  .claude/skills/commit/SKILL.md (added)
  .claude/skills/commit/references/*.md (added)

→ Single commit: "feat: commit skill 추가"
```

### Configuration Files

```
Files:
  build.gradle.kts (modified)
  settings.gradle.kts (modified)
  docker-compose.yml (modified)

→ Single commit: "config: Gradle 및 Docker 설정 변경"
```

### Test Files Only

```
Files:
  kokpick-exam/service/ExamServiceTest.java (added)
  kokpick-exam/domain/ExamTest.java (added)

→ Single commit: "test(exam): Exam 도메인 테스트 추가"
```

## Grouping Decision Tree

```
Start
  │
  ├─ Are files from multiple domains?
  │   ├─ Yes ── Are they related (single feature)?
  │   │   ├─ Yes ── Single commit (cross-domain)
  │   │   └─ No  ── Split by domain
  │   │
  │   └─ No ── Continue
  │
  ├─ Are files from multiple change types?
  │   ├─ Yes ── Split by change type
  │   └─ No ── Continue
  │
  ├─ Are there more than 15 files?
  │   ├─ Yes ── Split by feature
  │   └─ No ── Continue
  │
  ├─ Are there more than 500 lines changed?
  │   ├─ Yes ── Split by feature
  │   └─ No ── Continue
  │
  └─ Single commit
```

## Heuristics

### Cohesion Indicators (Merge)

- Files in same domain/module
- Files modified at same time
- Files with related names (Exam*, ExamProblem*)
- Files in same layer (all domain, all service)
- Single feature/purpose

### Coupling Indicators (Split)

- Files in different domains (unrelated)
- Files with different change types
- Large file count (>15)
- Large line count (>500)
- Multiple features/purposes

## Edge Cases

### Single File

```
Files:
  CLAUDE.md (modified)

→ Single commit: "docs: CLAUDE.md에 Aggregate 패턴 추가"
```

### Large Number of Files

```
Files:
  kokpick-exam/... (20 files - all domain layer)

→ Split by feature:
  1. "refactor(exam): Exam 엔티티 리팩토링"
  2. "refactor(exam): Exam Service 계층 분리"
  3. "refactor(exam): Exam Repository 패턴 적용"
```

### Untracked Files Only

```
Files:
  kokpick-exam/domain/NewFeature.java (untracked)
  kokpick-exam/service/NewFeatureService.java (untracked)

→ Single commit: "feat(exam): 새로운 기능 추가"
```

### Staged + Unstaged

```
Staged:
  kokpick-exam/domain/Exam.java

Unstaged:
  kokpick-exam/service/ExamService.java

→ Ask user: "Split into 2 commits or commit together?"
  If together: "refactor(exam): Exam 도메인 전체 리팩토링"
  If split: 1. "refactor(exam): Exam 엔티티 변경"
           2. "refactor(exam): Exam Service 변경"
```

## Best Practices

### Do's ✅

- Group by domain/module first
- Split multiple unrelated changes
- Keep commits focused on single feature
- Follow existing commit patterns
- Consider code reviewability

### Don'ts ❌

- Mix multiple domains in single commit (unless related)
- Mix change types in single commit
- Create massive commits (>30 files, >1000 lines)
- Split too granularly (1 file per commit)
- Ignore file coherence
