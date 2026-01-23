# Branch & Worktree Naming Convention

## Branch Prefix

| Prefix      | Usage                     | Example                                                  |
|-------------|---------------------------|----------------------------------------------------------|
| `fix/`      | Bug fixes                 | `fix/gradle-config`, `fix/docker-volume`                 |
| `refactor/` | Code refactoring          | `refactor/extract-validator`, `refactor/cleanup-imports` |
| `docs/`     | Documentation             | `docs/api-guide`, `docs/readme-update`                   |
| `config/`   | Configuration             | `config/docker-mysql`, `config/env-variables`            |
| `hotfix/`   | Critical production fixes | `hotfix/auth-bug`, `hotfix/security-patch`               |
| `chore/`    | Maintenance tasks         | `chore/update-dependencies`, `chore/cleanup-code`        |

## Branch Naming Format

```
{prefix}/{description}
```

**Rules**:

- Use lowercase letters and hyphens
- Be descriptive but concise
- Use kebab-case for multi-word descriptions
- Maximum 64 characters

**Good examples**:

- `fix/gradle-java-home`
- `refactor/extract-validation`
- `docs/api-deployment-guide`
- `config/docker-mysql-volume`
- `hotfix/auth-bug-login`

**Bad examples**:

- `fix` (too vague)
- `fix/gradleConfig` (camelCase)
- `refactor/ExtractValidationLogic` (PascalCase)
- `docs/update_the_readme_file_for_deployment` (too verbose)

## Worktree Directory Naming

Use descriptive names for clarity:

```
../kokpick-task-{type}
```

| Worktree Name           | Task Type | Example Task          |
|-------------------------|-----------|-----------------------|
| `kokpick-task-gradle`   | Gradle 관련 | gradle.properties 수정  |
| `kokpick-task-docker`   | Docker 관련 | docker-compose.yml 수정 |
| `kokpick-task-deploy`   | 배포 관련     | GitHub Actions 배포 설정  |
| `kokpick-task-refactor` | 리팩토링      | 코드 리팩토링               |
| `kokpick-task-docs`     | 문서화       | README, API 문서        |
| `kokpick-task-hotfix`   | 핫픽스       | 긴급 버그 수정              |

## Naming Examples by Task Type

### CI/CD Fixes

```
Branch: fix/actions-deployment
Worktree: ../kokpick-task-deploy
```

### Gradle Configuration

```
Branch: fix/gradle-java-home
Worktree: ../kokpick-task-gradle
```

### Docker Configuration

```
Branch: config/docker-mysql-volume
Worktree: ../kokpick-task-docker
```

### Code Refactoring

```
Branch: refactor/extract-validator
Worktree: ../kokpick-task-refactor
```

### Documentation

```
Branch: docs/api-deployment-guide
Worktree: ../kokpick-task-docs
```

### Hotfix

```
Branch: hotfix/auth-bug-login
Worktree: ../kokpick-task-hotfix
```

## Base Branch Selection

| Base Branch | Use Case                   |
|-------------|----------------------------|
| `develop`   | Default development branch |
| `main`      | Production fixes           |
| `feature/*` | Feature-specific fixes     |
| Custom      | User-specified branch      |

## Complete Example

```bash
# Current branch: fix/exam
# Task: Fix gradle.properties

git worktree add ../kokpick-task-gradle develop -b fix/gradle-java-home
cd ../kokpick-task-gradle
# Make changes...
git push -u origin fix/gradle-java-home
# Create PR
cd ../kokpick
git fetch origin
git rebase origin/develop
git worktree remove ../kokpick-task-gradle
```
