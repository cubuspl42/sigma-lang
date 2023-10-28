package com.github.cubuspl42.sigmaLang.analyzer.semantics.types

// Thought: KindAlike?
abstract class TypeAlike {
    abstract fun resolveTypePlaceholders(
        assignedType: SpecificType,
    ): TypePlaceholderResolution

    abstract fun substituteTypePlaceholders(
        resolution: TypePlaceholderResolution,
    ): TypePlaceholderSubstitution<TypeAlike>

    open fun match(
        assignedType: SpecificType,
    ): SpecificType.MatchResult {
        throw UnsupportedOperationException() // TODO: Only membership types should be able to match
    }

    fun dump(): String = dumpRecursively(depth = 0)

    fun dumpRecursively(depth: Int): String {
        if (depth > SpecificType.maxDumpDepth) return "(...)"

        return dumpDirectly(depth = depth)
    }

    open val asLiteral: PrimitiveLiteralType? = null

    open val asArray: ArrayType? = null

    abstract fun dumpDirectly(depth: Int): String

    fun replaceType(
        typeReplacer: TypeReplacer,
    ): TypeAlike = replaceTypeDirectly(
        context = TypeReplacementContext(
            typeReplacer = typeReplacer,
        ),
    )

    fun replaceTypeDirectly(
        context: TypeReplacementContext,
    ): TypeAlike {
        val typeAlike = context.typeReplacer.replace(this)
        return typeAlike ?: run {
            replaceTypeRecursively(context = context)
        }
    }

    open fun replaceTypeRecursively(
        context: TypeReplacementContext,
    ): TypeAlike = this

    abstract fun specifyImplicitly(): Type

    class TypeReplacementContext(
        val typeReplacer: TypeReplacer,
    )

    interface TypeReplacer {
        companion object {
            fun combineAll(
                replacers: Collection<TypeReplacer>,
            ): TypeReplacer = object : TypeReplacer {
                override fun replace(type: TypeAlike): TypeAlike? = replacers.fold(
                    initial = null,
                    operation = { accType: TypeAlike?, replacer: TypeReplacer ->
                        replacer.replace(type = accType ?: type) ?: accType
                    },
                )
            }
        }

        fun replace(type: TypeAlike): TypeAlike?
    }
}
