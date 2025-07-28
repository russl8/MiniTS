# Current features

1. Class declarations with syntacical (but not semantic) inheritance
2. Arithmetic, relational, equality, logical statements, with type-checking,
	supporting operator-to-operator and variable-to-variable comparisons
3. INT and BOOL types
4. Variable assignment, declaration (with and without initialization) 
	with type-checking
5. Proper evaluation of nested parentheses
6. Support for f-statements

# To-do
1. Add a print() function for more customizable outputs
2. Loops (while, for)
3. Inheritance
4. Handling else-if/else statements
5. Support for float datatype (integer division for now)
6. Enforce operator precedence in grammar (ie. * vs +)


# Syntax

### Class Declaration:
1. Creating a class: "Class <classname>"
	ex: class myClass {...}
2. Inheriting from a superclass by appending "extends <className>"
	ex: class myClass extends superClass {...}
	

### Variable Declaration
1. Initialized declarations: <varName> : <type> = <value/expression>;
	ex: x : INT = (2+2);
2. Unnitialized declarations: <varname>
	ex: y : BOOL;

### Variable assignment
1. <varName> = <value/expression>
	ex: x = 7+1;


### Conditional Statements 
1. If statements only: IF (<boolean_expression>) {...}
	ex: IF ( (1+1)==2 ) {...}

	
