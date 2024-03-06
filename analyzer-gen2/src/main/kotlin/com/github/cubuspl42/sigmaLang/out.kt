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
                  Identifier(name = "bleedingResult") to lazy {
                    UnorderedTuple(
                      valueByKey = mapOf(
                        Identifier(name = "bleedingHands") to lazy {
                          BooleanPrimitive(value = true) 
                        },
                      )
                    ) 
                  },
                  Identifier(name = "calmResult") to lazy {
                    UnorderedTuple(
                      valueByKey = mapOf(
                        Identifier(name = "purringCat") to lazy {
                          BooleanPrimitive(value = true) 
                        },
                      )
                    ) 
                  },
                  Identifier(name = "Cat") to lazy {
                    object {
                      public val knot2: Value by lazy {
                            UnorderedTuple(
                              valueByKey = mapOf(
                                Identifier(name = "of") to lazy {
                                  object : Abstraction() {
                                    override fun compute(argument: Value): Value {
                                      val arg3 = argument
                                      return arg3
                                    }
                                  } 
                                },
                                Identifier(name = "pet") to lazy {
                                  object : Abstraction() {
                                    override fun compute(argument: Value): Value {
                                      val arg4 = argument
                                      return (((arg0 as Callable).call(
                                        argument = Identifier(name = "builtin"),
                                      ) as Callable).call(
                                        argument = Identifier(name = "if"),
                                      ) as Callable).call(
                                        argument = UnorderedTuple(
                                          valueByKey = mapOf(
                                            Identifier(name = "condition") to lazy {
                                              ((arg4 as Callable).call(
                                                argument = Identifier(name = "this"),
                                              ) as Callable).call(
                                                argument = Identifier(name = "hasSharpClaws"),
                                              ) 
                                            },
                                            Identifier(name = "then") to lazy {
                                              (knot1 as Callable).call(
                                                argument = Identifier(name = "bleedingResult"),
                                              ) 
                                            },
                                            Identifier(name = "else") to lazy {
                                              (knot1 as Callable).call(
                                                argument = Identifier(name = "calmResult"),
                                              ) 
                                            },
                                          )
                                        ),
                                      )
                                    }
                                  } 
                                },
                              )
                            ) 
                          }
                    }.knot2 
                  },
                  Identifier(name = "foo") to lazy {
                    object : Abstraction() {
                      override fun compute(argument: Value): Value {
                        val arg5 = argument
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
                  Identifier(name = "pussy") to lazy {
                    (((knot1 as Callable).call(
                      argument = Identifier(name = "Cat"),
                    ) as Callable).call(
                      argument = Identifier(name = "of"),
                    ) as Callable).call(
                      argument = UnorderedTuple(
                        valueByKey = mapOf(
                          Identifier(name = "hasSharpClaws") to lazy {
                            BooleanPrimitive(value = false) 
                          },
                        )
                      ),
                    ) 
                  },
                  Identifier(name = "main") to lazy {
                    (((arg0 as Callable).call(
                      argument = Identifier(name = "builtin"),
                    ) as Callable).call(
                      argument = Identifier(name = "unionWith"),
                    ) as Callable).call(
                      argument = UnorderedTuple(
                        valueByKey = mapOf(
                          Identifier(name = "first") to lazy {
                            UnorderedTuple(
                              valueByKey = mapOf(
                                Identifier(name = "x1") to lazy {
                                  BooleanPrimitive(value = false) 
                                },
                                Identifier(name = "x2") to lazy {
                                  BooleanPrimitive(value = false) 
                                },
                              )
                            ) 
                          },
                          Identifier(name = "second") to lazy {
                            UnorderedTuple(
                              valueByKey = mapOf(
                                Identifier(name = "x2") to lazy {
                                  BooleanPrimitive(value = true) 
                                },
                                Identifier(name = "x3") to lazy {
                                  BooleanPrimitive(value = true) 
                                },
                              )
                            ) 
                          },
                        )
                      ),
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
