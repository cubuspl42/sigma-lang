package sigma.semantics

import getResourceAsText
import sigma.syntax.Module
import sigma.syntax.expressions.LetExpression

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

            val root = Module.build(
                ctx = Program.buildParser(
                    sourceName = fileName,
                    source = source,
                ).module(),
            )

            return Program(
                prelude = prelude,
                root = root,
            )
        }
    }
}
