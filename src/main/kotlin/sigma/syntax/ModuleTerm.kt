package sigma.syntax

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import sigma.SyntaxValueScope
import sigma.TypeScope
import sigma.parser.antlr.SigmaLexer
import sigma.parser.antlr.SigmaParser
import sigma.parser.antlr.SigmaParser.ImportStatementContext
import sigma.parser.antlr.SigmaParser.ModuleContext
import sigma.evaluation.values.LoopedStaticValueScope
import sigma.evaluation.values.Symbol
import sigma.evaluation.values.Value
import sigma.evaluation.scope.LoopedScope
import sigma.evaluation.scope.Scope

data class ModuleTerm(
    override val location: SourceLocation,
    val imports: List<Import>,
    val declarations: List<DefinitionTerm>,
) : Term() {
    override fun validate(
        typeScope: TypeScope,
        valueScope: SyntaxValueScope,
    ) {
        val newValueScope = LoopedStaticValueScope(
            typeContext = typeScope,
            valueContext = valueScope,
            declarations = declarations,
        )

        declarations.forEach {
            it.validate(
                typeScope = typeScope,
                valueScope = newValueScope,
            )
        }
    }

    fun evaluateDeclaration(
        name: String,
        scope: Scope,
    ): Value {
        val newScope = LoopedScope(
            context = scope,
            declarations = declarations.associate {
                it.name to it.value
            },
        )

        val thunk = newScope.getValue(
            name = Symbol.of(name),
        ) ?: throw IllegalStateException("Can't find symbol `${name}`")

        return thunk.toEvaluatedValue
    }

    companion object {
        fun parse(
            source: String,
        ): ModuleTerm {
            val sourceName = "__module__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            return build(parser.module())
        }

        fun build(ctx: ModuleContext): ModuleTerm {
            return ModuleTerm(
                location = SourceLocation.build(ctx),
                imports = ctx.importSection().importStatement().map {
                    Import.build(it)
                },
                declarations = ctx.moduleBody().declaration().map {
                    DefinitionTerm.build(it)
                },
            )
        }
    }

    data class Import(
        override val location: SourceLocation,
        val path: List<String>,
    ) : Term() {
        companion object {
            fun build(ctx: ImportStatementContext): Import {
                return Import(
                    location = SourceLocation.build(ctx),
                    path = ctx.importPath().identifier().map { it.text },
                )
            }
        }
    }
}
