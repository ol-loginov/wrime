/* Based on:
  Copyright 2008 Chris Lambrou.
  All rights reserved.
*/
grammar WrimeExpression;

options
{
	output=AST;
	backtrack=true;
	memoize=true;
}

@header {
package wrime.antlr;
}

@lexer::header {
package wrime.antlr;
}

@members{
}

command
	:	expression
	|	tagIf
	|	tagFor
	|	tagBreak
	|	tagContinue	
	|	tagInclude
	|	tagParam
	|	tagSet
	;
	
tagSet
	:	'set' Identifier '=' expression;

tagParam
	:	'param' className Identifier tagParamOpts*;

tagParamOpts
	:	'transient'
	;
	
className
	:	Identifier ('.' Identifier)* classNameGeneric?
	;
	
classNameGeneric
	:	'<' className (',' className)* '>'
	;
	
tagInclude
	:	'include' '(' funcall (',' assignOrIdentifierExpr)* ')'
	;
	
tagFor
	:	'for' ('(' Identifier ':' funcall ')')?
	;

tagBreak
	:	'break'
	;
	
tagContinue
	:	'continue'
	;
			
tagIf
	:	'if'
	|	'if' '(' expression ')'
	|	'elif' ('(' expression ')')
	|	'else'
	;	

assignOrIdentifierExpr
	:	Identifier ('=' expression)?
	;
	
expression
	:	logicExpr (('and' | 'or' | 'xor') logicExpr)*
	;
		
logicExpr
	:	addExpr (('lt' | 'gt' | 'lte' | 'gte' | 'eq' | 'neq') addExpr)*
	|	'not' logicExpr
	;
			
addExpr
	:	multExpr (('+' | '-') multExpr)*
	;
	
multExpr
    :   atom (('*'|'/'|'%') atom)*
    ; 

atom
	:   literal
	|	funcall
    |   '(' expression ')'
    ;   
    
literal
	:	'true'
	|	'false'
	|	'null'    
	|	NumericLiteral
    |   StringLiteral
    ;
    
funcall
	:	functorExpr memberExpr funcallArguments?
	|	memberExpr funcallArguments?
	;

functorExpr
	:	Identifier ':' 
	;	
	
memberExpr
	:	Identifier ('.' Identifier)*
	;
	    
funcallArguments
	: '(' (expression (',' expression)*)? ')'
	;

NumericLiteral
	: DecimalLiteral
	| HexIntegerLiteral
	;

StringLiteral
	: '"' DoubleStringCharacter* '"'
	| '\'' SingleStringCharacter* '\''
	;

fragment DoubleStringCharacter
	: ~('"' | '\\' | LT)	
	| '\\' EscapeSequence
	;

fragment SingleStringCharacter
	: ~('\'' | '\\' | LT)	
	| '\\' EscapeSequence
	;

fragment EscapeSequence
	: CharacterEscapeSequence
	| '0'
	| HexEscapeSequence
	| UnicodeEscapeSequence
	;	

fragment CharacterEscapeSequence
	: SingleEscapeCharacter
	| NonEscapeCharacter
	;

fragment NonEscapeCharacter
	: ~(EscapeCharacter | LT)
	;
	
fragment SingleEscapeCharacter
	: '\'' | '"' | '\\' | 'b' | 'f' | 'n' | 'r' | 't' | 'v'
	;
	
fragment EscapeCharacter
	: SingleEscapeCharacter
	| DecimalDigit
	| 'x'
	| 'u'
	;
	
fragment HexEscapeSequence
	: 'x' HexDigit HexDigit
	;
	
fragment UnicodeEscapeSequence
	: 'u' HexDigit HexDigit HexDigit HexDigit
	;

fragment HexIntegerLiteral
	: '0' ('x' | 'X') HexDigit+
	;
	
fragment HexDigit
	: DecimalDigit | ('a'..'f') | ('A'..'F')
	;

fragment DecimalLiteral
	: DecimalDigit+ '.' DecimalDigit* ExponentPart?
	| '.'? DecimalDigit+ ExponentPart?
	;

fragment ExponentPart
	: ('e' | 'E') ('+' | '-') ? DecimalDigit+
	;
								
Identifier : IdentifierStart IdentifierPart*;
fragment IdentifierStart
	: 'a'..'z' 
	| 'A'..'Z'
	;
fragment IdentifierPart
	: 'a'..'z' 
	| 'A'..'Z'
	| '_'
	| '0'..'9'
	;	
	
fragment DecimalDigit
	:	'0'..'9'
	;
	
LT
	: '\n'		// Line feed.
	| '\r'		// Carriage return.
	| '\u2028'	// Line separator.
	| '\u2029'	// Paragraph separator.
	;

WhiteSpace // Tab, vertical tab, form feed, space, non-breaking space and any other unicode "space separator".
	: ('\t' | '\v' | '\f' | ' ' | '\u00A0')	{$channel=HIDDEN;}
	;
