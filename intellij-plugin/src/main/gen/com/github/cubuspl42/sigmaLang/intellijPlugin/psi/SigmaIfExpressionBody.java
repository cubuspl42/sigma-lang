// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLang.intellijPlugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SigmaIfExpressionBody extends PsiElement {

  @NotNull
  List<SigmaExpression> getExpressionList();

  @NotNull
  SigmaExpression getTrueBranch();

  @Nullable
  SigmaExpression getFalseBranch();

}
