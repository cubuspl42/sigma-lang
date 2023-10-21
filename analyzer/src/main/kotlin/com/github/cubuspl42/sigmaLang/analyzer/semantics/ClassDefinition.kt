package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.StrictBuiltinOrderedFunction
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.SymbolLiteral
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.AnyType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SymbolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.ExpressionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

class ClassDefinition(
    override val name: Symbol,
    override val bodyStub: Stub<Expression>,
) : Definition {
    object Is : StrictBuiltinOrderedFunction() {
        override val argTypes: List<SpecificType> = listOf(
            AnyInstanceType,
            AnyClassType,
        )

        override val imageType: SpecificType = BoolType

        override fun compute(args: List<Value>): Value {
            val instanceValue = args[0] as DictValue
            val classValue = args[1] as DictValue

            val instanceTagValue = instanceValue.readValue(key = instanceTagKey) as Identifier
            val classTagValue = classValue.readValue(key = classTagKey) as Identifier

            return BoolValue(
                value = instanceTagValue == classTagValue,
            )
        }
    }

    companion object {
        // TODO: `object` symbols
        val classTagKey = Identifier.of("__classTag__")
        val instanceTypeKey = Identifier.of("type")

        val instanceTagKey = Identifier.of("__instanceTag__")

        val AnyClassType = UnorderedTupleType(
            valueTypeByName = mapOf(
                classTagKey to AnyType,
                instanceTypeKey to TypeType,
            ),
        )

        val AnyInstanceType = UnorderedTupleType(
            valueTypeByName = mapOf(
                instanceTagKey to AnyType,
            ),
        )

        fun build(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: ClassDefinitionTerm,
        ): ClassDefinition {
            val outerScope = context.outerScope

            val userInstanceTypeStub = UnorderedTupleTypeConstructor.build(
                context = context,
                term = term.body,
            )

            val classBodyStub = object : Stub<UnorderedTupleConstructor> {
                override val resolved: UnorderedTupleConstructor by lazy {
                    val bodyTypeConstructor = userInstanceTypeStub.resolved

                    val bodyTypeConstructorAnalysis = bodyTypeConstructor.evaluateAsType()

                    val bodyType = bodyTypeConstructorAnalysis.type as UnorderedTupleType

                    val tag = qualifiedPath

                    val tagType = SymbolType(value = tag)

                    val instanceType = UnorderedTupleType(
                        valueTypeByName = bodyType.valueTypeByName + (instanceTagKey to tagType)
                    )

                    object : UnorderedTupleConstructor() {
                        override val term: UnorderedTupleConstructorTerm? = null

                        override val entries: Set<Entry> = setOf(
                            object : Entry() {
                                override val name: Symbol = classTagKey

                                override val value: Expression = SymbolLiteral(
                                    value = qualifiedPath,
                                    outerScope = outerScope,
                                )
                            },
                            object : Entry() {
                                override val name: Symbol = instanceTypeKey

                                override val value: Expression by lazy { userInstanceTypeStub.resolved }
                            },
                            object : Entry() {
                                override val name: Symbol = Identifier.of("new")

                                override val value: Expression = object : AbstractionConstructor() {
                                    override val term: ExpressionTerm? = null

                                    override val argumentType: TupleType = bodyType

                                    override val declaredImageType: TypeAlike = instanceType

                                    override val image: Expression
                                        get() = TODO() // {tag: tag, ...argument}

                                    override val outerScope: StaticScope = outerScope

//                                    override val argumentDeclarationBlock: ArgumentStaticBlock
//                                        get() = throw NotImplementedError()
                                }
                            },
                        )

                        override val outerScope: StaticScope = outerScope
                    }
                }
            }

            return ClassDefinition(
                name = term.name,
                bodyStub = classBodyStub,
            )
        }
    }
}
