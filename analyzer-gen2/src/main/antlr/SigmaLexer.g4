lexer grammar SigmaLexer;

LetKeyword : '%let' ;
InKeyword : '%in' ;
ValKeyword : '%val' ;
FunKeyword : '%fun' ;
WhenKeyword : '%when' ;
CaseKeyword : '%case' ;
ElseKeyword : '%else' ;
TrueKeyword : '%true' ;
FalseKeyword : '%false' ;

LeftParen : '(' ;
RightParen : ')' ;
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
