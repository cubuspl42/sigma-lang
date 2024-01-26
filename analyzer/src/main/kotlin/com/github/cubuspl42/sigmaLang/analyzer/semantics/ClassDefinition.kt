package com.github.cubuspl42.sigmaLang.analyzer.semantics

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.FunctionValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Expression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.Stub
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.SymbolLiteral
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.UnorderedTupleTypeConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.asLazy
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SymbolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TableType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.asValue
import com.github.cubuspl42.sigmaLang.analyzer.syntax.ClassDefinitionTerm
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.UnorderedTupleConstructorTerm

object ClassDefinition {
    data class BuildOutput(
        val classBodyLazy: Lazy<Expression>,
    ) {
        val classBody: Expression by classBodyLazy
    }

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
    ): BuildOutput {
        val outerScope = context.outerScope

        val userInstanceTypeStub = UnorderedTupleTypeConstructor.build(
            context = context,
            term = term.body,
        )

        val classBodyStub = object : Stub<UnorderedTupleConstructor> {
            override val resolved: UnorderedTupleConstructor by lazy {
                val bodyTypeConstructor = userInstanceTypeStub.resolved

                val bodyTypeConstructorAnalysis = bodyTypeConstructor.evaluateAsType()

                val fieldsType = bodyTypeConstructorAnalysis.evaluatedType as UnorderedTupleType

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

        return BuildOutput(
            classBodyLazy = classBodyStub.asLazy(),
        )
    }
}
