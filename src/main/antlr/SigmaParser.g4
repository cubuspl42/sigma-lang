parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

module
    : importSection moduleBody
    ;

importSection
    : importStatement*
    ;

importStatement
    : ImportKeyword importPath
    ;

importPath
    : identifier (Dot identifier)*
    ;

moduleBody
    : declaration*
    ;

// Expressions

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
    | tupleLiteral # tupleLiteralAlt
    | dictLiteral # dictLiteralAlt
    | letExpression # letExpressionAlt
    | isUndefinedCheck # isUndefinedCheckAlt
    | SymbolLiteral # symbolLiteralAlt
    | IntLiteral # intLiteralAlt
    | callableExpression # callableExpressionAlt
    ;

// For left-recursion
callableExpression
    : callee=callableExpression LeftParen argument=expression RightParen # callExpressionAlt
    | callee=callableExpression argument=tupleLiteral # callExpressionTupleLiteralAlt
    | subject=callableExpression Dot fieldName=Identifier # fieldReadAlt
    | parenExpression # callableParenAlt
    | reference # callableReferenceAlt
    | tupleLiteral # callableTupleLiteralAlt
    ;

// Let expression

letExpression
    : LetKeyword scope=localScope InKeyword result=expression ;

localScope
    : LeftBrace declaration (Comma declaration)* Comma? RightBrace ;

declaration
    : name=identifier (Colon valueType=typeExpression)? Assign value=expression
    ;

// end

abstraction
    :   (Bang genericParametersTuple)? argumentType=tupleTypeLiteral
        (ThinArrow imageType=typeExpression)? FatArrow image=expression
    ;

// Tuple literal

tupleLiteral
    : LeftBracket (tupleAssociation (Comma tupleAssociation)*)? Comma? RightBracket ;

tupleAssociation
    : (targetName=identifier Colon)? passedValue=expression
    ;

// end

// Dict literal

dictLiteral
    : LeftBrace dictAssociation (Comma dictAssociation)* Comma? RightBrace
    ;

dictAssociation
    : LeftBracket key=expression RightBracket Colon value=expression
    ;

// end

isUndefinedCheck
    : IsUndefinedKeyword argument=expression
    ;

parenExpression
    : LeftParen expression RightParen
    ;

reference
    : referee=identifier
    ;

// Type expressions

typeExpression
    : functionTypeDepiction # typeExpressionFunctionTypeDepictionAlt
    | tupleTypeLiteral # typeExpressionTupleTypeLiteralAlt
    | arrayTypeLiteral # typeExpressionArrayTypeLiteralAlt
    | dictTypeDepiction # typeExpressionDictTypeDepictionAlt
    | reference # typeExpressionReferenceAlt
    ;

functionTypeDepiction
    : (Bang genericParametersTuple)? argumentType=tupleTypeLiteral ThinArrow imageType=typeExpression
    ;

// Tuple type literal

tupleTypeLiteral
    : LeftBrace RightBrace # emptyTupleTypeLiteralAlt
    | LeftBrace tupleTypeLiteralOrderedPart Comma? RightBrace # orderedTupleTypeLiteralAlt
    | LeftBrace tupleTypeLiteralUnorderedPart Comma? RightBrace # unorderedTupleTypeLiteralAlt
    | LeftBrace tupleTypeLiteralOrderedPart Comma tupleTypeLiteralUnorderedPart Comma? RightBrace # mixedTupleTypeLiteralAlt
    ;

tupleTypeLiteralOrderedPart
    : LeftParen entries+=tupleTypeLiteralEntry (Comma entries+=tupleTypeLiteralEntry)* RightParen
    ;

tupleTypeLiteralUnorderedPart
    : entries+=tupleTypeLiteralEntry (Comma entries+=tupleTypeLiteralEntry)*
    ;

tupleTypeLiteralEntry
    : name=identifier Colon valueType=typeExpression
    ;

// end

arrayTypeLiteral
    : LeftBracket type=typeExpression Asterisk RightBracket
    ;

dictTypeDepiction
    : LeftBrace LeftBracket keyType=typeExpression RightBracket Colon valueType=typeExpression RightBrace
    ;

// Other

genericParametersTuple
    : LeftBracket genericParameterDeclaration ((Comma genericParameterDeclaration)+ Comma?)? RightBracket
    ;

genericParameterDeclaration
    : name=identifier
    ;

identifier
    : Identifier ;
