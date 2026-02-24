---
name: java-check
description: Review Java code and suggest improvements
arguments:
  - name: file
    description: Path to Java file to review (or paste code)
    type: string
    required: false
  - name: focus
    description: Review focus (performance/security/best-practice/all)
    type: string
    required: false
    default: all
---

# Java Code Review

Get automated code review suggestions for your Java code.

## Usage

```
/java-check [file] [focus]
```

Or simply paste your Java code after the command.

## Focus Areas

- `performance` - Performance optimizations
- `security` - Security issues and vulnerabilities
- `best-practice` - Java best practices violations
- `all` - All areas (default)

## Review Checks

### Performance
- String concatenation optimization
- Collection selection
- Auto-boxing/unboxing issues
- Stream vs loop efficiency
- Thread pool configuration
- Caching opportunities

### Security
- SQL injection risks
- Path traversal vulnerabilities
- Cryptography issues
- Input validation
- Serialization risks

### Best Practice
- Exception handling
- Resource management
- Naming conventions
- Code duplication
- Design pattern usage
- Testing suggestions

## Examples

```
/java-check MyClass.java
/java-check MyClass.java performance
/java-check  # Paste code directly
```
