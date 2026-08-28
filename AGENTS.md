# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Low. I am a year 2 cs students. I have done cs1101s, cs2030s, cs2040s only
* IDE and level of expertise: same as above

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Testing

Tests are JUnit 5 (Jupiter) and run with `./gradlew test`.

### Layout and naming

* Test sources mirror the main sources: `src/main/java/zhangwei/task/Todo.java` is tested by
  `src/test/java/zhangwei/task/TodoTest.java`, in the same package.
* One test class per production class, named `<ClassUnderTest>Test`.
* Test methods use `featureUnderTest_testScenario_expectedBehavior()`, e.g.
  `parse_todoWithoutDescription_exceptionThrown()`.
* Tests that touch the file system use JUnit's `@TempDir`, never the real `data/` folder.

### Coverage target

* Aim to cover roughly the **top 50% highest-value methods** -- the complex, core, or critical
  ones. In this project that means, in priority order: `Parser`, `Storage`, `TaskList`,
  `CommandType`, the `Command` subclasses that change state (`Add`, `Delete`, `Mark`, `Unmark`),
  and the `Task` hierarchy's `toString`/status behaviour.
* A method is high value when it branches a lot, validates input, throws, converts between
  formats, or can lose the user's data. Trivial getters, constructors, and pure console printing
  are below the line and do not need tests of their own.
* For each covered method, include all reasonable cases: the normal case, the boundaries
  (empty list, first and last task number), and every way it can fail.
* The target is about value, not a raw percentage: 50% of the methods, chosen for risk, not
  50% of the lines.

### Keeping tests in step with the code

**JUnit tests must be updated in the same change as the code, so the 50% target still holds
afterwards.** Concretely, whenever you change production code:

* **New method** -- decide whether it falls in the high-value half. If it does, add tests for it
  in the matching `*Test` class in the same commit.
* **Changed behaviour** -- update the affected tests so they assert the new behaviour. Never
  delete a failing test to make the build green; either the test or the code is wrong, and it has
  to be worked out which.
* **New class** -- create its `*Test` class if the class carries any logic.
* **Removed or renamed method** -- remove or rename its tests too.
* Run `./gradlew test` before committing, and report the result. A change is not finished while
  the suite is red.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.
