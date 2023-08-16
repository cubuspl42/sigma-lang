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

public class SigmaAbstractionConstructorImpl extends ASTWrapperPsiElement implements SigmaAbstractionConstructor {

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
  public SigmaExpression getExpression() {
    return findNotNullChildByClass(SigmaExpression.class);
  }

  @Override
  @Nullable
  public SigmaGenericParametersTuple getGenericParametersTuple() {
    return findChildByClass(SigmaGenericParametersTuple.class);
  }

  @Override
  @NotNull
  public SigmaTupleTypeConstructorCompactExpression getTupleTypeConstructorCompactExpression() {
    return findNotNullChildByClass(SigmaTupleTypeConstructorCompactExpression.class);
  }

  @Override
  @Nullable
  public SigmaTypeExpression getTypeExpression() {
    return findChildByClass(SigmaTypeExpression.class);
  }

}
