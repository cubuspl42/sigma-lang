parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

program
    : expression ;

expression
    : value # valueAlt
    | referee=identifier # referenceAlt
    | subject=expression LeftBracket key=expression RightBracket # readAlt ;

value
    : dict # dictAlt
    | symbol # symbolAlt ;

identifier
    : CharSequence ;

dict
    : (label=identifier At)? LeftBrace (entry (Comma entry)*)? Comma? RightBrace ;

entry
    : argument=value Colon image=expression ;

symbol
    : Quote text=CharSequence Quote ;
