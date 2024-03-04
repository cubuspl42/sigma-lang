parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

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

expression
    : reference # referenceExpressionAlt
    | call # callExpressionAlt
    | fieldRead # fieldReadExpressionAlt
    | unorderedTupleConstructor # unorderedTupleConstructorExpressionAlt
    | abstractionConstructor # abstractionConstructorExpressionAlt
    | letIn # letInExpressionAlt
    ;

reference
    : referredName=Identifier
    ;

call
    : callee=reference passedArgument=unorderedTupleConstructor
    ;


fieldRead
    : subject=reference readFieldName=Identifier
    ;

unorderedTupleConstructor
    : LeftBrace (unorderedTupleConstructorEntry (Comma unorderedTupleConstructorEntry)*)? Comma? RightBrace
    ;

unorderedTupleConstructorEntry
    : key=Identifier Equals value=expression
    ;

unorderedTupleTypeConstructor
    : LeftBrace (unorderedTupleTypeConstructorEntry (Comma unorderedTupleTypeConstructorEntry)*)? Comma? RightBrace
    ;

unorderedTupleTypeConstructorEntry
    : key=Identifier Colon value=expression
    ;

abstractionConstructor
    : argumentType=unorderedTupleTypeConstructor FatArrow image=expression
    ;

letIn
    : LetKeyword block=unorderedTupleConstructor InKeyword result=expression
    ;
