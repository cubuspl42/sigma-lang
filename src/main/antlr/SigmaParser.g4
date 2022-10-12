parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

program
    : expression ;

expression
    : table # tableAlt
    | abstraction # abstractionAlt
    | referee=identifier # referenceAlt
    | subject=expression LeftBracket key=expression RightBracket # applicationAlt
    | symbol # symbolAlt ;

identifier
    : CharSequence ;

table
    : (label=identifier At)? LeftBrace (entry (Comma entry)*)? Comma? RightBrace ;

entry
    : argument=symbol Colon image=expression ;

symbol
    : Quote text=CharSequence Quote ;

abstraction
    : argument=identifier Arrow image=expression ;
