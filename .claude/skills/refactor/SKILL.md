---
name: refactor
description: Refactor code to eliminate YAGNI violations, remove unnecessary dependencies, extract duplicated code, and apply DDD best practices. Use when user asks to refactor, mentions /refactor command, or wants to improve code quality.
---

# Refactoring Assistant

Refactors code to eliminate **YAGNI violations**, **code smells**, and **DDD anti-patterns**.

**Focus**: YAGNI, DRY, SOLID, DDD principles

## Usage

```
/refactor {target}
```

**Examples**:

- `/refactor ExamFacade` - Remove unnecessary repository calls
- `/refactor ProblemRepository` - Remove unused methods
- `/refactor ExamCommandService` - Extract duplicated validation
- `/refactor kokpick-exam` - Entire module refactoring
- `/refactor .` - All changes in working directory

## Core Workflow

**🚀 START REFACTOR SKILL**

### 1. Identify Scope

Single class, Module, or Working directory

### 2. Read Files

Target class, dependencies, CLAUDE.md guidelines, test files

### 3. Detect Code Smells

**YAGNI** → `references/yagni-patterns.md`
**DRY** → `references/dry-patterns.md`
**DDD** → `references/ddd-antipatterns.md`
**SOLID** → `references/solid-violations.md`

### 4. Generate Plan

```markdown
## Refactoring Plan: {Class/Module}

### 🔍 Detected Issues
- YAGNI violations ({count})
- Code duplication ({count})
- DDD anti-patterns ({count})

### 📋 Steps
1. Remove unused code
2. Extract duplicated logic
3. Apply DDD patterns
4. Update tests

### ✅ Benefits
- Eliminates YAGNI violations
- Removes duplication
- Improves DDD alignment
```

### 5. Apply Refactoring

Follow patterns in `references/refactoring-patterns.md`

### 6. Update Tests

```bash
./gradlew test --tests "*{Domain}*Test"
./gradlew jacocoTestReport
```

### 7. Verify

```bash
./gradlew test
./gradlew :kokpick-service-api:bootRun --args='--spring.profiles.active=dev'
```

### 8. Commit

```
refactor: {summary}

- Changes description

Co-Authored-By: Claude <noreply@anthropic.com>
```

**✅ END REFACTOR SKILL**

## Quick Reference

### Code Smells

- **YAGNI violations** → `references/yagni-patterns.md`
- **Code duplication** → `references/dry-patterns.md`
- **DDD anti-patterns** → `references/ddd-antipatterns.md`
- **SOLID violations** → `references/solid-violations.md`

### Refactoring Patterns

- **Extract Method** → `references/refactoring-patterns.md`
- **Extract Class** → `references/refactoring-patterns.md`
- **Replace Conditional with Polymorphism** → `references/refactoring-patterns.md`

### Checklist

- Complete checklist → `references/checklist.md`

## Related Skills

- `review` - Review code before refactoring
- `aggregate` - Analyze aggregate boundaries
- `query` - Detect N+1 query issues
- `test-generator` - Regenerate tests after refactoring
