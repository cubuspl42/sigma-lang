// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaTypes.*;
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.*;

public class SigmaTupleConstructorCompactExpressionImpl extends SigmaCompactExpressionImpl implements SigmaTupleConstructorCompactExpression {

  public SigmaTupleConstructorCompactExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public void accept(@NotNull SigmaVisitor visitor) {
    visitor.visitTupleConstructorCompactExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SigmaVisitor) accept((SigmaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public SigmaTupleConstructorExpression getTupleConstructorExpression() {
    return findNotNullChildByClass(SigmaTupleConstructorExpression.class);
  }

}
