---
name: java-practice
description: Generate Java coding practice problems and solutions
arguments:
  - name: topic
    description: Practice topic (e.g., "collections", "concurrency", "stream")
    type: string
    required: false
    - name: difficulty
    description: Difficulty level (easy/medium/hard)
    type: string
    required: false
    default: medium
---

# Java Practice Problems

Get coding practice problems with solutions to improve your Java skills.

## Usage

```
/java-practice [topic] [difficulty]
```

## Topics

- `basics` - Core Java fundamentals
- `collections` - Collection framework
- `stream` - Stream API
- `concurrency` - Concurrent programming
- `string` - String manipulation
- `oop` - Object-oriented programming
- `algorithm` - Algorithm implementation
- `design-pattern` - Design pattern usage
- `spring` - Spring framework
- `random` - Random topic

## Difficulty Levels

- `easy` - Basic concepts and simple problems
- `medium` - Practical problems requiring some thought (default)
- `hard` - Complex scenarios requiring deep understanding

## Example Problems

### Collections - Medium
**Problem:** Given a list of strings, count the frequency of each word and return a map with the top 5 most frequent words.

### Concurrency - Medium
**Problem:** Implement a thread-safe cache that can store up to 100 items and expires items after 1 hour of inactivity.

### Stream - Hard
**Problem:** Given a list of employees with department and salary, find the department with the highest average salary.

## Examples

```
/java-practice collections medium      # Collection problems
/java-practice stream hard           # Hard stream problems
/java-practice                      # Random medium problem
```
