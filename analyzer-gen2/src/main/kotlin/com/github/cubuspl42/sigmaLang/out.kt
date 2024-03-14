@file:Suppress(
  "RedundantVisibilityModifier",
  "unused",
)

package com.github.cubuspl42.sigmaLang

import com.github.cubuspl42.sigmaLang.core.values.AbstractionValue
import com.github.cubuspl42.sigmaLang.core.values.CallableValue
import com.github.cubuspl42.sigmaLang.core.values.Identifier
import com.github.cubuspl42.sigmaLang.core.values.ListValue
import com.github.cubuspl42.sigmaLang.core.values.StringValue
import com.github.cubuspl42.sigmaLang.core.values.UnorderedTupleValue
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.values.builtin.BuiltinModule
import kotlin.Suppress

public object Out {
  public val root: Value = object {
    public val knot0: Value by lazy {
          UnorderedTupleValue(
            valueByKey = mapOf(
              Identifier(name = "main") to lazy {
                object {
                  public val knot1: Value by lazy {
                        UnorderedTupleValue(
                          valueByKey = mapOf(
                            Identifier(name = "Dog") to lazy {
                              ((((knot0 as CallableValue).call(
                                argument = Identifier(name = "__builtin__"),
                              ) as CallableValue).call(
                                argument = Identifier(name = "Class"),
                              ) as CallableValue).call(
                                argument = Identifier(name = "of"),
                              ) as CallableValue).call(
                                argument = UnorderedTupleValue(
                                  valueByKey = mapOf(
                                    Identifier(name = "tag") to lazy {
                                      Identifier(name = "Dog")
                                    },
                                    Identifier(name = "instanceConstructorName") to lazy {
                                      Identifier(name = "of")
                                    },
                                    Identifier(name = "methods") to lazy {
                                      UnorderedTupleValue(
                                        valueByKey = mapOf(
                                          Identifier(name = "bark") to lazy {
                                            object : AbstractionValue() {
                                              override fun compute(argument: Value): Value {
                                                val arg2 = argument
                                                return object : AbstractionValue() {
                                                  override fun compute(argument: Value): Value {
                                                    val arg3 = argument
                                                    return object : AbstractionValue() {
                                                      override fun compute(argument: Value): Value {
                                                        val arg4 = argument
                                                        return StringValue(value = "Woof!")
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          },
                                        ),
                                      )
                                    },
                                  ),
                                ),
                              )
                            },
                            Identifier(name = "pussy") to lazy {
                              ((((knot0 as CallableValue).call(
                                argument = Identifier(name = "cat"),
                              ) as CallableValue).call(
                                argument = Identifier(name = "Cat"),
                              ) as CallableValue).call(
                                argument = Identifier(name = "of"),
                              ) as CallableValue).call(
                                argument = UnorderedTupleValue(
                                  valueByKey = mapOf(
                                  ),
                                ),
                              )
                            },
                            Identifier(name = "rex") to lazy {
                              (((knot1 as CallableValue).call(
                                argument = Identifier(name = "Dog"),
                              ) as CallableValue).call(
                                argument = Identifier(name = "of"),
                              ) as CallableValue).call(
                                argument = UnorderedTupleValue(
                                  valueByKey = mapOf(
                                  ),
                                ),
                              )
                            },
                            Identifier(name = "letters") to lazy {
                              ListValue(
                                values = listOf(
                                  StringValue(value = "a"),
                                  StringValue(value = "b"),
                                  StringValue(value = "c"),
                                  StringValue(value = "d"),
                                ),
                              )
                            },
                            Identifier(name = "thing") to lazy {
                              (knot1 as CallableValue).call(
                                argument = Identifier(name = "letters"),
                              )
                            },
                            Identifier(name = "main") to lazy {
                              (object : AbstractionValue() {
                                override fun compute(argument: Value): Value {
                                  val arg5 = argument
                                  return (arg5 as CallableValue).call(
                                    argument = Identifier(name = "result"),
                                  )
                                }
                              } as CallableValue).call(
                                argument = object {
                                  public val knot6: Value by lazy {
                                        UnorderedTupleValue(
                                          valueByKey = mapOf(
                                            Identifier(name = "result") to lazy {
                                              (object : AbstractionValue() {
                                                override fun compute(argument: Value): Value {
                                                  val arg7 = argument
                                                  return (((knot0 as CallableValue).call(
                                                    argument = Identifier(name = "__builtin__"),
                                                  ) as CallableValue).call(
                                                    argument = Identifier(name = "if"),
                                                  ) as CallableValue).call(
                                                    argument = UnorderedTupleValue(
                                                      valueByKey = mapOf(
                                                        Identifier(name = "condition") to lazy {
                                                          (((knot0 as CallableValue).call(
                                                            argument = Identifier(name =
                                                                "__builtin__"),
                                                          ) as CallableValue).call(
                                                            argument = Identifier(name = "isA"),
                                                          ) as CallableValue).call(
                                                            argument = UnorderedTupleValue(
                                                              valueByKey = mapOf(
                                                                Identifier(name = "instance") to
                                                                    lazy {
                                                                  arg7
                                                                },
                                                                Identifier(name = "class") to lazy {
                                                                  ((knot0 as CallableValue).call(
                                                                    argument = Identifier(name =
                                                                        "cat"),
                                                                  ) as CallableValue).call(
                                                                    argument = Identifier(name =
                                                                        "Cat"),
                                                                  )
                                                                },
                                                              ),
                                                            ),
                                                          )
                                                        },
                                                        Identifier(name = "then") to lazy {
                                                          (object : AbstractionValue() {
                                                            override fun compute(argument: Value):
                                                                Value {
                                                              val arg8 = argument
                                                              return ((((knot0 as CallableValue).call(
                                                                argument = Identifier(name = "cat"),
                                                              ) as CallableValue).call(
                                                                argument = Identifier(name = "Cat"),
                                                              ) as CallableValue).call(
                                                                argument = Identifier(name =
                                                                    "meow"),
                                                              ) as CallableValue).call(
                                                                argument = UnorderedTupleValue(
                                                                  valueByKey = mapOf(
                                                                    Identifier(name = "this") to
                                                                        lazy {
                                                                      (arg8 as CallableValue).call(
                                                                        argument = Identifier(name =
                                                                            "c"),
                                                                      )
                                                                    },
                                                                  ),
                                                                ),
                                                              )
                                                            }
                                                          } as CallableValue).call(
                                                            argument = UnorderedTupleValue(
                                                              valueByKey = mapOf(
                                                                Identifier(name = "c") to lazy {
                                                                  arg7
                                                                },
                                                              ),
                                                            ),
                                                          )
                                                        },
                                                        Identifier(name = "else") to lazy {
                                                          (((knot0 as CallableValue).call(
                                                            argument = Identifier(name =
                                                                "__builtin__"),
                                                          ) as CallableValue).call(
                                                            argument = Identifier(name = "if"),
                                                          ) as CallableValue).call(
                                                            argument = UnorderedTupleValue(
                                                              valueByKey = mapOf(
                                                                Identifier(name = "condition") to
                                                                    lazy {
                                                                  (((knot0 as CallableValue).call(
                                                                    argument = Identifier(name =
                                                                        "__builtin__"),
                                                                  ) as CallableValue).call(
                                                                    argument = Identifier(name =
                                                                        "isA"),
                                                                  ) as CallableValue).call(
                                                                    argument = UnorderedTupleValue(
                                                                      valueByKey = mapOf(
                                                                        Identifier(name =
                                                                            "instance") to lazy {
                                                                          arg7
                                                                        },
                                                                        Identifier(name = "class")
                                                                            to lazy {
                                                                          (knot1 as CallableValue).call(
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
                                                                  (object : AbstractionValue() {
                                                                    override
                                                                        fun compute(argument: Value):
                                                                        Value {
                                                                      val arg9 = argument
                                                                      return (((knot1 as
                                                                          CallableValue).call(
                                                                        argument = Identifier(name =
                                                                            "Dog"),
                                                                      ) as CallableValue).call(
                                                                        argument = Identifier(name =
                                                                            "bark"),
                                                                      ) as CallableValue).call(
                                                                        argument = UnorderedTupleValue(
                                                                          valueByKey = mapOf(
                                                                            Identifier(name =
                                                                                "this") to lazy {
                                                                              (arg9 as
                                                                                  CallableValue).call(
                                                                                argument =
                                                                                    Identifier(name
                                                                                    = "d"),
                                                                              )
                                                                            },
                                                                          ),
                                                                        ),
                                                                      )
                                                                    }
                                                                  } as CallableValue).call(
                                                                    argument = UnorderedTupleValue(
                                                                      valueByKey = mapOf(
                                                                        Identifier(name = "d") to
                                                                            lazy {
                                                                          arg7
                                                                        },
                                                                      ),
                                                                    ),
                                                                  )
                                                                },
                                                                Identifier(name = "else") to lazy {
                                                                  (((knot0 as CallableValue).call(
                                                                    argument = Identifier(name =
                                                                        "__builtin__"),
                                                                  ) as CallableValue).call(
                                                                    argument = Identifier(name =
                                                                        "if"),
                                                                  ) as CallableValue).call(
                                                                    argument = UnorderedTupleValue(
                                                                      valueByKey = mapOf(
                                                                        Identifier(name =
                                                                            "condition") to lazy {
                                                                          ((((knot0 as
                                                                              CallableValue).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "__builtin__"),
                                                                          ) as CallableValue).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "List"),
                                                                          ) as CallableValue).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "isNotEmpty"),
                                                                          ) as CallableValue).call(
                                                                            argument =
                                                                                UnorderedTupleValue(
                                                                              valueByKey = mapOf(
                                                                                Identifier(name =
                                                                                    "this") to lazy
                                                                                    {
                                                                                  arg7
                                                                                },
                                                                              ),
                                                                            ),
                                                                          )
                                                                        },
                                                                        Identifier(name = "then") to
                                                                            lazy {
                                                                          (object : AbstractionValue() {
                                                                            override
                                                                                fun compute(argument: Value):
                                                                                Value {
                                                                              val arg10 = argument
                                                                              return (arg10 as
                                                                                  CallableValue).call(
                                                                                argument =
                                                                                    Identifier(name
                                                                                    = "head"),
                                                                              )
                                                                            }
                                                                          } as CallableValue).call(
                                                                            argument =
                                                                                UnorderedTupleValue(
                                                                              valueByKey = mapOf(
                                                                                Identifier(name =
                                                                                    "head") to lazy
                                                                                    {
                                                                                  ((((knot0 as
                                                                                      CallableValue).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        =
                                                                                        "__builtin__"),
                                                                                  ) as
                                                                                      CallableValue).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        = "List"),
                                                                                  ) as
                                                                                      CallableValue).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        = "head"),
                                                                                  ) as
                                                                                      CallableValue).call(
                                                                                    argument =
                                                                                        UnorderedTupleValue(
                                                                                      valueByKey =
                                                                                          mapOf(
                                                                                        Identifier(name
                                                                                            =
                                                                                            "this")
                                                                                            to lazy
                                                                                            {
                                                                                          arg7
                                                                                        },
                                                                                      ),
                                                                                    ),
                                                                                  )
                                                                                },
                                                                                Identifier(name =
                                                                                    "tail") to lazy
                                                                                    {
                                                                                  ((((knot0 as
                                                                                      CallableValue).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        =
                                                                                        "__builtin__"),
                                                                                  ) as
                                                                                      CallableValue).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        = "List"),
                                                                                  ) as
                                                                                      CallableValue).call(
                                                                                    argument =
                                                                                        Identifier(name
                                                                                        = "tail"),
                                                                                  ) as
                                                                                      CallableValue).call(
                                                                                    argument =
                                                                                        UnorderedTupleValue(
                                                                                      valueByKey =
                                                                                          mapOf(
                                                                                        Identifier(name
                                                                                            =
                                                                                            "this")
                                                                                            to lazy
                                                                                            {
                                                                                          arg7
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
                                                                              CallableValue).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "__builtin__"),
                                                                          ) as CallableValue).call(
                                                                            argument =
                                                                                Identifier(name =
                                                                                "panic"),
                                                                          ) as CallableValue).call(
                                                                            argument =
                                                                                UnorderedTupleValue(
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
                                              } as CallableValue).call(
                                                argument = (knot1 as CallableValue).call(
                                                  argument = Identifier(name = "thing"),
                                                ),
                                              )
                                            },
                                          ),
                                        )
                                      }
                                }.knot6,
                              )
                            },
                          ),
                        )
                      }
                }.knot1
              },
              Identifier(name = "cat") to lazy {
                object {
                  public val knot11: Value by lazy {
                        UnorderedTupleValue(
                          valueByKey = mapOf(
                            Identifier(name = "Cat") to lazy {
                              ((((knot0 as CallableValue).call(
                                argument = Identifier(name = "__builtin__"),
                              ) as CallableValue).call(
                                argument = Identifier(name = "Class"),
                              ) as CallableValue).call(
                                argument = Identifier(name = "of"),
                              ) as CallableValue).call(
                                argument = UnorderedTupleValue(
                                  valueByKey = mapOf(
                                    Identifier(name = "tag") to lazy {
                                      Identifier(name = "Cat")
                                    },
                                    Identifier(name = "instanceConstructorName") to lazy {
                                      Identifier(name = "of")
                                    },
                                    Identifier(name = "methods") to lazy {
                                      UnorderedTupleValue(
                                        valueByKey = mapOf(
                                          Identifier(name = "pet") to lazy {
                                            object : AbstractionValue() {
                                              override fun compute(argument: Value): Value {
                                                val arg12 = argument
                                                return object : AbstractionValue() {
                                                  override fun compute(argument: Value): Value {
                                                    val arg13 = argument
                                                    return object : AbstractionValue() {
                                                      override fun compute(argument: Value): Value {
                                                        val arg14 = argument
                                                        return (((knot0 as CallableValue).call(
                                                          argument = Identifier(name =
                                                              "__builtin__"),
                                                        ) as CallableValue).call(
                                                          argument = Identifier(name = "if"),
                                                        ) as CallableValue).call(
                                                          argument = UnorderedTupleValue(
                                                            valueByKey = mapOf(
                                                              Identifier(name = "condition") to lazy
                                                                  {
                                                                ((arg13 as CallableValue).call(
                                                                  argument = Identifier(name =
                                                                      "this"),
                                                                ) as CallableValue).call(
                                                                  argument = Identifier(name =
                                                                      "hasSharpClaws"),
                                                                )
                                                              },
                                                              Identifier(name = "then") to lazy {
                                                                StringValue(value = "Bleeding")
                                                              },
                                                              Identifier(name = "else") to lazy {
                                                                StringValue(value = "Purrr")
                                                              },
                                                            ),
                                                          ),
                                                        )
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          },
                                          Identifier(name = "meow") to lazy {
                                            object : AbstractionValue() {
                                              override fun compute(argument: Value): Value {
                                                val arg15 = argument
                                                return object : AbstractionValue() {
                                                  override fun compute(argument: Value): Value {
                                                    val arg16 = argument
                                                    return object : AbstractionValue() {
                                                      override fun compute(argument: Value): Value {
                                                        val arg17 = argument
                                                        return StringValue(value = "Meow!")
                                                      }
                                                    }
                                                  }
                                                }
                                              }
                                            }
                                          },
                                        ),
                                      )
                                    },
                                  ),
                                ),
                              )
                            },
                          ),
                        )
                      }
                }.knot11
              },
              Identifier(name = "__builtin__") to lazy {
                BuiltinModule
              },
            ),
          )
        }
  }.knot0
}
