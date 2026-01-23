# Commit Message Patterns

## Commit Types

### Type Detection Rules

| Type         | Description   | File Patterns                                   | Examples                                                |
|--------------|---------------|-------------------------------------------------|---------------------------------------------------------|
| **feat**     | New feature   | New entities, services, controllers, endpoints  | `Exam.java` (new), `ExamController.java` (new endpoint) |
| **fix**      | Bug fix       | Error handling, validation fixes                | `ExamValidationException.java`, bug fix in service      |
| **refactor** | Restructuring | Code moves, pattern changes, no behavior change | Moving code between layers, applying patterns           |
| **test**     | Test changes  | `*Test.java`, `*Tests.java`                     | `ExamServiceTest.java`, `ExamValidatorTest.java`        |
| **docs**     | Documentation | `*.md`, CLAUDE.md, README                       | `CLAUDE.md`, `README.md`, `.claude/skills/*`            |
| **config**   | Configuration | build.gradle.kts, application*.yml, Dockerfile  | `build.gradle.kts`, `application-dev.yml`               |

## Commit Message Format

### Standard Format

```
{type}({scope}): {summary}

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Type Only (No Scope)

```
{type}: {summary}

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Scope Values

**Domain Scopes**:

- `exam` - kokpick-exam module
- `quiz` - kokpick-quiz module
- `problem` - kokpick-problem module
- `user` - kokpick-user module
- `curriculum` - kokpick-curriculum module
- `user-study` - kokpick-user-study module

**Layer Scopes** (when cross-domain):

- `domain` - Domain layer changes
- `infrastructure` - Infrastructure layer changes
- `application` - Application layer changes
- `common` - Common module changes

**No Scope**:

- Docs changes
- Config changes
- Cross-cutting concerns

## Summary Guidelines

### Length

- **Target**: 50-80 characters
- **Maximum**: 100 characters (soft limit)

### Style

- **Korean**: Use Korean for user-facing features
- **English**: Use English for technical terms
- **Concise**: Focus on WHAT changed, not HOW

### Examples

```
feat(exam): 모의고사 채점 기능 추가
fix(problem): 문제 답안 검증 로직 수정
refactor(quiz): Quiz-QuizProblem 연관관계 단방향 변경
test(exam): ExamValidator 테스트 추가
docs: CLAUDE.md에 Aggregate 패턴 추가
config: JWT 토큰 만료 시간 설정
```

## Real Examples from Kokpick

### Recent Commits

```
feat: Repository 및 Avatar Presigned URL 관리 메서드 추가
config: S3 버킷명 변경
feat(app): Service 레이어 imageUrl 생성 로직 추가
feat(app): MeResponse avatar 이미지 필드 추가
feat(app): Response DTO imageUrl 필드 추가
```

### Domain-Specific Commits

```
feat(exam): 모의고사 제출 기능 구현
fix(quiz): 퀴즈 상태 변경 오류 수정
refactor(problem): Problem-Answer 연관관계 재구성
test(user): UserService 통합 테스트 추가
docs(exam): Exam Aggregate 패턴 문서화
```

## Commit Message Generation Algorithm

```python
def generate_commit_message(group):
    module = group['module']
    files = group['files']
    change_type = group['change_type']

    # Extract domain from module name
    domain = module.replace('kokpick-', '').upper()

    # Determine scope
    scope = None
    if domain in ['EXAM', 'QUIZ', 'PROBLEM', 'USER',
                  'CURRICULUM', 'USER_STUDY']:
        scope = domain.lower()

    # Generate summary based on files
    summary = generate_summary(files, change_type)

    # Format commit message
    if scope:
        return f"{change_type}({scope}): {summary}"
    else:
        return f"{change_type}: {summary}"

def generate_summary(files, change_type):
    # Extract key information from file paths
    domains = extract_domains(files)
    actions = extract_actions(files, change_type)

    # Build summary
    if change_type == 'refactor':
        if len(files) > 5:
            return f"{domains[0]} 도메인 리팩토링" if domains else "코드 리팩토링"
        else:
            return f"{domains[0]} {actions[0]}" if domains else f"{actions[0]}"

    elif change_type == 'feat':
        return f"{domains[0]} {actions[0]} 기능 추가" if domains else f"{actions[0]} 기능 추가"

    elif change_type == 'fix':
        return f"{domains[0]} {actions[0]} 수정" if domains else f"{actions[0]} 수정"

    elif change_type == 'test':
        return f"{domains[0]} 테스트 추가" if domains else "테스트 추가"

    elif change_type == 'docs':
        return "문서 업데이트"

    elif change_type == 'config':
        return "설정 변경"

    return "코드 변경"
```

