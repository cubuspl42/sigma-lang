// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.*;

public class SigmaExpressionImpl extends ASTWrapperPsiElement implements SigmaExpression {

  public SigmaExpressionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SigmaVisitor visitor) {
    visitor.visitExpression(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SigmaVisitor) accept((SigmaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SigmaAbstractionConstructor getAbstractionConstructor() {
    return findChildByClass(SigmaAbstractionConstructor.class);
  }

  @Override
  @Nullable
  public SigmaIfExpression getIfExpression() {
    return findChildByClass(SigmaIfExpression.class);
  }

  @Override
  @Nullable
  public SigmaIsUndefinedExpression getIsUndefinedExpression() {
    return findChildByClass(SigmaIsUndefinedExpression.class);
  }

  @Override
  @Nullable
  public SigmaLetExpression getLetExpression() {
    return findChildByClass(SigmaLetExpression.class);
  }

  @Override
  @Nullable
  public SigmaTerm getTerm() {
    return findChildByClass(SigmaTerm.class);
  }

}
