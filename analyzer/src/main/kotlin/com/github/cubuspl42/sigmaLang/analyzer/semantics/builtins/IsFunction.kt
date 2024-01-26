package com.github.cubuspl42.sigmaLang.analyzer.semantics.builtins

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BoolValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinGenericFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.BuiltinOrderedFunctionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.DictValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.IntValue
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Thunk
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.syntax.introductions.ClassDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.BoolType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.GenericType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.OrderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeVariable

object IsFunction : BuiltinGenericFunctionConstructor() {
    override val parameterDeclaration = GenericType.orderedTraitDeclaration(
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

            val instanceTagValue = instance.readValue(key = ClassDefinition.instanceTagKey) as Symbol
            val classTagValue = classValue.readValue(key = ClassDefinition.classTagKey) as Symbol

            return Thunk.pure(
                BoolValue(
                    value = instanceTagValue == classTagValue,
                )
            )
        }
    }
}
