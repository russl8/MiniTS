grammar Expr;

@header {
    package antlr;
}

// PROGRAM STRUCTURE
prog: (class_decl)+ EOF #Program;

class_decl: 
    'class' ID ( 'extends' ID )? '{' (statement)* '}' #ClassDeclaration;

// STATEMENTS
statement:
      declaration
    | assignment
    | conditional
    | loop
    ;

// DECLARATIONS
declaration:
      ID ':' type ('=' expr)? ';' #DeclarationWithOptionalAssignment;

// ASSIGNMENTS
assignment:
      ID '=' expr ';' #VariableAssignment;

// CONDITIONALS
conditional:
    'if' '(' expr ')' '{' (statement)* '}' #IfStatement
    ;

// LOOPS
loop
	: 'while' '(' expr ')' '{' (statement)* '}' # WhileLoop
	; 
	

// EXPRESSIONS
expr 
    : '!' expr             			       # Not
    | expr '*' expr                        # Multiplication
    | expr '/' expr                        # Division
    | expr '%' expr                        # Modulo
    | expr '+' expr                        # Addition
    | expr '-' expr                        # Subtraction
    | expr '<' expr                        # LessThan
    | expr '<=' expr                       # LessEqualThan
    | expr '>' expr                        # GreaterThan
    | expr '>=' expr                       # GreaterEqualThan
    | expr '==' expr                       # Equal
    | expr '!=' expr                       # NotEqual
    | expr '&&' expr                       # And
    | expr '||' expr                       # Or
    | '(' expr ')'                         # Parenthesis
    | ID                                   # Variable
    | NUM                                  # NumberLiteral
    | BOOL                                 # BooleanLiteral
    | CHAR								   # CharacterLiteral
    | '[' expr? (',' expr)* ']'            # ListLiteral
    | '"' ( ~('\\'|'"') | '\\.' )* '"'     # StringLiteral
    ;

// TYPES
type: 'int' | 'bool' | 'char' | 'list' '[' type ']';

// LEXICAL TOKENS
BOOL: 'True' | 'False';
NUM: '0' | '-'?[1-9][0-9]*;
CHAR: '\'' [a-zA-Z0-9_] '\'';
ID: [a-zA-Z][a-zA-Z0-9_]*;

// WHITESPACE AND COMMENTS
COMMENT: '//' ~[\r\n]* -> skip;
WS: [ \t\r\n]+ -> skip;