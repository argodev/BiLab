header {
  package scigol;
}

options {
  language="Java";
}




// TODOs:
//  * static constructors - no arg func that run at initialization time
//  * constructors must call base constructors
//  * syntax for base constructor calls
//  * finalization
//  * indexers - DONE
//  * accessors - DONE
//  * genralize the special case of calling func(->type) func in type expressions to
//    a general implicit func values (no arg funcs that can be implicitly called when
//    the required type doesn't match a func, but does match the return type of the func)
//  * change .ctor name from nothing to this?  use this for indexing? / call or operator()
//    what about .ctors as static operator() = (!!?) (long form?)
//  * list lvalue assignments i.e. [a,b] = f()
//  * external call declarations (to C etc.)
//  * enforce access restrictions (public/protected/private)
//  * enumerations
//  * attributes/annotations
//  * events
//  * delegates
//  * design-by-contract (assert, invariate func, func precond, postcond & body sections - see D lang) - DONE
//  * multiple declarations (e.g. let a,b:int = 4) : perhaps like list lvalue assignments - let [a,b]:int = 6, or let [a,b]:int = [1,2]
//  * correct call signature to formal prototype matching
//  * regex syntax
//  * list comprehensions
//  * handle 'num' - DONE
//  * introduce 'exception' type ?
//  * handle 'any' properly - DONE - sortof
//  * finish implementing for/foreach; try...catch
//  * syntax for switch/case
//  * think about const and how it should work
//  * assignment math ops (+=, -=, /= etc.)
//  * operator overloading of <, <=, >, >= via compareTo()
//  * preprocessing
//  * var args
//  * named call params (e.g. f(x=3,y=5), or f(x==3,y==5), or even f(x:3,y:5) ? )
//  * ? : syntax if's
//  * parallel constructs (loops, arrays)
//  * distributed constructs ( f() @ <execution_location> )
//  * documentation comments (ala C#)
//
// Partially completed:
//  * syntax for norm/length |expr| - dimension or length? - DONE
//  * namespaces/using & path search
//  * operator overloading - DONE



class ScigolParser extends Parser;
options {
  k = 2;                           // token lookahead
  exportVocab=Scigol;              // Call its vocabulary "Scigol"
  codeGenMakeSwitchThreshold = 2;  // Some optimizations
  codeGenBitsetTestThreshold = 3;
  defaultErrorHandler = false;     // Don't generate parser error handlers
  buildAST = true;
}


tokens {
  DOT; DOTDOT; 
  UNARY_MINUS; UNARY_PLUS;
  EXPRLIST; INITLIST; 
  MATRIX; LIST; MAP; FUNC;
  POST_INC; POST_DEC; APPLICATION;
  LIT_TRUE="true"; LIT_FALSE="false"; LIT_NULL="null";
  LIT_FUNC; MODIFIERS;
  BUILTIN_TYPE;
  CTOR; PROP; 
}


// class code
{

  public CombinedSharedInputState istate()
  {
    return ((ParserSharedInputStateWrapper)getInputState()).state;
  }
  
}




program
   : (
       (LCURLY)=> namespaceBody // list of expressions in top-level namespace
     | expression  // or, a single expression
     )  
     (SEMI!)?
     EOF!
  ;

  
namespaceScope
  : "namespace"^ namespaceName namespaceBody
  ;   

namespaceBody : LCURLY 
                (usingDecl)*
                (expression)? (SEMI! (expression)? )* 
                RCURLY! 
              ;     
         
namespaceName : IDENT (DOT IDENT)* ;

usingDecl : "using"^ namespaceName ("as" IDENT)? ("from" STRING_LITERAL)? SEMI! ;



  
expression
  : namespaceScope
  | declaration
  | (funcLiteralPrefix)=>functionLiteral 
  | complexTypeLiteral  // no need to use typeLiteral as applicationSelectionExpression includes builtInType in primaryExpression
  | assignmentExpression 
  | ifExpression
  | whileExpression
  | forExpression
  | foreachExpression
  | throwExpression
  | assertExpression
  | debugExpression // !!! do we need/want this?
  | logCall
  ;

  
