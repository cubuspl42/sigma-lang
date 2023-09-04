package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.*
import com.github.cubuspl42.sigmaLang.analyzer.semantics.VariableClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Formula
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvableDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.Declaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ExpressionClassification
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.FunctionType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionTerm

class Abstraction(
    override val outerScope: StaticScope,
    private val innerScope: StaticScope,
    override val term: AbstractionTerm,
    val genericParameters: Set<TypeVariable>,
    val argumentType: TupleType,
    val declaredImageTypeConstructor: Expression?,
    val image: Expression,
) : Expression() {
    class ArgumentDeclaration(
        override val name: Symbol,
        val type: Type,
    ) : Declaration, ResolvableDeclaration {
        override val effectiveValueType: Thunk<Type> = Thunk.pure(type)

        override val resolvedType: Thunk<Type> = Thunk.pure(type)

        override val expressionClassification: ExpressionClassification = VariableClassification(
            Formula(name = name),
        )
    }

    class ArgumentStaticBlock(
        argumentDeclarations: List<ArgumentDeclaration>,
    ) : StaticBlock() {
        private val declarationByName = argumentDeclarations.associateBy { it.name }

        override fun resolveNameLocally(
            name: Symbol,
        ): ResolvableDeclaration? = declarationByName[name]

        override fun getLocalNames(): Set<Symbol> = declarationByName.keys
    }

    companion object {
        fun build(
            outerScope: StaticScope,
            term: AbstractionTerm,
        ): Abstraction {
            val genericDeclarationBlock = term.genericParametersTuple?.asDeclarationBlock

            val innerDeclarationScope1 = genericDeclarationBlock?.chainWith(
                outerScope = outerScope,
            ) ?: outerScope

            val argumentTypeBody = TupleTypeConstructor.build(
                outerScope = innerDeclarationScope1,
                term = term.argumentType,
            )

            val argumentType = argumentTypeBody.evaluateValue(
                context = EvaluationContext.Initial,
                dynamicScope = TranslationDynamicScope(
                    staticScope = innerDeclarationScope1,
                ),
            )?.asType as TupleType

            val innerDeclarationScope2 = argumentType.toArgumentDeclarationBlock().chainWith(
                outerScope = innerDeclarationScope1,
            )

            val declaredImageType = term.declaredImageType?.let {
                Expression.build(
                    outerScope = innerDeclarationScope2,
                    term = it,
                )
            }

            val image = build(
                outerScope = innerDeclarationScope2,
                term = term.image,
            )

            return Abstraction(
                outerScope = outerScope,
                innerScope = innerDeclarationScope2,
                term = term,
                genericParameters = term.genericParametersTuple?.typeVariables ?: emptySet(),
                argumentType = argumentType,
                declaredImageTypeConstructor = declaredImageType,
                image = image,
            )
        }
    }

    override fun bind(dynamicScope: DynamicScope): Thunk<Value> = Closure(
        outerDynamicScope = dynamicScope,
        argumentType = argumentType,
        image = image,
    ).toThunk()

    val declaredImageType: Thunk<Type>? by lazy {
        declaredImageTypeConstructor?.bindTranslated(
            staticScope = innerScope,
        )?.thenJust { it.asType!! }
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

    override val subExpressions: Set<Expression> = setOf(image)

    override val errors: Set<SemanticError> by lazy {
        image.errors
    }
}
