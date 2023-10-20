import utils.Matcher
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.TypeAlike
import com.github.cubuspl42.sigmaLang.analyzer.semantics.types.UniversalFunctionType

class UniversalFunctionTypeMatcher(
    val argumentType: Matcher<TypeAlike>,
    val imageType: Matcher<TypeAlike>,
) : Matcher<UniversalFunctionType>() {
    override fun match(actual: UniversalFunctionType) {
        argumentType.match(actual = actual.argumentType)
        imageType.match(actual = actual.imageType)
    }
}
