# Safety Checks

## Pre-Commit Validation

### Environment Checks

```bash
# Check for merge conflicts
if ! git diff --quiet; then
    if grep -r "<<<<<<< HEAD" . 2>/dev/null; then
        echo "❌ Merge conflicts detected. Please resolve conflicts first."
        return 1
    fi
fi

# Check current branch
current_branch=$(git branch --show-current)
if [ "$current_branch" = "main" ] || [ "$current_branch" = "master" ]; then
    echo "⚠️  Warning: You're on $current_branch branch."
    read -p "Continue anyway? (y/N): " confirm
    if [ "$confirm" != "y" ]; then
        echo "Aborted."
        return 1
    fi
fi

# Check for uncommitted changes in unrelated files
if [ -n "$(git status --porcelain | grep -v '^??')" ]; then
    echo "⚠️  Warning: You have uncommitted changes."
    git status --short
    read -p "Continue? (y/N): " confirm
    if [ "$confirm" != "y" ]; then
        echo "Aborted."
        return 1
    fi
fi
```

### File Validation

```bash
# Check for large binary files
large_files=$(find . -type f -size +10M ! -path "./.git/*")
if [ -n "$large_files" ]; then
    echo "⚠️  Warning: Large files detected:"
    echo "$large_files"
    read -p "Include in commit? (y/N): " confirm
    if [ "$confirm" != "y" ]; then
        echo "Aborted."
        return 1
    fi
fi

# Check for sensitive files
sensitive_patterns=(
    "*.pem"
    "*.key"
    "*.secret"
    "passwords.txt"
    ".env.local"
)

for pattern in "${sensitive_patterns[@]}"; do
    if git diff --cached --name-only | grep -E "$pattern"; then
        echo "❌ Error: Sensitive files detected: $pattern"
        echo "Please remove them from commit."
        return 1
    fi
done
```

### Commit Message Validation

```bash
# Validate commit message format
validate_commit_message() {
    local message="$1"

    # Check type
    if ! echo "$message" | grep -qE "^(feat|fix|refactor|test|docs|config)\("; then
        echo "❌ Invalid commit type. Use: feat|fix|refactor|test|docs|config"
        return 1
    fi

    # Check length
    if [ ${#message} -gt 100 ]; then
        echo "⚠️  Warning: Commit message too long (${#message} chars). Max 100."
    fi

    # Check for Co-Authored-By
    if ! echo "$message" | grep -q "Co-Authored-By:"; then
        echo "⚠️  Warning: Missing Co-Authored-By tag"
    fi

    return 0
}
```

## Execution-Time Validation

### Before Each Commit

```bash
# Show diff summary
echo "=== Diff Summary ==="
git diff --cached --stat

# Show commit message
echo "=== Commit Message ==="
echo "$commit_message"

# Confirm
read -p "Commit this group? (y/N): " confirm
if [ "$confirm" != "y" ]; then
    echo "Skipped."
    continue
fi
```

### After Each Commit

```bash
# Verify commit success
if ! git commit -m "$commit_message"; then
    echo "❌ Commit failed!"
    read -p "Retry with modified message? (y/N): " retry
    if [ "$retry" = "y" ]; then
        # Allow retry with modified message
    else
        echo "Aborted. Remaining groups skipped."
        return 1
    fi
fi

echo "✓ Committed successfully"
```

## Post-Commit Safety

### Rollback Options

```bash
# After all commits
echo ""
echo "=== Commit Summary ==="
git log --oneline -3

echo ""
echo "=== Rollback Options ==="
echo "To undo last commit:"
echo "  git reset HEAD~1"
echo ""
echo "To undo all commits:"
echo "  git reset HEAD~<n>  # Replace <n> with number of commits"
echo ""
echo "To rollback changes (keep commits):"
echo "  git reset --soft HEAD~<n>"
```

### Verification

```bash
# Verify commit success
if [ $? -eq 0 ]; then
    echo "✓ All commits created successfully"

    # Show final status
    echo ""
    echo "=== Final Status ==="
    git status --short

    # Show commit log
    echo ""
    echo "=== Recent Commits ==="
    git log --oneline -5
else
    echo "❌ Some commits failed. Please check and retry."
    return 1
fi
```

## Error Handling

### Git Add Failure

```bash
# If git add fails
if ! git add $files; then
    echo "❌ Failed to add files: $files"
    read -p "Continue with next group? (y/N): " continue
    if [ "$continue" != "y" ]; then
        echo "Aborted."
        return 1
    fi
fi
```

