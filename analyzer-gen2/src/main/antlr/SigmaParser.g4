parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

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
    : key=Identifier Colon value=expression
    ;

abstractionConstructor
    : argumentType=unorderedTupleTypeConstructor FatArrow image=expression
    ;

unorderedTupleTypeConstructor
    : Dash body=unorderedTupleConstructor
    ;

letIn
    : LetKeyword block=unorderedTupleConstructor InKeyword result=expression
    ;
