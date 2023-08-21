// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLangIntellijPlugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SigmaLetExpression extends SigmaExpression {

  @Nullable
  SigmaExpression getExpression();

  @NotNull
  List<SigmaLetExpressionScopeEntry> getLetExpressionScopeEntryList();

}
