---
name: commit
description: Analyze git changes and create commits grouped by functionality. Use when user asks to commit changes, mentions /commit command, or wants organized commits following project conventions.
---

# Commit Organizer

Analyzes **git changes** and creates **organized commits** following Kokpick conventions.

**Scope**: All changed files (modified, added, deleted)
**Focus**: Intelligent grouping, meaningful commit messages, safety

## Usage

```
/commit [mode]
```

**Examples**:

- `/commit` - Interactive mode with user confirmation (default)
- `/commit auto` - Automatic mode (skip confirmation, use carefully)

## Workflow

**🚀 START COMMIT SKILL**

### 1. Get Git Status

```bash
git status --porcelain
```

Store the output - this shows all changed files with status indicators (M, A, D, ??).

### 2. Parse and Analyze Files

Parse the `git status --porcelain` output to get list of changed files.

**Format**: `{status} {file_path}`

- `M` - Modified
- `A` - Added (staged)
- `D` - Deleted
- `??` - Untracked

### 3. Group Files by Functionality

**Grouping Priority**:

1. **Domain/Module**: kokpick-exam, kokpick-quiz, kokpick-problem, kokpick-user, kokpick-curriculum, kokpick-user-study,
   kokpick-database, kokpick-external, kokpick-service-api, kokpick-admin-api, kokpick-common
2. **Special folders**: .claude, build, config, docs
3. **File type**: Entity, Repository, Service, Controller, Test, Docs

**Grouping Rules**:

- Files in same domain/module → Same group
- .claude files → Separate group (docs type)
- Maximum 15 files per group
- If group > 15 files → Split by sub-feature

**Algorithm**:

```
For each changed file:
  1. Check if file path contains a domain name (kokpick-exam, kokpick-quiz, etc.)
  2. If yes, add to that domain group
  3. If .claude file, add to claude group
  4. If build/config file, add to config group
  5. Otherwise, add to common group

For each group:
  If file count > 15:
    Split by layer (domain, service, controller, test)
```

### 4. Determine Commit Type for Each Group

Analyze file paths to determine commit type:

- **feat**: New files (*.java not in test), new endpoints
- **fix**: Bug fixes (file names contain "fix", "bug", "error")
- **refactor**: Restructuring (existing files modified, large line reductions)
- **test**: Test files (*Test.java, *Tests.java)
- **docs**: *.md files
- **config**: build.gradle.kts, application*.yml, settings.*

### 5. Generate Commit Message

For each group:

```
{type}({scope}): {summary}

Co-Authored-By: Claude <noreply@anthropic.com>
```

**Summary generation**:

- Extract domain from module name (e.g., "kokpick-exam" → "exam")
- Action: "구조 개선", "추가", "수정"
- Keep under 80 characters

### 6. Present Plan to User

Display commit plan:

```
=== Commit Plan ===

Found {total_files} changed files across {group_count} groups:

Group 1: {name} ({file_count} files)
  Type: {type}
  Message: {commit_message}

  Files:
    {status} {file_path}

=== Actions ===
1. Execute commits
2. Cancel

Enter choice [1-2]:
```

### 7. Execute Commits

For each approved group:

```bash
# Add files for this group
git add {file1} {file2} ...

# Commit with message
git commit -m "{commit_message}

Co-Authored-By: Claude <noreply@anthropic.com>"
```

**Important**: Add files for EACH group separately, then commit.

### 8. Verify and Show Results

```bash
git log --oneline -{group_count + 2}
git status
```

Show commit summary:

```
✓ Committed: {commit_message_1}
✓ Committed: {commit_message_2}
...
```

---

**✅ END COMMIT SKILL**

## Implementation Steps

### Step 1: Run git status

```bash
git status --porcelain
```

**Output example**:

```
M  .claude/skills/work-parallel/SKILL.md
M  kokpick-exam/domain/Exam.java
A  kokpick-quiz/service/QuizService.java
```

### Step 2: Parse and group files

Parse each line from git status output.

**Grouping logic**:

- Extract file paths
- Determine group by matching domain names in path
- Create groups with list of files

### Step 3: Split large groups

If a group has > 15 files:

```
kokpick-exam (20 files):
  - Split by:
    1. kokpick-exam/domain (8 files) → Commit 1
    2. kokpick-exam/service (6 files) → Commit 2
    3. kokpick-exam/application (6 files) → Commit 3
```

### Step 4: Generate commit messages

For each group, generate:

- Type based on file analysis
- Scope from domain name
- Summary from context

### Step 5: Execute commits sequentially

```
For group in groups:
  1. git add {files_in_group}
  2. git commit -m "{message}"
  3. Print success message
```

### Step 6: Show final results

```
=== Commit Summary ===

Created {count} commits:

1. {commit_message_1}
2. {commit_message_2}
...

=== Git Status ===
On branch {branch}
nothing to commit, working tree clean
```

## Quick Reference

### Commit Message Patterns

**Type detection, format, examples** → `references/commit-message-patterns.md`

### Grouping Strategies

**When to split/merge, module boundaries** → `references/grouping-strategies.md`

### Safety Checks

**Pre-commit validation, rollback options** → `references/safety-checks.md`

## Commit Conventions

**Types**: feat, fix, refactor, test, docs, config
**Format**: `{type}({scope}): {summary}` or `{type}: {summary}`
**Required**: `Co-Authored-By: Claude <noreply@anthropic.com>`

## Example Execution

```bash
# Step 1: Get status
git status --porcelain

# Step 2: Parse output and create groups
Group 1: kokpick-exam (5 files) → refactor(exam): Exam 도메인 구조 개선
Group 2: kokpick-quiz (3 files) → feat(quiz): 퀴즈 생성 기능 추가
Group 3: .claude/skills (10 files) → docs: Skills 문서 업데이트

# Step 3: Execute commits
git add kokpick-exam/...
git commit -m "refactor(exam): Exam 도메인 구조 개선..."

git add kokpick-quiz/...
git commit -m "feat(quiz): 퀴즈 생성 기능 추가..."

git add .claude/skills/...
git commit -m "docs: Skills 문서 업데이트..."
```

## Related Skills

- `work-parallel` - Branch management and workflow
- `refactor` - Code refactoring patterns
