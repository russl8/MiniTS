# miniTS
A statically-typed interpreted language with inheritance, typed variables, functions, and control flow.

<img width="1708" height="948" alt="image" src="https://github.com/user-attachments/assets/01db1440-e36a-4ec1-90e5-6d41c32def4e" />


Try it out: [minits-ui.vercel.app](https://minits-ui.vercel.app/)

## Table of Contents
- [Language Guide](#language-guide)
  - [Your First Program](#your-first-program)
  - [Program Structure](#program-structure)
  - [Comments](#comments)
  - [Types](#types)
  - [Literals](#literals)
  - [Variables](#variables)
  - [Operators](#operators)
  - [Control Flow](#control-flow)
  - [Functions](#functions)
  - [Classes & Inheritance](#classes--inheritance)
  - [Scope](#scope)
  - [Errors](#errors)
- [Deep Dive](#deep-dive)
  - [Overview](#overview)
  - [Compiler Workflow & Architecture](#compiler-workflow--architecture)
  - [Limitations](#limitations)
  - [Spring Boot API Integration](#spring-boot-api-integration)
  - [Running Locally](#running-locally)

---

# Language Guide

## Your First Program
Every miniTS program is made up of classes. Statements inside a class run top to bottom, and when the program finishes you get back the final value of every variable in each class.
```
class Hello {
    x : int = 5;
    y : int = x * 2;
}
```
Output:
```
Hello → x = 5, y = 10
```

## Program Structure
- A program is one or more classes
- Variable declarations, assignments, if-statements, loops, and functions all go directly inside the class body
- There's no `main`. The class body itself runs top to bottom
- Each class is evaluated separately, so classes can't see each other's variables (unless one extends the other)

```
class Counter {
    count : int = 0;

    function add(a : int, b : int) : int {
        return a + b;
    }

    count = add(count, 10);
}

class Flags {
    done : bool = True;
}
```

## Comments
Only single-line comments. Everything after `//` is ignored.
```
// this is a comment
x : int = 1; // so is this
```

## Types
| Type | Description | Example |
|---|---|---|
| `int` | whole numbers | `age : int = 21;` |
| `bool` | `True` or `False` | `isAdmin : bool = False;` |
| `char` | a single character | `grade : char = 'A';` |
| `list[int]` | list of ints | `scores : list[int] = [90, 85, 77];` |
| `list[bool]` | list of bools | `flags : list[bool] = [True, False];` |
| `list[char]` | list of chars (this is how strings work) | `name : list[char] = "russell";` |

There's no separate string type. A string literal is just a `list[char]`.

## Literals
- **int:** `0`, `42`, `-5`
- **bool:** `True`, `False` (capitalized)
- **char:** `'a'`, `'Z'`, `'7'`, `'_'` (exactly one letter, digit, or underscore)
- **list:** `[1, 2, 3]`, `[True, False]`, `[a, b, 3]`
- **string:** `"hello"` (becomes a `list[char]`)

## Variables
Variables are declared with `name : type`, and can optionally be given a value right away.
```
total : int;         // declared, no value yet
total = 10;          // assignment
total = total + 5;   // total is now 15

name : list[char] = "hi";
name = "bye";        // lists can be reassigned to a new list literal
```
- A variable has to be declared before it's used
- The value has to match the declared type (`a : int = True;` is an error)
- You can't declare the same name twice in the same scope

## Operators
| Category | Operators | Works on | Result |
|---|---|---|---|
| Arithmetic | `+` `-` `*` `/` `%` | `int`, `int` | `int` |
| Relational | `<` `<=` `>` `>=` | `int`, `int` | `bool` |
| Equality | `==` `!=` | `int`, `int` or `bool`, `bool` | `bool` |
| Logical | `&&` `\|\|` | `bool`, `bool` | `bool` |
| Not | `!` | `bool` | `bool` |
| Grouping | `( )` | anything | same as inside |

```
a : int = 10;
b : int = 3;
rem : int = a % b;              // 1
bigger : bool = a > b;          // True
both : bool = bigger && (b > 0); // True
grouped : int = (a + b) * 2;    // 26
```
- `/` is integer division (`10 / 3` is `3`)
- `==` and `!=` don't work on `char` or lists
- **Use parentheses.** Operator precedence doesn't follow the usual math rules, so `10 - 2 + 3` gives `5` (it's read as `10 - (2 + 3)`). Write `(10 - 2) + 3` if that's what you mean

## Control Flow
Conditions always have to be a `bool`. `if (x)` where `x` is an `int` is an error.

### if
```
if (x > 5) {
    big = True;
}
```
There's no `else` or `else if`. Use a second `if` with the opposite condition instead.

### while
```
i : int = 0;
while (i < 3) {
    i = i + 1;
}
```

### for
`for (declaration; condition; assignment)`. The first part has to be a declaration and the last part has to be an assignment.
```
total : int = 0;
for (j : int = 1; j <= 5; j = j + 1) {
    total = total + j;
}
// total = 15
```

## Functions
```
function square(n : int) : int {
    result : int = n * n;
    return result;
}

sq : int = square(4); // 16
```
- Parameters and the return type are all typed
- Every function ends with exactly one `return`, and it has to be the last line
- Functions can take zero or more parameters: `function five() : int { return 5; }`
- Functions can return lists too: `function nums() : list[int] { return [1, 2, 3]; }`
- A function has to be declared **before** it's called
- Arguments are checked against the parameter types and count
- Functions can read and update the class's variables
```
class Bank {
    balance : int = 0;

    function deposit(amount : int) : int {
        balance = balance + amount;
        return balance;
    }

    result : int = deposit(50); // balance = 50, result = 50
}
```

## Classes & Inheritance
A class can extend one other class with `extends`. The subclass gets all of the superclass's variables and functions.
```
class Animal {
    legs : int = 4;

    function speak(times : int) : int {
        return times * 2;
    }
}

class Dog extends Animal {
    barks : int = speak(3); // 6
    legs = legs - 1;        // 3
}
```
- The superclass has to be declared earlier in the file than the subclass
- No overriding. Redeclaring an inherited variable (`legs : int = 2;` in `Dog`) is an error
- Two classes can't have the same name

## Scope
miniTS uses lexical (block) scope.
- Variables declared inside an `if`, `while`, or `for` block only exist inside that block
- The `for` loop variable only exists inside the loop
- Function parameters and variables declared inside a function are local to that function
- Class-level variables are visible everywhere in the class, including inside functions

```
x : int = 1;
if (x == 1) {
    temp : int = 5;
    x = temp;
}
y : int = temp; // error: temp isn't declared out here
temp : int = 2; // this is fine though, the old temp is gone
```

## Errors
There are two kinds of errors:
- **Syntax errors:** the code doesn't match the grammar (missing `;`, missing `return`, etc.). Nothing runs
- **Semantic errors:** the code parses but breaks a rule. For example:
  - using or assigning a variable that was never declared
  - declaring a variable twice
  - type mismatches (`a : int = True;`, `5 > False`)
  - calling a function that doesn't exist (or isn't declared yet)
  - passing the wrong number or type of arguments
  - extending a class that doesn't exist

Errors are tracked per class. If a class has any semantic errors it doesn't get evaluated, but the other classes in the file still run.

---

# Deep Dive

## Overview
miniTS accepts class-based programs with inheritance, typed variables, functions, and control flow.

### Example Program
```
class Superclass {
    c : list[char] = "hello";
    i : int = 0;  
}

class Overview extends Superclass {    
    function f (i : int, j : int) : int {
        return i + j;
    }

    for (j : int = 1; j <= 5; j = j + 1) {
        i = f(i, j); 
    }    

    j : int = 0;
    if(i == 15) {
        j = i; 
    }
}
```

### Key Constructs:
- Classes & Inheritance: Classes can extend superclasses
- Variables: Strongly typed (int, char, bool, list[...])
- Functions: Typed parameters + return values
- Control Flow: if, while-loops, for-loops

### Advanced Features
- Inheritance (`class B extends A {}`)
- For-Loops (`for (i:int=0; i<5; i=i+1) {}`)
- Scoping (lexical scope: variables local to block/function)
- Function Declaration & Invocation
- Lists (`list[int]`, `list[char]`, `list[bool]`)

## Workflow & Architecture
- ANTLR Parser → builds AST
- VariableBindingVisitor → ensures variables are declared before use
- TypeCheckerVisitor → enforces type rules
- Evaluator → evaluates expressions (if no errors)
- Pretty Printer → outputs HTML reports

<img width="814" height="423" alt="image" src="https://github.com/user-attachments/assets/7ddbd8c8-1b43-41c8-b7a8-fe3ac461921c" />

### How it runs
- The grammar lives in `minits-compiler/src/main/java/antlr/Expr.g4`. If the parser reports any syntax errors, compilation stops there
- Each class gets its own fresh variable/function maps, and both visitors run over every statement in the class body
- If the class extends something, the superclass's variables and functions get copied in first
- For blocks (`if`, `while`, `for`), the variable scope is saved before the block and restored after, which is how block scoping works
- Functions are registered as they're visited, which is why a function has to be declared before it's called
- Semantic errors are collected per class. The evaluator (`ExpressionProcessor`) only runs on classes with no errors
- The result is a list of classes, each with its final variable values (or its errors), which then goes to the HTML report or the API response

## Limitations
- No method overloading/overriding
- No nested functions
- Limited list operations (declaration/assignment only)
- No else if / else
- No string methods or indexing
- No anonymous/lambda functions
- No operator precedence in grammar
- No object instantiation (new) or dot notation

## Spring Boot API Integration
- Supports string-based inputs via a Spring Boot API: [minits-ui.vercel.app](https://minits-ui.vercel.app/)
- To run the api locally:
```
# SETUP
cd minits-compiler
mvn clean install
cd ../minits-webserver
mvn clean install

# from .../minits-webserver
mvn spring-boot:run
```

Using the API to run code (assuming port=8080):
```
curl -X POST localhost:8080/api/compile \
  -H "Content-Type: application/json" \
  -d '{"code":"class A { a : int = 4 ; b : bool = a ; }"}'
```
The response has `success`, a list of `semanticErrors`, and `classes` (each with its `className` and `evaluatedVars`).

## Running Locally

### Setup (Eclipse)
#### Setting up run config
1. Right-click the Java project in the project explorer
2. Select **Run As → Run Configurations**
3. Select the **Arguments** tab, and paste `${file_prompt}` into the **Program Arguments** field
4. Name the config **MiniTS** then click **Apply**

#### Creating a runnable JAR
**Note:** A runnable JAR already exists, so you do **not** need to create a new one.  
If needed:
1. Right-click the Java project → **Export**
2. Select **Java → Runnable JAR File**
3. Set the launch configuration to **MiniTS**
4. Set export destination to `minits-compiler/miniTs.jar`

### Running the JAR
Make sure you are in the **`minits-compiler`** directory:
```bash
cd minits-compiler
```
Run with:
```bash
java -jar miniTs.jar {input}
```
- {input} can be:
  - A directory of .txt files
  - A single .txt file

### Running the 10 Example Files
```bash
java -jar miniTs.jar src/tests/finalSubmission/section5
```
#### Results:
- Individual reports in `src/tests/<filename>-report.html`
- Combined results in `combined_report.html` (preferred)

### Running an Individual File
```bash
java -jar miniTs.jar src/tests/finalSubmission/section5/inventory.txt
```
#### Results:
`src/tests/<filename>-report.html`
