parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

program
    : expression ;

expression
    : left=expression operator=Asterisk right=expression # binaryOperationAlt
    | left=expression operator=Slash right=expression # binaryOperationAlt
    | left=expression operator=Plus right=expression # binaryOperationAlt
    | left=expression operator=Minus right=expression # binaryOperationAlt
    | left=expression operator=Lte right=expression # binaryOperationAlt
    | left=expression operator=Gte right=expression # binaryOperationAlt
    | left=expression operator=Equals right=expression # binaryOperationAlt
    | left=expression operator=Lt right=expression # binaryOperationAlt
    | left=expression operator=Gt right=expression # binaryOperationAlt
    | left=expression operator=Link right=expression # binaryOperationAlt
    | parenExpression # parenExpressionAlt
    | reference # referenceAlt
    | abstraction # abstractionAlt
    | dict # dictAlt
    | letExpression # letExpressionAlt
    | symbol # symbolAlt
    | callableExpression # callableExpressionAlt
    ;

// For left-recursion
callableExpression
    : callee=callableExpression LeftBracket argument=expression RightBracket # callExpressionAlt
    | callee=callableExpression argument=dict # callExpressionDictAlt
    | parenExpression # callableParenAlt
    | reference # callableReferenceAlt
    | dict # callableDictAlt
    ;

parenExpression
    : LeftParen expression RightParen
    ;

reference
    : referee=identifier
    ;

abstraction
    : argument=identifier Arrow image=expression ;

identifier
    : CharSequence ;

dict
    : content=table # dictTableAlt
    | content=array # dictArrayAlt;

table
    : LeftBrace (bind (bindSeparator bind)*)? bindSeparator? RightBrace ;

array
    : LeftBrace (bindImage (bindSeparator bindImage)*)? bindSeparator? RightBrace ;

// For [table]
bind
    : name=identifier Assign image=bindImage # symbolBindAlt
    | LeftBracket key=expression RightBracket Assign image=bindImage # arbitraryBindAlt
    ;

bindImage
    : image=expression ;

// For [table]
bindSeparator
    : Comma ;

letExpression
    : LetKeyword scope=table InKeyword result=expression ;

symbol
    : Backtick identifier Backtick ;
