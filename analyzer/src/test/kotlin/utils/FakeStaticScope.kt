package utils

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.semantics.MappingStaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedName
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedUnorderedArgument
import com.github.cubuspl42.sigmaLang.analyzer.semantics.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AbstractionConstructor
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.introductions.Definition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType

abstract class FakeIntroduction

data class FakeUserDeclaration(
    val name: Identifier,
    val declaredType: Type,
) : FakeIntroduction()

data class FakeDefinition(
    val name: Identifier,
    val type: SpecificType,
    val value: Value,
) : FakeIntroduction() {
    val definition = Definition(
        name = name,
        body = AtomicExpression(
            type = type,
            value = value,
        ),
    )
}

@Suppress("TestFunctionName")
fun FakeStaticScope(
    declarations: Set<FakeIntroduction>,
): StaticScope = FakeDefinitionBlock(
    definitions = declarations.filterIsInstance<FakeDefinition>().toSet(),
).chainWith(
    FakeArgumentDeclarationBlock(
        declarations = declarations.filterIsInstance<FakeUserDeclaration>().toSet(),
    ),
)


class FakeArgumentDeclarationBlock(
    declarations: Set<FakeUserDeclaration>,
) : MappingStaticBlock() {
    companion object {
        fun of(
            vararg declarations: FakeUserDeclaration,
        ): FakeArgumentDeclarationBlock = FakeArgumentDeclarationBlock(
            declarations = declarations.toSet(),
        )
    }

    val argumentDeclaration = AbstractionConstructor.ArgumentDeclaration(
        declaredType = UnorderedTupleType(
            valueTypeByName = declarations.associate { it.name to it.declaredType },
        ),
    )

    override val resolvedNameByName: Map<Symbol, ResolvedName> = declarations.associate { declaration ->
        declaration.name to ResolvedUnorderedArgument(
            argumentDeclaration = argumentDeclaration,
            name = declaration.name,
        )
    }
}

class FakeDefinitionBlock(
    definitions: Set<FakeDefinition>,
) : MappingStaticBlock() {
    companion object {
        fun of(
            vararg definitions: FakeDefinition,
        ): FakeDefinitionBlock = FakeDefinitionBlock(
            definitions = definitions.toSet(),
        )
    }

    override val resolvedNameByName: Map<Symbol, ResolvedName> = definitions.associate { definition ->
        definition.name to ResolvedDefinition(
            definition = definition.definition,
        )
    }
}

object FakeStaticScope {
    fun of(
        vararg declarations: FakeIntroduction,
    ): StaticScope = FakeStaticScope(
        declarations = declarations.toSet(),
    )
}
