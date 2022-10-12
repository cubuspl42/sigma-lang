lexer grammar SigmaLexer;

LeftBrace : '{' ;
RightBrace : '}' ;
LeftBracket : '[' ;
RightBracket : ']' ;

At : '@' ;
Colon : ':' ;
Comma : ',' ;
Quote : '\'' ;

CharSequence : ([a-zA-Z1-9])+ ;

Whitespace : (' ' | '\n') -> skip ;
