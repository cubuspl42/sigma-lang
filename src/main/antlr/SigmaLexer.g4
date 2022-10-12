lexer grammar SigmaLexer;

LeftBrace : '{' ;
RightBrace : '}' ;
LeftBracket : '[' ;
RightBracket : ']' ;

At : '@' ;
Colon : ':' ;
Comma : ',' ;
Quote : '\'' ;

Arrow : '=>' ;
CharSequence : ([a-zA-Z1-9])+ ;

Whitespace : (' ' | '\n') -> skip ;
