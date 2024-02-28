parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

program
    : expression ;

expression
    : form # formAlt ;

form
    : LeftBrace (entry (Comma entry)*)? RightBrace ;

entry
    : key=expression Colon value=expression ;