### Git Commit Failure

```bash
# If git commit fails
if ! git commit -m "$message"; then
    echo "❌ Failed to commit"

    # Diagnose issue
    echo ""
    echo "=== Error Details ==="
    git status

    # Options
    read -p "Options: [r]etry [s]kip [a]bort: " choice
    case $choice in
        r)
            # Retry with modified message
            ;;
        s)
            # Skip this group
            continue
            ;;
        a)
            # Abort all
            echo "Aborted."
            return 1
            ;;
    esac
fi
```

### Merge Conflict Detection

```bash
# Check for merge conflict markers
if git diff --name-only | xargs grep -l "<<<<<<< HEAD"; then
    echo "❌ Merge conflict detected!"
    echo "Please resolve conflicts before committing."
    echo ""
    echo "Conflicted files:"
    git diff --name-only | grep "<<<<<<< HEAD"
    return 1
fi
```

## User Interaction Prompts

### Initial Confirmation

```
=== Commit Plan ===

Found 8 changed files across 2 groups:

Group 1: kokpick-exam domain (5 files)
  Type: refactor
  Scope: exam
  Message: refactor(exam): Exam 도메인 HTTP 상태 코드 재분배

  Files:
    M kokpick-domains/kokpick-exam/.../ExamValidationException.java
    M kokpick-domains/kokpick-exam/.../ExamValidator.java
    ...

Group 2: Claude skills documentation (3 files)
  Type: docs
  Scope: (none)
  Message: docs: Skill 문서화 및 참고자료 추가

  Files:
    M .claude/skills/aggregate/SKILL.md
    M .claude/skills/query/SKILL.md
    ...

=== Actions ===
1. Approve and commit all groups
2. Modify commit messages
3. Split/merge groups
4. Cancel

Choose action [1-4]:
```

### Modify Message Prompt

```
=== Modify Commit Messages ===

Current messages:
  1. refactor(exam): Exam 도메인 HTTP 상태 코드 재분배
  2. docs: Skill 문서화 및 참고자료 추가

Enter new message (or press Enter to keep):
```

### Split Group Prompt

```
=== Split Group ===

Group 1: kokpick-exam domain (5 files)

Split by:
  1. Layer (domain, service, application)
  2. Feature (specific functionality)
  3. File type (entities, tests, docs)
  4. Cancel

Choose split method [1-4]:
```

## Safety Checklist

### Before Execution

- [ ] No merge conflicts
- [ ] No uncommitted unrelated changes
- [ ] No sensitive files included
- [ ] Branch is appropriate (not main/master unless intended)
- [ ] Commit messages follow format
- [ ] Co-Authored-By tag included

### During Execution

- [ ] Each commit verified before execution
- [ ] Diff summary shown for each group
- [ ] User confirmation obtained
- [ ] Rollback options documented

### After Execution

- [ ] All commits verified successful
- [ ] Git log shows expected commits
- [ ] Working directory clean
- [ ] Rollback instructions provided
- [ ] No errors or warnings

## Emergency Procedures

### Abort All Commits

```bash
# If critical error occurs
echo "Aborting all commits..."

# Reset to last known good state
git reset HEAD~<n>  # Reset last n commits

# Or reset to specific commit
git reset <commit-hash>

# Verify
git log --oneline -5
git status
```

### Recover from Bad Commit

```bash
# Option 1: Reset (remove commit, keep changes)
git reset --soft HEAD~1
# Make corrections
git add .
git commit -m "corrected message"

# Option 2: Amend (fix last commit)
git commit --amend -m "corrected message"

# Option 3: Revert (create new commit that undoes)
git revert HEAD
```

### Undo Multiple Commits

```bash
# Undo last 3 commits (keep changes)
git reset --soft HEAD~3

# Undo last 3 commits (discard changes)
git reset --hard HEAD~3  # ⚠️ Destructive!

# Verify before discard
git log --oneline -5
```

## Best Practices

### For Users

1. **Review plan carefully** before approving
2. **Check commit messages** for accuracy
3. **Verify file groupings** make sense
4. **Test rollback commands** in safe environment first
5. **Keep backups** of important branches

### For Implementation

1. **Always show diff** before committing
2. **Get explicit confirmation** for each commit
3. **Provide clear error messages** with recovery options
4. **Document rollback procedures** after execution
5. **Validate all inputs** before git operations