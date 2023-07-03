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
    | tupleConstructor # tupleConstructorAlt
    | dictConstructor # dictConstructorAlt
    | setConstructor # setConstructorAlt
    | letExpression # letExpressionAlt
    | isUndefinedCheck # isUndefinedCheckAlt
    | SymbolLiteral # symbolLiteralAlt
    | IntLiteral # intLiteralAlt
    | callableExpression # callableExpressionAlt
    ;

// For left-recursion
callableExpression
    : callee=callableExpression LeftParen argument=expression RightParen # callExpressionAlt
    | callee=callableExpression argument=tupleConstructor # callExpressionTupleConstructorAlt
    | subject=callableExpression Dot fieldName=Identifier # fieldReadAlt
    | parenExpression # callableParenAlt
    | reference # callableReferenceAlt
    | tupleConstructor # callableTupleConstructorAlt
    ;

tupleConstructor
    : unorderedTupleConstructor
    | orderedTupleConstructor
    ;

// Let expression

letExpression
    : LetKeyword scope=localScope InKeyword result=expression ;

localScope
    : LeftBrace (declaration (Comma declaration)*)? Comma? RightBrace ;

declaration
    : name=identifier (Colon valueType=typeExpression)? Assign value=expression
    ;

// end

abstraction
    :   (Bang genericParametersTuple)? argumentType=tupleTypeConstructor
        (ThinArrow imageType=typeExpression)? FatArrow image=expression
    ;

// Unordered tuple constructor

unorderedTupleConstructor
    : LeftBrace (unorderedTupleAssociation (Comma unorderedTupleAssociation)*)? Comma? RightBrace ;

unorderedTupleAssociation
    : name=identifier Colon value=expression
    ;

// end

// Ordered tuple constructor

orderedTupleConstructor
    : LeftBracket (orderedTupleElement (Comma orderedTupleElement)* Comma?)? RightBracket
    ;

orderedTupleElement
    : expression
    ;

// end

// Dict constructor

dictConstructor
    : LeftBrace dictAssociation (Comma dictAssociation)* Comma? RightBrace
    ;

dictAssociation
    : LeftBracket key=expression RightBracket Colon value=expression
    ;

// end

// Set constructor

setConstructor
    : LeftBrace (elements+=expression (Comma elements+=expression)* Comma?)? RightBrace
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
    : typeCall
    | typeReference
    | functionTypeDepiction
    | tupleTypeConstructor
    | arrayTypeConstructor
    | dictTypeDepiction
    ;

typeCall
    : callee=typeReference passedArgument=typeTupleConstructor
    ;

typeTupleConstructor
    : LeftBracket (elements+=typeExpression (Comma elements+=typeExpression)* Comma?)? RightBracket
    ;

typeReference
    : referee=identifier
    ;

tupleTypeConstructor
    : unorderedTupleTypeConstructor
    | orderedTupleTypeConstructor
    ;

functionTypeDepiction
    : (Bang genericParametersTuple)? argumentType=tupleTypeConstructor ThinArrow imageType=typeExpression
    ;

// Unordered tuple type constructor

unorderedTupleTypeConstructor
    : LeftBrace (unorderedTupleTypeEntry (Comma unorderedTupleTypeEntry)*)? Comma? RightBrace ;

unorderedTupleTypeEntry
    : name=identifier Colon valueType=typeExpression
    ;

// end

// Ordered tuple type constructor

orderedTupleTypeConstructor
    : LeftBracket (orderedTupleTypeElement (Comma orderedTupleTypeElement)* Comma?)? RightBracket
    ;

orderedTupleTypeElement
    : (name=identifier Colon)? type=typeExpression
    ;

// end

arrayTypeConstructor
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
