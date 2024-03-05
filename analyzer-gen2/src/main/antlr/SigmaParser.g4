parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

// ## Module

module
    : moduleDefinition+
    ;

moduleDefinition
    : valueDefinition
    | functionDefinition
    ;

valueDefinition
    : ValKeyword name=Identifier Equals initializer=expression
    ;

functionDefinition
    : FunKeyword name=Identifier argumentType=unorderedTupleTypeConstructor FatArrow body=expression
    ;

// ## Expression

expression
    : callee # calleeExpressionAlt
    | when # whenExpressionAlt
    | abstractionConstructor # abstractionConstructorExpressionAlt
    | unorderedTupleConstructor # unorderedTupleConstructorExpressionAlt
    | booleanLiteral # booleanLiteralExpressionAlt
    | letIn # letInExpressionAlt
    ;

// For left-recursion
callee
    : callee argument=unorderedTupleConstructor # callCallableExpressionAlt
    | callee Dot readFieldName=Identifier # fieldReadCallableExpressionAlt
    | reference # referenceCallableExpressionAlt
    ;

// ### Reference

reference
    : referredName=Identifier
    ;

// ### Call & call-alikes

// #### When

when
    : WhenKeyword LeftParen whenConditionalEntry+ whenElseEntry? RightParen
    ;

whenConditionalEntry
    : CaseKeyword condition=expression FatArrow result=expression
    ;

whenElseEntry
    : ElseKeyword FatArrow result=expression
    ;

// ### Abstraction constructor & abstraction constructor-alikes

// #### Abstraction

abstractionConstructor
    : argumentType=unorderedTupleTypeConstructor FatArrow image=expression
    ;

// #### Unordered tuple constructor

unorderedTupleConstructor
    : LeftBrace (unorderedTupleConstructorEntry (Comma unorderedTupleConstructorEntry)*)? Comma? RightBrace
    ;

unorderedTupleConstructorEntry
    : key=Identifier Equals value=expression
    ;

// #### Literals

booleanLiteral
    : trueLiteral
    | falseLiteral
    ;

falseLiteral
    : FalseKeyword
    ;

trueLiteral
    : TrueKeyword
    ;

// #### Let-in

letIn
    : LetKeyword block=unorderedTupleConstructor InKeyword result=expression
    ;

// ### Unordered tuple type constructor

unorderedTupleTypeConstructor
    : Dash LeftBrace (unorderedTupleTypeConstructorEntry (Comma unorderedTupleTypeConstructorEntry)*)? Comma? RightBrace
    ;

unorderedTupleTypeConstructorEntry
    : key=Identifier Colon value=expression
    ;