//listLiteral : LTHAN expression (COMMA expression)* GTHAN ;
  

// just for identifying the start of a functional literal, as opposed to a type literal of type 'func'
funcLiteralPrefix : funcSignature (LCURLY | "pre" | "post") ;
  
  
// simplified expressions for where values of type 'type' are needed
//  but a full expression can't be parsed unless parenthesized
typeExpression
  : complexTypeLiteral // no need to use typeLiteral as applicationSelectionExpression includes builtInType in primaryExpression
  | applicationSelectionExpression
  ;

  

/*
matrixLeftExpression
  : { istate().inMatrix = true; }
    lb:LBRACK^ { #lb.setType(MATRIX);}
    leftExpressionRow
    ( (SEMI)+ leftExpressionRow )*
    RBRACK!
    { istate().inMatrix = false; }
  ;
  
leftExpressionRow : simpleLeftExpression ( (COMMA!)? simpleLeftExpression)* ;
*/  
  
  
typeofExpression : "typeof"^ LPAREN! expression RPAREN! ;  
  

assertExpression : "assert"^ LPAREN! expression (COMMA! expression) RPAREN! ;

debugExpression : "debug"^ LPAREN! (IDENT COMMA^)? expression RPAREN! ;


logCall : "logger"^ LPAREN! (IDENT COMMA^)? expression RPAREN! ;


assignmentExpression
  : conditionalExpression (ASSIGN^ conditionalExpression)?
  ;

conditionalExpression
  : logicalOrExpression
  ;

logicalOrExpression
  : logicalAndExpression ("or"^ logicalAndExpression)*
  ;

logicalAndExpression
  : equalityExpression ("and"^ equalityExpression)*
  ;
  
equalityExpression
  : isExpression ((NOT_EQUAL^ | EQUAL^) isExpression)*
  ;
  
isExpression
  : relationalExpression ( ("is"^ | "isnt"^) typeExpression)*
  ;
  
  
relationalExpression
//  : additiveExpression ((LTHAN^ | GTHAN^ | LTE^ | GTE^) additiveExpression)*
  : rangeExpression ((LTHAN^ | GTHAN^ | LTE^ | GTE^) rangeExpression)*
  ;

  
rangeExpression
  : additiveExpression (DOTDOT^ additiveExpression)?
  | DOTDOT^
  ;

  
additiveExpression
  : multiplicativeExpression ((PLUS^ | MINUS^) multiplicativeExpression)*
  ;
  
  
multiplicativeExpression
  : powerExpression ((STAR^ | DIV^ | MOD^ ) powerExpression )* 
  ;

powerExpression
  : unaryExpression (HAT^ unaryExpression)*
  ;
    
