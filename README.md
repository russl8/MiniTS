# MiniTS: A Compiler for a TypeScript-Inspired Class-Based Language
<img width="1898" height="917" alt="image" src="https://github.com/user-attachments/assets/bc79e7ed-85fb-4fa5-96d2-c93bf2f89730" />

## NEW: Spring Boot API Integration
- Supports string-based inputs via a Spring Boot API.
- To run the spring boot api locally:
```
# SETUP
cd minits-compiler
mvn clean install
cd ../minits-webserver
mvn clean install

# from .../minits-webserver
mvn springboot:run
```

Using the API to run code (assuming port=8080):
```
curl -X POST localhost:8080/api/compile \
  -H "Content-Type: application/json" \
  -d '{"code":"class A { a : int = 4 ; b : bool = a ; }"}'
```
  

## Input
MiniTS accepts class-based programs with inheritance, typed variables, functions, and control flow.

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

## Key Constructs:
- Classes & Inheritance: Classes can extend superclasses
- Variables: Strongly typed (int, char, bool, list[...])
- Functions: Typed parameters + return values
- Control Flow: if, while-loops, for-loops

## Advanced Features
- Inheritance (`class B extends A {}`)
- For-Loops (`for (i:int=0; i<5; i=i+1) {}`)
- Scoping (lexical scope: variables local to block/function)
- Function Declaration & Invocation
- Lists (`list[int]`, `list[char]`, `list[bool]`)



## Compiler Workflow & Architecture
- ANTLR Parser → builds AST
- VariableBindingVisitor → ensures variables are declared before use
- TypeCheckerVisitor → enforces type rules
- Evaluator → evaluates expressions (if no errors)
- Pretty Printer → outputs HTML reports
<img width="814" height="423" alt="image" src="https://github.com/user-attachments/assets/7ddbd8c8-1b43-41c8-b7a8-fe3ac461921c" />


## Limitations
- No method overloading/overriding
- No nested functions
- Limited list operations (declaration/assignment only)
- No else if / else
- No string methods or indexing
- No anonymous/lambda functions
- No operator precedence in grammar
- No object instantiation (new) or dot notation
## Setup

### Creating a Runnable JAR from Eclipse
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
4. Set export destination to `MiniTS/miniTs.jar`

## Running the JAR Locally

Make sure you are in the **root directory** (`MiniTS`):  

`
/mnt/c/Users/russe/Github/MiniTS
`
Run with:
```bash
java -jar miniTs.jar {input}
```
- {input} can be:
  - A directory of .txt files
  - A single .txt file

## Running the 10 Example Files
`
java -jar miniTs.jar src/tests/finalSubmission/section5
`
### Results:
Individual reports in src/tests/<filename>-report.html
Combined results in combined_report.html (preferred)

## Running an Individual File
`
java -jar miniTs.jar src/tests/finalSubmission/section5/inventory.txt
`
### Results:
`src/tests/<filename>-report.html`









