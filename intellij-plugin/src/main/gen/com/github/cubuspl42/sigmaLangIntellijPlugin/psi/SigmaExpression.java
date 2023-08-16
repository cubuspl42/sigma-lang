// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLangIntellijPlugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SigmaExpression extends PsiElement {

  @Nullable
  SigmaAbstractionConstructor getAbstractionConstructor();

  @Nullable
  SigmaIfExpression getIfExpression();

  @Nullable
  SigmaIsUndefinedExpression getIsUndefinedExpression();

  @Nullable
  SigmaLetExpression getLetExpression();

  @Nullable
  SigmaTerm getTerm();

}
