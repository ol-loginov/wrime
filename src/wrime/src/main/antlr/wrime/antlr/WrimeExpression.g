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
    DOT='.';
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
    DOLLAR='$';
}

@header {
package wrime.antlr;

import wrime.ast.*;
import wrime.Location;
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
    LocatableString getLocatableString(Token token);
    Location getLocation(Token token);
    ClassName getClassName(String packageName, LocatableString className);

    Group makeGroup(Token o, Emitter e);
	Gate makeGate(Emitter l, Token o, Emitter r);
	Comparison makeComparison(Emitter l, Token o, Emitter r);
    Inverter makeInversion(Token o, Emitter e);
    Algebraic makeMath(Emitter l, Token o, Emitter r);
    Assignment makeAssignment(LocatableString varName);
    Oppositer makeOpposite(Token o, Emitter e);
    VariableRef makeVariableAccess(LocatableString name);
    FunctorRef makeFunctorAccess(LocatableString name);
    MethodCall makeMethodCall(Emitter invocable, LocatableString method, List<Emitter> arguments);

    TagImport makeTagImport(Location location, String packagePath, LocatableString packageName);
    TagSet makeTagSet(Location location, LocatableString var, Emitter e);
    TagContinue makeTagContinue(Location location);
    TagBreak makeTagBreak(Location location);
    TagFor makeTagFor(Location location);
    TagInclude makeTagInclude(Location location, Emitter e);
    TagParam makeTagParam(Location location,ClassName className, LocatableString paramName);
    TagIf makeTagIf(Location location, Emitter emitter);
    TagIf makeTagIfElif(Location location, Emitter emitter);
    TagIf makeTagIfElse(Location location);
    TagIf makeTagIfClose(Location location);
    TagCustom makeCustomTag(Location location, LocatableString name, List<Emitter> arguments);
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
	|   c= customTag EOF									{$tag=c.tag;}
	;

/*************************************************************************
TAGS
*************************************************************************/

customTag returns [WrimeTag tag]
    :   o= DOLLAR
        n= Identifier
        a= funcallArguments?                {$tag=ef.makeCustomTag(ef.getLocation(o), ef.getLocatableString(n),a==null?null:a.list);}
    ;

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
	:	o= IMPORT
        t= packageName 
        n= Identifier               {$tag=ef.makeTagImport(ef.getLocation(o),t.path,ef.getLocatableString(n));}
	|	o= IMPORT
        t= packageName
        STAR                        {$tag=ef.makeTagImport(ef.getLocation(o),t.path,null);}
	;
	
tagSet returns [TagSet tag]
	:	o= SET
        t= Identifier
        EQUAL
        e= expression               {$tag=ef.makeTagSet(ef.getLocation(o),ef.getLocatableString(t),e.e);}
	    (
	        ','
            t= Identifier
            EQUAL
            e= expression               {$tag.addVariable(ef.getLocatableString(t),e.e);}
        )*
	;

tagParam returns [TagParam tag]
	:	t= PARAM
        c= className
        n= Identifier               {$tag=ef.makeTagParam(ef.getLocation(t),c.name,ef.getLocatableString(n));}
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

packageName returns [String path]
@init { $path=""; }
	:	(
            n= Identifier           {$path += n.getText() + '.';}
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
	:	o= INCLUDE
        '(' 
        f= atom                          {$tag=ef.makeTagInclude(ef.getLocation(o),f.e);}
        (
            ',' 
            p= assignOrIdentifierExpr       {$tag.addAssignment(p.a);}
        )* 
        ')'
	;
	
tagFor returns [TagFor tag]
	:	t= FOR                                 {$tag=ef.makeTagFor(ef.getLocation(t));}
        (
            '(' 
            v= Identifier                      {$tag.setVar(ef.getLocatableString(v));}
            ':' 
            a= funcall                         {$tag.setIterable(a.e);}
            ')'
        )?
	;

tagBreak returns [TagBreak tag]
	:	t= BREAK                                {$tag=ef.makeTagBreak(ef.getLocation(t));}
	;
	
tagContinue returns [TagContinue tag]
	:	t= CONTINUE                             {$tag=ef.makeTagContinue(ef.getLocation(t));}
	;
			
tagIf returns [TagIf tag]
	:	t= IF                                   {$tag=ef.makeTagIfClose(ef.getLocation(t));}
	|	t= IF   '(' e= expression ')'           {$tag=ef.makeTagIf(ef.getLocation(t),e.e);}
	|	t= ELIF '(' e= expression ')'           {$tag=ef.makeTagIfElif(ef.getLocation(t),e.e);}
	|	t= ELSE                                 {$tag=ef.makeTagIfElse(ef.getLocation(t));}
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
	| o= MINUS
		r= multExpr                     {$e=ef.makeOpposite(o,r.e);}

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
	:	(
	        t= Identifier ':'                         {$e=ef.makeFunctorAccess(ef.getLocatableString(t));}
            r= funcallRest[$e]                {$e=r.e;}
        |
            t= Identifier                           {$e=ef.makeVariableAccess(ef.getLocatableString(t));}
        )
        (
            DOT
            r= funcallRest[$e]                {$e=r.e;}
        ) *
	;

funcallRest[Emitter invocable] returns [Emitter e]
    :
            n= Identifier
            a= funcallArguments?                {$e=ef.makeMethodCall(invocable, ef.getLocatableString(n), a==null?null:a.list);}
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
