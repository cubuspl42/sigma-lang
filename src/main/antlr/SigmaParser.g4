parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

program
    : letExpression ;

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
    | SymbolLiteral # symbolLiteralAlt
    | IntLiteral # intLiteralAlt
    | callableExpression # callableExpressionAlt
    ;

// For left-recursion
callableExpression
    : callee=callableExpression LeftParen argument=expression RightParen # callExpressionAlt
    | callee=callableExpression argument=tupleLiteral # callExpressionTupleLiteralAlt
    | parenExpression # callableParenAlt
    | reference # callableReferenceAlt
    | tupleLiteral # callableTupleLiteralAlt
    ;

tupleLiteral
    : unorderedTupleLiteral
    | orderedTupleLiteral
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

// Abstraction

abstraction
    : (Bang genericParametersTuple)? argumentType=tupleTypeLiteral Arrow image=expression
    ;

genericParametersTuple
    : LeftBracket genericParameterDeclaration ((Comma genericParameterDeclaration)+ Comma?)? RightBracket
    ;

genericParameterDeclaration
    : name=identifier
    ;

// end

// Unordered tuple literal

unorderedTupleLiteral
    : LeftBrace (unorderedTupleAssociation (Comma unorderedTupleAssociation)*)? Comma? RightBrace ;

unorderedTupleAssociation
    : name=identifier Colon value=expression
    ;

// end

// Ordered tuple literal

orderedTupleLiteral
    : LeftBracket (orderedTupleElement (Comma orderedTupleElement)* Comma?)? RightBracket
    ;

orderedTupleElement
    : expression
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

parenExpression
    : LeftParen expression RightParen
    ;

reference
    : referee=identifier
    ;

// Type expressions

typeExpression
    : reference
    | tupleTypeLiteral
    | arrayTypeLiteral
    ;

tupleTypeLiteral
    : unorderedTupleTypeLiteral
    | orderedTupleTypeLiteral
    ;

// Unordered tuple type literal

unorderedTupleTypeLiteral
    : LeftBrace (unorderedTupleTypeEntry (Comma unorderedTupleTypeEntry)*)? Comma? RightBrace ;

unorderedTupleTypeEntry
    : name=identifier Colon valueType=typeExpression
    ;

// end

// Ordered tuple type literal

orderedTupleTypeLiteral
    : LeftBracket (orderedTupleTypeElement (Comma orderedTupleTypeElement)* Comma?)? RightBracket
    ;

orderedTupleTypeElement
    : (name=identifier Colon)? type=typeExpression
    ;

// end

arrayTypeLiteral
    : LeftBracket type=typeExpression Asterisk RightBracket
    ;

// Other

identifier
    : Identifier ;
