parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

program
    : expression ;

expression
    : referee=identifier # referenceAlt
    | abstraction # abstractionAlt
    | subject=expression LeftBracket key=expression RightBracket # applicationAlt
    | letExpression # letExpressionAlt
    | scope # scopeAlt
    | symbol # symbolAlt ;

identifier
    : CharSequence ;

bind
    : name=identifier Assign bound=expression ;

bindSeparator
    : Comma ;

symbol
    : Backtick identifier Backtick ;

abstraction
    : argument=identifier Arrow image=expression ;

letExpression
    : LetKeyword body=scope InKeyword result=expression ;

scope
    : LeftBrace (bind (bindSeparator bind)*)? bindSeparator? RightBrace ;
