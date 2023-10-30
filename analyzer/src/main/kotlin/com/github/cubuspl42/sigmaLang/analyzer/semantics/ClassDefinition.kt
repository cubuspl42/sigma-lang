package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.SymbolLiteral
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.AnyType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType.Companion.orderedTraitDeclaration
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SymbolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

//     object MapFn : BuiltinGenericFunctionExpression() {
//        override val parameterDeclaration = orderedTraitDeclaration(
//            Identifier.of("e"),
//            Identifier.of("r"),
//        )
//
//        private val eTypeVariable = TypeVariable(
//            parameterDeclaration,
//            path = TypeVariable.Path.of(IntValue(value = 0L)),
//        )
//
//        private val rTypeVariable = TypeVariable(
//            parameterDeclaration,
//            path = TypeVariable.Path.of(IntValue(value = 1L)),
//        )
//
//        override val body = object : BuiltinOrderedFunctionExpression() {
//            private val transformType = UniversalFunctionType(
//                argumentType = OrderedTupleType(
//                    elements = listOf(
//                        OrderedTupleType.Element(
//                            name = null,
//                            type = eTypeVariable,
//                        ),
//                    )
//                ),
//                imageType = rTypeVariable,
//            )
//
//            override val argumentElements: List<OrderedTupleType.Element> = listOf(
//                OrderedTupleType.Element(
//                    name = Identifier.of("elements"),
//                    type = ArrayType(
//                        elementType = eTypeVariable,
//                    ),
//                ),
//                OrderedTupleType.Element(
//                    name = Identifier.of("transform"),
//                    type = transformType,
//                ),
//            )
//
//            override val imageType = ArrayType(
//                elementType = rTypeVariable,
//            )
//
//            override fun computeThunk(args: List<Value>): Thunk<Value> {
//                val elements = (args[0] as FunctionValue).toList()
//                val transform = args[1] as FunctionValue
//
//                return Thunk.traverseList(elements) {
//                    transform.applyOrdered(it)
//                }.thenJust { values ->
//                    DictValue.fromList(values)
//                }
//            }
//        }
//    }

class ClassDefinition(
    override val bodyStub: Stub<Expression>,
) : Definition {
    object Is : BuiltinGenericFunctionConstructor() {
        override val parameterDeclaration = orderedTraitDeclaration(
            Identifier.of("e"),
            Identifier.of("c"),
        )

        private val eTypeVariable = TypeVariable(
            parameterDeclaration,
            path = TypeVariable.Path.of(IntValue(value = 0L)),
        )

        private val cTypeVariable = TypeVariable(
            parameterDeclaration,
            path = TypeVariable.Path.of(IntValue(value = 1L)),
        )

        override val body = object : BuiltinOrderedFunctionConstructor() {
            override val argumentElements: List<OrderedTupleType.Element> = listOf(
                OrderedTupleType.Element(
                    name = Identifier.of("instance"),
                    type = eTypeVariable,
                ),
                OrderedTupleType.Element(
                    name = Identifier.of("class"),
                    type = cTypeVariable,
                ),
            )

            override val imageType = BoolType

            override fun computeThunk(args: List<Value>): Thunk<Value> {
                val instance = args[0] as DictValue
                val classValue = args[1] as DictValue

                val instanceTagValue = instance.readValue(key = instanceTagKey) as Symbol
                val classTagValue = classValue.readValue(key = classTagKey) as Symbol

                return Thunk.pure(
                    BoolValue(
                        value = instanceTagValue == classTagValue,
                    )
                )
            }
        }
    }

    companion object {
        // TODO: `object` symbols
        val classTagKey = Identifier.of("__classTag__")
        val instanceTypeKey = Identifier.of("type")

        val instanceTagKey = Identifier.of("__instanceTag__")

//        val AnyClassType = UnorderedTupleType(
//            valueTypeByName = mapOf(
//                classTagKey to AnyType,
//                instanceTypeKey to TypeType,
//            ),
//        )
//
//        val AnyInstanceType = UnorderedTupleType(
//            valueTypeByName = mapOf(
//                instanceTagKey to AnyType,
//            ),
//        )

        fun build(
            context: Expression.BuildContext,
            qualifiedPath: QualifiedPath,
            term: ClassDefinitionTerm,
        ): ResolvedDefinition {
            val outerScope = context.outerScope

            val userInstanceTypeStub = UnorderedTupleTypeConstructor.build(
                context = context,
                term = term.body,
            )

            val classBodyStub = object : Stub<UnorderedTupleConstructor> {
                override val resolved: UnorderedTupleConstructor by lazy {
                    val bodyTypeConstructor = userInstanceTypeStub.resolved

                    val bodyTypeConstructorAnalysis = bodyTypeConstructor.evaluateAsType()

                    val fieldsType = bodyTypeConstructorAnalysis.type as UnorderedTupleType

                    val tag = qualifiedPath

                    val tagType = SymbolType(value = tag)

                    val instanceType = UnorderedTupleType(
                        valueTypeByName = fieldsType.valueTypeByName + (instanceTagKey to tagType)
                    )

                    object : UnorderedTupleConstructor() {
                        override val term: UnorderedTupleConstructorTerm? = null

                        override val entries: Set<Entry> = setOf(
                            Entry(
                                name = classTagKey,
                                value = SymbolLiteral(
                                    value = qualifiedPath,
                                    outerScope = outerScope,
                                ),
                            ),
                            Entry(
                                name = instanceTypeKey,
                                value = AtomicExpression(
                                    type = TypeType,
                                    value = instanceType.asValue,
                                ),
                            ),
                            Entry(
                                name = Identifier.of("new"),
                                value = object : BuiltinFunctionConstructor() {
                                    override val argumentType: TableType = fieldsType

                                    override val imageType: SpecificType = instanceType

                                    override val function: FunctionValue = object : FunctionValue() {
                                        override fun apply(argument: Value): Thunk<Value> {
                                            val fields = argument as DictValue

                                            return Thunk.pure(
                                                DictValue(
                                                    valueByKey = mapOf(
                                                        instanceTagKey to tag,
                                                    ),
                                                ).mergeWith(fields)
                                            )
                                        }

                                        override fun dump(): String = "${qualifiedPath.dump()}.new"
                                    }
                                },
                            ),
                        )

                        override val outerScope: StaticScope = outerScope
                    }
                }
            }

            val classDefinition = ClassDefinition(
                bodyStub = classBodyStub,
            )

            return ResolvedDefinition(
                definition = classDefinition,
            )
        }
    }
}
