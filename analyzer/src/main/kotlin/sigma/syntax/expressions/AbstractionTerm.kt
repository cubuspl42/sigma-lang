package sigma.syntax.expressions

interface AbstractionTerm {
    val genericParametersTuple: GenericParametersTuple?

    val argumentType: TupleTypeConstructorTerm

    val declaredImageType: ExpressionSourceTerm?

    val image: ExpressionSourceTerm
}
