package com.github.cubuspl42.sigmaLang.core.expressions

import com.github.cubuspl42.sigmaLang.core.DynamicScope
import com.github.cubuspl42.sigmaLang.core.Package
import com.github.cubuspl42.sigmaLang.core.values.Value
import com.github.cubuspl42.sigmaLang.core.visitors.CodegenRepresentationContext

class PackageReference(
    private val referredPackageLazy: Lazy<Package>,
): Reference()  {
    private val referredPackage by referredPackageLazy

    override fun buildCodegenRepresentation(
        context: CodegenRepresentationContext,
    ): CodegenRepresentation = TODO()

    override fun bind(scope: DynamicScope): Lazy<Value> = lazy {
        TODO() // Package is not a Wrapper
    }
}
