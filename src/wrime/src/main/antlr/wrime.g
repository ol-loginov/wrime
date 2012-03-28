/* Based on:
  Copyright 2008 Chris Lambrou.
  All rights reserved.
*/
grammar wrime;

options
{
	output=AST;
	backtrack=true;
	memoize=true;
}


command
	:	expression
	;

expression
	:	multExpr (( '+' | '-') multExpr)*
	;

multExpr
    :   atom (('*'|'/'|'%') atom)*
    ; 

atom:   INT 
    |   Identifier ('.' Identifier)* funcallArguments?
    |   '(' expression ')'
    ;   
    
funcallArguments
	: '(' (expression (',' expression)*)? ')'
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
	
INT :   '0'..'9'+ ;

LT
	: '\n'		// Line feed.
	| '\r'		// Carriage return.
	| '\u2028'	// Line separator.
	| '\u2029'	// Paragraph separator.
	;

WhiteSpace // Tab, vertical tab, form feed, space, non-breaking space and any other unicode "space separator".
	: ('\t' | '\v' | '\f' | ' ' | '\u00A0')	{$channel=HIDDEN;}
	;

