parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

module
    : importSection namespaceBody
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

namespaceEntry
    : constantDefinition
    | classDefinition
    | namespaceDefinition
    ;

constantDefinition
    : ConstKeyword name=Identifier (Colon type=expression)? Assign definer=expression
    ;

namespaceDefinition
    : NamespaceKeyword name=Identifier LeftParen namespaceBody RightParen
    ;

namespaceBody
    : namespaceEntry*
    ;

// Class

classDefinition
    : ClassKeyword name=Identifier LeftParen
          FieldsKeyword LeftParen fieldDeclaration+ RightParen
          methodDefinition*
      RightParen
    ;

fieldDeclaration
    : name=Identifier Colon type=expression
    ;

methodDefinition
    : MethodKeyword name=Identifier Assign body=expression
    ;

// end

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
    | ifExpression # ifExpressionAlt
    | SymbolLiteral # symbolLiteralAlt
    | IntLiteral # intLiteralAlt
    | tupleTypeConstructor # tupleTypeConstructorAlt
    | functionTypeConstructor # functionTypeConstructorAlt
    | arrayTypeConstructor # arrayTypeConstructorAlt
    | dictTypeConstructor # dictTypeConstructorAlt
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
    : LeftBrace (definition (Comma definition)*)? Comma? RightBrace ;

definition
    : name=identifier (Colon valueType=expression)? Assign value=expression
    ;

// end

ifExpression
    : IfKeyword guard=expression LeftParen
          ThenKeyword trueBranch=expression Comma
          ElseKeyword falseBranch=expression Comma?
      RightParen
    ;

abstraction
    :   genericParametersTuple? argumentType=tupleTypeConstructor
        (ThinArrow imageType=expression)? FatArrow image=expression
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

typeTupleConstructor
    : LeftBracket (elements+=expression (Comma elements+=expression)* Comma?)? RightBracket
    ;

tupleTypeConstructor
    : unorderedTupleTypeConstructor
    | orderedTupleTypeConstructor
    ;

functionTypeConstructor
    : genericParametersTuple? argumentType=tupleTypeConstructor ThinArrow imageType=expression
    ;

// Unordered tuple type constructor

unorderedTupleTypeConstructor
    : Dash LeftBrace (unorderedTupleTypeEntry (Comma unorderedTupleTypeEntry)*)? Comma? RightBrace ;

unorderedTupleTypeEntry
    : name=identifier Colon valueType=expression
    ;

// end

// Ordered tuple type constructor

orderedTupleTypeConstructor
    : Dash LeftBracket (orderedTupleTypeElement (Comma orderedTupleTypeElement)* Comma?)? RightBracket
    ;

orderedTupleTypeElement
    : (name=identifier Colon)? type=expression
    ;

// end

arrayTypeConstructor
    : Dash LeftBracket type=expression Ellipsis RightBracket
    ;

dictTypeConstructor
    : Dash LeftBrace LeftBracket keyType=expression RightBracket Colon valueType=expression RightBrace
    ;

genericTypeConstructor
    : genericParametersTuple body=expression
    ;

// Other

genericParametersTuple
    : Bang LeftBracket genericParameterDeclaration ((Comma genericParameterDeclaration)+ Comma?)? RightBracket
    ;

genericParameterDeclaration
    : name=identifier
    ;

identifier
    : Identifier ;
