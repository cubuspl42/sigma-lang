@file:Suppress(
  "RedundantVisibilityModifier",
  "unused",
)

package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.values.Abstraction
import com.github.cubuspl42.sigmaLang.core.values.BooleanPrimitive
import com.github.cubuspl42.sigmaLang.core.values.BuiltinModule
import com.github.cubuspl42.sigmaLang.core.values.Callable
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.ListValue
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
                            Identifier(name = "bleedingResult") to lazy {
                              UnorderedTuple(
                                valueByKey = mapOf(
                                  Identifier(name = "bleedingHands") to lazy {
                                    BooleanPrimitive(value = true) 
                                  },
                                ),
                              ) 
                            },
                            Identifier(name = "calmResult") to lazy {
                              UnorderedTuple(
                                valueByKey = mapOf(
                                  Identifier(name = "purringCat") to lazy {
                                    BooleanPrimitive(value = true) 
                                  },
                                ),
                              ) 
                            },
                            Identifier(name = "tailWavingResult") to lazy {
                              UnorderedTuple(
                                valueByKey = mapOf(
                                  Identifier(name = "wavingTail") to lazy {
                                    BooleanPrimitive(value = true) 
                                  },
                                ),
                              ) 
                            },
                            Identifier(name = "Dog") to lazy {
                              object {
                                public val knot2: Value by lazy {
                                      UnorderedTuple(
                                        valueByKey = mapOf(
                                          Identifier(name = "of") to lazy {
                                            object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg3 = argument
                                                return (((knot0 as Callable).call(
                                                  argument = Identifier(name = "__builtin__"),
                                                ) as Callable).call(
                                                  argument = Identifier(name = "unionWith"),
                                                ) as Callable).call(
                                                  argument = UnorderedTuple(
                                                    valueByKey = mapOf(
                                                      Identifier(name = "first") to lazy {
                                                        arg3 
                                                      },
                                                      Identifier(name = "second") to lazy {
                                                        UnorderedTuple(
                                                          valueByKey = mapOf(
                                                            Identifier(name =
                                                                "__instance_prototype__") to lazy {
                                                              (knot2 as Callable).call(
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
                                                      val arg4 = argument
                                                      return (object : Abstraction() {
                                                        override fun compute(argument: Value):
                                                            Value {
                                                          val arg5 = argument
                                                          return (knot1 as Callable).call(
                                                            argument = Identifier(name =
                                                                "tailWavingResult"),
                                                          )
                                                        }
                                                      } as Callable).call(
                                                        argument = (arg4 as Callable).call(
                                                          argument = Identifier(name = "__args__"),
                                                        ),
                                                      )
                                                    }
                                                  } 
                                                },
                                                Identifier(name = "__class_tag__") to lazy {
                                                  Identifier(name = "Dog") 
                                                },
                                              ),
                                            ) 
                                          },
                                          Identifier(name = "pet") to lazy {
                                            object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg6 = argument
                                                return ((((arg6 as Callable).call(
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
                                                        (arg6 as Callable).call(
                                                          argument = Identifier(name = "this"),
                                                        ) 
                                                      },
                                                      Identifier(name = "__args__") to lazy {
                                                        arg6 
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
                              }.knot2 
                            },
                            Identifier(name = "foo") to lazy {
                              object : Abstraction() {
                                override fun compute(argument: Value): Value {
                                  val arg7 = argument
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
                                  ),
                                ),
                              ) 
                            },
                            Identifier(name = "value1") to lazy {
                              UnorderedTuple(
                                valueByKey = mapOf(
                                  Identifier(name = "x4") to lazy {
                                    UnorderedTuple(
                                      valueByKey = mapOf(
                                      ),
                                    ) 
                                  },
                                  Identifier(name = "x5") to lazy {
                                    UnorderedTuple(
                                      valueByKey = mapOf(
                                        Identifier(name = "b") to lazy {
                                          BooleanPrimitive(value = true) 
                                        },
                                      ),
                                    ) 
                                  },
                                ),
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
                                      ),
                                    ) 
                                  },
                                ),
                              ) 
                            },
                            Identifier(name = "pussy") to lazy {
                              ((((knot0 as Callable).call(
                                argument = Identifier(name = "cat"),
                              ) as Callable).call(
                                argument = Identifier(name = "Cat"),
                              ) as Callable).call(
                                argument = Identifier(name = "of"),
                              ) as Callable).call(
                                argument = UnorderedTuple(
                                  valueByKey = mapOf(
                                    Identifier(name = "hasSharpClaws") to lazy {
                                      BooleanPrimitive(value = false) 
                                    },
                                  ),
                                ),
                              ) 
                            },
                            Identifier(name = "rex") to lazy {
                              (((knot1 as Callable).call(
                                argument = Identifier(name = "Dog"),
                              ) as Callable).call(
                                argument = Identifier(name = "of"),
                              ) as Callable).call(
                                argument = UnorderedTuple(
                                  valueByKey = mapOf(
                                  ),
                                ),
                              ) 
                            },
                            Identifier(name = "isA") to lazy {
                              (((knot0 as Callable).call(
                                argument = Identifier(name = "__builtin__"),
                              ) as Callable).call(
                                argument = Identifier(name = "builtin"),
                              ) as Callable).call(
                                argument = Identifier(name = "isA"),
                              ) 
                            },
                            Identifier(name = "main") to lazy {
                              ((((knot0 as Callable).call(
                                argument = Identifier(name = "__builtin__"),
                              ) as Callable).call(
                                argument = Identifier(name = "List"),
                              ) as Callable).call(
                                argument = Identifier(name = "of"),
                              ) as Callable).call(
                                argument = ListValue(
                                  values = listOf(
                                    BooleanPrimitive(value = true),
                                  ),
                                ),
                              ) 
                            },
                          ),
                        ) 
                      }
                }.knot1 
              },
              Identifier(name = "cat") to lazy {
                object {
                  public val knot8: Value by lazy {
                        UnorderedTuple(
                          valueByKey = mapOf(
                            Identifier(name = "Cat") to lazy {
                              object {
                                public val knot9: Value by lazy {
                                      UnorderedTuple(
                                        valueByKey = mapOf(
                                          Identifier(name = "of") to lazy {
                                            object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg10 = argument
                                                return (((knot0 as Callable).call(
                                                  argument = Identifier(name = "__builtin__"),
                                                ) as Callable).call(
                                                  argument = Identifier(name = "unionWith"),
                                                ) as Callable).call(
                                                  argument = UnorderedTuple(
                                                    valueByKey = mapOf(
                                                      Identifier(name = "first") to lazy {
                                                        arg10 
                                                      },
                                                      Identifier(name = "second") to lazy {
                                                        UnorderedTuple(
                                                          valueByKey = mapOf(
                                                            Identifier(name =
                                                                "__instance_prototype__") to lazy {
                                                              (knot9 as Callable).call(
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
                                                      val arg11 = argument
                                                      return (object : Abstraction() {
                                                        override fun compute(argument: Value):
                                                            Value {
                                                          val arg12 = argument
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
                                                                  ((arg11 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "this"),
                                                                  ) as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "hasSharpClaws"),
                                                                  ) 
                                                                },
                                                                Identifier(name = "then") to lazy {
                                                                  ((knot0 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "__builtin__"),
                                                                  ) as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "bleedingResult"),
                                                                  ) 
                                                                },
                                                                Identifier(name = "else") to lazy {
                                                                  ((knot0 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "__builtin__"),
                                                                  ) as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "calmResult"),
                                                                  ) 
                                                                },
                                                              ),
                                                            ),
                                                          )
                                                        }
                                                      } as Callable).call(
                                                        argument = (arg11 as Callable).call(
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
                                                val arg13 = argument
                                                return ((((arg13 as Callable).call(
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
                                                        (arg13 as Callable).call(
                                                          argument = Identifier(name = "this"),
                                                        ) 
                                                      },
                                                      Identifier(name = "__args__") to lazy {
                                                        arg13 
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
                              }.knot9 
                            },
                          ),
                        ) 
                      }
                }.knot8 
              },
              Identifier(name = "__builtin__") to lazy {
                BuiltinModule 
              },
            ),
          ) 
        }
  }.knot0
}
