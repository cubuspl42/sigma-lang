@file:Suppress(
  "RedundantVisibilityModifier",
  "unused",
)

package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.BooleanPrimitive
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.utils.LazyUtils
import kotlin.Lazy
import kotlin.Suppress

public object Out {
  public val root: Lazy<Value> = lazyOf(object : Abstraction() {
    override fun compute(argument: Value): Value {
      val arg0 = lazyOf(argument)
      return object {
        public val knot1: Lazy<Value> = lazyOf(
              UnorderedTuple(
                valueByKey = mapOf(
                  Identifier(name = "value1") to LazyUtils.lazier { value1 },
                  Identifier(name = "value2") to LazyUtils.lazier { value2 },
                  Identifier(name = "foo") to LazyUtils.lazier { foo },
                  Identifier(name = "bar") to LazyUtils.lazier { bar },
                  Identifier(name = "main") to LazyUtils.lazier { main },
                )
              ),
            )

        public val value1: Lazy<Value> = lazyOf(
              UnorderedTuple(
                valueByKey = mapOf(
                  Identifier(name = "x4") to lazyOf(
                    UnorderedTuple(
                      valueByKey = mapOf(
                      )
                    ),
                  ),
                  Identifier(name = "x5") to lazyOf(
                    UnorderedTuple(
                      valueByKey = mapOf(
                        Identifier(name = "b") to lazyOf(
                          BooleanPrimitive(value = true),
                        ),
                      )
                    ),
                  ),
                )
              ),
            )

        public val value2: Lazy<Value> = lazyOf(
              UnorderedTuple(
                valueByKey = mapOf(
                  Identifier(name = "x3") to lazyOf(
                    UnorderedTuple(
                      valueByKey = mapOf(
                      )
                    ),
                  ),
                )
              ),
            )

        public val foo: Lazy<Value> = lazyOf(object : Abstraction() {
          override fun compute(argument: Value): Value {
            val arg2 = lazyOf(argument)
            return lazyOf(
              UnorderedTuple(
                valueByKey = mapOf(
                )
              ),
            ).value
          }
        })

        public val bar: Lazy<Value> = lazyOf(
              (lazyOf(
                (knot1.value as Callable).call(
                  argument = lazyOf(Identifier(name = "foo")).value,
                )
              ).value as Callable).call(
                argument = lazyOf(
                  UnorderedTuple(
                    valueByKey = mapOf(
                      Identifier(name = "arg2") to lazyOf(
                        (knot1.value as Callable).call(
                          argument = lazyOf(Identifier(name = "value1")).value,
                        )
                      ),
                    )
                  ),
                ).value,
              )
            )

        public val main: Lazy<Value> = object {
          public val knot3: Lazy<Value> = lazyOf(
                UnorderedTuple(
                  valueByKey = mapOf(
                    Identifier(name = "foo2") to LazyUtils.lazier { foo2 },
                    Identifier(name = "bar3") to LazyUtils.lazier { bar3 },
                    Identifier(name = "baz") to LazyUtils.lazier { baz },
                  )
                ),
              )

          public val foo2: Lazy<Value> = lazyOf(object : Abstraction() {
            override fun compute(argument: Value): Value {
              val arg4 = lazyOf(argument)
              return lazyOf(
                UnorderedTuple(
                  valueByKey = mapOf(
                    Identifier(name = "a1") to lazyOf(
                      UnorderedTuple(
                        valueByKey = mapOf(
                        )
                      ),
                    ),
                    Identifier(name = "a2") to lazyOf(
                      (arg4.value as Callable).call(
                        argument = lazyOf(Identifier(name = "arg3")).value,
                      )
                    ),
                  )
                ),
              ).value
            }
          })

          public val bar3: Lazy<Value> = lazyOf(
                (lazyOf(
                  (knot1.value as Callable).call(
                    argument = lazyOf(Identifier(name = "foo")).value,
                  )
                ).value as Callable).call(
                  argument = lazyOf(
                    UnorderedTuple(
                      valueByKey = mapOf(
                        Identifier(name = "arg3") to lazyOf(
                          (knot1.value as Callable).call(
                            argument = lazyOf(Identifier(name = "value2")).value,
                          )
                        ),
                      )
                    ),
                  ).value,
                )
              )

          public val baz: Lazy<Value> = lazyOf(
                (lazyOf(
                  (lazyOf(
                    (arg0.value as Callable).call(
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
                          (knot1.value as Callable).call(
                            argument = lazyOf(Identifier(name = "value2")).value,
                          )
                        ),
                        Identifier(name = "then") to lazyOf(
                          (knot1.value as Callable).call(
                            argument = lazyOf(Identifier(name = "foo")).value,
                          )
                        ),
                        Identifier(name = "else") to lazyOf(
                          (lazyOf(
                            (lazyOf(
                              (arg0.value as Callable).call(
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
                                    (knot1.value as Callable).call(
                                      argument = lazyOf(Identifier(name = "value2")).value,
                                    )
                                  ),
                                  Identifier(name = "then") to lazyOf(
                                    (knot1.value as Callable).call(
                                      argument = lazyOf(Identifier(name = "bar")).value,
                                    )
                                  ),
                                  Identifier(name = "else") to lazyOf(
                                    (lazyOf(
                                      (lazyOf(
                                        (arg0.value as Callable).call(
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
                                              (lazyOf(
                                                (knot1.value as Callable).call(
                                                  argument = lazyOf(Identifier(name = "foo")).value,
                                                )
                                              ).value as Callable).call(
                                                argument = lazyOf(
                                                  UnorderedTuple(
                                                    valueByKey = mapOf(
                                                      Identifier(name = "arg2") to lazyOf(
                                                        UnorderedTuple(
                                                          valueByKey = mapOf(
                                                          )
                                                        ),
                                                      ),
                                                    )
                                                  ),
                                                ).value,
                                              )
                                            ),
                                            Identifier(name = "then") to lazyOf(
                                              (knot1.value as Callable).call(
                                                argument = lazyOf(Identifier(name = "bar")).value,
                                              )
                                            ),
                                            Identifier(name = "else") to lazyOf(
                                              (knot1.value as Callable).call(
                                                argument = lazyOf(Identifier(name =
                                                    "value1")).value,
                                              )
                                            ),
                                          )
                                        ),
                                      ).value,
                                    )
                                  ),
                                )
                              ),
                            ).value,
                          )
                        ),
                      )
                    ),
                  ).value,
                )
              )

          public val result: Lazy<Value> = lazyOf(
                (knot3.value as Callable).call(
                  argument = lazyOf(Identifier(name = "baz")).value,
                )
              )
        }.result

        public val result: Lazy<Value> = lazyOf(
              (knot1.value as Callable).call(
                argument = lazyOf(Identifier(name = "main")).value,
              )
            )
      }.result.value
    }
  })

  public val main: Value = (root.value as Callable).call(
        argument = UnorderedTuple(
          valueByKey = mapOf(
            Identifier(name = "builtin") to lazyOf(
              BuiltinScope,
            ),
          )
        ),
      )
}
