parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

program
    : expression ;

expression
    : form
    | identifier ;

form
    : LeftBrace (entry (Comma entry)*)? RightBrace ;

entry
    : key=expression Colon value=expression ;

identifier
    : Identifier ;
