package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.CyclicComputation
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ReachableDeclarationSet
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm

/**
 * Argument reference
 */
abstract class Reference : FirstOrderExpression() {
    abstract override val term: ReferenceTerm?

    abstract val referredDeclaration: Declaration

    data class NonValueDeclarationError(
        override val location: SourceLocation?,
        val name: Identifier,
    ) : SemanticError

    override val computedAnalysis by lazy {
        Computation.pure(
            Analysis(
                typeInference = TypeInference(
                    inferredType = referredDeclaration.declaredType,
                ),
                directErrors = emptySet(),
            )
        )
    }

    override val computedReachableDeclarations: CyclicComputation<ReachableDeclarationSet> =
        object : CyclicComputation<ReachableDeclarationSet>() {
            override fun compute(context: Context) = ReachableDeclarationSet(
                reachableDeclarations = setOf(referredDeclaration),
            )
        }

    override val subExpressions: Set<Expression> = emptySet()

    override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = dynamicScope.getValue(
        declaration = referredDeclaration,
    ) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referredDeclaration",
    )

    fun readField(fieldName: Identifier): FieldRead = FieldRead(
        subjectLazy = lazy { this },
        fieldName = fieldName,
    )
}

fun Reference(
    referredDeclaration: Declaration,
): Reference = object : Reference() {
    override val outerScope: StaticScope = StaticScope.Empty

    override val term: ReferenceTerm? = null

    override val referredDeclaration: Declaration = referredDeclaration
}
