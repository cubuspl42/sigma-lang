package sigma.syntax.expressions

interface AbstractionTerm {
    val genericParametersTuple: GenericParametersTuple?

    val argumentType: TupleTypeConstructorTerm

    val declaredImageType: ExpressionTerm?

    val image: ExpressionTerm
}
