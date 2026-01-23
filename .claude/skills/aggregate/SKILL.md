---
name: aggregate
description: Analyze DDD Aggregate boundaries, verify Aggregate Root pattern, check association mappings, and review Domain Events usage. Use when user asks to analyze aggregates, mentions /aggregate command, or needs DDD design verification.
---

# Aggregate Analyzer

Analyzes **DDD Aggregate** design and association mappings.

**Scope**: Domain entities, Aggregates, Value Objects
**Focus**: Aggregate boundaries, Root pattern, association mappings, Domain Events

## Usage

```
/aggregate {domain}
```

**Examples**:

- `/aggregate Exam` - Exam Aggregate (Root + Children)
- `/aggregate Problem` - Problem Aggregate
- `/aggregate kokpick-exam` - all aggregates in module
- `/aggregate kokpick-domains` - all domain aggregates

## Workflow

**🚀 START AGGREGATE SKILL**

### 1. Identify Aggregate Structure

**Find Entities**:

- Use `Glob` to find `*{Domain}*.java` in domain module
- Use `Grep` to find `@Entity` annotations

**Example structure**:

```
kokpick-domains/kokpick-exam/
├── domain/
│   ├── Exam.java              (Aggregate Root)
│   ├── ExamProblem.java       (Child Entity)
│   ├── ExamUserAnswer.java    (Aggregate Root)
│   └── ExamUserChoice.java    (Child Entity)
```

### 2. Read Entity Files

Use `Read` tool to examine:

- All entity classes in the domain
- Focus on: `@OneToMany`, `@ManyToOne`, `@OneToOne`, `@JoinColumn`

### 3. Analysis Checklist

**Detailed patterns** → `references/patterns.md`

#### Aggregate Root Identification

- [ ] Has `@OneToMany` associations
- [ ] Extends `AbstractAggregateRoot<Exam>`
- [ ] No `@ManyToOne` pointing to other Aggregates
- [ ] Repository interface exists

#### Child Entity Pattern

- [ ] Only `@ManyToOne` within same Aggregate (to Root)
- [ ] NO `@ManyToOne` to other Aggregates
- [ ] Extends `BaseEntity`
- [ ] No Repository interface

#### Association Mapping Pattern

- [ ] Root has `@OneToMany @JoinColumn` (unidirectional)
- [ ] Child does NOT reference Root
- [ ] Cross-Aggregate references use IDs only
- [ ] No `@ManyToOne` between Aggregates

#### Domain Events

- [ ] Root extends `AbstractAggregateRoot<T>`
- [ ] Uses `registerEvent()` to publish events
- [ ] Event classes exist in `domain/event/`
- [ ] `@EventListener` handles events

#### Aggregate Boundaries

- [ ] Each Root has its own Repository
- [ ] Transactions don't span multiple Aggregates
- [ ] Consistency boundary is clear

#### Entity Inheritance

- [ ] All entities extend `BaseEntity`
- [ ] `@Where(clause = "deleted_at IS NULL")` for soft delete
- [ ] `@EntityListeners(AuditingEntityListener.class)` enabled

### 4. Generate Report

```markdown
## Aggregate Analysis: {Domain}

### 📦 Aggregate Structure
**Aggregate Roots**: List roots and children

### ✅ Correct Patterns
- List patterns following DDD principles

### ⚠️ Issues Found
#### Aggregate Root Pattern
- [ ] **Location**: `File:line` - Issue - Fix

#### Association Mapping
- [ ] **Location**: `File:line` - Issue - Fix

#### Cross-Aggregate References
- [ ] **Location**: `File:line` - Issue - Fix

### 📋 Summary
- Total Aggregates: {count}
- Aggregate Roots: {count}
- Issues found: {count}
- Severity: {High/Medium/Low}
```

**Severity Levels**:

- **High**: Child references Root, cross-Aggregate direct reference
- **Medium**: Inconsistent boundaries, missing Domain Events
- **Low**: Naming, code style

---

**✅ END AGGREGATE SKILL**

## Quick Reference

### Association Mapping Patterns

**Root → Child, Cross-Aggregate, Domain Events** → `references/patterns.md`

### Examples

**Correct/wrong patterns, All Kokpick aggregates** → `references/examples.md`

### Key Rules

**Kokpick Rule**: "애그리거트의 상위 엔티티만 연관관계 참조를 가지도록"

- **Aggregate 내부**: Root → Child (`@OneToMany @JoinColumn` 단방향)
- **Aggregate 간**: ID 참조만 (`Long examId`, `Long problemId`)

## Related Skills

- `refactor` - Refactor DDD anti-patterns
- `query` - Analyze query patterns for CQRS
- `review` - Review code for DDD compliance
