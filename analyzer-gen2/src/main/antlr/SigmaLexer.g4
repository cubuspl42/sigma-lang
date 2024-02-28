lexer grammar SigmaLexer;

LeftBrace : '{' ;
RightBrace : '}' ;
Dash : '^' ;
Colon : ':' ;
Comma : ',' ;

FatArrow : '=>' ;

Identifier : [a-zA-Z] [a-zA-Z0-9]* ;
IntLiteral : [0-9]+ ;

Whitespace : (' ' | '\n') -> skip ;
