package utils

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Introduction
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

data class FakeUserDeclaration(
    override val name: Identifier,
    override val annotatedType: MembershipType,
) : Declaration

data class FakeDefinition(
    override val name: Identifier,
    val type: MembershipType,
    val value: Value,
) : Definition {
    override val computedBodyType: Expression.Computation<TypeAlike> = Expression.Computation.pure(type)

    override val bodyStub: Expression.Stub<Expression> = object : Expression.Stub<Expression> {
        override val resolved: Expression = object : Expression() {
            override val outerScope: StaticScope
                get() = StaticScope.Empty

            override val term: ExpressionTerm?
                get() = null
            override val computedDiagnosedAnalysis: Computation<DiagnosedAnalysis?>
                get() = Computation.pure(
                    DiagnosedAnalysis(
                        analysis = Analysis(
                            inferredType = type,
                        ),
                        directErrors = emptySet(),
                    )
                )
            override val subExpressions: Set<Expression>
                get() = emptySet()

            override fun bindDirectly(dynamicScope: DynamicScope): Thunk<Value> = Thunk.pure(value)
        }
    }

    override val errors: Set<SemanticError> = emptySet()
}


class FakeStaticBlock(
    declarations: Set<Introduction>,
) : StaticBlock() {
    companion object {
        fun of(
            vararg declarations: Introduction,
        ): FakeStaticBlock = FakeStaticBlock(
            declarations = declarations.toSet(),
        )
    }

    private val declarationByName = declarations.associateBy { it.name }

    override fun resolveNameLocally(
        name: Symbol,
    ): Introduction? = declarationByName[name]

    override fun getLocalNames(): Set<Symbol> = declarationByName.keys
}
