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

import wrime.ast.*;
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
    Emitter getBool(Token o, boolean value);
    Emitter getNull(Token o);
    Emitter getString(Token o);
    Name getName(Token o);
    LocatableString getLocatableString(Token n);
    ClassName getClassName(List<LocatableString> packageName, LocatableString className);

    Group makeGroup(Token o, Emitter e);
	Gate makeGate(Emitter l, Token o, Emitter r);
	Comparison makeComparison(Emitter l, Token o, Emitter r);
    Inverter makeInversion(Token o, Emitter e);
    Algebraic makeMath(Emitter l, Token o, Emitter r);
    Func makeFunc(String functor, List<Name> path, List<Emitter> arguments);
    Assignment makeAssignment(LocatableString varName);

    TagImport makeTagImport(List<LocatableString> packagePath, LocatableString packageName);
    TagSet makeTagSet(LocatableString var, Emitter e);
    TagContinue makeTagContinue(Token t);
    TagBreak makeTagBreak(Token t);
    TagFor makeTagFor(Token t);
    TagInclude makeTagInclude(Emitter e);
    TagParam makeTagParam(ClassName className, LocatableString paramName);
    TagIf makeTagIf(Token t, Emitter emitter);
    TagIf makeTagIfElif(Token t, Emitter emitter);
    TagIf makeTagIfElse(Token t);
    TagIf makeTagIfClose(Token t);
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

command returns [Emitter expression, WrimeTag tag]
	:	o= expression EOF                               {$expression=o.e;}
	|   t= anyTag EOF									{$tag=t.tag;}
	;

/*************************************************************************
TAGS
*************************************************************************/

anyTag returns [WrimeTag tag]
	:	t1= tagIf                    {$tag=t1.tag;}
	|	t2= tagFor                   {$tag=t2.tag;}
	|	t3= tagBreak                 {$tag=t3.tag;}
	|	t4= tagContinue              {$tag=t4.tag;}
	|	t5= tagInclude               {$tag=t5.tag;}   
	|	t6= tagParam                 {$tag=t6.tag;}
	|	t7= tagSet                   {$tag=t7.tag;}
	|	t8= tagImport                {$tag=t8.tag;}
	;
	
tagImport returns [TagImport tag]
	:	IMPORT 
        t= packageName 
        n= Identifier               {$tag=ef.makeTagImport(t.path,ef.getLocatableString(n));}
	|	IMPORT 
        t= packageName
        STAR                        {$tag=ef.makeTagImport(t.path,null);}
	;
	
tagSet returns [TagSet tag]
	:	SET 
        t= Identifier 
        EQUAL
        e= expression               {$tag=ef.makeTagSet(ef.getLocatableString(t),e.e);}
	;

tagParam returns [TagParam tag]
	:	PARAM 
        c= className
        n= Identifier               {$tag=ef.makeTagParam(c.name,ef.getLocatableString(n));}
        (
            o= tagParamOpts         {$tag.setOption(o.value);}
        )*            
	;

tagParamOpts returns [LocatableString value]
	:	t= TRANSIENT                {$value=ef.getLocatableString(t);}
	;
	
className returns [ClassName name]
	:	p= packageName? 
        n= Identifier               {$name=ef.getClassName(p==null ? null : p.path, ef.getLocatableString(n));}
        g= genericSpec?             {$name.setGenericTypes(g==null ? null : g.spec);}
	;

packageName returns [List<LocatableString> path]
@init { $path=new ArrayList<LocatableString>(); }
	:	(
            n= Identifier           {$path.add(ef.getLocatableString(n));}
            '.'
        )+
	;
	
genericSpec returns [List<ClassName> spec]
@init {$spec = new ArrayList<ClassName>();}
	:	'<' 
        c= className                {$spec.add(c.name);}
        (
            ',' 
            c= className            {$spec.add(c.name);}
        )* 
        '>'
	;
	
tagInclude returns [TagInclude tag]
	:	INCLUDE 
        '(' 
        f= funcall                          {$tag=ef.makeTagInclude(f.e);}
        (
            ',' 
            p= assignOrIdentifierExpr       {$tag.addAssignment(p.a);}
        )* 
        ')'
	;
	
