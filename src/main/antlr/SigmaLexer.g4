lexer grammar SigmaLexer;

LetKeyword : 'let' ;
InKeyword : 'in' ;
ImportKeyword : 'import' ;
IsUndefinedKeyword : 'isUndefined' ;

LeftParen : '(' ;
RightParen : ')' ;
LeftBrace : '{' ;
RightBrace : '}' ;
LeftBracket : '[' ;
RightBracket : ']' ;
Lt : '<' ;
Gt : '>' ;
Percent : '%' ;

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

ThinArrow : '->' ;
FatArrow : '=>' ;
Identifier : [a-zA-Z] [a-zA-Z0-9]* ;

IntLiteral : [0-9]+ ;
SymbolLiteral : Backtick [a-zA-Z0-9]+ Backtick ;

Whitespace : (' ' | '\n') -> skip ;
LineComment : '//' ~[\r\n]* -> skip ;
