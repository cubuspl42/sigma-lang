package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ReachableDeclarationSet
import com.github.cubuspl42.sigmaLang.analyzer.semantics.CyclicComputation
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm

abstract class Reference : FirstOrderExpression() {
    abstract override val term: ReferenceTerm?

    abstract val referredDeclaration: Declaration

    data class UnresolvedNameError(
        override val location: SourceLocation?,
        val name: Symbol,
    ) : SemanticError

    data class NonValueDeclarationError(
        override val location: SourceLocation?,
        val name: Identifier,
    ) : SemanticError

    companion object {
        fun build(
            context: BuildContext,
            term: ReferenceTerm,
        ): Stub<Expression> = build(
            context = context,
            referredName = term.referredName,
        )

        fun build(
            context: BuildContext,
            referredName: Symbol,
        ): Stub<Expression> = object : Stub<Expression> {
            override val resolved: Expression by lazy {
                val outerScope = context.outerScope

                // TODO: Clean error
                val resolvedName = outerScope.resolveNameLeveled(name = referredName) ?: run {
                    throw IllegalStateException("Unresolved name at compile-time: $referredName")
                }

                resolvedName.resolvedIntroduction.buildReference()
            }
        }
    }

    override val computedDiagnosedAnalysis by lazy {
        Computation.pure(
            DiagnosedAnalysis(
                analysis = Analysis(
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
