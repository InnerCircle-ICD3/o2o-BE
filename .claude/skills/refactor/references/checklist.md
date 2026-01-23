# Code Smell Detection Checklist

## YAGNI Violations

### Repository Methods

- [ ] `findAll()` - Never called
- [ ] `delete()` - Never called
- [ ] `findByXxx()` - Never called
- [ ] Search methods with no usage

### Service Methods

- [ ] Export methods (exportToExcel, exportToPdf) - Never called
- [ ] Statistics methods - Never called
- [ ] Batch operations - Never called

### DTO Fields

- [ ] Fields never populated in builders
- [ ] Fields never used in templates/responses
- [ ] Legacy fields from old requirements

### Entity Fields

- [ ] Migration artifact fields (legacyId, oldCode)
- [ ] Fields mapped but never accessed
- [ ] Deprecated fields with @Deprecated

### Utility Methods

- [ ] Static methods never called
- [ ] Helper methods with no usage

### Exception Classes

- [ ] Custom exceptions never thrown
- [ ] Factory methods never invoked

## Code Duplication

### Validation Logic

- [ ] Same null checks across Services
- [ ] Same validation rules across classes
- [ ] Same error messages in multiple places

### Conversion Logic

- [ ] Same toResponse() in multiple Facades
- [ ] Same DTO builders across classes
- [ ] Same Entity→DTO mapping

### Error Handling

- [ ] Same try-catch patterns in Controllers
- [ ] Same exception messages
- [ ] Same error response formatting

### Query Logic

- [ ] Same @Query definitions in Repositories
- [ ] Same WHERE clauses
- [ ] Same JOIN patterns

### Other Duplication

- [ ] Similar Entity constructors
- [ ] Repeated null checks
- [ ] Referred code patterns

## DDD Anti-Patterns

### Domain Model

- [ ] Anemic Domain: Setter methods in Entity
- [ ] Business logic in Service instead of Entity
- [ ] Entity with only getters

### Aggregates

- [ ] Bidirectional relationships (@ManyToOne in child)
- [ ] Child references Parent
- [ ] Aggregate crossing boundaries

### Architecture

- [ ] Repository in Facade (bypasses Service)
- [ ] Entity exposure in Controller
- [ ] DTO not used in API responses

### Patterns

- [ ] Missing Factory classes for complex creation
- [ ] No Domain Events for state changes
- [ ] Service directly modifies other Aggregates

## SOLID Violations

### SRP (Single Responsibility)

- [ ] Service does 3+ things (Command + Query + Validation)
- [ ] Controller handles business logic
- [ ] Repository contains business rules

### OCP (Open/Closed)

- [ ] Hard-coded type checks (if-else chains)
- [ ] Must modify existing code for new types
- [ ] No abstraction for variations

### LSP (Liskov Substitution)

- [ ] Child throws unexpected exception
- [ ] Child returns null when parent doesn't
- [ ] Child has stronger preconditions

### ISP (Interface Segregation)

- [ ] Fat interface (Repository with 10+ methods)
- [ ] Clients forced to depend on unused methods
- [ ] God interface

### DIP (Dependency Inversion)

- [ ] Direct instantiation (`new ExamRepositoryImpl()`)
- [ ] Depends on concrete classes
- [ ] No interface abstraction

## Detection Commands

```bash
# Find potentially unused Repository methods
grep -r "findAll(" kokpick-domains/
grep -r "delete(" kokpick-domains/

# Find duplicate validation patterns
grep -r "must not be null" kokpick-*/src/main/java/

# Find toResponse in Facades
grep -r "toResponse(" kokpick-applications/

# Find setters in Entities
grep -r "public void set" kokpick-domains/ | grep "\.java:"

# Find new keyword
grep -r "new [A-Z].*Repository\|Service\|Validator" kokpick-*/src/main/java/
```

## Refactoring Priority

| Priority | Type              | Impact                   |
|----------|-------------------|--------------------------|
| **P0**   | YAGNI violations  | Removes dead code        |
| **P1**   | Code duplication  | Improves maintainability |
| **P2**   | DDD anti-patterns | Aligns with architecture |
| **P3**   | SOLID violations  | Improves design quality  |
