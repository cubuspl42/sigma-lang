package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.Scope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Closure
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.asThunk
import com.github.cubuspl42.sigmaLang.analyzer.semantics.DynamicResolution
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedName
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ValueDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.FunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionSourceTerm

class Abstraction(
    private val innerDeclarationScope: StaticScope,
    override val term: AbstractionSourceTerm,
    val genericParameters: Set<TypeVariable>,
    val argumentType: TupleType,
    val declaredImageTypeConstructor: Expression?,
    val image: Expression,
) : Expression() {
    class ArgumentDeclaration(
        override val name: Symbol,
        val type: Type,
    ) : ValueDeclaration {
        override val effectiveValueType: Thunk<Type> = Thunk.pure(type)
    }

    class ArgumentStaticBlock(
        argumentDeclarations: List<ArgumentDeclaration>,
    ) : StaticBlock() {
        private val declarationByName = argumentDeclarations.associateBy { it.name }

        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvedName? = declarationByName[name]?.let {
            ResolvedName(
                type = it.type.asThunk,
                resolution = DynamicResolution(
                    resolvedFormula = Formula(
                        name = name,
                    ),
                ),
            )
        }
    }

    companion object {
        fun build(
            outerDeclarationScope: StaticScope,
            term: AbstractionSourceTerm,
        ): Abstraction {
            val genericDeclarationBlock = term.genericParametersTuple?.asDeclarationBlock

            val innerDeclarationScope1 = genericDeclarationBlock?.chainWith(
                outerScope = outerDeclarationScope,
            ) ?: outerDeclarationScope

            val argumentTypeBody = TupleTypeConstructor.build(
                declarationScope = innerDeclarationScope1,
                term = term.argumentType,
            )

            val argumentType = argumentTypeBody.evaluateValue(
                context = EvaluationContext.Initial,
                scope = TranslationScope(
                    staticScope = innerDeclarationScope1,
                ),
            ) as TupleType

            val innerDeclarationScope2 = argumentType.toArgumentDeclarationBlock().chainWith(
                outerScope = innerDeclarationScope1,
            )

            val declaredImageType = term.declaredImageType?.let {
                Expression.build(
                    declarationScope = innerDeclarationScope2,
                    term = it,
                )
            }

            val image = build(
                declarationScope = innerDeclarationScope2,
                term = term.image,
            )

            return Abstraction(
                innerDeclarationScope = innerDeclarationScope2,
                term = term,
                genericParameters = term.genericParametersTuple?.typeVariables ?: emptySet(),
                argumentType = argumentType,
                declaredImageTypeConstructor = declaredImageType,
                image = image,
            )
        }
    }

    override fun bind(scope: Scope): Thunk<Value> = Closure(
        outerScope = scope,
        argumentType = argumentType,
        image = image,
    ).asThunk

    val declaredImageType: Thunk<Type>? by lazy {
        declaredImageTypeConstructor?.bindTranslated(
            staticScope = innerDeclarationScope,
        )?.thenJust { it as Type }
    }

    private val effectiveImageType: Thunk<Type> by lazy {
        val declaredImageType = this.declaredImageType

        if (declaredImageType != null) {
            return@lazy declaredImageType
        } else {
            return@lazy image.inferredType
        }
    }

    override val inferredType: Thunk<FunctionType> by lazy {
        effectiveImageType.thenJust { effectiveImageType ->
            UniversalFunctionType(
                genericParameters = genericParameters,
                argumentType = argumentType,
                imageType = effectiveImageType,
            )
        }
    }

    override val errors: Set<SemanticError> by lazy {
        image.errors
    }
}