unaryExpression
  : INC^ unaryExpression
  | DEC^ unaryExpression
  | MINUS^ {#MINUS.setType(UNARY_MINUS);} unaryExpression
  | PLUS^  {#PLUS.setType(UNARY_PLUS);} unaryExpression
  | unaryExpressionNotPlusMinus
  ;
  
  
unaryExpressionNotPlusMinus
  /*: BNOT^ unaryExpression
  |*/: ( LNOT^ | "not"^ ) unaryExpression
  | HASH^ postfixExpression
  | postfixExpression
  ;
  
  
postfixExpression
  : applicationSelectionExpression (   incrmt:INC^ {#incrmt.setType(POST_INC);}
                                     | decrmt:DEC^ {#decrmt.setType(POST_DEC);} 
                                     | PRIME^ 
                                   )?
  ;
  
  
// function call, constructor, or indexer  
applicationSelectionExpression
  : primaryExpression
    (   (DOT^ IDENT) 
      | {
          #applicationSelectionExpression = #([APPLICATION,"APP"], #applicationSelectionExpression); 
          //!!! maybe thereis a way to set the location of the APP?
        }
        applicationParens
    )* 
  ;




  
primaryExpression
  : constant
  | (map)=> map
  | matrix
  | expressionList
  | exceptionBlock
  | listOrParenExpr
  | listLiteral
  | BAR^ expression BAR!        // norm
  | (SCOPE_ESCAPE)* IDENT
  | typeofExpression
  | builtInType
  ;  
  
//!!! for the moment, 0, 2 or more expressions is a listLiteral, but one expression is
// always just an expression (parenthesized/grouped)
// However, in the future a list of one element may be able to be made semantically
// similar to a parenthesized expression (for example by making an implicit conversion
// from a list of one element to it's element).
// However, this will be tricky, as we'll need to ensure that overload resolution and
//  other things still work as they should.  
// So; for now, we also provide an explicit syntax for lists.

// list (#0,#2..) or parenthesised expression (#1)  
listOrParenExpr 
                { int count=0; }
                :  LPAREN! ( expression {count++;} (COMMA! expression {count++;})* )? RPAREN! 
                {
                  if (count != 1) {
                    #listOrParenExpr = #([LIST,"LIST"], #listOrParenExpr);
                  }
                }
                ; 
  
// list
listLiteral : LISTSTART! ( expression (COMMA! expression)* )? RPAREN! 
            { #listLiteral = #([LIST,"LIST"], #listLiteral); }
            ;

  
  
  
  
exceptionBlock : "try"^  expressionList 
                 "catch" LPAREN! (IDENT COLON! typeExpression)? RPAREN! expressionList
               ;
  
  

applicationParens
  : ( LPAREN^ (expression)? (COMMA expression)* RPAREN! )
  ;
    

  

  
  
  
constant
  : NUM_INT
  | NUM_DINT
  | NUM_REAL
  | NUM_SREAL
  | LIT_TRUE
  | LIT_FALSE
  | LIT_NULL
  | CHAR_LITERAL
  | STRING_LITERAL
  ;


  
map
  : lb:LBRACK^ { #lb.setType(MAP);}
    mapping
    ( (SEMI!)+ mapping )*
    RBRACK!
  ;
  
mapping
  : expression GIVES expression ;
  
  
  
matrix
  : { istate().inMatrix = true; }
    lb:LBRACK^ { #lb.setType(MATRIX);}
    row
    ( (SEMI)+ row )*
    RBRACK!
    { istate().inMatrix = false; }
  ;  
  
row : rowelement ( (COMMA!)? rowelement)* ;
  
rowelement 
  : constant
  | typeLiteral
  | (HAT^)* IDENT (DOT^ IDENT)
  | LPAREN! expression RPAREN!
  ;
  
  
  
functionLiteral : ( funcSignature functionLiteralBody )
                  {
                    #functionLiteral = #([LIT_FUNC,"LIT_FUNC"], #functionLiteral);
                    //!!! maybe thereis a way to set the location of the LIT_FUNC?
                    
                  } 
                ;  
  
functionLiteralBody : ("pre" expressionList SEMI!)? ("post" expressionList SEMI!)? expressionList ;
  
  


// a list of 0 or more expressions enclosed in {}  
//  using ';' as separator
expressionList
  : (
      lc:LCURLY^ {#lc.setType(EXPRLIST);}
      (expression)? ( SEMI! (expression)? )* 
      RCURLY!
    )
  ;
  
  
annotation : ANNOT_START^ IDENT (DOT! IDENT)* (applicationParens)? RBRACK!
           ;  

  
//!!!  perhaps, we could change 'static' to 'own'??! - need to think about how constness works in detail!
declaration : (annotation)* "let"^ ("const")? (declModifiers)? IDENT 
               (  (COLON typeExpression (ASSIGN expression)? )
                 |(ASSIGN expression)
               )
            ;

//!!! do we need 'final' in general declarations? isn't const enough? - what does static mean?
//  (if it means that local scopes can have persistent vars, then own would be better. Don't think
//   it applies to namespaces (?) - currently sequntial namespaces go out of scope like local blocks,
//   but we probably actually want them to be persistent)
declModifiers :   ( 
                     accessModifier
                   | ("static"|"final")
                   | ( ("static"|"final") accessModifier )
                   | ( accessModifier ("static"|"final") )
                  )
                  { #declModifiers = #([MODIFIERS, "MODIFIERS"], #declModifiers); }
                ;
  


// literal of value 'type' (e.g. int, bool, func(a:int->int) etc.)
typeLiteral : builtInType | complexTypeLiteral ;

complexTypeLiteral : funcType | classType | interfaceType ;



classType : ( "class"^ (classBase)? 
              LCURLY! 
              (memberOrConstructor)? (SEMI! (memberOrConstructor)? )* 
              RCURLY! 
            ) ;

classBase : COLON^ typeExpression (COMMA! typeExpression)* ;

member : ("let"!)? classMember ;

memberOrConstructor : ("let"!)? (classMember|classConstructor) ;


interfaceType : ( "interface"^ (classBase)?
                  LCURLY! 
                  (member)? (SEMI! (member)? )*
                  RCURLY! 
                ) ;


classConstructor : (ctorModifiers)? functionLiteral 
                 {
                   #classConstructor = #([CTOR,"CTOR"], #classConstructor); 
                   //!!! maybe there is a way to set the location of the CTOR?
                 }
                 ;

// !!! in the event tht IDENT="this" then accept as a constructor - but verify that
//  the expression is a func literal & that no type is supplied (or that is it correct)
//  (or maybe use 'self' instead of 'this'? (may want to use 'this' for indexer)                 
classMember : (memberModifiers)? IDENT^ (COLON typeExpression)? (memberInitializer)? ;

memberInitializer : (ASSIGN expression)
                  | propertyDeclaration
                  ;

                  
propertyDeclaration : "property"! (LPAREN^ formalParamList RPAREN!)? 
                      LCURLY^
                      accessorFunc
                      (SEMI! accessorFunc)? (SEMI!)?
                      RCURLY!
                      { #propertyDeclaration = #([PROP,"PROP"], #propertyDeclaration); }
                    ;
                    
                     
accessorFunc : IDENT^ 
               (ASSIGN! 
                 (
                     (LCURLY | "pre" | "post")=>functionLiteralBody 
                   | expression 
                 ) 
               )? 
             ;

                     

memberModifiers : ( 
                     accessModifier
                   | ("static"|"override"|"final")
                   | ( ("static"|"override"|"final") accessModifier )
                   | ( accessModifier ("static"|"override"|"final") )
                  )
                  { #memberModifiers = #([MODIFIERS, "MODIFIERS"], #memberModifiers); }
                ;

//!!! any way to allow 'implicit' before accessModifier?
ctorModifiers : (   accessModifier ("implicit")?
                )
                { #ctorModifiers = #([MODIFIERS, "MODIFIERS"], #ctorModifiers); }
              ;         
              

accessModifier : ( "public" | "private" | "protected" ) ;



funcType : ( f:"func"^ LPAREN! paramTypeList (GIVES typeExpression)? RPAREN! )
           { #f.setType(FUNC);} 
         ;
paramTypeList : ( (paramType)? (COMMA! paramType)* ) ;
paramType : /*(IDENT COLON!)?*/ typeExpression (ASSIGN expression)? ;               


funcSignature :  ( f:"func"^ LPAREN! formalParamList (GIVES typeExpression)? RPAREN! )
                 { #f.setType(FUNC);} 
              ;
formalParamList : ( (formalParam)? (COMMA! formalParam)* ) ; 
formalParam : IDENT COLON! typeExpression (ASSIGN expression)?;



builtInType
    : ( "vector"
    |   "matrix"
    |   "range"
    |   "list"
    |   "map"
    |   "bool"
    |   "byte"
    |   "char"
    |   "int"
    |   "dint"
    |   "real"
    |   "sreal"
    |   "string"
    |   "type" 
    |   "num"
    |   "any"
    |   "object"
    )
    {
      #builtInType.setType(BUILTIN_TYPE);
    }
    ;

    
    
    
             
             
// Allow two forms for the if expression
//  1) if <bool expr> then <true expr> [else <false expr>]
//  2) if (<bool expr>) <true expr> [else <false expr>]
//   - i.e. the 'then' is optional providing the test is enclosed in parentheses   
 
ifExpression : ("if" expression "then")=> ifThenExpression
             |                            ifParenExpression
             ;

             
ifThenExpression : "if"^ expression "then"! expression 
                   ( // dangling else, quiet warning
                     options {
                       warnWhenFollowAmbig = false;
                     }
                     : ( (SEMI)? "else"  )=> ifElsePart
                   )?
                 ;            
             
             
ifParenExpression : "if"^ LPAREN! expression RPAREN! expression
                    ( // dangling else, quiet warning
                      options {
                        warnWhenFollowAmbig = false;
                      }
                      : ( (SEMI)? "else"  )=> ifElsePart
                    )?
                  ;
             
ifElsePart : (SEMI!)? "else"! expression ;



  
// while loop expression has two main forms:
//      while <bool expr> do <execute>
// and  do <execute> while <bool expr>
// additionally, like the if, the 'do' may be omitted if the condition
//  is enclosed in parenthesis
// i.e.
//      while (<bool expr>) <execute>

whileExpression : whileDoExpression
                | doWhileExpression
                ;
                
doWhileExpression : "do"^ expression "while"! expression
                  ;
                
whileDoExpression : ("while" expression "do") => "while"^ expression "do"! expression
                  |                              "while"^ LPAREN! expression RPAREN! expression
                  ;

  

// C-like for loop
//    for ([let] <ident>: <type expr> = <expr> ; <bool expr> ; <expr>) <execute expr>
// OR for (<expr> ; <bool expr> ; <expr> ) <execute expr>
//  e.g. for(i:int=0; i<10; i++) doit(i);
// Note that this for introduces a new local scope, in which the declaration is defined (if present)
forExpression : "for"^ LPAREN! forDeclPart SEMI! expression SEMI! expression RPAREN! expression 
              ;
              
forDeclPart : (("let")? IDENT COLON)=> ("let")? IDENT COLON typeExpression ASSIGN expression
            | expression
            ;
              
  
// foreach takes the form:
//   foreach(<ident> : <type expr> in <range expr> [by <increment expr>]) <execute expr>
// where <range expr> can either be any object that can be iterated over (e.g. a vector, list etc.)
// - in which case the 'by' must be omitted, OR of the form <num1>:<num2> in which case
// either the int 1 or the increment expression (if present) will be sucessively added to num1
// until num2 is reached.
//
// e.g.
//  foreach(i:int in 1:10) doit(i);
//  foreach(r:real in 1.0:10.0 by 0.5) doit(r);
//  foreach(r:real in vec) doit(r);    // vec must have elements of type real
//  foreach(e:any in list) doit(e);    // list can contain elements of any type
foreachExpression : "foreach"^ LPAREN! IDENT COLON! typeExpression "in"! foreachRangeExpr RPAREN! expression
                  ;

foreachRangeExpr : expression (COLON^ expression ("by"! expression)? )? ;



throwExpression : "throw"^ expression ;







  
  
  
  
  
class ScigolLexer extends Lexer;

options {
        exportVocab=Scigol;      // call the vocabulary "Scigol"
        testLiterals=false;     // don't automatically test for literals
        k=5;                    // five characters of lookahead
        charVocabulary='\u0003'..'\uFFFF';
        // without inlining some bitset tests, couldn't do unicode;
        // I need to make ANTLR generate smaller bitsets; see
        // bottom of JavaLexer.java
        codeGenBitsetTestThreshold=20;
}

// class code
{
  
  protected Token makeToken(int t)
  {
    CommonTokenWithLocation tok = new CommonTokenWithLocation();
    tok.setType(t);
//!!! fix this somehow (if input state is LexerSharedInputStateWrapper then add public getters to it)!!!
//    tok.setColumn(getInputState().tokenStartColumn);
//    tok.setLine(getInputState().tokenStartLine);
//    tok.setFilename(getInputState().filename);
    
    return tok;
  }
  
  
  public CombinedSharedInputState istate()
  {
      return ((LexerSharedInputStateWrapper)getInputState()).state;
  }
  
}


// This is derived from the example Java lexer                                                                                                                                                

// OPERATORS
QUESTION		:	'?'		;
LPAREN			:	'('		;
RPAREN			:	')'		;
LBRACK			:	'['		;
RBRACK			:	']'		;
ANNOT_START     :   "@["    ;
LCURLY			:	'{'		;
RCURLY	options { paraphrase = "}"; } :	'}' ;
COLON			:	':'		;
COMMA			:	','		;
//DOT			:	'.'		;
EQUAL			:	"=="	;
ASSIGN                  :       '='     ;
LNOT			:	'!'		;
//BNOT			:	'~'		;
NOT_EQUAL		:	"!="	;
DIV				:	'/'		;
//DIV_ASSIGN		:	"/="	;
PLUS			:	'+'		;
//PLUS_ASSIGN		:	"+="	;
INC				:	"++"	;
MINUS			:	'-'		;
//MINUS_ASSIGN	:	"-="	;
DEC				:	"--"	;
STAR			:	'*'		;
//STAR_ASSIGN		:	"*="	;
MOD				:	'%'		;
//MOD_ASSIGN		:	"%="	;
//SR				:	">>"	;
//SR_ASSIGN		:	">>="	;
//BSR				:	">>>"	;
//BSR_ASSIGN		:	">>>="	;
GTE				:	">="	;
GTHAN			:	">"		;
SL				:	"<<"	;
//SL_ASSIGN		:	"<<="	;
LTE				:	"<="	;
LTHAN			:	'<'		;
//BXOR			:	'^'		;
HAT			:	'^'		;
//BXOR_ASSIGN		:	"^="	;
BAR				:	'|'		;
//BOR_ASSIGN		:	"|="	;
//LOR				:	"||"	;
BAND			:	'&'		;
//BAND_ASSIGN		:	"&="	;
//LAND			:	"&&"	;
SEMI  options { paraphrase = ";"; }  : ';' ;
GIVES options { paraphrase = "->"; } : "->"  ;
HASH      : '#' ;
PRIME     : '\'' ;
SCOPE_ESCAPE : '~' ;
FROM : "<-" ;
LISTSTART options { paraphrase = "'("; }  : "'(" ;
//DOTDOT : ".." ;

// Whitespace -- ignored
/*
WS	:	(	NON_LINE_BREAK_WS | LINE_BREAK )	;

protected NON_LINE_BREAK_WS : ( ' ' 
                            |   '\t' 
                            |   '\f' 
                            )+
                            {
                              _ttype = Token.SKIP;
                            }
                            ;
*/
  
protected LINE_BREAK : ( options {generateAmbigWarnings=false;} 
      : "\r\n"  // Evil DOS
			|	'\r'    // Macintosh
			|	'\n'    // Unix (the right way)
      )
      { 
        newline();
        istate().sawNewline = true;
      }
      ;

protected NON_LINE_BREAK_WS : ( ' ' 
                            |   '\t'   
                            |   '\f' 
                            )
                            ;
                            
// Whitespace                            
WS	: { istate().sawNewline = false; } 
    (   NON_LINE_BREAK_WS 
      | LINE_BREAK
    )+
    {
      if (istate().sawNewline) {
        if (istate().inMatrix)
          _ttype = SEMI; // treat newline in a matrix as row delimier (';')
        else
          _ttype = Token.SKIP; 
      }
      else
        _ttype = Token.SKIP; // skip non line-break WS
    }
    ;  
  

  

// Single-line comments
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)
		{$setType(Token.SKIP); newline();}
	;

// multiple-line comments
ML_COMMENT
	:	"/*"
		(	/*	'\r' '\n' can be matched in one alternative or by matching
				'\r' in one iteration and '\n' in another.  I am trying to
				handle any flavor of newline that comes in, but the language
				that allows both "\r\n" and "\r" and "\n" to all be valid
				newline is ambiguous.  Consequently, the resulting grammar
				must be ambiguous.  I'm shutting this warning off.
			 */
			options {
				generateAmbigWarnings=false;
			}
		:
			{ LA(2)!='/' }? '*'
		|	'\r' '\n'		{newline();}
		|	'\r'			{newline();}
		|	'\n'			{newline();}
		|	~('*'|'\n'|'\r')
		)*
		"*/"
		{$setType(Token.SKIP);}
	;


        
STRING_OR_CHAR_LITERAL
  { boolean isChar=false; }
  : ( 
      (CHAR_LITERAL)=> CHAR_LITERAL { isChar=true; } 
      | STRING_LITERAL               
    )
    { if (isChar) $setType(CHAR_LITERAL); else $setType(STRING_LITERAL); }
  ;
        

// string literals
protected
STRING_LITERAL options { paraphrase = "a string literal"; }
  : SINGLE_LINE_STRING_LITERAL | MULTI_LINE_STRING_LITERAL 
  ;

protected
SINGLE_LINE_STRING_LITERAL options { paraphrase = "a string literal"; }
  : ( '"' (ESC|~('"'|'\\'))* '"' ) 
  ;

protected 
MULTI_LINE_STRING_LITERAL options { paraphrase = "a multi-line string literal"; }
  : ( '`' '`' ( NON_BACKQUOTE_STRING '`')* '`' )
  ;  
  
protected 
NON_BACKQUOTE_STRING
  : (~('`'))+ ;  
  
protected
CHAR_LITERAL options { paraphrase = "a char literal"; }
  : ( '"' (ESC|~('"'|'\\')) '"' )
    CHAR_LIT_SUFFIX 
  ;
  
  
protected
CHAR_LIT_SUFFIX : 'c' ;  
  
  
// escape sequence -- note that this is protected; it can only be called
//   from another lexer rule -- it will not ever directly return a token to
//   the parser
// There are various ambiguities hushed in this rule.  The optional
// '0'...'9' digit matches should be matched here rather than letting
// them go back to STRING_LITERAL to be matched.  ANTLR does the
// right thing by matching immediately; hence, it's ok to shut off
// the FOLLOW ambig warnings.
protected
ESC !
:	'\\' 
   (	'n' { $append('\n'); }
		|	'r' { $append('\r'); }
		|	't' { $append('\t'); }
		|	'b' { $append('\b'); }
		|	'f' { $append('\f'); }
		|	'"' { $append('\"'); }
		|	'\'' { $append('\''); }
		|	'\\' { $append('\\'); }
		|	('u')+ HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT  { Debug.WriteLine("unicode chars not implemented"); } //!!! 
		|	'0'..'3'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7'
				(	
					options {
						warnWhenFollowAmbig = false;
					}
				:	'0'..'7'
				)?
			)? { Debug.WriteLine("octal(?) chars not implemented"); } //!!!
		|	'4'..'7'
			(
				options {
					warnWhenFollowAmbig = false;
				}
			:	'0'..'7' 
			)? { Debug.WriteLine("(?) chars not implemented"); } //!!!
		)
	;


// hexadecimal digit (again, note it's protected!)
protected
HEX_DIGIT
	:	('0'..'9'|'A'..'F'|'a'..'f')
	;


// a dummy rule to force vocabulary to be all characters (except special
//   ones that ANTLR uses internally (0 to 2)
protected
VOCAB
	:	'\3'..'\377'
	;


// an identifier.  
//  Note that it is possible to prefix an identifier text with '@', which will be
//  removed in the token text (to allow keyword idents)
//  This also means that the check for keywords in the literals table can only
//  be performed in the absence of a '@' prefix - hence the 'manual' call to
//  testLiteralsTable() rather than using the rule option
IDENT 
        options {paraphrase = "an identifier";}
        : ( 
          options {generateAmbigWarnings=false;}
          // operator names are considered identifiers, despite having the special
          //  operator characters in them
          : ( "opera" ( // only 5 char lookahead
                        options {generateAmbigWarnings=false;} :
                        ( "tor" ('+' | '-' | '*' | '/' | '%' | "++" | "--" | "==" | "!=" |  
                               '>' | '<' | "<=" | ">=" | "||" | '#' | '^' | '\'' | "->" | "()") 
                        )
                      | ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')*
                      )
            )
            // regular alphanum idents (a '@' prefix should be removed to allow keyword idents)
          | ( 
              ('@')? ('a'..'z'|'A'..'Z'|'_'|'$') ('a'..'z'|'A'..'Z'|'_'|'0'..'9'|'$')* 
            )
          )
          {
            // discard '@' perfix (can't use ! suffix because we don't want to testLiterals if '@' present)
            String s = $getText;
            if (s.charAt(0) == '@') {
              s = s.substring(1,s.length()-1);
              $setText(s);
            }
            else
              $setType( testLiteralsTable(_ttype) );
          }
        ;


// a numeric literal
NUM_INT options { paraphrase = "a numeric literal"; }
	{boolean isDecimal=false; Token t=null;}
    :   '.' { _ttype = DOT; }
            (
              (	('0'..'9')+ (EXPONENT)? (f1:REAL_SUFFIX {t=f1;})?
              {
                if (t != null && t.getText().toUpperCase().indexOf('S')>=0) {
                  _ttype = NUM_SREAL;
                }
                else {
                  _ttype = NUM_REAL; // assume double precision
                }
              }
              )
            |
              '.'
              { _ttype = DOTDOT; }
            )?

	|	(	'0' {isDecimal = true;} // special case for just '0'
			(	('x'|'X')
				(											// hex
					// the 'e'|'E' and float suffix stuff look
					// like hex digits, hence the (...)+ doesn't
					// know when to stop: ambig.  ANTLR resolves
					// it correctly by matching immediately.  It
					// is therefor ok to hush warning.
					options {
						warnWhenFollowAmbig=false;
					}
				:	HEX_DIGIT
				)+
			|	('0'..'7')+									// octal
			)?
		|	('1'..'9') ('0'..'9')*  {isDecimal=true;}		// non-zero decimal
		)
		(	('d'|'D') { _ttype = NUM_DINT; }
		
		// only check to see if it's a real if looks like decimal so far
		|	{isDecimal}? (DOT_FLOAT_EXP)=> // have to backtrack so as not to eat '.' in case of trailing '..'
            (   '.' ('0'..'9')* (EXPONENT)? (f2:REAL_SUFFIX {t=f2;})?
            |   EXPONENT (f3:REAL_SUFFIX {t=f3;})?
            |   f4:REAL_SUFFIX {t=f4;}
			      )
            {
              if (t != null && t.getText().toUpperCase().indexOf('S') >= 0) {
                _ttype = NUM_SREAL;
              }
              else {
                _ttype = NUM_REAL; // assume double precision
              }
            }
		)?
	;


protected DOT_FLOAT_EXP
  : '.' (  ('0'..'9')+ (EXPONENT)? (REAL_SUFFIX)?
         | (EXPONENT | REAL_SUFFIX)
        )
  | EXPONENT (REAL_SUFFIX)?
  | REAL_SUFFIX
  ;
  
  
  
  
// a couple protected methods to assist in matching floating point numbers
protected
EXPONENT
	:	('e'|'E') ('+'|'-')? ('0'..'9')+
	;


protected
REAL_SUFFIX
	:	's'|'S'|'r'|'R'
	;

