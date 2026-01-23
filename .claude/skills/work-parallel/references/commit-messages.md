# Commit Message Examples

## Format

```
{type}: {summary}

- Detailed change description
- Reason for change
- Impact on existing code

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Commit Types

| Type       | Usage                     | Examples                             |
|------------|---------------------------|--------------------------------------|
| `fix`      | Bug fixes                 | CI/CD, configuration, critical bugs  |
| `refactor` | Code refactoring          | Clean up, extraction, reorganization |
| `docs`     | Documentation             | README, guides, API docs             |
| `config`   | Configuration             | Docker, environment, build settings  |
| `hotfix`   | Critical production fixes | Security, breaking changes           |

## CI/CD Fixes

### Gradle Configuration

```
fix: gradle.properties 로컬 Java 경로 분리

- org.gradle.java.home을 gradle.properties.local로 이동
- .gitignore에 gradle.properties.local 추가
- GitHub Actions에서는 setup-java@v4가 JDK 자동 설정

Co-Authored-By: Claude <noreply@anthropic.com>
```

### GitHub Actions

```
fix: GitHub Actions Gradle 캐시 설정

- deploy-dev.yml에 cache: gradle 추가
- Java setup에서 distribution 확인
- 빌드 시간 단축 (약 30초 → 10초)

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Docker Configuration

```
config: Docker MySQL 볼륨 권한 설정

- docker-compose.yml에 user UID/GID 매핑 추가
- MySQL 데이터 영구 볼륨 설정 수정
- README에 Docker 권한 설정 가이드 추가

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Refactoring

### Extract Validation

```
refactor: 공통 검증 로직을 CommonValidator로 추출

- CommonValidator 클래스 추가
- 중복된 null 체크 로직 제거
- 각 Validator에서 CommonValidator 사용

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Clean Up

```
refactor: 사용하지 않는 Repository 메서드 제거

- YAGNI 위반인 findAll(), delete() 제거
- QueryService에서만 사용하는 메서드 유지
- 불필요한 의존성 제거

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Code Organization

```
refactor: 유틸리티 클래스를 util 패키지로 이동

- StringUtils, DateUtil 이동
- 도메인 모듈에서 공통 모듈로 변경
- 의존성 방향 수정

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Documentation

### API Guide

```
docs: API 배포 가이드 추가

- API_DEPLOYMENT.md 문서 추가
- 배포 절차 단계별 설명
- 일반적인 오류 상황과 해결 방법 포함

Co-Authored-By: Claude <noreply@anthropic.com>
```

### README Update

```
docs: README에 개발 환경 설정 추가

- Java 21 설치 가이드
- Gradle wrapper 설정 방법
- Docker Compose 실행 방법

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Architecture Documentation

```
docs: DDD Aggregate 구조 문서화

- Aggregate Root 목록 추가
- 연관관계 매핑 규칙 설명
- CQRS 패턴 적용 방법 포함

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Hotfix

### Authentication Bug

```
hotfix: 로그인 시 JWT 토큰 만료 오류 수정

- 토큰 갱신 로직 수정
- 만료 시간 30분 → 1시간으로 변경
- 리프레시 토큰 처리 개선

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Security Patch

```
hotfix: SQL Injection 취약점 수정

- UserQueryService에서 Native Query 파라미터화
- 동적 쿼리 생성 로직 제거
- QueryDSL로 변경

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Configuration

### Environment Variables

```
config: 환경변수 관리 방식 개선

- .env.example에 필수 변수 추가
- application.yml에서 기본값 설정
- README에 환경변수 설명 추가

Co-Authored-By: Claude <noreply@anthropic.com>
```

### Build Settings

```
config: Gradle 빌드 최적화

- 병렬 빌드 활성화
- JVM 힙 메모리 설정
- 빌드 캐시 활성화

Co-Authored-By: Claude <noreply@anthropic.com>
```

## Tips

1. **Summary**: 50자 이내로 간결하게
2. **Body**: 왜(Why) 변경했는지 설명
3. **Impact**: 기존 코드에 미치는 영향
4. **Consistency**: 같은 타입의 커밋은 일관된 형식
5. **Co-Authored-By**: Claude Code가 생성한 커밋에 포함
