package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.ExpressedAbstraction
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
import com.github.cubuspl42.sigmaLang.shell.scope.StaticScope
import com.github.cubuspl42.sigmaLang.shell.terms.ModuleTerm
import com.github.cubuspl42.sigmaLang.utils.wrapWithLazyOf
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.typeNameOf
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class Module(
    val root: AbstractionConstructor,
) {
    abstract class VisitingContext {
        private val visitedExpressions = mutableSetOf<Expression>()

        fun visitOnce(expression: Expression) {
            if (expression in visitedExpressions) {
                return
            }

            visitedExpressions.add(expression)

            visit(expression)

            expression.subExpressions.forEach { visitOnce(it) }
        }

        abstract fun visit(expression: Expression)
    }

    class ReferenceCountContext : VisitingContext() {
        private val referenceCountByExpression = mutableMapOf<Expression, Int>()

        override fun visit(
            expression: Expression,
        ) {
            referenceCountByExpression[expression] = referenceCountByExpression.getOrDefault(expression, 0) + 1
        }

        fun getReferenceCount(
            expression: Expression,
        ): Int = referenceCountByExpression.getOrDefault(expression, 0)
    }

    class CodegenRepresentationContext(
        val module: Module,
    ) : VisitingContext() {
        companion object {
            val valueTypeName = typeNameOf<Value>()

            val lazyValueTypeName = typeNameOf<Lazy<Value>>()
        }

        private var nextId = 0

        private val representationByExpression: MutableMap<Expression, Expression.CodegenRepresentation> =
            mutableMapOf()

        fun getRepresentation(expression: Expression): Expression.CodegenRepresentation =
            representationByExpression[expression] ?: throw IllegalStateException("No representation for $expression")

        fun generateUniqueName(prefix: String): String = "${prefix}${nextId++}"

        override fun visit(expression: Expression) {
            representationByExpression[expression] = expression.buildCodegenRepresentation(
                context = this,
            )
        }
    }

    companion object {
        val builtinScopeMemberName = MemberName("com.github.cubuspl42.sigmaLang", "BuiltinScope")

        fun fromSource(source: String): Module {
            val sourceName = "__main__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            val moduleTerm = ModuleTerm.build(parser.module())

            return object {
                val module: Module by lazy {
                    Module(
                        root = moduleTerm.construct(
                            context = ConstructionContext(
                                scope = StaticScope.Empty,
                                moduleRoot = lazy { module.root },
                            ),
                        ).value,
                    )
                }
            }.module
        }
    }

    val main: Value
        get() {
            val rootAbstraction = root.bind(scope = DynamicScope.Empty).value as ExpressedAbstraction

            return rootAbstraction.call(
                argument = UnorderedTuple(
                    valueByKey = mapOf(
                        Identifier(name = "builtin") to lazyOf(BuiltinScope),
                    )
                ),
            )
        }

    private val referenceCountContext by lazy {
        ReferenceCountContext().apply { visitOnce(root) }
    }

    fun getReferenceCount(
        expression: Expression,
    ): Int = referenceCountContext.getReferenceCount(expression)

    fun generateCode(packageName: String, name: String): FileSpec {
        val context = CodegenRepresentationContext(
            module = this,
        ).apply {
            visitOnce(
                root,
            )
        }

        val rootRepresentation = context.getRepresentation(root)

        return FileSpec.builder(packageName, "out").addAnnotation(
            AnnotationSpec.builder(Suppress::class).addMember("%S", "RedundantVisibilityModifier")
                .addMember("%S", "unused").build()
        ).addType(
            TypeSpec.objectBuilder(
                name = name,
            ).addProperty(
                PropertySpec.builder(
                    name = "root",
                    type = CodegenRepresentationContext.lazyValueTypeName,
                ).initializer(
                    rootRepresentation.generateCode(),
                ).build()
            ).addProperty(
                PropertySpec.builder(
                    name = "main",
                    type = CodegenRepresentationContext.valueTypeName,
                ).initializer(
                    CodeBlock.builder().add(
                        """
                            (root.value as %T).call(
                            ⇥argument = %L,
                            ⇤)
                        """.trimIndent(),
                        Callable::class,
                        UnorderedTupleConstructor.generateCode(
                            valueByKey = mapOf(
                                Identifier(name = "builtin") to CodeBlock.of("%M", builtinScopeMemberName)
                                    .wrapWithLazyOf(),
                            ),
                        ),
                    ).build(),
                ).build()
            ).build()
        ).build()
    }
}
