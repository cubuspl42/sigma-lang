// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLang.intellijPlugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SigmaLetExpressionScopeEntry extends SigmaLetExpressionScopeEntryBase {

  @Nullable
  SigmaTypeAnnotation getTypeAnnotation();

  @NotNull
  PsiElement getDefinedName();

  @NotNull
  SigmaExpression getBody();

}
