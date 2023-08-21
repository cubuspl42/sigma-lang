// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaTypes.*;
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.*;

public class SigmaAbstractionConstructorImpl extends SigmaAbstractionConstructorImplMixin implements SigmaAbstractionConstructor {

  public SigmaAbstractionConstructorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SigmaVisitor visitor) {
    visitor.visitAbstractionConstructor(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SigmaVisitor) accept((SigmaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<SigmaExpression> getExpressionList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, SigmaExpression.class);
  }

  @Override
  @Nullable
  public SigmaGenericParametersTuple getGenericParametersTuple() {
    return findChildByClass(SigmaGenericParametersTuple.class);
  }

  @Override
  @Nullable
  public SigmaTypeExpression getTypeExpression() {
    return findChildByClass(SigmaTypeExpression.class);
  }

  @Override
  @NotNull
  public SigmaExpression getArgumentType() {
    List<SigmaExpression> p1 = getExpressionList();
    return p1.get(0);
  }

  @Override
  @Nullable
  public SigmaExpression getImage() {
    List<SigmaExpression> p1 = getExpressionList();
    return p1.size() < 2 ? null : p1.get(1);
  }

}
