package com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions

import com.github.cubuspl42.sigmaLang.analyzer.BinaryOperationPrototype
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.scope.DynamicScope
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.SemanticError
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.*
import com.github.cubuspl42.sigmaLang.analyzer.syntax.SourceLocation
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.*

abstract class Call : FirstOrderExpression() {
    abstract val subject: Expression

    abstract val argument: Expression

    companion object {
        fun build(
            context: BuildContext,
            term: CallTerm,
        ): Stub<Call> = when (term) {
            is InfixCallTerm -> buildInfix(
                context = context,
                term = term,
            )

            is PostfixCallTerm -> buildPostfix(
                context = context,
                term = term,
            )

            else -> throw UnsupportedOperationException("Unsupported call term: $term")
        }

        private fun buildPostfix(
            context: BuildContext,
            term: PostfixCallTerm,
        ): Stub<Call> = object : Stub<Call> {
            override val resolved: Call by lazy {
                object : Call() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: CallTerm = term

                    override val subject: Expression by lazy {
                        build(
                            context = context,
                            term = term.subject,
                        ).resolved
                    }

                    override val argument: Expression by lazy {
                        build(
                            context = context,
                            term = term.argument,
                        ).resolved
                    }
                }
            }
        }

        private fun buildInfix(
            context: BuildContext,
            term: InfixCallTerm,
        ): Stub<Call> = object : Stub<Call> {
            override val resolved: Call by lazy {
                val prototype = BinaryOperationPrototype.build(term.operator)

                val leftArgument = Expression.build(
                    context = context,
                    term = term.leftArgument,
                ).resolved

                val rightArgument = Expression.build(
                    context = context,
                    term = term.rightArgument,
                ).resolved

                val subjectStub = Reference.build(
                    context,
                    referredName = Symbol.of(prototype.functionName),
                )

                object : Call() {
                    override val outerScope: StaticScope = context.outerScope

                    override val term: CallTerm = term

                    override val subject: Expression by lazy { subjectStub.resolved }

                    override val argument: Expression by lazy {
                        object : UnorderedTupleConstructor() {
                            override val outerScope: StaticScope = context.outerScope

                            override val term: UnorderedTupleConstructorTerm? = null

                            override val entries: Set<Entry> = setOf(
                                object : Entry() {
                                    override val name: Symbol = prototype.leftArgument

                                    override val value: Expression = leftArgument
                                },
                                object : Entry() {
                                    override val name: Symbol = prototype.rightArgument

                                    override val value: Expression = rightArgument
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    data class NonFullyInferredCalleeTypeError(
        override val location: SourceLocation? = null,
        val calleeGenericType: FunctionType,
        val unresolvedPlaceholders: Set<TypePlaceholder>,
    ) : SemanticError

    data class NonFunctionCallError(
        override val location: SourceLocation? = null,
        val illegalSubjectType: Type,
    ) : SemanticError

    data class InvalidArgumentError(
        override val location: SourceLocation?,
        val matchResult: SpecificType.MatchResult,
    ) : SemanticError {
        override fun dump(): String = "$location: Invalid argument: ${matchResult.dump()}"
    }

    class OutOfRangeCallError(
        val tupleType: OrderedTupleType,
        val index: Long,
    ) : SemanticError {
        override fun dump(): String = "Index $index is out of range for tuple type ${tupleType.dump()}"
    }

    override val computedDiagnosedAnalysis = buildDiagnosedAnalysisComputation {
        val subjectAnalysis = compute(subject.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null
        val argumentAnalysis = compute(argument.computedAnalysis) ?: return@buildDiagnosedAnalysisComputation null

        val subjectType = subjectAnalysis.inferredType.specifyImplicitly()
        val argumentType = argumentAnalysis.inferredType as SpecificType

        when {
            subjectType is OrderedTupleType && argumentType is IntLiteralType -> {
                val index = argumentType.value.value

                if (index >= subjectType.elements.size) {
                    DiagnosedAnalysis.fromError(
                        OutOfRangeCallError(
                            tupleType = subjectType,
                            index = index,
                        )
                    )
                } else {
                    DiagnosedAnalysis(
                        analysis = Analysis(
                            inferredType = subjectType.indexedElements[index.toInt()].type,
                        ),
                        directErrors = emptySet(),
                    )
                }
            }

            subjectType is FunctionType -> {
                val typeVariableResolution = subjectType.argumentType.resolveTypePlaceholders(
                    assignedType = argumentType,
                )

                val argumentSubstitution = subjectType.argumentType.substituteTypePlaceholders(
                    resolution = typeVariableResolution,
                )

                val effectiveArgumentType = argumentSubstitution.result

                val imageSubstitution = subjectType.imageType.substituteTypePlaceholders(
                    resolution = typeVariableResolution,
                )

                val effectiveImageType = imageSubstitution.result

                val unresolvedPlaceholders = imageSubstitution.unresolvedPlaceholders

                when {
                    unresolvedPlaceholders.isEmpty() -> {
                        val matchResult = effectiveArgumentType.match(
                            assignedType = argumentType,
                        )

                        val directError = if (!matchResult.isFull()) {
                            InvalidArgumentError(
                                location = argument.location,
                                matchResult = matchResult,
                            )
                        } else {
                            null
                        }

                        DiagnosedAnalysis(
                            analysis = Analysis(
                                inferredType = effectiveImageType,
                            ),
                            directErrors = setOfNotNull(directError),
                        )
                    }

                    else -> {
                        DiagnosedAnalysis.fromError(
                            NonFullyInferredCalleeTypeError(
                                location = subject.location,
                                calleeGenericType = subjectType,
                                unresolvedPlaceholders = unresolvedPlaceholders,
                            ),
                        )
                    }
                }
            }

            else -> DiagnosedAnalysis.fromError(
                NonFunctionCallError(
                    location = subject.location,
                    illegalSubjectType = subjectType,
                )
            )
        }
    }

    override val subExpressions: Set<Expression>
        get() = setOf(subject, argument)

    override fun bindDirectly(
        dynamicScope: DynamicScope,
    ): Thunk<Value> = Thunk.combine2(
        subject.bind(
            dynamicScope = dynamicScope,
        ),
        argument.bind(
            dynamicScope = dynamicScope,
        ),
    ) { subjectValue, argumentValue ->
        if (subjectValue !is FunctionValue) throw IllegalStateException("Subject $subjectValue is not a function")

        subjectValue.apply(
            argument = argumentValue,
        )
    }.thenDo { it }
}

fun Call(
    subjectLazy: Lazy<Expression>,
    argumentLazy: Lazy<Expression>,
): Call = object : Call() {
    override val outerScope: StaticScope = StaticScope.Empty

    override val term: CallTerm? = null

    override val subject: Expression by subjectLazy

    override val argument: Expression by argumentLazy
}
