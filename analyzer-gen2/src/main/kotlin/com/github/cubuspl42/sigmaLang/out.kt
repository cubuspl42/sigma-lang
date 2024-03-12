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
                            Identifier(name = "Dog") to lazy {
                              object {
                                public val knot2: Value by lazy {
                                      UnorderedTuple(
                                        valueByKey = mapOf(
                                          Identifier(name = "of") to lazy {
                                            object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg3 = argument
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
                                                Identifier(name = "bark") to lazy {
                                                  object : Abstraction() {
                                                    override fun compute(argument: Value): Value {
                                                      val arg4 = argument
                                                      return (object : Abstraction() {
                                                        override fun compute(argument: Value):
                                                            Value {
                                                          val arg5 = argument
                                                          return StringPrimitive(value = "Woof!")
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
                                          Identifier(name = "bark") to lazy {
                                            object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg6 = argument
                                                return ((((arg6 as Callable).call(
                                                  argument = Identifier(name = "this"),
                                                ) as Callable).call(
                                                  argument = Identifier(name =
                                                      "__instance_prototype__"),
                                                ) as Callable).call(
                                                  argument = Identifier(name = "bark"),
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
                            Identifier(name = "letters") to lazy {
                              ListValue(
                                values = listOf(
                                  StringPrimitive(value = "a"),
                                  StringPrimitive(value = "b"),
                                  StringPrimitive(value = "c"),
                                  StringPrimitive(value = "d"),
                                ),
                              ) 
                            },
                            Identifier(name = "thing") to lazy {
                              (knot1 as Callable).call(
                                argument = Identifier(name = "letters"),
                              ) 
                            },
                            Identifier(name = "main") to lazy {
                              (object : Abstraction() {
                                override fun compute(argument: Value): Value {
                                  val arg7 = argument
                                  return (arg7 as Callable).call(
                                    argument = Identifier(name = "result"),
                                  )
                                }
                              } as Callable).call(
                                argument = object {
                                  public val knot8: Value by lazy {
                                        UnorderedTuple(
                                          valueByKey = mapOf(
                                            Identifier(name = "result") to lazy {
                                              (object : Abstraction() {
                                                override fun compute(argument: Value): Value {
                                                  val arg9 = argument
                                                  return (((knot0 as Callable).call(
                                                    argument = Identifier(name = "__builtin__"),
                                                  ) as Callable).call(
                                                    argument = Identifier(name = "if"),
                                                  ) as Callable).call(
                                                    argument = UnorderedTuple(
                                                      valueByKey = mapOf(
                                                        Identifier(name = "condition") to lazy {
                                                          (((knot0 as Callable).call(
                                                            argument = Identifier(name =
                                                                "__builtin__"),
                                                          ) as Callable).call(
                                                            argument = Identifier(name = "isA"),
                                                          ) as Callable).call(
                                                            argument = UnorderedTuple(
                                                              valueByKey = mapOf(
                                                                Identifier(name = "instance") to
                                                                    lazy {
                                                                  arg9 
                                                                },
                                                                Identifier(name = "class") to lazy {
                                                                  ((knot0 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "cat"),
                                                                  ) as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "Cat"),
                                                                  ) 
                                                                },
                                                              ),
                                                            ),
                                                          ) 
                                                        },
                                                        Identifier(name = "then") to lazy {
                                                          (object : Abstraction() {
                                                            override fun compute(argument: Value):
                                                                Value {
                                                              val arg10 = argument
                                                              return ((((knot0 as Callable).call(
                                                                argument = Identifier(name = "cat"),
                                                              ) as Callable).call(
                                                                argument = Identifier(name = "Cat"),
                                                              ) as Callable).call(
                                                                argument = Identifier(name =
                                                                    "meow"),
                                                              ) as Callable).call(
                                                                argument = UnorderedTuple(
                                                                  valueByKey = mapOf(
                                                                    Identifier(name = "this") to
                                                                        lazy {
                                                                      (arg10 as Callable).call(
                                                                        argument = Identifier(name =
                                                                            "c"),
                                                                      ) 
                                                                    },
                                                                  ),
                                                                ),
                                                              )
                                                            }
                                                          } as Callable).call(
                                                            argument = UnorderedTuple(
                                                              valueByKey = mapOf(
                                                                Identifier(name = "c") to lazy {
                                                                  arg9 
                                                                },
                                                              ),
                                                            ),
                                                          ) 
                                                        },
                                                        Identifier(name = "else") to lazy {
                                                          (((knot0 as Callable).call(
                                                            argument = Identifier(name =
                                                                "__builtin__"),
                                                          ) as Callable).call(
                                                            argument = Identifier(name = "if"),
                                                          ) as Callable).call(
                                                            argument = UnorderedTuple(
                                                              valueByKey = mapOf(
                                                                Identifier(name = "condition") to
                                                                    lazy {
                                                                  (((knot0 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "__builtin__"),
                                                                  ) as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "isA"),
                                                                  ) as Callable).call(
                                                                    argument = UnorderedTuple(
                                                                      valueByKey = mapOf(
                                                                        Identifier(name =
                                                                            "instance") to lazy {
                                                                          arg9 
                                                                        },
                                                                        Identifier(name = "class")
                                                                            to lazy {
                                                                          (knot1 as Callable).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "Dog"),
                                                                          ) 
                                                                        },
                                                                      ),
                                                                    ),
                                                                  ) 
                                                                },
                                                                Identifier(name = "then") to lazy {
                                                                  (object : Abstraction() {
                                                                    override
                                                                        fun compute(argument: Value):
                                                                        Value {
                                                                      val arg11 = argument
                                                                      return (((knot1 as
                                                                          Callable).call(
                                                                        argument = Identifier(name =
                                                                            "Dog"),
                                                                      ) as Callable).call(
                                                                        argument = Identifier(name =
                                                                            "bark"),
                                                                      ) as Callable).call(
                                                                        argument = UnorderedTuple(
                                                                          valueByKey = mapOf(
                                                                            Identifier(name =
                                                                                "this") to lazy {
                                                                              (arg11 as
                                                                                  Callable).call(
                                                                                argument =
                                                                                    Identifier(name
                                                                                    = "d"),
                                                                              ) 
                                                                            },
                                                                          ),
                                                                        ),
                                                                      )
                                                                    }
                                                                  } as Callable).call(
                                                                    argument = UnorderedTuple(
                                                                      valueByKey = mapOf(
                                                                        Identifier(name = "d") to
                                                                            lazy {
                                                                          arg9 
                                                                        },
                                                                      ),
                                                                    ),
                                                                  ) 
                                                                },
                                                                Identifier(name = "else") to lazy {
                                                                  (((knot0 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "__builtin__"),
                                                                  ) as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "if"),
                                                                  ) as Callable).call(
                                                                    argument = UnorderedTuple(
                                                                      valueByKey = mapOf(
                                                                        Identifier(name =
                                                                            "condition") to lazy {
                                                                          ((((knot0 as
                                                                              Callable).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "__builtin__"),
                                                                          ) as Callable).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "List"),
                                                                          ) as Callable).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "isNotEmpty"),
                                                                          ) as Callable).call(
                                                                            argument =
                                                                                UnorderedTuple(
                                                                              valueByKey = mapOf(
                                                                                Identifier(name =
                                                                                    "this") to lazy
                                                                                    {
                                                                                  arg9 
                                                                                },
                                                                              ),
                                                                            ),
                                                                          ) 
                                                                        },
                                                                        Identifier(name = "then") to
                                                                            lazy {
                                                                          (object : Abstraction() {
                                                                            override
                                                                                fun compute(argument: Value):
                                                                                Value {
                                                                              val arg12 = argument
                                                                              return (arg12 as
                                                                                  Callable).call(
                                                                                argument =
                                                                                    Identifier(name
                                                                                    = "head"),
                                                                              )
                                                                            }
                                                                          } as Callable).call(
                                                                            argument =
                                                                                UnorderedTuple(
                                                                              valueByKey = mapOf(
                                                                                Identifier(name =
                                                                                    "head") to lazy
                                                                                    {
                                                                                  ((((knot0 as
                                                                                      Callable).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        =
                                                                                        "__builtin__"),
                                                                                  ) as
                                                                                      Callable).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        = "List"),
                                                                                  ) as
                                                                                      Callable).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        = "head"),
                                                                                  ) as
                                                                                      Callable).call(
                                                                                    argument =
                                                                                        UnorderedTuple(
                                                                                      valueByKey =
                                                                                          mapOf(
                                                                                        Identifier(name
                                                                                            =
                                                                                            "this")
                                                                                            to lazy
                                                                                            {
                                                                                          arg9 
                                                                                        },
                                                                                      ),
                                                                                    ),
                                                                                  ) 
                                                                                },
                                                                                Identifier(name =
                                                                                    "tail") to lazy
                                                                                    {
                                                                                  ((((knot0 as
                                                                                      Callable).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        =
                                                                                        "__builtin__"),
                                                                                  ) as
                                                                                      Callable).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        = "List"),
                                                                                  ) as
                                                                                      Callable).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        = "tail"),
                                                                                  ) as
                                                                                      Callable).call(
                                                                                    argument =
                                                                                        UnorderedTuple(
                                                                                      valueByKey =
                                                                                          mapOf(
                                                                                        Identifier(name
                                                                                            =
                                                                                            "this")
                                                                                            to lazy
                                                                                            {
                                                                                          arg9 
                                                                                        },
                                                                                      ),
                                                                                    ),
                                                                                  ) 
                                                                                },
                                                                              ),
                                                                            ),
                                                                          ) 
                                                                        },
                                                                        Identifier(name = "else") to
                                                                            lazy {
                                                                          (((knot0 as
                                                                              Callable).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "__builtin__"),
                                                                          ) as Callable).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "panic"),
                                                                          ) as Callable).call(
                                                                            argument =
                                                                                UnorderedTuple(
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
                                                        },
                                                      ),
                                                    ),
                                                  )
                                                }
                                              } as Callable).call(
                                                argument = (knot1 as Callable).call(
                                                  argument = Identifier(name = "thing"),
                                                ),
                                              ) 
                                            },
                                          ),
                                        ) 
                                      }
                                }.knot8,
                              ) 
                            },
                          ),
                        ) 
                      }
                }.knot1 
              },
              Identifier(name = "cat") to lazy {
                object {
                  public val knot13: Value by lazy {
                        UnorderedTuple(
                          valueByKey = mapOf(
                            Identifier(name = "Cat") to lazy {
                              object {
                                public val knot14: Value by lazy {
                                      UnorderedTuple(
                                        valueByKey = mapOf(
                                          Identifier(name = "of") to lazy {
                                            object : Abstraction() {
                                              override fun compute(argument: Value): Value {
                                                val arg15 = argument
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
                                                        arg15 
                                                      },
                                                      Identifier(name = "second") to lazy {
                                                        UnorderedTuple(
                                                          valueByKey = mapOf(
                                                            Identifier(name =
                                                                "__instance_prototype__") to lazy {
                                                              (knot14 as Callable).call(
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
                                                      val arg16 = argument
                                                      return (object : Abstraction() {
                                                        override fun compute(argument: Value):
                                                            Value {
                                                          val arg17 = argument
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
                                                                  ((arg16 as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "this"),
                                                                  ) as Callable).call(
                                                                    argument = Identifier(name =
                                                                        "hasSharpClaws"),
                                                                  ) 
                                                                },
                                                                Identifier(name = "then") to lazy {
                                                                  StringPrimitive(value =
                                                                      "Bleeding") 
                                                                },
                                                                Identifier(name = "else") to lazy {
                                                                  StringPrimitive(value = "Purrr") 
                                                                },
                                                              ),
                                                            ),
                                                          )
                                                        }
                                                      } as Callable).call(
                                                        argument = (arg16 as Callable).call(
                                                          argument = Identifier(name = "__args__"),
                                                        ),
                                                      )
                                                    }
                                                  } 
                                                },
                                                Identifier(name = "meow") to lazy {
                                                  object : Abstraction() {
                                                    override fun compute(argument: Value): Value {
                                                      val arg18 = argument
                                                      return (object : Abstraction() {
                                                        override fun compute(argument: Value):
                                                            Value {
                                                          val arg19 = argument
                                                          return StringPrimitive(value = "Meow!")
                                                        }
                                                      } as Callable).call(
                                                        argument = (arg18 as Callable).call(
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
                                                val arg20 = argument
                                                return ((((arg20 as Callable).call(
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
                                                        (arg20 as Callable).call(
                                                          argument = Identifier(name = "this"),
                                                        ) 
                                                      },
                                                      Identifier(name = "__args__") to lazy {
                                                        arg20 
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
                                                val arg21 = argument
                                                return ((((arg21 as Callable).call(
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
                                                        (arg21 as Callable).call(
                                                          argument = Identifier(name = "this"),
                                                        ) 
                                                      },
                                                      Identifier(name = "__args__") to lazy {
                                                        arg21 
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
                              }.knot14 
                            },
                          ),
                        ) 
                      }
                }.knot13 
              },
              Identifier(name = "__builtin__") to lazy {
                BuiltinModule 
              },
            ),
          ) 
        }
  }.knot0
}
