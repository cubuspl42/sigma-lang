parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

// ## Module

module
    : definition+
    ;

definition
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
    : reference # referenceExpressionAlt
    | call # callExpressionAlt
    | fieldRead # fieldReadExpressionAlt
    | abstractionConstructor # abstractionConstructorExpressionAlt
    | unorderedTupleConstructor # unorderedTupleConstructorExpressionAlt
    | letIn # letInExpressionAlt
    ;

// ### Reference

reference
    : referredName=Identifier
    ;

// ### Call & call-alikes

// #### Call

call
    : callee=reference passedArgument=unorderedTupleConstructor
    ;

// #### Field read

fieldRead
    : subject=reference readFieldName=Identifier
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

// ### Let-in

letIn
    : LetKeyword block=unorderedTupleConstructor InKeyword result=expression
    ;

// ### Unordered tuple type constructor

unorderedTupleTypeConstructor
    : LeftBrace (unorderedTupleTypeConstructorEntry (Comma unorderedTupleTypeConstructorEntry)*)? Comma? RightBrace
    ;

unorderedTupleTypeConstructorEntry
    : key=Identifier Colon value=expression
    ;
