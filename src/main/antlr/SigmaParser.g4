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
    | tuple # TupleAlt
    | letExpression # letExpressionAlt
    | SymbolLiteral # symbolLiteralAlt
    | IntLiteral # intLiteralAlt
    | callableExpression # callableExpressionAlt
    ;

// For left-recursion
callableExpression
    : callee=callableExpression LeftBracket argument=expression RightBracket # callExpressionAlt
    | callee=callableExpression argument=tuple # callExpressionTupleAlt
    | parenExpression # callableParenAlt
    | reference # callableReferenceAlt
    | tuple # callableTupleAlt
    ;

parenExpression
    : LeftParen expression RightParen
    ;

reference
    : referee=identifier
    ;

abstraction
    :   (Bang metaArgument)?
        LeftBracket argumentName=identifier (Colon argumentType=typeExpression)? RightBracket
        Arrow image=expression
    ;

metaArgument
    : LeftBracket name=identifier RightBracket
    ;

identifier
    : Identifier ;

// TODO: Rename to *Literal?
tuple
    // TODO: Nuke content?
    : content=unorderedTuple # unorderedTupleAlt
    | content=orderedTuple # orderedTupleAlt
    ;

unorderedTuple
    : LeftBrace (association (Comma association)*)? Comma? RightBrace ;

association
    : name=identifier Colon image=expression # symbolBindAlt // TOOD: Rename
    | LeftBracket key=expression RightBracket Colon image=expression # arbitraryBindAlt
    ;

orderedTuple
    : LeftBracket ((element (Comma element)*)? Comma?)? RightBracket ;

element
    : expression
    ;

letExpression
    : LetKeyword scope=letScope InKeyword result=expression ;

letScope
    : LeftBrace (declaration (Comma declaration)*)? Comma? RightBrace ;

declaration
    : name=identifier (Colon valueType=typeExpression)? Assign value=expression
    ;

typeExpression
    : reference ;
