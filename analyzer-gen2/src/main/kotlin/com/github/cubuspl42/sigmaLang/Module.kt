package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaLexer
import com.github.cubuspl42.sigmaLang.analyzer.parser.antlr.SigmaParser
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.shell.ConstructionContext
import com.github.cubuspl42.sigmaLang.shell.terms.ExpressionTerm
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.typeNameOf
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class Module(
    val root: Expression,
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
            val lazyValueTypeName = typeNameOf<Lazy<Value>>()
        }

        private var nextId = 0

        private val representationByExpression: MutableMap<Expression, Expression.OuterCodegenRepresentation> =
            mutableMapOf()

        fun getRepresentation(expression: Expression): Expression.OuterCodegenRepresentation =
            representationByExpression[expression] ?: throw IllegalStateException("No representation for $expression")

        fun generateUniqueName(): String = "var${nextId++}"

        override fun visit(expression: Expression) {
            representationByExpression[expression] = expression.buildOuterCodegenRepresentation(
                context = this,
            )
        }
    }

    companion object {
        fun fromSource(source: String): Module {
            val sourceName = "__main__"

            val lexer = SigmaLexer(CharStreams.fromString(source, sourceName))
            val tokenStream = CommonTokenStream(lexer)
            val parser = SigmaParser(tokenStream)

            val program = parser.expression()

            val rootExpressionTerm = ExpressionTerm.build(program)

            return Module(
                root = rootExpressionTerm.construct(
                    context = ConstructionContext.Empty,
                ).value,
            )
        }
    }

    private val referenceCountContext by lazy {
        ReferenceCountContext().apply { visitOnce(root) }
    }

    fun getReferenceCount(
        expression: Expression,
    ): Int = referenceCountContext.getReferenceCount(expression)

    fun generateCode(packageName: String): FileSpec {
        val context = CodegenRepresentationContext(
            module = this,
        ).apply {
            visitOnce(
                root,
            )
        }

        val rootRepresentation = context.getRepresentation(root)

        return FileSpec.builder(packageName, "out")
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                    .addMember("%S", "RedundantVisibilityModifier")
                    .addMember("%S", "unused")
                    .build()
            )
            .addProperty(
            PropertySpec.builder(
                name = "root",
                type = CodegenRepresentationContext.lazyValueTypeName,
            ).initializer(
                rootRepresentation.generateUsage()
            ).build()
        ).build()
    }
}
