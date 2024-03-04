@file:Suppress(
  "RedundantVisibilityModifier",
  "unused",
)

package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import kotlin.Lazy
import kotlin.Suppress

public object Out {
  public val root: Lazy<Value> = lazyOf(object : Abstraction() {
    override fun compute(argument: Value): Value = object {
      public val var0: Lazy<Value> = lazyOf(argument)

      public val result: Lazy<Value> = lazyOf(
            (lazyOf(object : Abstraction() {
              override fun compute(argument: Value): Value = object {
                public val var1: Lazy<Value> = lazyOf(argument)

                public val result: Lazy<Value> = lazyOf(
                      UnorderedTuple(
                        valueByKey = mapOf(
                          Identifier(name = "a1") to lazyOf(
                            UnorderedTuple(
                              valueByKey = mapOf(
                              )
                            )
                          ),
                          Identifier(name = "a2") to lazyOf(
                            (var1.value as Callable).call(
                              argument = lazyOf(Identifier(name = "arg3")).value,
                            )
                          ),
                        )
                      )
                    )
              }.result.value
            }).value as Callable).call(
              argument = lazyOf(
                UnorderedTuple(
                  valueByKey = mapOf(
                    Identifier(name = "arg3") to lazyOf(
                      UnorderedTuple(
                        valueByKey = mapOf(
                          Identifier(name = "x3") to lazyOf(
                            UnorderedTuple(
                              valueByKey = mapOf(
                              )
                            )
                          ),
                        )
                      )
                    ),
                  )
                )
              ).value,
            )
          )
    }.result.value
  })
}