## Summary Generation Patterns

### By Change Type

| Type         | Pattern                  | Examples               |
|--------------|--------------------------|------------------------|
| **feat**     | {domain} {feature} 기능 추가 | `모의고사 채점 기능 추가`        |
| **fix**      | {domain} {issue} 수정      | `문제 검증 오류 수정`          |
| **refactor** | {domain} {pattern} 적용/변경 | `연관관계 단방향 변경`          |
| **test**     | {domain} 테스트 추가          | `ExamValidator 테스트 추가` |
| **docs**     | {topic} 문서 추가/업데이트       | `Aggregate 패턴 문서화`     |
| **config**   | {what} 설정                | `JWT 토큰 만료 시간 설정`      |

### By File Count

**Small changes** (1-5 files):

- Focus on specific action

```
refactor(exam): Exam-ExamProblem 단방향 연관관계 적용
```

**Medium changes** (5-15 files):

- Focus on domain/feature

```
refactor(exam): Exam 도메인 HTTP 상태 코드 재분배
```

**Large changes** (15+ files):

- Should be split into multiple commits

```
refactor(exam): Exam 도메인 리팩토링 - 1/3
refactor(exam): Exam 도메인 리팩토링 - 2/3
refactor(exam): Exam 도메인 리팩토링 - 3/3
```

## Common Patterns

### Entity/Repository Changes

```
feat(exam): Exam 엔티티 및 Repository 추가
refactor(quiz): Quiz-QuizProblem 연관관계 재구성
fix(problem): Problem 엔티티 유효성 검증 수정
```

### Service Layer Changes

```
feat(exam): ExamCommandService 제출 기능 구현
refactor(user): UserService 계층 분리
fix(quiz): QuizValidator 상태 검증 수정
```

### API/Controller Changes

```
feat(exam): 모의고사 제출 API 엔드포인트 추가
fix(problem): 문제 조회 API 404 오류 수정
refactor(user): 사용자 API 응답 DTO 재구성
```

### Documentation Changes

```
docs: CLAUDE.md에 Aggregate 패턴 추가
docs(exam): Exam 도메인 기술 문서 업데이트
docs: README에 개발 환경 설정 추가
```

### Configuration Changes

```
config: JWT 토큰 만료 시간 30분으로 변경
config: MySQL 연결 풀 설정 추가
config: Docker Compose 환경변수 설정
```

## Anti-Patterns to Avoid

### ❌ Too Vague

```
fix: 버그 수정
refactor: 코드 개선
update: 업데이트
```

### ❌ Too Long

```
feat(exam): 모의고사 제출 기능을 추가하고 검증 로직을 구현하며
          API 엔드포인트도 함께 추가했습니다 (150+ chars)
```

### ❌ Mixed Types

```
feat(fix): 기능 추가 및 버그 수정  # Don't combine types
```

### ❌ Wrong Scope

```
feat(java): Exam 기능 추가  # Wrong: language/platform
feat(code): 리팩토링  # Wrong: too generic
```

## Quality Checklist

- [ ] Correct commit type (feat/fix/refactor/test/docs/config)
- [ ] Appropriate scope (domain/module or none)
- [ ] Concise summary (50-80 chars)
- [ ] Korean for user-facing, English for technical
- [ ] Co-Authored-By tag included
- [ ] No typos or grammatical errors
