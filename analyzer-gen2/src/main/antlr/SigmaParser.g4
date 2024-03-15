parser grammar SigmaParser;

options { tokenVocab = SigmaLexer; }

// ## Module

module
    : imports=import_* moduleDefinition+
    ;

import_
    : ImportKeyword importedModuleName=Identifier (AsKeyword aliasName=Identifier)?
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
    : FunKeyword name=Identifier argumentType=tupleTypeConstructor FatArrow body=expression
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
    : MatchKeyword matched=expression LeftParen patternBlocks+=matchCase+ RightParen
    ;

matchCase
    : matchPattern FatArrow result=expression
    ;

matchPattern
    : destructuringPattern
    | tagPattern
    ;

tagPattern
    : class=expression AsKeyword newName=Identifier
    ;

// ### Abstraction constructor & abstraction constructor-alikes

// #### Abstraction

abstractionConstructor
    : argumentType=tupleTypeConstructor FatArrow image=expression
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
    : LetKeyword block=definitionBlock InKeyword result=expression
    ;

definitionBlock
    : LeftBrace (definition (Comma definition)*)? Comma? RightBrace
    ;

definition
    : definitionLhs Equals initializer=expression
    ;

definitionLhs
    : name=Identifier # nameDefinitionLhs
    | destructuringPattern # destructuringPatternDefinitionLhs
    ;

// ### Destructuring patterns

destructuringPattern
    : listEmptyPattern
    | listUnconsPattern
//    | unorderedTupleDestructuringPattern
    ;

listEmptyPattern
    : LeftBracket RightBracket
    ;

listUnconsPattern
    : LeftBracket headName=Identifier Comma Ellipsis tailName=Identifier RightBracket
    ;

// ### Tuple type constructors

tupleTypeConstructor
    : unorderedTupleTypeConstructor
    | orderedTupleTypeConstructor
    ;

// #### Unordered tuple type constructor

unorderedTupleTypeConstructor
    : Dash LeftBrace (unorderedTupleTypeConstructorEntry (Comma unorderedTupleTypeConstructorEntry)*)? Comma? RightBrace
    ;

unorderedTupleTypeConstructorEntry
    : key=Identifier
    ;

// #### Ordered tuple type constructor

orderedTupleTypeConstructor
    : Dash LeftBracket (orderedTupleTypeConstructorEntry (Comma orderedTupleTypeConstructorEntry)*)? Comma? RightBracket
    ;

orderedTupleTypeConstructorEntry
    : key=Identifier
    ;
