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
ClassKeyword : '%class' ;
ConstructorKeyword : '%constructor' ;
IsAKeyword : '%is_a' ;
ImportKeyword : '%import' ;

LeftParen : '(' ;
RightParen : ')' ;
LeftBrace : '{' ;
RightBrace : '}' ;
LeftBracket : '[' ;
RightBracket : ']' ;

Dash : '^' ;
Colon : ':' ;
Comma : ',' ;
Equals : '=' ;
Dot : '.' ;
ConcatStringsOperator : '..s' ;
ConcatListsOperator : '..l' ;

StringLiteral : '"' StringLiteralContent '"' ;

fragment StringLiteralContent : (~["\r\n])* ;

FatArrow : '=>' ;

Identifier : [a-zA-Z] [a-zA-Z0-9]* ;
IntLiteral : [0-9]+ ;

Whitespace : (' ' | '\n') -> skip ;
