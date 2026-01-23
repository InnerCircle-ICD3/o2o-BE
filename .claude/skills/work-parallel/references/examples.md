# Work-Parallel 작업 유형별 예시

## CI/CD Configuration

**Task**: GitHub Actions 배포 설정 수정

```bash
# Current: fix/auth
git worktree add ../kokpick-task-deploy develop -b fix/actions-deployment

# Changes in worktree:
- .github/workflows/deploy-dev.yml
- .github/workflows/deploy-prod.yml
```

**Typical changes**:

- Gradle configuration (gradle.properties)
- Docker setup (docker-compose.yml)
- GitHub Actions (deploy-dev.yml, deploy-prod.yml)
- Environment variables (.env.example)

## Code Refactoring

**Task**: 공통 검증 로직 추출

```bash
# Current: feature/quiz
git worktree add ../kokpick-task-refactor develop -b refactor/extract-validation

# Changes in worktree:
+ kokpick-common/validator/CommonValidator.java
~ kokpick-domains/*/validator/*Validator.java
```

**Typical changes**:

- Extract common validation logic
- Remove code duplication
- Apply DRY principles
- Clean up imports

## Documentation

**Task**: API 가이드 문서화

```bash
# Current: fix/user
git worktree add ../kokpick-task-docs develop -b docs/api-guide

# Changes in worktree:
+ docs/api/API_GUIDE.md
+ docs/api/ENDPOINTS.md
```

**Typical changes**:

- README updates
- API documentation
- Deployment guides
- Architecture documentation

## Configuration

**Task**: Docker MySQL 설정 수정

```bash
# Current: main
git worktree add ../kokpick-task-docker main -b config/docker-mysql

# Changes in worktree:
~ docker-compose.yml
~ Dockerfile
```

**Typical changes**:

- Docker compose configuration
- Application settings (application.yml)
- Environment configuration
- Database schema adjustments

## Hotfix

**Task**: 인증 버그 긴급 수정

```bash
# Current: feature/payment
git worktree add ../kokpick-task-hotfix develop -b hotfix/auth-bug

# Changes in worktree:
~ kokpick-domains/kokpick-user/**/*.java
```

**Typical changes**:

- Critical bug fixes
- Security patches
- Production issues
- Breaking changes

## Implementation Example

### Example 1: Gradle Configuration Fix

```properties
# gradle.properties - Remove local Java path
- org.gradle.java.home=C:/Users/USER/Downloads/jdk-21.0.1
+ # org.gradle.java.home configured in gradle.properties.local
```

### Example 2: Code Refactoring

```java
// Extract common validation logic
@Component
public class CommonValidator {
    public void validateNotNull(Object obj, String message) {
        Objects.requireNonNull(obj, message);
    }
}
```

### Example 3: Documentation

```markdown
# API_DEPLOYMENT.md
## 배포 가이드
### 1. 빌드
./gradlew build
```

### Example 4: Docker Configuration

```yaml
# docker-compose.yml
services:
  mysql:
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-root}
```
