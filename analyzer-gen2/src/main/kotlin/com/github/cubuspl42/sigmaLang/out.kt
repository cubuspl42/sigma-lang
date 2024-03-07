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
      return object {
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
                                      return (((arg0 as Callable).call(
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
                                                  Identifier(name = "__instance_prototype__") to
                                                      lazy {
                                                    (knot2 as Callable).call(
                                                      argument = Identifier(name =
                                                          "__class_prototype__"),
                                                    ) 
                                                  },
                                                )
                                              ) 
                                            },
                                          )
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
                                              override fun compute(argument: Value): Value {
                                                val arg5 = argument
                                                return (((arg0 as Callable).call(
                                                  argument = Identifier(name = "__builtin__"),
                                                ) as Callable).call(
                                                  argument = Identifier(name = "if"),
                                                ) as Callable).call(
                                                  argument = UnorderedTuple(
                                                    valueByKey = mapOf(
                                                      Identifier(name = "condition") to lazy {
                                                        ((arg4 as Callable).call(
                                                          argument = Identifier(name = "this"),
                                                        ) as Callable).call(
                                                          argument = Identifier(name =
                                                              "hasSharpClaws"),
                                                        ) 
                                                      },
                                                      Identifier(name = "then") to lazy {
                                                        (knot1 as Callable).call(
                                                          argument = Identifier(name =
                                                              "bleedingResult"),
                                                        ) 
                                                      },
                                                      Identifier(name = "else") to lazy {
                                                        (knot1 as Callable).call(
                                                          argument = Identifier(name =
                                                              "calmResult"),
                                                        ) 
                                                      },
                                                    )
                                                  ),
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
                                        Identifier(name = "Cat") 
                                      },
                                    )
                                  ) 
                                },
                                Identifier(name = "pet") to lazy {
                                  object : Abstraction() {
                                    override fun compute(argument: Value): Value {
                                      val arg6 = argument
                                      return ((((arg6 as Callable).call(
                                        argument = Identifier(name = "this"),
                                      ) as Callable).call(
                                        argument = Identifier(name = "__instance_prototype__"),
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
                  Identifier(name = "tailWavingResult") to lazy {
                    UnorderedTuple(
                      valueByKey = mapOf(
                        Identifier(name = "wavingTail") to lazy {
                          BooleanPrimitive(value = true) 
                        },
                      )
                    ) 
                  },
                  Identifier(name = "Dog") to lazy {
                    object {
                      public val knot7: Value by lazy {
                            UnorderedTuple(
                              valueByKey = mapOf(
                                Identifier(name = "of") to lazy {
                                  object : Abstraction() {
                                    override fun compute(argument: Value): Value {
                                      val arg8 = argument
                                      return (((arg0 as Callable).call(
                                        argument = Identifier(name = "__builtin__"),
                                      ) as Callable).call(
                                        argument = Identifier(name = "unionWith"),
                                      ) as Callable).call(
                                        argument = UnorderedTuple(
                                          valueByKey = mapOf(
                                            Identifier(name = "first") to lazy {
                                              arg8 
                                            },
                                            Identifier(name = "second") to lazy {
                                              UnorderedTuple(
                                                valueByKey = mapOf(
                                                  Identifier(name = "__instance_prototype__") to
                                                      lazy {
                                                    (knot7 as Callable).call(
                                                      argument = Identifier(name =
                                                          "__class_prototype__"),
                                                    ) 
                                                  },
                                                )
                                              ) 
                                            },
                                          )
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
                                            val arg9 = argument
                                            return (object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg10 = argument
                                                return (knot1 as Callable).call(
                                                  argument = Identifier(name = "tailWavingResult"),
                                                )
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
                                        Identifier(name = "Dog") 
                                      },
                                    )
                                  ) 
                                },
                                Identifier(name = "pet") to lazy {
                                  object : Abstraction() {
                                    override fun compute(argument: Value): Value {
                                      val arg11 = argument
                                      return ((((arg11 as Callable).call(
                                        argument = Identifier(name = "this"),
                                      ) as Callable).call(
                                        argument = Identifier(name = "__instance_prototype__"),
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
                                          )
                                        ),
                                      )
                                    }
                                  } 
                                },
                              )
                            ) 
                          }
                    }.knot7 
                  },
                  Identifier(name = "foo") to lazy {
                    object : Abstraction() {
                      override fun compute(argument: Value): Value {
                        val arg12 = argument
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
                  Identifier(name = "rex") to lazy {
                    (((knot1 as Callable).call(
                      argument = Identifier(name = "Dog"),
                    ) as Callable).call(
                      argument = Identifier(name = "of"),
                    ) as Callable).call(
                      argument = UnorderedTuple(
                        valueByKey = mapOf(
                        )
                      ),
                    ) 
                  },
                  Identifier(name = "isA") to lazy {
                    ((arg0 as Callable).call(
                      argument = Identifier(name = "__builtin__"),
                    ) as Callable).call(
                      argument = Identifier(name = "isA"),
                    ) 
                  },
                  Identifier(name = "main") to lazy {
                    UnorderedTuple(
                      valueByKey = mapOf(
                        Identifier(name = "isPussyCat") to lazy {
                          ((knot1 as Callable).call(
                            argument = Identifier(name = "isA"),
                          ) as Callable).call(
                            argument = UnorderedTuple(
                              valueByKey = mapOf(
                                Identifier(name = "instance") to lazy {
                                  (knot1 as Callable).call(
                                    argument = Identifier(name = "pussy"),
                                  ) 
                                },
                                Identifier(name = "class") to lazy {
                                  (knot1 as Callable).call(
                                    argument = Identifier(name = "Cat"),
                                  ) 
                                },
                              )
                            ),
                          ) 
                        },
                        Identifier(name = "isPussyDog") to lazy {
                          ((knot1 as Callable).call(
                            argument = Identifier(name = "isA"),
                          ) as Callable).call(
                            argument = UnorderedTuple(
                              valueByKey = mapOf(
                                Identifier(name = "instance") to lazy {
                                  (knot1 as Callable).call(
                                    argument = Identifier(name = "pussy"),
                                  ) 
                                },
                                Identifier(name = "class") to lazy {
                                  (knot1 as Callable).call(
                                    argument = Identifier(name = "Dog"),
                                  ) 
                                },
                              )
                            ),
                          ) 
                        },
                        Identifier(name = "isRexCat") to lazy {
                          ((knot1 as Callable).call(
                            argument = Identifier(name = "isA"),
                          ) as Callable).call(
                            argument = UnorderedTuple(
                              valueByKey = mapOf(
                                Identifier(name = "instance") to lazy {
                                  (knot1 as Callable).call(
                                    argument = Identifier(name = "rex"),
                                  ) 
                                },
                                Identifier(name = "class") to lazy {
                                  (knot1 as Callable).call(
                                    argument = Identifier(name = "Cat"),
                                  ) 
                                },
                              )
                            ),
                          ) 
                        },
                        Identifier(name = "isRexDog") to lazy {
                          ((knot1 as Callable).call(
                            argument = Identifier(name = "isA"),
                          ) as Callable).call(
                            argument = UnorderedTuple(
                              valueByKey = mapOf(
                                Identifier(name = "instance") to lazy {
                                  (knot1 as Callable).call(
                                    argument = Identifier(name = "rex"),
                                  ) 
                                },
                                Identifier(name = "class") to lazy {
                                  (knot1 as Callable).call(
                                    argument = Identifier(name = "Dog"),
                                  ) 
                                },
                              )
                            ),
                          ) 
                        },
                      )
                    ) 
                  },
                )
              ) 
            }
      }.knot1
    }
  }

  public val main: Value = (root as Callable).call(
        argument = UnorderedTuple(
          valueByKey = mapOf(
            Identifier(name = "__builtin__") to lazyOf(
              BuiltinScope,
            ),
          )
        ),
      )
}
