parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

// ## Module

module
    : imports=import_* moduleDefinition+
    ;

import_
    : ImportKeyword importedModuleName=Identifier
    ;

moduleDefinition
    : valueDefinition
    | functionDefinition
    | classDefinition
    ;

valueDefinition
    : ValKeyword name=Identifier Equals initializer=expression
    ;

functionDefinition
    : FunKeyword name=Identifier argumentType=unorderedTupleTypeConstructor FatArrow body=expression
    ;

classDefinition
    : ClassKeyword name=Identifier LeftBrace constructor=classConstructorDeclaration? methodDefinitions+=functionDefinition* RightBrace
    ;

classConstructorDeclaration
    : ConstructorKeyword name=Identifier argumentType=unorderedTupleTypeConstructor
    ;

// ## Expression

expression
    : instance=expression IsAKeyword class=expression # isAExpressionAlt
    | left=expression variant=(ConcatStringsOperator|ConcatListsOperator) right=expression # concatExpressionAlt
    | callee # calleeExpressionAlt
    | when # whenExpressionAlt
    | match # matchExpressionAlt
    | abstractionConstructor # abstractionConstructorExpressionAlt
    | tupleConstructor # tupleConstructorExpressionAlt
    | stringLiteral # stringLiteralExpressionAlt
    | booleanLiteral # booleanLiteralExpressionAlt
    | letIn # letInExpressionAlt
    ;

// For left-recursion
callee
    : callee argument=tupleConstructor # callCallableExpressionAlt
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

// #### Match

match
    : MatchKeyword matched=expression LeftParen patternBlocks+=patternBlock+ RightParen
    ;

patternBlock
    : pattern FatArrow result=expression
    ;

// ### Abstraction constructor & abstraction constructor-alikes

// #### Abstraction

abstractionConstructor
    : argumentType=unorderedTupleTypeConstructor FatArrow image=expression
    ;

tupleConstructor
    : unorderedTupleConstructor
    | orderedTupleConstructor
    ;

// #### Unordered tuple constructor

unorderedTupleConstructor
    : LeftBrace (unorderedTupleConstructorEntry (Comma unorderedTupleConstructorEntry)*)? Comma? RightBrace
    ;

unorderedTupleConstructorEntry
    : key=Identifier Equals value=expression
    ;

// #### Ordered tuple constructor

orderedTupleConstructor
    : LeftBracket (expression (Comma expression)*)? Comma? RightBracket
    ;

// #### Literals

stringLiteral
    : StringLiteral # stringLiteralAlt
    ;

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
    : LetKeyword block=letInBlock InKeyword result=expression
    ;

letInBlock
    : LeftBrace (letInBlockEntry (Comma letInBlockEntry)*)? Comma? RightBrace
    ;

letInBlockEntry
    : pattern Equals initializer=expression
    ;

// ### Patterns

pattern
    : identityPattern
    | listUnconsPattern
    | tagPattern
    ;

identityPattern
    : name=Identitfier
    ;

listUnconsPattern
    : LeftBracket headName=Identifier Comma Ellipsis tailName=Identifier RightBracket
    ;

tagPattern
    : class=expression AsKeyword newName=Identifier
    ;

// ### Unordered tuple type constructor

unorderedTupleTypeConstructor
    : Dash LeftBrace (unorderedTupleTypeConstructorEntry (Comma unorderedTupleTypeConstructorEntry)*)? Comma? RightBrace
    ;

unorderedTupleTypeConstructorEntry
    : key=Identifier Colon value=expression
    ;
