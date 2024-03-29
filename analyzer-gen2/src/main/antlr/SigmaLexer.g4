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
MatchKeyword : '%match' ;
AsKeyword : '%as' ;

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
Ellipsis : '...' ;

StringLiteral : '"' StringLiteralContent '"' ;

fragment StringLiteralContent : (~["\r\n])* ;

FatArrow : '=>' ;

Identifier : [a-zA-Z] [a-zA-Z0-9]* ;
IntLiteral : [0-9]+ ;

Whitespace : (' ' | '\n') -> skip ;
LineComment : '//' ~[\r\n]* -> skip ;
