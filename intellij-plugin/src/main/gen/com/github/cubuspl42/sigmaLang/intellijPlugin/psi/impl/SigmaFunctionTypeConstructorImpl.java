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

public class SigmaFunctionTypeConstructorImpl extends SigmaFunctionTypeConstructorImplMixin implements SigmaFunctionTypeConstructor {

  public SigmaFunctionTypeConstructorImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SigmaVisitor visitor) {
    visitor.visitFunctionTypeConstructor(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SigmaVisitor) accept((SigmaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SigmaGenericParametersTuple getGenericParametersTuple() {
    return findChildByClass(SigmaGenericParametersTuple.class);
  }

  @Override
  @NotNull
  public SigmaTupleTypeConstructor getArgumentType() {
    return findNotNullChildByClass(SigmaTupleTypeConstructor.class);
  }

  @Override
  @NotNull
  public SigmaTypeExpression getImageType() {
    return findNotNullChildByClass(SigmaTypeExpression.class);
  }

}
