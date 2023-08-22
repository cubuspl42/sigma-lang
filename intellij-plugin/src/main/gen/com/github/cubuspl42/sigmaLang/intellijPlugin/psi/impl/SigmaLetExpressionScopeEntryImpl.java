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

public class SigmaLetExpressionScopeEntryImpl extends SigmaLetExpressionScopeEntryImplMixin implements SigmaLetExpressionScopeEntry {

  public SigmaLetExpressionScopeEntryImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SigmaVisitor visitor) {
    visitor.visitLetExpressionScopeEntry(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SigmaVisitor) accept((SigmaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public SigmaTypeAnnotation getTypeAnnotation() {
    return findChildByClass(SigmaTypeAnnotation.class);
  }

  @Override
  @NotNull
  public PsiElement getDefinedNameElement() {
    return findNotNullChildByType(IDENTIFIER);
  }

  @Override
  @NotNull
  public SigmaExpression getBodyElement() {
    return findNotNullChildByClass(SigmaExpression.class);
  }

}
