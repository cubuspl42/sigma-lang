package sigma.semantics

import sigma.syntax.ModuleTerm

class Project {
    interface Store {
        fun load(fileName: String): String
    }

    class ResourceStore(private val javaClass: Class<*>) : Store {
        override fun load(fileName: String): String {
            val content = javaClass.getResource(fileName)?.readText()
            return content ?: throw RuntimeException("Couldn't load the source file: $fileName")
        }
    }

    class Loader private constructor(
        private val prelude: Prelude,
        private val store: Store,
    ) {
        companion object {
            fun create(
                store: Store,
            ): Loader {
                val prelude = Prelude.load()

                return Loader(
                    prelude = prelude,
                    store = store,
                )
            }
        }

        fun load(fileBaseName: String): Program {
            val fileName = "${fileBaseName}.sigma"
            val source = store.load(fileName)

            val moduleTerm = ModuleTerm.build(
                ctx = Program.buildParser(
                    sourceName = fileName,
                    source = source,
                ).module(),
            )

            val module = Module.build(
                prelude = prelude,
                term = moduleTerm,
            )

            return Program(
                module = module,
            )
        }
    }
}
