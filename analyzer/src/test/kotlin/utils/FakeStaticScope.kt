package utils

import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Identifier
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Symbol
import com.github.cubuspl42.sigmaLang.analyzer.evaluation.values.Value
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.LeveledResolvedIntroduction
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.MappingStaticBlock
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedDefinition
import com.github.cubuspl42.sigmaLang.analyzer.semantics.ResolvedUnorderedArgument
import com.github.cubuspl42.sigmaLang.analyzer.syntax.scope.StaticScope
import com.github.cubuspl42.sigmaLang.analyzer.semantics.expressions.AtomicExpression
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.Type
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.SpecificType
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UnorderedTupleType
import com.github.cubuspl42.sigmaLang.analyzer.syntax.expressions.AbstractionConstructorTerm

abstract class FakeIntroduction

data class FakeUserDeclaration(
    val name: Identifier,
    val declaredType: Type,
) : FakeIntroduction()

data class FakeDefinition(
    val name: Identifier,
    val type: SpecificType,
    val value: Value,
) : FakeIntroduction()

@Suppress("TestFunctionName")
fun FakeStaticScope(
    introductions: Set<FakeIntroduction>,
): StaticScope = FakeDefinitionBlock(
    definitions = introductions.filterIsInstance<FakeDefinition>().toSet(),
).chainWith(
    FakeArgumentDeclarationBlock(
        declarations = introductions.filterIsInstance<FakeUserDeclaration>().toSet(),
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

    private val argumentDeclaration = AbstractionConstructorTerm.ArgumentDeclaration(
        declaredType = UnorderedTupleType(
            valueTypeByName = declarations.associate { it.name to it.declaredType },
        ),
    )

    override val resolvedNameByName: Map<Symbol, LeveledResolvedIntroduction> = declarations.associate { declaration ->
        declaration.name to LeveledResolvedIntroduction(
            level = StaticScope.Level.Primary,
            resolvedIntroduction = ResolvedUnorderedArgument(
                argumentDeclaration = argumentDeclaration,
                name = declaration.name,
            ),
        )
    }
}

class FakeDefinitionBlock(
    level: StaticScope.Level = StaticScope.Level.Primary,
    definitions: Set<FakeDefinition>,
) : MappingStaticBlock() {
    companion object {
        fun of(
            vararg definitions: FakeDefinition,
        ): FakeDefinitionBlock = FakeDefinitionBlock(
            definitions = definitions.toSet(),
        )
    }

    override val resolvedNameByName: Map<Symbol, LeveledResolvedIntroduction> = definitions.associate { fakeDefinition ->
        fakeDefinition.name to LeveledResolvedIntroduction(
            level = level,
            resolvedIntroduction = ResolvedDefinition(
                body = AtomicExpression(
                    type = fakeDefinition.type,
                    value = fakeDefinition.value,
                ),
            ),
        )
    }
}

object FakeStaticScope {
    fun of(
        vararg declarations: FakeIntroduction,
    ): StaticScope = FakeStaticScope(
        introductions = declarations.toSet(),
    )
}
