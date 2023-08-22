// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLang.intellijPlugin.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface SigmaAbstractionConstructor extends SigmaExpression {

  @NotNull
  List<SigmaExpression> getExpressionList();

  @Nullable
  SigmaGenericParametersTuple getGenericParametersTuple();

  @Nullable
  SigmaTypeExpression getTypeExpression();

  @NotNull
  SigmaExpression getArgumentTypeElement();

  @Nullable
  SigmaExpression getImageElement();

}