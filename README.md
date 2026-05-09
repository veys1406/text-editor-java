# Stack-Based Text Editor

`This project was implemented without the use of any AI tools.`

A console-based text editor implemented in Java that supports insert, delete, and replace operations with full undo/redo functionality using a dual-stack approach.

## Features

- **insert** `<text> <position>` — inserts text starting at the given index
- **delete** `<position> <length>` — removes characters from position for given length
- **replace** `<text> <position> <length>` — replaces characters at position with new text
- **undo** — reverts the last operation
- **redo** — reapplies the last undone operation

## How It Works

Two stacks (`undoStack` and `redoStack`) hold `Action` objects that store operation type, new text, old text, and position. Every edit pushes to `undoStack` and clears `redoStack`. Undo pops from `undoStack`, applies the inverse operation, and pushes to `redoStack`. Redo does the reverse.

Commands are read from `actions.txt`, one per line.

## File Structure

| File | Description |
|------|-------------|
| `TextEditor.java` | Core editor logic — insert, delete, replace, undo, redo |
| `TextDriver.java` | Reads `actions.txt` and drives the editor |
| `Action.java` | Stores metadata for each edit operation |
| `Stack.java` | Stack interface |
| `LinkedStack.java` | Linked-list-based stack implementation |
| `SinglyLinkedList.java` | Underlying singly linked list |

> `Stack.java`, `LinkedStack.java`, and `SinglyLinkedList.java` are from *Data Structures and Algorithms in Java* (Goodrich, Tamassia, Goldwasser).

## Running

1. Place your commands in `actions.txt` in the project root.
2. Compile all `.java` files.
3. Run `TextDriver`.

```
javac *.java
java TextDriver
```

### Example `actions.txt`

```
insert Hello 0
insert World 5
delete 5 1
undo
redo
```

## Sample Output

```
Inserted: 'Hello' Between index 0 and 4
[H][e][l][l][o]

Inserted: ' World' Between index 5 and 10
[H][e][l][l][o][ ][W][o][r][l][d]

Deleted: ' ' Between index 5 and 5
[H][e][l][l][o][W][o][r][l][d]

Inserted: ',' Between index 5 and 5
[H][e][l][l][o][,][W][o][r][l][d]

Undo - Action: insert - New Text: null - Old Text: ,
[H][e][l][l][o][W][o][r][l][d]

Redo - Action: delete - New Text: , - Old Text: null
[H][e][l][l][o][,][W][o][r][l][d]

Replaced: Java, Between index 6 and 10
[H][e][l][l][o][,][J][a][v][a][ ]

Final Text: Hello,Java
```
