package com.github.cubuspl42.sigmaLang.shell.terms

import com.github.cubuspl42.sigmaLang.core.values.ListValue
import com.github.cubuspl42.sigmaLang.core.values.Nil
import com.github.cubuspl42.sigmaLang.core.values.StringPrimitive
import com.github.cubuspl42.sigmaLang.core.values.Value

interface Wrappable {
    fun wrap(): Value
}

fun String.wrap(): Value = StringPrimitive(value = this)

fun List<Wrappable>.wrap(): Value = ListValue(
    values = map { it.wrap() },
)

fun Wrappable?.wrapOrNil(): Value = this?.wrap() ?: Nil
