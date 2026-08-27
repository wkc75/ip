# UI Test Plan

Manual-style UI tests for the ZhangWei chatbot, run automatically by the
`test-ui` skill. Each case pipes its input into a freshly started program and
compares the console output against the expected output.

## Configuration

- Source directory: `src/main/java`
- Main class: `ZhangWei`
- Comparison starts after the line: `What can I do for you?`

The banner and greeting are printed on every run, so comparison starts after
the greeting's last line. Pass `--full` to the runner to compare the whole
output instead, including the banner.

## Conventions

- Every input block must end with `bye`, otherwise the program never exits.
- The first fenced block in a case is the input; the second is the expected output.
- Leading and trailing blank lines are ignored; trailing spaces on a line are ignored.
- Each case runs in its own empty working directory, so files the program
  saves (such as `data/zhangwei.txt`) never leak from one case into the next.

## Test cases

### TC1 - Greet and exit

**Aim:** Verify the chatbot exits cleanly on `bye` and prints the farewell.

```text
bye
```

```text
Bye. Hope to see you again soon!
```

### TC2 - Add a todo

**Aim:** Verify `todo` stores a task with the `[T]` icon and reports the new count.

```text
todo read book
bye
```

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Bye. Hope to see you again soon!
```

### TC3 - Add a deadline

**Aim:** Verify `deadline` parses the `/by` argument and shows it in parentheses.

```text
deadline return book /by Sunday
bye
```

```text
Got it. I've added this task:
  [D][ ] return book (by: Sunday)
Now you have 1 tasks in the list.
Bye. Hope to see you again soon!
```

### TC4 - Add an event

**Aim:** Verify `event` parses both `/from` and `/to` arguments.

```text
event project meeting /from Mon 2pm /to 4pm
bye
```

```text
Got it. I've added this task:
  [E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 1 tasks in the list.
Bye. Hope to see you again soon!
```

### TC5 - List an empty task list

**Aim:** Verify `list` prints only its header when no tasks have been added.

```text
list
bye
```

```text
Here are the tasks in your list:
Bye. Hope to see you again soon!
```

### TC6 - List all three task types

**Aim:** Verify `list` numbers tasks from 1 and renders each type correctly.

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
list
bye
```

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[D][ ] return book (by: June 6th)
3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Bye. Hope to see you again soon!
```

### TC7 - Mark and unmark a task

**Aim:** Verify `mark` sets the done status, `unmark` clears it, and both are
reflected in a subsequent `list`.

```text
todo read book
mark 1
list
unmark 1
list
bye
```

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Nice! I've marked this task as done:
  [T][X] read book
Here are the tasks in your list:
1.[T][X] read book
OK, I've marked this task as not done yet:
  [T][ ] read book
Here are the tasks in your list:
1.[T][ ] read book
Bye. Hope to see you again soon!
```

### TC8 - Mark the correct task among several

**Aim:** Verify the 1-based task number maps to the right task (off-by-one check).

```text
todo aaa
todo bbb
todo ccc
mark 2
list
bye
```

```text
Got it. I've added this task:
  [T][ ] aaa
Now you have 1 tasks in the list.
Got it. I've added this task:
  [T][ ] bbb
Now you have 2 tasks in the list.
Got it. I've added this task:
  [T][ ] ccc
Now you have 3 tasks in the list.
Nice! I've marked this task as done:
  [T][X] bbb
Here are the tasks in your list:
1.[T][ ] aaa
2.[T][X] bbb
3.[T][ ] ccc
Bye. Hope to see you again soon!
```

### TC9 - Todo without a description

**Aim:** Verify an empty todo description is reported instead of stored.

```text
todo
bye
```

```text
A todo needs a description. For example: todo read book
Bye. Hope to see you again soon!
```

### TC10 - Unrecognised command

**Aim:** Verify unknown input is rejected with the list of known commands,
rather than being stored as a task.

```text
blah
bye
```

```text
I don't know the command "blah". I understand: todo, deadline, event, list, mark, unmark, delete, bye.
Bye. Hope to see you again soon!
```

### TC11 - Deadline without /by

**Aim:** Verify a deadline missing its /by argument is reported.

```text
deadline return book
bye
```

```text
A deadline needs a description and a /by. For example: deadline return book /by Sunday
Bye. Hope to see you again soon!
```

### TC12 - Event missing /to

**Aim:** Verify an event missing one of its two date arguments is reported.

```text
event project meeting /from Mon 2pm
bye
```

```text
An event needs a description, a /from and a /to. For example: event project meeting /from Mon 2pm /to 4pm
Bye. Hope to see you again soon!
```

### TC13 - Mark with a bad task number

**Aim:** Verify a missing, non-numeric, or out-of-range task number is each
reported specifically.

```text
mark
mark abc
mark 3
todo read book
mark 3
bye
```

```text
Which task? For example: mark 2
"abc" is not a task number. For example: mark 2
You have no tasks yet, so there is no task 3.
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
There is no task 3. You have 1 tasks.
Bye. Hope to see you again soon!
```

### TC14 - Chatbot continues after an error

**Aim:** Verify an error does not end the session or corrupt the task list.

```text
todo read book
blah
list
bye
```

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
I don't know the command "blah". I understand: todo, deadline, event, list, mark, unmark, delete, bye.
Here are the tasks in your list:
1.[T][ ] read book
Bye. Hope to see you again soon!
```

### TC15 - Delete a task

**Aim:** Verify `delete` removes the right task, shows it, and reports the new
count, and that the remaining tasks are renumbered.

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
delete 2
list
bye
```

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
Got it. I've added this task:
  [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Now you have 3 tasks in the list.
Noted. I've removed this task:
  [D][ ] return book (by: June 6th)
Now you have 2 tasks in the list.
Here are the tasks in your list:
1.[T][ ] read book
2.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
Bye. Hope to see you again soon!
```

### TC16 - Delete with a bad task number

**Aim:** Verify `delete` rejects a missing or out-of-range task number the same
way `mark` does, and leaves the list untouched.

```text
todo read book
delete
delete 9
list
bye
```

```text
Got it. I've added this task:
  [T][ ] read book
Now you have 1 tasks in the list.
Which task? For example: delete 2
There is no task 9. You have 1 tasks.
Here are the tasks in your list:
1.[T][ ] read book
Bye. Hope to see you again soon!
```

## Known gaps

- A blank line is ignored rather than reported, so it has no test case.
- `/from` and `/to` given in the wrong order are not detected; the parser
  splits on either marker without checking which came first.
