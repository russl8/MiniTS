grammar Proj;

@header {
	package antlr;
}

prog: (class_decl)+ EOF # Program
;

class_decl:(
	'class' ID '{'
		(attr_decl | oper_decl)+
	'}'
) # ClassDeclaration
;

attr_decl: ID ':' TYPE # AttributeDeclaration
;

assign: ID '=' expr # Assignment
;

expr: rel_expr
	| log_expr
	| ari_expr
	| ID 
	| NUM
	| BOOL
	;
rel_expr: rel_expr '==' rel_expr 
		| rel_expr '<=' rel_expr
		| rel_expr '>=' rel_expr
		| rel_expr '<' rel_expr
		| rel_expr '>' rel_expr
		;
log_expr: log_expr 'and' log_expr
		| log_expr 'or' log_expr
		| log_expr '=>' log_expr
		| log_expr '<=>' log_expr
		| '(' log_expr ')'
		| 'not' log_expr
		| ID
		| BOOL
		;

ari_expr: ari_expr '*' ari_expr
		| ari_expr '/' ari_expr 
		| ari_expr '-' ari_expr
		| ari_expr '+' ari_expr	
	    | ID			
		| NUM 			
		;


BOOL: 'true' | 'false';
TYPE: 'INT' | 'BOOL';
ID:[a-zA-Z][a-zA-Z0-9_]*;
NUM: '0' | '-'?[1-9][0-9]*;
COMMENT: '--' ~[\r\n]* ->skip;
WS: [ \t\n\r]+ -> skip;