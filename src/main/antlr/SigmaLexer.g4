lexer grammar SigmaLexer;

LetKeyword : 'let' ;
InKeyword : 'in' ;

LeftBrace : '{' ;
RightBrace : '}' ;
LeftBracket : '[' ;
RightBracket : ']' ;

Colon : ':' ;
Comma : ',' ;
Backtick : '`' ;
Assign : '=' ;

Arrow : '=>' ;
CharSequence : ([a-zA-Z0-9])+ ;

Whitespace : (' ' | '\n') -> skip ;
