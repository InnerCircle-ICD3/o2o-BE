---
name: query
description: Analyze database queries for N+1 problems, verify CQRS pattern, check Fetch Join usage, and review Query DSL/Native Query optimization. Use when user asks to analyze queries, mentions /query command, or reports performance issues.
---

# Query Analyzer

Analyzes **database queries** for performance issues and CQRS pattern compliance.

**Scope**: Repository, Service, Facade layers
**Focus**: N+1 queries, Fetch Join, CQRS separation, Query optimization

## Usage

```
/query {target}
```

**Examples**:

- `/query ExamQueryService` - Query Service
- `/query ProblemRepository` - Repository interface
- `/query ExamFacade.getExamResult` - Specific method
- `/query kokpick-exam` - entire module

## Workflow

**🚀 START QUERY SKILL**

### 1. Identify Target Type

**Repository Interface**: Check Command Query separation, Verify Fetch Join, Review Query DSL/Native Query
**Query Service**: Verify `@Transactional(readOnly = true)`, Check for direct JPA usage, Review CQRS
**Facade/Service**: Verify no direct EntityManager, Check Query/Info record pattern

### 2. Read Files

Use `Read` tool to examine:

- Target class file
- Repository implementation (JpaRepository)
- Related entities (for association analysis)

### 3. Analysis Checklist

**Detailed patterns** → `references/patterns.md`

#### N+1 Query Detection

- [ ] Check `@OneToMany` associations without `@EntityGraph`
- [ ] Look for lazy access in loops
- [ ] Search for `.size()`, `.stream()` on collections
- [ ] Review queries without explicit fetch joins

**Examples** → `references/patterns.md#n1-detection`

#### Fetch Join Verification

- [ ] Command queries: Use Fetch Join for complete Aggregate
- [ ] Query DSL: Use `join()`, `fetch()`
- [ ] EntityGraph: `@EntityGraph(attributePaths = {"examProblems"})`

#### MultipleBagFetchException Check

- [ ] Check nested `@OneToMany` in same query
- [ ] Look for `answer.objectiveChoices` pattern
- [ ] Verify `@EntityGraph` with multiple `@OneToMany` paths

**Examples & Solutions** → `references/patterns.md#multiplebagfetch`

#### CQRS Pattern Verification

- [ ] Command Service has `@Transactional`
- [ ] Query Service has `@Transactional(readOnly = true)`
- [ ] Command returns Entity/ID
- [ ] Query returns Info/DTO/Projection

#### Query Optimization

- [ ] Index Usage (WHERE on indexed columns)
- [ ] Pagination (Pageable for large result sets)
- [ ] Batch Operations (@Modifying for bulk updates)

### 4. Generate Report

```markdown
## Query Analysis: {Class/Method}

### ✅ Optimized Queries
- List queries following best practices

### ⚠️ Issues Found

#### N+1 Queries
- [ ] **Location**: `File:line`
  - **Issue**: Description
  - **Impact**: High (N+1 queries)
  - **Fix**: Add Fetch Join / EntityGraph

#### MultipleBagFetchException Risk
- [ ] **Location**: `File:line`
  - **Issue**: Two @OneToMany with Fetch Join
  - **Fix**: Separate queries or redesign

#### CQRS Violation
- [ ] **Location**: `File:line`
  - **Issue**: Query Service without readOnly
  - **Fix**: Add @Transactional(readOnly = true)

### 📊 Summary
- Total issues: {count}
- Performance impact: {High/Medium/Low}
```

**Severity Levels**:

- **High**: N+1 queries, MultipleBagFetchException, missing index
- **Medium**: CQRS violation, missing pagination
- **Low**: Code style, naming

---

**✅ END QUERY SKILL**

## Quick Reference

### N+1 Detection & Fix

**Patterns, examples, solutions** → `references/patterns.md`

### Fetch Join Patterns

**Single/collection joins, examples** → `references/patterns.md`

### CQRS Pattern

**Command vs Query examples** → `references/patterns.md`

### Common Issues

**MultipleBagFetchException, optimization** → `references/patterns.md`

## Related Skills

- `aggregate` - Analyze aggregate boundaries
- `refactor` - Refactor query-related anti-patterns
- `review` - Review query code for best practices