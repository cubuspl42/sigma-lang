package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParserBaseVisitor
import com.github.cubuspl42.sigmaLang.core.ExpressionBuilder
import com.github.cubuspl42.sigmaLang.core.LocalScope
import com.github.cubuspl42.sigmaLang.core.ShadowExpression
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.FormationContext
import com.github.cubuspl42.sigmaLang.shell.stubs.ExpressionStub
import com.github.cubuspl42.sigmaLang.shell.stubs.map
import kotlinx.serialization.json.internal.decodeStringToJsonTree
import org.antlr.v4.runtime.Token

sealed class BindingTerm {
    companion object {
        fun build(ctx: SigmaParser.BindingContext): BindingTerm {
            return object : SigmaParserBaseVisitor<BindingTerm>() {
                override fun visitListUnconsBinding(
                    ctx: SigmaParser.ListUnconsBindingContext,
                ): BindingTerm = ListUnconsBindingTerm(
                    headName = IdentifierTerm.build(ctx.headName).transmute(),
                    tailName = IdentifierTerm.build(ctx.tailName).transmute(),
                )

                override fun visitNameBinding(
                    ctx: SigmaParser.NameBindingContext,
                ): BindingTerm = NameBindingTerm(
                    name = IdentifierTerm.build(ctx.name).transmute(),
                )
            }.visit(ctx)
        }
    }

    abstract val names: Set<Identifier>

    abstract fun transmute(
        initializerStub: ExpressionStub<ShadowExpression>,
    ): ExpressionStub<LocalScope.Constructor.Definition>

}

data class ListUnconsBindingTerm(
    val headName: Identifier,
    val tailName: Identifier,
) : BindingTerm() {
    override val names: Set<Identifier>
        get() = setOf(headName, tailName)

    override fun transmute(
        initializerStub: ExpressionStub<ShadowExpression>,
    ) = object : ExpressionStub<LocalScope.Constructor.ListUnconsDefinition>() {
        override fun transform(
            context: FormationContext,
        ) = object : ExpressionBuilder<LocalScope.Constructor.ListUnconsDefinition>() {
            override fun build(buildContext: Expression.BuildContext) = LocalScope.Constructor.ListUnconsDefinition(
                headName = headName,
                tailName = tailName,
                listInitializer = initializerStub.build(
                    formationContext = context,
                    buildContext = buildContext,
                ),
            )
        }
    }
}

data class NameBindingTerm(
    val name: Identifier,
) : BindingTerm() {
    override val names: Set<Identifier>
        get() = setOf(name)

    override fun transmute(
        initializerStub: ExpressionStub<ShadowExpression>,
    ): ExpressionStub<LocalScope.Constructor.Definition> = initializerStub.map { initializer ->
        LocalScope.Constructor.SimpleDefinition(
            name = name,
            initializer = initializer,
        )
    }
}
