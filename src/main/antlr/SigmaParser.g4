parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

program
    : expression ;

expression
    : form # formAlt
    | identifier # identifierAlt
    | subject=expression LeftBracket key=expression RightBracket # readAlt ;

form
    : LeftBrace (entry (Comma entry)*)? RightBrace ;

entry
    : key=expression Colon value=expression ;

identifier
    : Identifier ;
