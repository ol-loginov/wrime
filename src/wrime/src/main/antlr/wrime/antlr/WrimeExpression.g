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
public static interface NodeFactory {
    Operand createLogical(Token act, Operand lhs, Operand rhs);
}

public RecognitionErrorListener recognitionErrorListener;
private NodeFactory nf;

public void setNodeFactory(NodeFactory nf) {
    this.nf = nf;
}

public NodeFactory getNodeFactory() {
    return this.nf;
}

@Override
public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
    String hdr = getErrorHeader(e);
    String msg = getErrorMessage(e, tokenNames);
    if (recognitionErrorListener != null) recognitionErrorListener.report(e, hdr + " " + msg);
    super.displayRecognitionError(tokenNames, e);
}
}

command
	:	expression  EOF
	|   anyTag EOF
	;

anyTag
	:	tagIf
	|	tagFor
	|	tagBreak
	|	tagContinue
	|	tagInclude
	|	tagParam
	|	tagSet
	|  tagImport
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
	
expression returns [Operand op]
	:	r1=logicExpr act=(AND | OR | XOR) r2=logicExpr {$op=nf.createLogical(act,r1.op,r2.op);}
	|	r1=logicExpr act=(AND | OR | XOR) r3=expression {$op=nf.createLogical(act,r1.op,r3.op);}
	;
		
logicExpr returns [Operand op]
	:	addExpr ((LT | GT | LTE | GTE | EQ | NEQ) addExpr)*
	|	NOT logicExpr
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
	:	TRUE
	|	FALSE
	|	NULL
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
