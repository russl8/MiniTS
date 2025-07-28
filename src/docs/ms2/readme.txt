# MS2

## New features
1. Lists: int, bool, char
	ex: x : list[bool] = [True, False];  

2. CharType
	- Motivation: String as an array of characters. A string can be declared both ways:
	ex:  str: list[char] = "string";
	     str2: list[char] = ['s', 't', 'r', 'i', 'n', 'g'];
	   
3. While loops
	- ex: while( i < 10) { i = i + 1; }
	
4. Scoping
	- Motivation: Variables decalred in forloops, if-statements, etc shoudl not be accessed anywhere else.
	- Important for forloops, function calls
	ex1: 
		if (True) {
			i : int = 0;
		}
		i : bool = True;
		
	ex2 <future impl>:
		for (i : int = 0; i < 2; i=i+1) {
			// do something...
		}
		i : int = 1; // i can be re-declared.
	- Process: In visitor, if encountering a statement with an inner block (ex: if {...}, while {...}) 
		save the current variables
		visit the statements inside the block where u can declare more vars
		after exiting statements, reset the variable list to the saved list
		
	

## Type-checking
1. List declaration/assignment
	a. check the type of list declared (eg: list[int/char/bool])
	b. if declaration is instantiated, make sure the instantiated object is an instance of a list.
		i. if is instance of list, iterate over the list elements and make sure that each element
			corresponds to the type specifed in declaration.
			
2. While-loops
	a. type-check the expression to make sure it returns a boolean
	
		
		
		
		
// MUST DO
- forloop
- function 
- list operations
- () => {} //optional
- classes are
- adding evaluations are a bonus. focus on type-checking and add evaluations only if u want
		=> evaluations are bonus marks
- inheritance and poly are bonus
		
		
HTML
- make report more natural language insrtead of x :[type  int, value = 5]

test cases
- make the testcases more interesting 

project report
- 
		
		
		

		
