grammar Expr;

@header {
    package antlr;
}

// PROGRAM STRUCTURE
prog: (class_decl)+ EOF #Program;

class_decl: 
    'class' ID ( 'extends' ID )? '{' (declaration | assignment | conditional)* '}' #ClassDeclaration;

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
COMMENT: '//' ~[\r\n]* -> skip;
WS: [ \t\r\n]+ -> skip;