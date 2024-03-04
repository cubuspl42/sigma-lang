lexer grammar SigmaLexer;

LetKeyword : '%let' ;
InKeyword : '%in' ;
ValKeyword : '%val' ;
FunKeyword : '%fun' ;

LeftBrace : '{' ;
RightBrace : '}' ;
Dash : '^' ;
Colon : ':' ;
Comma : ',' ;
Equals : '=' ;

FatArrow : '=>' ;

Identifier : [a-zA-Z] [a-zA-Z0-9]* ;
IntLiteral : [0-9]+ ;

Whitespace : (' ' | '\n') -> skip ;
