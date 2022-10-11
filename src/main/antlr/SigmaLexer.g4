lexer grammar SigmaLexer;

LeftBrace : '{' ;
RightBrace : '}' ;
Colon : ':' ;
Comma : ',' ;
Whitespace : (' ' | '\n') -> skip ;
Identifier : [a-zA-Z] ([a-zA-Z1-9])* ;
