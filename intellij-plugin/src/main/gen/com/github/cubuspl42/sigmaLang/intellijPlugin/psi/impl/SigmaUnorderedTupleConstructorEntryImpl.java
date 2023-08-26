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

public class SigmaUnorderedTupleConstructorEntryImpl extends SigmaUnorderedTupleConstructorEntryImplMixin implements SigmaUnorderedTupleConstructorEntry {

  public SigmaUnorderedTupleConstructorEntryImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull SigmaVisitor visitor) {
    visitor.visitUnorderedTupleConstructorEntry(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof SigmaVisitor) accept((SigmaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public PsiElement getPassedName() {
    return findNotNullChildByType(IDENTIFIER);
  }

  @Override
  @NotNull
  public SigmaExpression getPassedValue() {
    return findNotNullChildByClass(SigmaExpression.class);
  }

}
