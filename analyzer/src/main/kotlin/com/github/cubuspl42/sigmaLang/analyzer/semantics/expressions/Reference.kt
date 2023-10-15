package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassifiedExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ReachableDeclarationSet
import com.github.cubuspl42.sigmaLang.analyzer.semantics.CyclicComputation
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ReferenceTerm

abstract class Reference : Expression() {
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
            term = term,
            referredName = term.referredName,
        )

        fun build(
            context: BuildContext,
            term: ReferenceTerm?,
            referredName: Symbol,
        ): Stub<Expression> = object : Stub<Expression> {
            override val resolved: Expression by lazy {
                val outerScope = context.outerScope

                when (val resolvedIntroduction = outerScope.resolveName(name = referredName)!!) {
                    is Declaration -> object : Reference() {
                        override val outerScope: StaticScope = outerScope

                        override val term: ReferenceTerm? = term

                        override val referredDeclaration: Declaration = resolvedIntroduction
                    }

                    is Definition -> resolvedIntroduction.bodyStub.resolved

                    else -> throw UnsupportedOperationException()
                }
            }
        }
    }

    override val computedDiagnosedAnalysis by lazy {
        Computation.pure(
            DiagnosedAnalysis(
                analysis = Analysis(
                    inferredType = referredDeclaration.annotatedType,
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

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = dynamicScope.getValue(
        name = referredDeclaration,
    ) ?: throw RuntimeException(
        "Unresolved reference at run-time: $referredDeclaration",
    )
}
