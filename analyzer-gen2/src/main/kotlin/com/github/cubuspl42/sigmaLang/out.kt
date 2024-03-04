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
            (lazyOf(
              (lazyOf(
                (var0.value as Callable).call(
                  argument = lazyOf(Identifier(name = "builtin")).value,
                )
              ).value as Callable).call(
                argument = lazyOf(Identifier(name = "if")).value,
              )
            ).value as Callable).call(
              argument = lazyOf(
                UnorderedTuple(
                  valueByKey = mapOf(
                    Identifier(name = "condition") to lazyOf(
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
                    Identifier(name = "then") to lazyOf(object : Abstraction() {
                      override fun compute(argument: Value): Value = object {
                        public val var1: Lazy<Value> = lazyOf(argument)

                        public val result: Lazy<Value> = lazyOf(
                              UnorderedTuple(
                                valueByKey = mapOf(
                                )
                              )
                            )
                      }.result.value
                    }),
                    Identifier(name = "else") to lazyOf(
                      (lazyOf(
                        (lazyOf(
                          (var0.value as Callable).call(
                            argument = lazyOf(Identifier(name = "builtin")).value,
                          )
                        ).value as Callable).call(
                          argument = lazyOf(Identifier(name = "if")).value,
                        )
                      ).value as Callable).call(
                        argument = lazyOf(
                          UnorderedTuple(
                            valueByKey = mapOf(
                              Identifier(name = "condition") to lazyOf(
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
                              Identifier(name = "then") to lazyOf(
                                (lazyOf(object : Abstraction() {
                                  override fun compute(argument: Value): Value = object {
                                    public val var1: Lazy<Value> = lazyOf(argument)

                                    public val result: Lazy<Value> = lazyOf(
                                          UnorderedTuple(
                                            valueByKey = mapOf(
                                            )
                                          )
                                        )
                                  }.result.value
                                }).value as Callable).call(
                                  argument = lazyOf(
                                    UnorderedTuple(
                                      valueByKey = mapOf(
                                        Identifier(name = "arg2") to lazyOf(
                                          UnorderedTuple(
                                            valueByKey = mapOf(
                                              Identifier(name = "x4") to lazyOf(
                                                UnorderedTuple(
                                                  valueByKey = mapOf(
                                                  )
                                                )
                                              ),
                                              Identifier(name = "x5") to lazyOf(
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
                              ),
                              Identifier(name = "else") to lazyOf(
                                (lazyOf(
                                  (lazyOf(
                                    (var0.value as Callable).call(
                                      argument = lazyOf(Identifier(name = "builtin")).value,
                                    )
                                  ).value as Callable).call(
                                    argument = lazyOf(Identifier(name = "if")).value,
                                  )
                                ).value as Callable).call(
                                  argument = lazyOf(
                                    UnorderedTuple(
                                      valueByKey = mapOf(
                                        Identifier(name = "condition") to lazyOf(
                                          (lazyOf(object : Abstraction() {
                                            override fun compute(argument: Value): Value = object {
                                              public val var1: Lazy<Value> = lazyOf(argument)

                                              public val result: Lazy<Value> = lazyOf(
                                                    UnorderedTuple(
                                                      valueByKey = mapOf(
                                                      )
                                                    )
                                                  )
                                            }.result.value
                                          }).value as Callable).call(
                                            argument = lazyOf(
                                              UnorderedTuple(
                                                valueByKey = mapOf(
                                                  Identifier(name = "arg2") to lazyOf(
                                                    UnorderedTuple(
                                                      valueByKey = mapOf(
                                                      )
                                                    )
                                                  ),
                                                )
                                              )
                                            ).value,
                                          )
                                        ),
                                        Identifier(name = "then") to lazyOf(
                                          (lazyOf(object : Abstraction() {
                                            override fun compute(argument: Value): Value = object {
                                              public val var1: Lazy<Value> = lazyOf(argument)

                                              public val result: Lazy<Value> = lazyOf(
                                                    UnorderedTuple(
                                                      valueByKey = mapOf(
                                                      )
                                                    )
                                                  )
                                            }.result.value
                                          }).value as Callable).call(
                                            argument = lazyOf(
                                              UnorderedTuple(
                                                valueByKey = mapOf(
                                                  Identifier(name = "arg2") to lazyOf(
                                                    UnorderedTuple(
                                                      valueByKey = mapOf(
                                                        Identifier(name = "x4") to lazyOf(
                                                          UnorderedTuple(
                                                            valueByKey = mapOf(
                                                            )
                                                          )
                                                        ),
                                                        Identifier(name = "x5") to lazyOf(
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
                                        ),
                                        Identifier(name = "else") to lazyOf(
                                          UnorderedTuple(
                                            valueByKey = mapOf(
                                            )
                                          )
                                        ),
                                      )
                                    )
                                  ).value,
                                )
                              ),
                            )
                          )
                        ).value,
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
