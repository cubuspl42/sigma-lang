package com.github.cubuspl42.sigmaLang.core

import com.github.cubuspl42.sigmaLang.core.expressions.BuiltinModuleReference
import com.github.cubuspl42.sigmaLang.core.expressions.Expression
import com.github.cubuspl42.sigmaLang.core.values.Identifier

abstract class Pattern {
    data class Application(
        val condition: Expression,
        val definitionBlock: LocalScope.DefinitionBlock,
    )

    abstract fun apply(expression: Expression): Application
}

class TagPattern(
    @Suppress("PropertyName") val class_: Expression,
    private val newName: Identifier,
) : Pattern() {
    override fun apply(
        expression: Expression,
    ) = Application(
        condition = BuiltinModuleReference.isAFunction.call(
            instance = expression,
            class_ = class_,
        ),
        definitionBlock = LocalScope.DefinitionBlock.makeSimple(
            definitions = setOf(
                LocalScope.Constructor.SimpleDefinition(
                    name = newName,
                    initializer = expression,
                ),
            ),
        ),
    )
}

class ListUnconsPattern(
    private val listClass: BuiltinModuleReference.ListClassReference,
    private val headName: Identifier,
    private val tailName: Identifier,
) : Pattern() {
    override fun apply(
        expression: Expression,
    ): Pattern.Application = Pattern.Application(
        condition = listClass.isNotEmpty.call(list = expression),
        definitionBlock = LocalScope.DefinitionBlock.makeSimple(
            definitions = setOf(
                LocalScope.Constructor.SimpleDefinition(
                    name = headName,
                    initializer = listClass.head.call(list = expression),
                ),
                LocalScope.Constructor.SimpleDefinition(
                    name = tailName,
                    initializer = listClass.tail.call(list = expression),
                ),
            ),
        ),
    )
}

class ListEmptyPattern(
    private val listClass: BuiltinModuleReference.ListClassReference,
) : Pattern() {
    override fun apply(
        expression: Expression,
    ): Pattern.Application = Pattern.Application(
        condition = listClass.isEmpty.call(list = expression),
        definitionBlock = LocalScope.DefinitionBlock.Empty,
    )
}
