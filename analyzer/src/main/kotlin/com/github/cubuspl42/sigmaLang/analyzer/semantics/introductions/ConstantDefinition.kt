package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ConstClassificationContext
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ConstantDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceEntryTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm

abstract class ConstantDefinition : Definition {
    companion object {
        fun build(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: NamespaceEntryTerm,
        ): ConstantDefinition {
            val extendedQualifiedPath = qualifiedPath.extend(term.name)

            return when (term) {
                is ConstantDefinitionTerm -> UserConstantDefinition.build(
                    context = context,
                    term = term,
                )

                is ClassDefinitionTerm -> ClassDefinition.build(
                    context = context,
                    qualifiedPath = extendedQualifiedPath,
                    term = term,
                )

                is NamespaceDefinitionTerm -> TODO()

                else -> throw UnsupportedOperationException("Unsupported namespace entry term: $term")
            }
        }
    }

    //    fun evaluateResult(): EvaluationOutcome<Value> = valueThunk.evaluateInitial()
    private val classifiedBody: ConstClassificationContext<Value>
        get() = bodyStub.resolved.classifiedValue as ConstClassificationContext<Value>

    val valueThunk: Thunk<Value>
        get() = classifiedBody.valueThunk

//    open val expressionMap: ExpressionMap = ExpressionMap.Empty

    override val errors: Set<SemanticError> = emptySet()
}
