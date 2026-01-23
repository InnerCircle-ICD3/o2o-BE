---
name: work-parallel
description: Create parallel development branch using git worktree. Use when user mentions "병렬", "worktree", or wants to fix issues without interrupting main feature branch work. Enables parallel development by creating isolated worktree for fixes, CI changes, refactoring, or any modifications that need separate PR workflow.
---

# Parallel Development with Git Worktree

Creates **parallel development branch** using **git worktree** to work on fixes without interrupting main feature
branch.

**Purpose**: 병렬 작업을 위해 git worktree를 활용하여 메인 브랜치의 작업을 중단하지 않고 독립적으로 수정 작업 수행

**Key Benefit**: Zero conflict development - work on fixes while main feature branch remains untouched

## Trigger Keywords

**Korean**: "병렬", "워크트리", "worktree", "별도 브랜치", "독립 작업"
**English**: "parallel", "worktree", "separate branch", "isolated work"

## Usage

```
/work-parallel {task_description} {target_branch}
```

**Examples**:

- `/work-parallel gradle.java.home error fix/exam` - fix/exam 작업 중 Gradle 설정 수정
- `/work-parallel docker compose mysql settings feature/quiz` - Docker 설정 수정
- `/work-parallel code cleanup utils package fix/exam` - 유틸 리팩토링
- `/work-parallel update README deployment guide main` - 문서화

## Core Workflow

**🚀 START WORK_PARALLEL SKILL**

### 1. 현재 상태 파악

```bash
git branch --show-current
# Identify: What needs fixing? Can it be independent? Base branch?
```

### 2. Worktree 생성

```bash
git worktree add ../kokpick-task-{type} {base_branch} -b {new_branch}
# Example: git worktree add ../kokpick-task-gradle develop -b fix/gradle-java-home
git worktree list
```

### 3. 작업 수행

```bash
cd ../kokpick-task-{type}
# Implement fix/modification
```

### 4. 로컬 전용 파일 (필요시)

```bash
# Create .gitignore-ed files in BOTH worktree and original project
echo "org.gradle.java.home=C:/Path/To/Java" > gradle.properties.local
```

### 5. 테스트

```bash
./gradlew clean build --no-daemon
./gradlew test --no-daemon
```

### 6. Commit & Push

```bash
git add <files>
git commit -m "fix: summary"
git push -u origin {branch}
```

### 7. PR 위임

사용자에게 PR 생성/머지 요청. **머지 완료 전까지 다음 단계 금지**

### 8. Merge 확인 후 Rebase

```bash
cd C:/Users/USER/IdeaProjects/kokpick
git fetch origin
git stash push -m "WIP: 병렬 작업 rebase"
git rebase origin/develop
git stash pop
```

### 9. Worktree 정리

```bash
git worktree remove ../kokpick-task-{type}
```

### 10. 원래 브랜치 확인

```bash
git branch --show-current
./gradlew clean build --no-daemon
```

**✅ END WORK_PARALLEL SKILL**

## Quick Reference

### Branch Naming Patterns

- `fix/*` - Bug fixes, CI/CD → `references/branch-naming.md`
- `refactor/*` - Code refactoring → `references/branch-naming.md`
- `docs/*` - Documentation → `references/branch-naming.md`
- `config/*` - Configuration → `references/branch-naming.md`
- `hotfix/*` - Critical fixes → `references/branch-naming.md`

### Task Examples

- CI/CD Fixes → `references/examples.md`
- Code Refactoring → `references/examples.md`
- Documentation → `references/examples.md`
- Configuration → `references/examples.md`
- Hotfixes → `references/examples.md`

### Commit Messages

- CI Fix examples → `references/commit-messages.md`
- Refactoring examples → `references/commit-messages.md`
- Documentation examples → `references/commit-messages.md`

## Related Skills

- `migrate` - Database migration (Flyway)
- `refactor` - Code refactoring with YAGNI analysis
- `review` - Code review before commit
- `test-generator` - Generate tests for changes
- `aggregate` - DDD aggregate analysis
