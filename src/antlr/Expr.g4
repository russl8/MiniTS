grammar Expr;

@header {
    package antlr;
}

// PROGRAM STRUCTURE
prog: (class_decl)+ EOF #Program;

class_decl: 
    'class' ID ( 'extends' ID )? '{' class_body '}' #ClassDeclaration;

class_body:
    (statement)* #ClassBody;

// STATEMENTS
statement:
      declaration
    | assignment
    | conditional
    ;

// DECLARATIONS
declaration:
      ID ':' type ('=' expr)? ';' #DeclarationWithOptionalAssignment;

// ASSIGNMENTS
assignment:
      ID '=' expr ';' #VariableAssignment;

// CONDITIONALS
conditional:
    'IF' '(' expr ')' '{' (statement)* '}' #IfStatement;

// EXPRESSIONS
expr:
      expr op=('*' | '/' | '%' | '+' | '-' | '==' | '!=' | '<' | '>' | '<=' | '>=' | '&&' | '||') expr #BinaryExpr
    | '(' expr ')' #ParenExpr
    | ID #Variable
    | BOOL #BooleanLiteral
    | NUM #NumberLiteral
    ;

// TYPES
type: INT_TYPE | BOOL_TYPE;

// LEXICAL TOKENS
INT_TYPE: 'INT';
BOOL_TYPE: 'BOOL';
BOOL: 'True' | 'False';
ID: [a-zA-Z][a-zA-Z0-9_]*;
NUM: '0' | '-'?[1-9][0-9]*;

// WHITESPACE AND COMMENTS
COMMENT: '--' ~[\r\n]* -> skip;
WS: [ \t\r\n]+ -> skip;