tagFor returns [TagFor tag]
	:	t= FOR                                 {$tag=ef.makeTagFor(t);}
        (
            '(' 
            v= Identifier                      {$tag.setVar(ef.getLocatableString(v));}
            ':' 
            a= funcall                         {$tag.setIterable(a.e);}
            ')'
        )?
	;

tagBreak returns [TagBreak tag]
	:	t= BREAK                                {$tag=ef.makeTagBreak(t);}
	;
	
tagContinue returns [TagContinue tag]
	:	t= CONTINUE                             {$tag=ef.makeTagContinue(t);}
	;
			
tagIf returns [TagIf tag]
	:	t= IF                                   {$tag=ef.makeTagIfClose(t);}
	|	t= IF   '(' e= expression ')'           {$tag=ef.makeTagIf(t,e.e);}
	|	t= ELIF '(' e= expression ')'           {$tag=ef.makeTagIfElif(t,e.e);}
	|	t= ELSE                                 {$tag=ef.makeTagIfElse(t);}
	;	

assignOrIdentifierExpr returns [Assignment a]
	:	t= Identifier                      {$a=ef.makeAssignment(ef.getLocatableString(t));}
        (
            EQUAL
            e= expression                  {$a.setEmitter(e.e);}
        )?
	;
	
/*************************************************************************
EXPRESSION
*************************************************************************/

/*----------------- Logical --------------------*/

expression returns [Emitter e]
	:	l= boolyExpr							{$e=l.e;}
	;

boolyExpr returns [Emitter e]
	:	l= logicExpr							{$e=l.e;}
		(
			o= (AND|OR|XOR) 
			r= logicExpr						{$e=ef.makeGate($e,o,r.e);}
		)*						
	;
		
logicExpr returns [Emitter e]
	:	l= addExpr								{$e=l.e;} 
		(
			o= (LT|GT|LTE|GTE|EQ|NEQ) 
			r= addExpr							{$e=ef.makeComparison($e,o,r.e);}
		)*
	|	t= NOT l2= logicExpr                    {$e=ef.makeInversion(t,l2.e);}
	;

/*----------------- Math --------------------*/
			
addExpr returns [Emitter e]
	:	l= multExpr							{$e=l.e;} 
		(
			o= (MINUS|PLUS) 
			r= multExpr                     {$e=ef.makeMath($e,o,r.e);}
		)*
	;
	
multExpr returns [Emitter e]
    :   l= atom 							{$e=l.e;}
		(
			o= (STAR|DIV|MOD) 
			r= atom                         {$e=ef.makeMath($e,o,r.e);}
		)*
    ;

/*----------------- Atoms --------------------*/

atom returns [Emitter e]
	:   l= literal							    {$e=l.e;}
	|	f= funcall                              {$e=f.e;}
    |   t='(' g= expression ')'                 {$e=ef.makeGroup(t,g.e);}
    ;   

/*----------------- Literals --------------------*/
    
literal returns [Emitter e]
	:	t= TRUE                             {$e=ef.getBool(t, true);}
	|	t= FALSE                            {$e=ef.getBool(t, false);}
	|	t= NULL                             {$e=ef.getNull(t);}
	|	t= NumericLiteral					{$e=ef.getNumber(t);}
    |   t= StringLiteral                    {$e=ef.getString(t);}
    ;
    
/*----------- Member Access or Func Call ---------------*/

funcall returns [Emitter e]
	:	f= functorExpr? 
        p= memberExpr 
        a= funcallArguments?                {$e=ef.makeFunc(f==null?null:f.name,p.path,a==null?null:a.list);}
	;

functorExpr returns [String name] 
	:	t= Identifier ':'                   {$name=t.getText();}
	;	
	
memberExpr returns [List<Name> path]
@init { $path = new ArrayList<Name>(); }
	:	t= Identifier                           {$path.add(ef.getName(t));}
	    (
	        '.'
	        t= Identifier                       {$path.add(ef.getName(t));}
        )*
	;
	    
funcallArguments returns [List<Emitter> list]
@init { $list = new ArrayList<Emitter>(); }
	:   '(' 
        (
            e= expression                   {$list.add(e.e);}
            (
                ',' 
                e= expression               {$list.add(e.e);}
            )* 
        )? 
        ')'
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
