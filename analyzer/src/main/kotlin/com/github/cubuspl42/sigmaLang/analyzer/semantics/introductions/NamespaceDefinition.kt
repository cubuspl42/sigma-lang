package com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.QualifiedPath
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.ExpressionMap
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.MembershipType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.membership_types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.NamespaceDefinitionTerm

class NamespaceDefinition(
    private val outerScope: StaticScope,
    private val qualifiedPath: QualifiedPath,
    private val term: NamespaceDefinitionTerm,
) : ConstantDefinition(), UserDefinition {
    companion object {
        fun build(
            outerScope: StaticScope,
            qualifiedPath: QualifiedPath,
            term: NamespaceDefinitionTerm,
        ): NamespaceDefinition = NamespaceDefinition(
            outerScope = outerScope,
            qualifiedPath = qualifiedPath,
            term = term,
        )
    }

    inner class NamespaceStaticBlock : StaticBlock() {
        override fun resolveNameLocally(
            name: Symbol,
        ): ClassifiedIntroduction? = getDefinition(name = name)

        override fun getLocalNames(): Set<Symbol> = definitions.map { it.name }.toSet()
    }

    override val name: Symbol
        get() = term.name

    private val asDeclarationBlock = NamespaceStaticBlock()

    val innerStaticScope: StaticScope = asDeclarationBlock.chainWith(
        outerScope = outerScope,
    )

    val definitions: Set<ConstantDefinition> by lazy {
        term.namespaceEntries.map {
            build(
                outerScope = innerStaticScope,
                qualifiedPath = qualifiedPath,
                term = it,
            )
        }.toSet()
    }

    fun getDefinition(
        name: Symbol,
    ): ConstantDefinition? = definitions.singleOrNull {
        it.name == name
    }

    override val expressionMap: ExpressionMap = ExpressionMap.unionAllOf(definitions) {
        it.expressionMap
    }

    override val errors: Set<SemanticError> by lazy {
        definitions.fold(emptySet()) { acc, it -> acc + it.errors }
    }

    fun printErrors() {
        errors.forEach { println(it.dump()) }
    }

    override val valueThunk: Thunk<Value> by lazy {
        Thunk.pure(
            DictValue(
                entries = definitions.associate {
                    it.name to it.valueThunk.value!!
                },
            )
        )
    }

    override val computedEffectiveType: Expression.Computation<MembershipType> by lazy {
        Expression.Computation.pure(
            UnorderedTupleType(
                valueTypeByName = definitions.associate {
                    it.name to it.computedEffectiveType.getOrCompute()
                },
            ),
        )
    }
}
