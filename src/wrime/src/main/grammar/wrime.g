grammar wrime;

options
{
	output=AST;
	backtrack=true;
	memoize=true;
}

program
	: WS!* expression WS!*
	;
	
expression
	: assignmentExpression (WS!* ',' WS!* assignmentExpression)*
	;

arguments
	: '(' (WS!* assignmentExpression (WS!* ',' WS!* assignmentExpression)*)? WS!* ')'
	;

memberExpressionSuffix
	: indexSuffix
	| propertyReferenceSuffix
	;

indexSuffix
	: '[' WS!* expression WS!* ']'
	;	
	
assignmentOperator
	: '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '>>>=' | '&=' | '^=' | '|='
	;
	
propertyReferenceSuffix
	: '.' WS!* ID
	;
					
assignmentExpression
	: conditionalExpression
	| leftHandSideExpression WS!* assignmentOperator WS!* assignmentExpression
	;	

conditionalExpression
	: logicalORExpression (WS!* '?' WS!* assignmentExpression WS!* ':' WS!* assignmentExpression)?
	;

logicalORExpression
	: logicalANDExpression (WS!* '||' WS!* logicalANDExpression)*
	;

logicalANDExpression
	: bitwiseORExpression (WS!* '&&' WS!* bitwiseORExpression)*
	;

bitwiseORExpression
	: bitwiseXORExpression (WS!* '|' WS!* bitwiseXORExpression)*
	;
	
bitwiseXORExpression
	: bitwiseANDExpression (WS!* '^' WS!* bitwiseANDExpression)*
	;	
	
bitwiseANDExpression
	: equalityExpression (WS!* '&' WS!* equalityExpression)*
	;

equalityExpression
	: relationalExpression (WS!* ('==' | '!=') WS!* relationalExpression)*
	;		
	
relationalExpression
	: shiftExpression (WS!* ('<' | '>' | '<=' | '>=' | 'instanceof' | 'in') WS!* shiftExpression)*
	;	
	
shiftExpression
	: additiveExpression (WS!* ('<<' | '>>' | '>>>') WS!* additiveExpression)*
	;

additiveExpression
	: multiplicativeExpression (WS!* ('+' | '-') WS!* multiplicativeExpression)*
	;
	
multiplicativeExpression
	: unaryExpression (WS!* ('*' | '/' | '%') WS!* unaryExpression)*
	;
	
unaryExpression
	: postfixExpression
	| ('delete' | 'void' | 'typeof' | '++' | '--' | '+' | '-' | '~' | '!') unaryExpression
	;	
		
postfixExpression
	: leftHandSideExpression ('++' | '--')?
	;		

leftHandSideExpression
	: callExpression
	;
	
callExpression
	: memberExpression WS!* arguments (WS!* callExpressionSuffix)*
	;
				
memberExpression
	: (primaryExpression) (WS!* memberExpressionSuffix)*
	;		
			

primaryExpression
	: 'this'
	| ID
	| literal
	| '(' WS!* expression WS!* ')'
	;			

literal
	: 'null'
	| 'true'
	| 'false'
	| StringLiteral
	| NumericLiteral
	;
	
callExpressionSuffix
	: arguments
	| indexSuffix
	| propertyReferenceSuffix
	;	
	
StringLiteral
	: '"' DoubleStringCharacter* '"'
	| '\'' SingleStringCharacter* '\''
	;
	
fragment 
DoubleStringCharacter
	: ~('"' | '\\' | WS)	
	| '\\' EscapeSequence
	;

fragment 
SingleStringCharacter
	: ~('\'' | '\\' | WS)	
	| '\\' EscapeSequence
	;						
	
NumericLiteral
	: DecimalLiteral
	| HexIntegerLiteral
	;

fragment 
HexIntegerLiteral
	: '0' ('x' | 'X') HexDigit+
	;
	
fragment 
HexDigit
	: DecimalDigit | ('a'..'f') | ('A'..'F')
	;
	
fragment 
DecimalLiteral
	: DecimalDigit+ '.' DecimalDigit* ExponentPart?
	| '.'? DecimalDigit+ ExponentPart?
	;

fragment DecimalDigit
	: ('0'..'9')
	;

fragment ExponentPart
	: ('e' | 'E') ('+' | '-') ? DecimalDigit+
	;

fragment EscapeSequence
	: CharacterEscapeSequence
	| '0'
	| HexEscapeSequence
	| UnicodeEscapeSequence
	;
					
ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;

STRING
    :  '\'' ( ESC_SEQ | ~('\\'|'\'') )* '\''
    ;

CHAR:  '\'' ( ESC_SEQ | ~('\''|'\\') ) '\''
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
