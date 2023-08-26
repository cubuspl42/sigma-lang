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

public class SigmaUnorderedTupleTypeConstructorEntryImpl extends SigmaUnorderedTupleTypeConstructorEntryImplMixin implements SigmaUnorderedTupleTypeConstructorEntry {

  public SigmaUnorderedTupleTypeConstructorEntryImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SigmaVisitor visitor) {
    visitor.visitUnorderedTupleTypeConstructorEntry(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SigmaVisitor) accept((SigmaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getDeclaredName() {
    return findNotNullChildByType(IDENTIFIER);
  }

  @Override
  @NotNull
  public SigmaTypeExpression getDeclaredType() {
    return findNotNullChildByClass(SigmaTypeExpression.class);
  }

}
