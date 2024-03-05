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
import kotlin.Suppress

public object Out {
  public val root: Value = object : Abstraction() {
    override fun compute(argument: Value): Value {
      val arg0 = argument
      return (object {
        public val knot1: Value by lazy {
              UnorderedTuple(
                valueByKey = mapOf(
                  Identifier(name = "value1") to lazy {
                    UnorderedTuple(
                      valueByKey = mapOf(
                        Identifier(name = "x4") to lazy {
                          UnorderedTuple(
                            valueByKey = mapOf(
                            )
                          ) 
                        },
                        Identifier(name = "x5") to lazy {
                          UnorderedTuple(
                            valueByKey = mapOf(
                              Identifier(name = "b") to lazy {
                                BooleanPrimitive(value = true) 
                              },
                            )
                          ) 
                        },
                      )
                    ) 
                  },
                  Identifier(name = "value2") to lazy {
                    UnorderedTuple(
                      valueByKey = mapOf(
                        Identifier(name = "b1") to lazy {
                          BooleanPrimitive(value = false) 
                        },
                        Identifier(name = "b2") to lazy {
                          BooleanPrimitive(value = true) 
                        },
                        Identifier(name = "x3") to lazy {
                          UnorderedTuple(
                            valueByKey = mapOf(
                            )
                          ) 
                        },
                      )
                    ) 
                  },
                  Identifier(name = "foo") to lazy {
                    object : Abstraction() {
                      override fun compute(argument: Value): Value {
                        val arg2 = argument
                        return BooleanPrimitive(value = false)
                      }
                    } 
                  },
                  Identifier(name = "bar") to lazy {
                    ((knot1 as Callable).call(
                      argument = Identifier(name = "foo"),
                    ) as Callable).call(
                      argument = UnorderedTuple(
                        valueByKey = mapOf(
                          Identifier(name = "arg2") to lazy {
                            (knot1 as Callable).call(
                              argument = Identifier(name = "value1"),
                            ) 
                          },
                        )
                      ),
                    ) 
                  },
                  Identifier(name = "main") to lazy {
                    (object {
                      public val knot3: Value by lazy {
                            UnorderedTuple(
                              valueByKey = mapOf(
                                Identifier(name = "foo2") to lazy {
                                  object : Abstraction() {
                                    override fun compute(argument: Value): Value {
                                      val arg4 = argument
                                      return UnorderedTuple(
                                        valueByKey = mapOf(
                                          Identifier(name = "a1") to lazy {
                                            UnorderedTuple(
                                              valueByKey = mapOf(
                                              )
                                            ) 
                                          },
                                          Identifier(name = "a2") to lazy {
                                            (arg4 as Callable).call(
                                              argument = Identifier(name = "arg3"),
                                            ) 
                                          },
                                        )
                                      )
                                    }
                                  } 
                                },
                                Identifier(name = "bar3") to lazy {
                                  ((knot1 as Callable).call(
                                    argument = Identifier(name = "foo"),
                                  ) as Callable).call(
                                    argument = UnorderedTuple(
                                      valueByKey = mapOf(
                                        Identifier(name = "arg3") to lazy {
                                          (knot1 as Callable).call(
                                            argument = Identifier(name = "value2"),
                                          ) 
                                        },
                                      )
                                    ),
                                  ) 
                                },
                                Identifier(name = "baz") to lazy {
                                  (((arg0 as Callable).call(
                                    argument = Identifier(name = "builtin"),
                                  ) as Callable).call(
                                    argument = Identifier(name = "if"),
                                  ) as Callable).call(
                                    argument = UnorderedTuple(
                                      valueByKey = mapOf(
                                        Identifier(name = "condition") to lazy {
                                          ((knot1 as Callable).call(
                                            argument = Identifier(name = "value2"),
                                          ) as Callable).call(
                                            argument = Identifier(name = "b1"),
                                          ) 
                                        },
                                        Identifier(name = "then") to lazy {
                                          (knot1 as Callable).call(
                                            argument = Identifier(name = "foo"),
                                          ) 
                                        },
                                        Identifier(name = "else") to lazy {
                                          (((arg0 as Callable).call(
                                            argument = Identifier(name = "builtin"),
                                          ) as Callable).call(
                                            argument = Identifier(name = "if"),
                                          ) as Callable).call(
                                            argument = UnorderedTuple(
                                              valueByKey = mapOf(
                                                Identifier(name = "condition") to lazy {
                                                  ((knot1 as Callable).call(
                                                    argument = Identifier(name = "value2"),
                                                  ) as Callable).call(
                                                    argument = Identifier(name = "b2"),
                                                  ) 
                                                },
                                                Identifier(name = "then") to lazy {
                                                  (knot1 as Callable).call(
                                                    argument = Identifier(name = "bar"),
                                                  ) 
                                                },
                                                Identifier(name = "else") to lazy {
                                                  (((arg0 as Callable).call(
                                                    argument = Identifier(name = "builtin"),
                                                  ) as Callable).call(
                                                    argument = Identifier(name = "if"),
                                                  ) as Callable).call(
                                                    argument = UnorderedTuple(
                                                      valueByKey = mapOf(
                                                        Identifier(name = "condition") to lazy {
                                                          ((knot1 as Callable).call(
                                                            argument = Identifier(name = "foo"),
                                                          ) as Callable).call(
                                                            argument = UnorderedTuple(
                                                              valueByKey = mapOf(
                                                                Identifier(name = "arg2") to lazy {
                                                                  UnorderedTuple(
                                                                    valueByKey = mapOf(
                                                                    )
                                                                  ) 
                                                                },
                                                              )
                                                            ),
                                                          ) 
                                                        },
                                                        Identifier(name = "then") to lazy {
                                                          (knot1 as Callable).call(
                                                            argument = Identifier(name = "bar"),
                                                          ) 
                                                        },
                                                        Identifier(name = "else") to lazy {
                                                          (knot1 as Callable).call(
                                                            argument = Identifier(name = "value1"),
                                                          ) 
                                                        },
                                                      )
                                                    ),
                                                  ) 
                                                },
                                              )
                                            ),
                                          ) 
                                        },
                                      )
                                    ),
                                  ) 
                                },
                                Identifier(name = "__result__") to lazy {
                                  (knot3 as Callable).call(
                                    argument = Identifier(name = "baz"),
                                  ) 
                                },
                              )
                            ) 
                          }
                    }.knot3 as Callable).call(
                      argument = Identifier(name = "__result__"),
                    ) 
                  },
                  Identifier(name = "__result__") to lazy {
                    (knot1 as Callable).call(
                      argument = Identifier(name = "main"),
                    ) 
                  },
                )
              ) 
            }
      }.knot1 as Callable).call(
        argument = Identifier(name = "__result__"),
      )
    }
  }

  public val main: Value = (root as Callable).call(
        argument = UnorderedTuple(
          valueByKey = mapOf(
            Identifier(name = "builtin") to lazyOf(
              BuiltinScope,
            ),
          )
        ),
      )
}
