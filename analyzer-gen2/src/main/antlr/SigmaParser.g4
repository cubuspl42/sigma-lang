parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

expression
    : unorderedTupleConstructor # unorderedTupleConstructorExpressionAlt
    ;

unorderedTupleConstructor
    : LeftBrace (unorderedTupleConstructorEntry (Comma unorderedTupleConstructorEntry)*)? Comma? RightBrace
    ;

unorderedTupleConstructorEntry
    : key=Identifier Colon value=expression
    ;
