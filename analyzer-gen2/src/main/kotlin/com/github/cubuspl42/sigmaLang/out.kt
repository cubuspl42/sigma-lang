@file:Suppress(
  "RedundantVisibilityModifier",
  "unused",
)

package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.BuiltinModule
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.ListValue
import com.github.cubuspl42.sigmaLang.core.values.StringPrimitive
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTuple
import com.github.cubuspl42.sigmaLang.core.values.Value
import kotlin.Suppress

public object Out {
  public val root: Value = object {
    public val knot0: Value by lazy {
          UnorderedTuple(
            valueByKey = mapOf(
              Identifier(name = "main") to lazy {
                object {
                  public val knot1: Value by lazy {
                        UnorderedTuple(
                          valueByKey = mapOf(
                            Identifier(name = "main") to lazy {
                              (object : Abstraction() {
                                override fun compute(argument: Value): Value {
                                  val arg2 = argument
                                  return (arg2 as Callable).call(
                                    argument = Identifier(name = "head"),
                                  )
                                }
                              } as Callable).call(
                                argument = object {
                                  public val knot3: Value by lazy {
                                        ((((knot0 as Callable).call(
                                          argument = Identifier(name = "__builtin__"),
                                        ) as Callable).call(
                                          argument = Identifier(name = "Dict"),
                                        ) as Callable).call(
                                          argument = Identifier(name = "unionWith"),
                                        ) as Callable).call(
                                          argument = UnorderedTuple(
                                            valueByKey = mapOf(
                                              Identifier(name = "first") to lazy {
                                                UnorderedTuple(
                                                  valueByKey = mapOf(
                                                  ),
                                                ) 
                                              },
                                              Identifier(name = "second") to lazy {
                                                (((knot0 as Callable).call(
                                                  argument = Identifier(name = "__builtin__"),
                                                ) as Callable).call(
                                                  argument = Identifier(name = "if"),
                                                ) as Callable).call(
                                                  argument = UnorderedTuple(
                                                    valueByKey = mapOf(
                                                      Identifier(name = "condition") to lazy {
                                                        ((((knot0 as Callable).call(
                                                          argument = Identifier(name =
                                                              "__builtin__"),
                                                        ) as Callable).call(
                                                          argument = Identifier(name = "List"),
                                                        ) as Callable).call(
                                                          argument = Identifier(name =
                                                              "isNotEmpty"),
                                                        ) as Callable).call(
                                                          argument = UnorderedTuple(
                                                            valueByKey = mapOf(
                                                              Identifier(name = "this") to lazy {
                                                                ListValue(
                                                                  values = listOf(
                                                                  ),
                                                                ) 
                                                              },
                                                            ),
                                                          ),
                                                        ) 
                                                      },
                                                      Identifier(name = "then") to lazy {
                                                        UnorderedTuple(
                                                          valueByKey = mapOf(
                                                            Identifier(name = "head") to lazy {
                                                              ((((knot0 as Callable).call(
                                                                argument = Identifier(name =
                                                                    "__builtin__"),
                                                              ) as Callable).call(
                                                                argument = Identifier(name =
                                                                    "List"),
                                                              ) as Callable).call(
                                                                argument = Identifier(name =
                                                                    "head"),
                                                              ) as Callable).call(
                                                                argument = UnorderedTuple(
                                                                  valueByKey = mapOf(
                                                                    Identifier(name = "this") to
                                                                        lazy {
                                                                      ListValue(
                                                                        values = listOf(
                                                                        ),
                                                                      ) 
                                                                    },
                                                                  ),
                                                                ),
                                                              ) 
                                                            },
                                                            Identifier(name = "tail") to lazy {
                                                              ((((knot0 as Callable).call(
                                                                argument = Identifier(name =
                                                                    "__builtin__"),
                                                              ) as Callable).call(
                                                                argument = Identifier(name =
                                                                    "List"),
                                                              ) as Callable).call(
                                                                argument = Identifier(name =
                                                                    "tail"),
                                                              ) as Callable).call(
                                                                argument = UnorderedTuple(
                                                                  valueByKey = mapOf(
                                                                    Identifier(name = "this") to
                                                                        lazy {
                                                                      ListValue(
                                                                        values = listOf(
                                                                        ),
                                                                      ) 
                                                                    },
                                                                  ),
                                                                ),
                                                              ) 
                                                            },
                                                          ),
                                                        ) 
                                                      },
                                                      Identifier(name = "else") to lazy {
                                                        (((knot0 as Callable).call(
                                                          argument = Identifier(name =
                                                              "__builtin__"),
                                                        ) as Callable).call(
                                                          argument = Identifier(name = "panic"),
                                                        ) as Callable).call(
                                                          argument = UnorderedTuple(
                                                            valueByKey = mapOf(
                                                            ),
                                                          ),
                                                        ) 
                                                      },
                                                    ),
                                                  ),
                                                ) 
                                              },
                                            ),
                                          ),
                                        ) 
                                      }
                                }.knot3,
                              ) 
                            },
                          ),
                        ) 
                      }
                }.knot1 
              },
              Identifier(name = "cat") to lazy {
                object {
                  public val knot4: Value by lazy {
                        UnorderedTuple(
                          valueByKey = mapOf(
                            Identifier(name = "Cat") to lazy {
                              object {
                                public val knot5: Value by lazy {
                                      UnorderedTuple(
                                        valueByKey = mapOf(
                                          Identifier(name = "of") to lazy {
                                            object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg6 = argument
                                                return ((((knot0 as Callable).call(
                                                  argument = Identifier(name = "__builtin__"),
                                                ) as Callable).call(
                                                  argument = Identifier(name = "Dict"),
                                                ) as Callable).call(
                                                  argument = Identifier(name = "unionWith"),
                                                ) as Callable).call(
                                                  argument = UnorderedTuple(
                                                    valueByKey = mapOf(
                                                      Identifier(name = "first") to lazy {
                                                        arg6 
                                                      },
                                                      Identifier(name = "second") to lazy {
                                                        UnorderedTuple(
                                                          valueByKey = mapOf(
                                                            Identifier(name =
                                                                "__instance_prototype__") to lazy {
                                                              (knot5 as Callable).call(
                                                                argument = Identifier(name =
                                                                    "__class_prototype__"),
                                                              ) 
                                                            },
                                                          ),
                                                        ) 
                                                      },
                                                    ),
                                                  ),
                                                )
                                              }
                                            } 
                                          },
                                          Identifier(name = "__class_prototype__") to lazy {
                                            UnorderedTuple(
                                              valueByKey = mapOf(
                                                Identifier(name = "pet") to lazy {
                                                  object : Abstraction() {
                                                    override fun compute(argument: Value): Value {
                                                      val arg7 = argument
                                                      return (object : Abstraction() {
                                                        override fun compute(argument: Value):
                                                            Value {
                                                          val arg8 = argument
                                                          return (((knot0 as Callable).call(
                                                            argument = Identifier(name =
                                                                "__builtin__"),
                                                          ) as Callable).call(
                                                            argument = Identifier(name = "if"),
                                                          ) as Callable).call(
                                                            argument = UnorderedTuple(
                                                              valueByKey = mapOf(
                                                                Identifier(name = "condition") to
                                                                    lazy {
                                                                  ((arg7 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "this"),
                                                                  ) as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "hasSharpClaws"),
                                                                  ) 
                                                                },
                                                                Identifier(name = "then") to lazy {
                                                                  (knot0 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "__builtin__"),
                                                                  ) 
                                                                },
                                                                Identifier(name = "else") to lazy {
                                                                  (knot0 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "__builtin__"),
                                                                  ) 
                                                                },
                                                              ),
                                                            ),
                                                          )
                                                        }
                                                      } as Callable).call(
                                                        argument = (arg7 as Callable).call(
                                                          argument = Identifier(name = "__args__"),
                                                        ),
                                                      )
                                                    }
                                                  } 
                                                },
                                                Identifier(name = "meow") to lazy {
                                                  object : Abstraction() {
                                                    override fun compute(argument: Value): Value {
                                                      val arg9 = argument
                                                      return (object : Abstraction() {
                                                        override fun compute(argument: Value):
                                                            Value {
                                                          val arg10 = argument
                                                          return StringPrimitive(value = "Meow!")
                                                        }
                                                      } as Callable).call(
                                                        argument = (arg9 as Callable).call(
                                                          argument = Identifier(name = "__args__"),
                                                        ),
                                                      )
                                                    }
                                                  } 
                                                },
                                                Identifier(name = "__class_tag__") to lazy {
                                                  Identifier(name = "Cat") 
                                                },
                                              ),
                                            ) 
                                          },
                                          Identifier(name = "pet") to lazy {
                                            object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg11 = argument
                                                return ((((arg11 as Callable).call(
                                                  argument = Identifier(name = "this"),
                                                ) as Callable).call(
                                                  argument = Identifier(name =
                                                      "__instance_prototype__"),
                                                ) as Callable).call(
                                                  argument = Identifier(name = "pet"),
                                                ) as Callable).call(
                                                  argument = UnorderedTuple(
                                                    valueByKey = mapOf(
                                                      Identifier(name = "this") to lazy {
                                                        (arg11 as Callable).call(
                                                          argument = Identifier(name = "this"),
                                                        ) 
                                                      },
                                                      Identifier(name = "__args__") to lazy {
                                                        arg11 
                                                      },
                                                    ),
                                                  ),
                                                )
                                              }
                                            } 
                                          },
                                          Identifier(name = "meow") to lazy {
                                            object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg12 = argument
                                                return ((((arg12 as Callable).call(
                                                  argument = Identifier(name = "this"),
                                                ) as Callable).call(
                                                  argument = Identifier(name =
                                                      "__instance_prototype__"),
                                                ) as Callable).call(
                                                  argument = Identifier(name = "meow"),
                                                ) as Callable).call(
                                                  argument = UnorderedTuple(
                                                    valueByKey = mapOf(
                                                      Identifier(name = "this") to lazy {
                                                        (arg12 as Callable).call(
                                                          argument = Identifier(name = "this"),
                                                        ) 
                                                      },
                                                      Identifier(name = "__args__") to lazy {
                                                        arg12 
                                                      },
                                                    ),
                                                  ),
                                                )
                                              }
                                            } 
                                          },
                                        ),
                                      ) 
                                    }
                              }.knot5 
                            },
                          ),
                        ) 
                      }
                }.knot4 
              },
              Identifier(name = "__builtin__") to lazy {
                BuiltinModule 
              },
            ),
          ) 
        }
  }.knot0
}
