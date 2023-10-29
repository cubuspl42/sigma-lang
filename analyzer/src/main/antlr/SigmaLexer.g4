lexer grammar SigmaLexer;

LetKeyword : '%let' ;
InKeyword : '%in' ;
ImportKeyword : '%import' ;
IsUndefinedKeyword : '%isUndefined' ;
ConstKeyword : '%const' ;
ClassKeyword : '%class' ;
DefKeyword : '%def' ;
IfKeyword : '%if' ;
NamespaceKeyword : '%namespace' ;
FieldsKeyword : '%fields' ;
MethodKeyword : '%method' ;
ThenKeyword : '%then' ;
ElseKeyword : '%else' ;
MetaKeyword : '%meta' ;
UnrecognizedKeyword : '%' [a-zA-Z]+ ;

LeftParen : '(' ;
RightParen : ')' ;
LeftBrace : '{' ;
RightBrace : '}' ;
LeftBracket : '[' ;
RightBracket : ']' ;
Lt : '<' ;
Gt : '>' ;

Lte : '<=' ;
Gte : '>=' ;
Equals : '==' ;
Dot : '.' ;
Link : '..' ;
Colon : ':' ;
Comma : ',' ;
Backtick : '`' ;
Assign : '=' ;
Asterisk : '*' ;
Slash : '/' ;
Plus : '+' ;
Minus : '-' ;
Bang : '!' ;
Dash : '^' ;
Ellipsis : '...' ;
Pipe : '|' ;

ThinArrow : '->' ;
FatArrow : '=>' ;
BangFatArrow : '!=>' ;
Identifier : [a-zA-Z] [a-zA-Z0-9]* ;

IntLiteral : [0-9]+ ;
SymbolLiteral : Backtick [a-zA-Z0-9]+ Backtick ;
StringLiteral : '"' StringLiteralContent '"' ;

fragment StringLiteralContent : (~["\r\n])* ;

Whitespace : (' ' | '\n') -> skip ;
LineComment : '//' ~[\r\n]* -> skip ;
