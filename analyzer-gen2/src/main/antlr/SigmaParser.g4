parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

expression
    : reference # referenceExpressionAlt
    | unorderedTupleConstructor # unorderedTupleConstructorExpressionAlt
    | abstractionConstructor # abstractionConstructorExpressionAlt
    ;

reference
    : referredName=Identifier
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
