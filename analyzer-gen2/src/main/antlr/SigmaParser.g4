parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

expression
    : reference # referenceExpressionAlt
    | call # callExpressionAlt
    | unorderedTupleConstructor # unorderedTupleConstructorExpressionAlt
    | abstractionConstructor # abstractionConstructorExpressionAlt
    ;

reference
    : referredName=Identifier
    ;

call
    : callee=reference passedArgument=unorderedTupleConstructor
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
