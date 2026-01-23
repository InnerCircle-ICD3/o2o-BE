---
name: review
description: Review code changes with CLAUDE.md guidelines. Check DDD principles, coding standards, and best practices. Use when user asks to review code, mentions /review command, or wants feedback on implementation quality.
---

# Code Reviewer

Reviews code against **Kokpick CLAUDE.md guidelines** and DDD best practices.

**Scope**: All layers (Domain, Service, Facade, Controller, Infrastructure)
**Focus**: DDD principles, coding standards, architecture patterns

## Usage

```
/review {target}
```

**Examples**:

- `/review Exam` - Domain entity
- `/review ExamFacade` - Facade class
- `/review ExamController` - Controller class
- `/review kokpick-exam` - entire module
- `/review .` - all changes in working directory

## Workflow

**🚀 START REVIEW SKILL**

### 1. Identify Review Scope

**Single class**: Review that class file
**Module**: Review all files in module directory
**Working directory**: Review all staged/unstaged changes

### 2. Read Files

Use `Read` tool to examine:

- Target class file
- Related dependencies (Repository, Service, DTO)
- CLAUDE.md guidelines for reference

### 3. Review Checklist

**Detailed checklists** → `references/ddd-checklist.md`, `references/coding-standards.md`

#### DDD Principles (Domain Layer)

- [ ] Aggregate Root has `@OneToMany` + `@JoinColumn` (unidirectional)
- [ ] Child entities do NOT reference Parent (no `@ManyToOne`)
- [ ] Aggregate Root extends `AbstractAggregateRoot` for Domain Events
- [ ] All entities extend `BaseEntity`
- [ ] `@Where(clause = "deleted_at IS NULL")` for soft delete
- [ ] `@Getter` only (no `@Setter`)
- [ ] `@Builder` pattern for creation
- [ ] Business logic in domain model (Rich Domain Model)

**Details** → `references/ddd-checklist.md`

#### Coding Standards

- [ ] No `var` keyword - explicit strong types only
- [ ] Constructor injection (no `@RequiredArgsConstructor`)
- [ ] `@Transactional` for Command Service
- [ ] `@Transactional(readOnly = true)` for Query Service
- [ ] DTO has `@Getter`, `@Builder`, `@Schema`
- [ ] Response DTO has `from()` method (static)
- [ ] Controller has `@Operation`, `@ApiResponses`

**Details** → `references/coding-standards.md`

#### Repository Pattern

- [ ] Interface in domain layer
- [ ] Implementation in infrastructure layer
- [ ] YAGNI: Only required methods
- [ ] Aggregate Root pattern: Return complete aggregate

#### Exception Handling

- [ ] Custom exceptions only (no direct `IllegalArgumentException`/`IllegalStateException`)
- [ ] Exceptions extend `DomainException`
- [ ] `ErrorCode` mapping with HTTP status codes

#### CQRS Pattern

- [ ] Command: JPA + Aggregate Root, `@Transactional`
- [ ] Query: Query Repository + Native Query, `@Transactional(readOnly = true)`
- [ ] Inner record for Query/Info

### 4. Generate Report

```markdown
## Code Review: {Class/Module}

### ✅ Strengths
- List what follows guidelines

### ⚠️ Issues Found

#### DDD Principles
- [ ] Issue description
  - **Location**: `File:line`
  - **Fix**: Suggestion

#### Coding Standards
- [ ] Issue description
  - **Location**: `File:line`
  - **Fix**: Suggestion

### 📋 Summary
- Total issues: {count}
- Severity: {High/Medium/Low}
```

**Severity Levels**:

- **High**: Violates DDD principles (Aggregate structure, Repository pattern)
- **Medium**: Violates coding standards (Service layer, DTO pattern)
- **Low**: Style/naming issues

---

**✅ END REVIEW SKILL**

## Quick Reference

### DDD Principles

**Aggregate, Entity, Repository patterns** → `references/ddd-checklist.md`

### Coding Standards

**Type declaration, Service, DTO, Controller** → `references/coding-standards.md`

### CLAUDE.md Guidelines

**Complete reference** → `references/guidelines.md`

## Related Skills

- `aggregate` - Analyze aggregate boundaries
- `refactor` - Refactor anti-patterns
- `query` - Analyze query patterns
