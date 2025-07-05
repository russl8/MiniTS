grammar Expr;

@header {
	package antlr;
}

prog:(class_decl)+ EOF #Program
	;

class_decl: 'class' ID '{' attributes_section operations_section '}' #ClassDeclaration
	;

attributes_section: 'attributes' (attribute)* #AttributesSection
	;

attribute: ID ':' INT_TYPE #AttributeDeclaration
	;

operations_section: 'operations' (operation)* #OperationsSection
	;

operation: ID do_block #OperationDeclaration
	;

do_block: 'do' (assignment)* 'end' #DoBlock
	;

assignment: ID '=' expr #AttributeAssignment
	;

expr: expr '*' expr	#Multiplication
	| expr '+' expr	#Addition
	| ID	#Variable
	| NUM	#Number
	;

INT_TYPE: 'INT';
ID:[a-zA-Z][a-zA-Z0-9_]*;
NUM: '0' | '-'?[1-9][0-9]*;
COMMENT: '--' ~[\r\n]* ->skip;
WS: [ \t\n\r]+ -> skip;