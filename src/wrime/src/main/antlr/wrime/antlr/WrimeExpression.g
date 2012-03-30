/* Based on:
  Copyright 2008 Chris Lambrou.
  All rights reserved.
*/
grammar WrimeExpression;

options
{
	output=AST;
//	backtrack=true;
	memoize=true;
}

tokens {
    IMPORT='import';
    STAR='*';
    SET='set';
    EQUAL='=';
    PARAM='param';
    TRANSIENT='transient';
    INCLUDE='include';
    FOR='for';
    BREAK='break';
    CONTINUE='continue';
    IF='if';
    ELIF='elif';
    ELSE='else';
    AND='and';
    OR='or';
    XOR='xor';
    LT='lt';
    GT='gt';
    GTE='gte';
    LTE='lte';
    EQ='eq';
    NEQ='neq';
    NOT='not';
    TRUE='true';
    FALSE='false';
    NULL='null';
    PLUS='+';
    MINUS='-';
    DIV='/';
    MOD='%';
}

@header {
package wrime.antlr;
}

@lexer::header {
package wrime.antlr;
}

@members{
public static interface RecognitionErrorListener {
    void report(RecognitionException e, String message);
}

public static interface EmitterFactory {
    Emitter getNumber(Token o);
    Emitter getBool(boolean value);
    Emitter getNull();
    Emitter getString(String text);

	Gate makeGate(Emitter l, Token o, Emitter r);
	Comparison makeComparison(Emitter l, Token o, Emitter r);
}

private RecognitionErrorListener recognitionErrorListener;
private EmitterFactory ef;

public void setEmitterFactory(EmitterFactory ef) {
    this.ef = ef;
}

public void setRecognitionErrorListener(RecognitionErrorListener recognitionErrorListener) {
	this.recognitionErrorListener=recognitionErrorListener;
}

@Override
public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    String hdr = getErrorHeader(e);
    String msg = getErrorMessage(e, tokenNames);
    if (recognitionErrorListener != null) recognitionErrorListener.report(e, hdr + " " + msg);
    super.displayRecognitionError(tokenNames, e);
}
}

command returns [Emitter expression]
	:	o=expression EOF                                {$expression=o.e;}
	|   t=anyTag EOF									{}
	;

/*************************************************************************
TAGS
*************************************************************************/

anyTag
	:	tagIf
	|	tagFor
	|	tagBreak
	|	tagContinue
	|	tagInclude
	|	tagParam
	|	tagSet
	|	tagImport
	;
	
tagImport
	:	IMPORT classNamePackage Identifier
	|	IMPORT classNamePackage STAR
	;
	
tagSet
	:	SET Identifier EQUAL expression
	;

tagParam
	:	PARAM className Identifier tagParamOpts*
	;

tagParamOpts
	:	TRANSIENT
	;
	
className
	:	classNamePackage Identifier classNameGeneric?
	;

classNamePackage
	:	(Identifier '.')*
	;
	
classNameGeneric
	:	'<' className (',' className)* '>'
	;
	
tagInclude
	:	INCLUDE '(' funcall (',' assignOrIdentifierExpr)* ')'
	;
	
tagFor
	:	FOR ('(' Identifier ':' funcall ')')?
	;

tagBreak
	:	BREAK
	;
	
tagContinue
	:	CONTINUE
	;
			
tagIf
	:	IF
	|	IF '(' expression ')'
	|	ELIF ('(' expression ')')
	|	ELSE
	;	

assignOrIdentifierExpr
	:	Identifier (EQUAL expression)?
	;
	
/*************************************************************************
EXPRESSION
*************************************************************************/

/*----------------- Logical --------------------*/

expression returns [Emitter e]
	:	l= boolyExpr							{$e=l.e;}
	;

boolyExpr returns [Emitter e]
@init {Emitter res;}
@after {$e = res;}
	:	l= logicExpr							{res=l.e;}
		(
			o= (AND|OR|XOR) 
			r= logicExpr						{res=ef.makeGate(res,o,l.e);}
		)*						
	;
		
logicExpr returns [Emitter e]
	:	l= addExpr								{$e=l.e;} 
		(
			o= (LT|GT|LTE|GTE|EQ|NEQ) 
			r= addExpr							{$e=ef.makeComparison($e,o,l.e);}
		)*
	|	NOT logicExpr
	;

/*----------------- Math --------------------*/
			
addExpr returns [Emitter e]
	:	l= multExpr							{$e=l.e;} 
		(
			o= (MINUS|PLUS) 
			r= multExpr
		)*
	;
	
multExpr returns [Emitter e]
    :   l= atom 							{$e=l.e;}
		(
			o= (STAR|DIV|MOD) 
			r= atom
		)*
    ;

/*----------------- Atoms --------------------*/

atom returns [Emitter e]
	:   l= literal							{$e=l.e;}
	|	funcall
    |   '(' expression ')'
    ;   

/*----------------- Literals --------------------*/
    
literal returns [Emitter e]
	:	TRUE
	|	FALSE
	|	NULL
	|	l= NumericLiteral					{$e=ef.getNumber(l);}
    |   StringLiteral
    ;
    
/*----------- Member Access or Func Call ---------------*/

funcall
	:	functorExpr? memberExpr funcallArguments?
	;

functorExpr
	:	Identifier ':'
	;	
	
memberExpr
	:	Identifier ('.' Identifier)*
	;
	    
funcallArguments
	: '(' (expression (',' expression)* )? ')'
	;



/*************************************************************************
TOKENS
*************************************************************************/

NumericLiteral
	: DecimalLiteral
	| HexIntegerLiteral
	;

StringLiteral
	: '"' DoubleStringCharacter* '"'
	| '\'' SingleStringCharacter* '\''
	;

fragment DoubleStringCharacter
	: ~('"' | '\\' | LineEnd)
	| '\\' EscapeSequence
	;

fragment SingleStringCharacter
	: ~('\'' | '\\' | LineEnd)
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
	: ~(EscapeCharacter | LineEnd)
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
	
LineEnd
	: '\n'		// Line feed.
	| '\r'		// Carriage return.
	| '\u2028'	// Line separator.
	| '\u2029'	// Paragraph separator.
	;

WhiteSpace // Tab, vertical tab, form feed, space, non-breaking space and any other unicode "space separator".
	: ('\t' | '\v' | '\f' | ' ' | '\u00A0')	{$channel=HIDDEN;}
	;
