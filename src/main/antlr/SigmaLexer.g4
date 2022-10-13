lexer grammar SigmaLexer;

LetKeyword : 'let' ;
InKeyword : 'in' ;

LeftParen : '(' ;
RightParen : ')' ;
LeftBrace : '{' ;
RightBrace : '}' ;
LeftBracket : '[' ;
RightBracket : ']' ;

Lte : '<=' ;
Gte : '>=' ;
Equals : '==' ;
Link : '..' ;
Colon : ':' ;
Comma : ',' ;
Backtick : '`' ;
Assign : '=' ;
Asterisk : '*' ;
Slash : '/' ;
Plus : '+' ;
Minus : '-' ;
Lt : '<' ;
Gt : '>' ;

Arrow : '=>' ;
CharSequence : ([a-zA-Z0-9])+ ;

Whitespace : (' ' | '\n') -> skip ;
LineComment : '//' ~[\r\n]* -> skip ;
