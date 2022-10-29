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
    | SymbolLiteral # symbolLiteralAlt
    | IntLiteral # intLiteralAlt
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
    : Identifier ;

dict
    : content=table # dictTableAlt
    | content=array # dictArrayAlt;

table
    : LeftBrace (tableBind (Comma tableBind)*)? Comma? RightBrace ;

tableBind
    : name=identifier Assign image=bindImage # symbolBindAlt
    | LeftBracket key=expression RightBracket Assign image=bindImage # arbitraryBindAlt
    ;

array
    : LeftBrace (bindImage (Comma bindImage)*)? Comma? RightBrace ;

bindImage
    : image=expression ;

letExpression
    : LetKeyword scope=letScope InKeyword result=expression ;

letScope
    : LeftBrace (declaration (Comma declaration)*)? Comma? RightBrace ;

declaration
    : name=identifier (Colon valueType=typeExpression)? Assign value=expression
    ;

typeExpression
    : reference ;
