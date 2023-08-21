// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLangIntellijPlugin.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class SigmaVisitor extends PsiElementVisitor {

  public void visitAbstractionConstructor(@NotNull SigmaAbstractionConstructor o) {
    visitExpression(o);
  }

  public void visitAdditionExpression(@NotNull SigmaAdditionExpression o) {
    visitExpression(o);
  }

  public void visitCallExpression(@NotNull SigmaCallExpression o) {
    visitExpression(o);
  }

  public void visitConstantDefinition(@NotNull SigmaConstantDefinition o) {
    visitNamespaceEntry(o);
  }

  public void visitDivisionExpression(@NotNull SigmaDivisionExpression o) {
    visitExpression(o);
  }

  public void visitEqualsExpression(@NotNull SigmaEqualsExpression o) {
    visitExpression(o);
  }

  public void visitExpression(@NotNull SigmaExpression o) {
    visitExpressionBase(o);
  }

  public void visitGenericParameterDeclaration(@NotNull SigmaGenericParameterDeclaration o) {
    visitPsiElement(o);
  }

  public void visitGenericParametersTuple(@NotNull SigmaGenericParametersTuple o) {
    visitPsiElement(o);
  }

  public void visitGreaterThanEqualsExpression(@NotNull SigmaGreaterThanEqualsExpression o) {
    visitExpression(o);
  }

  public void visitGreaterThanExpression(@NotNull SigmaGreaterThanExpression o) {
    visitExpression(o);
  }

  public void visitIfExpression(@NotNull SigmaIfExpression o) {
    visitExpression(o);
  }

  public void visitIfExpressionBody(@NotNull SigmaIfExpressionBody o) {
    visitPsiElement(o);
  }

  public void visitIntLiteral(@NotNull SigmaIntLiteral o) {
    visitExpression(o);
  }

  public void visitIsUndefinedExpression(@NotNull SigmaIsUndefinedExpression o) {
    visitExpression(o);
  }

  public void visitLessThanEqualsExpression(@NotNull SigmaLessThanEqualsExpression o) {
    visitExpression(o);
  }

  public void visitLessThanExpression(@NotNull SigmaLessThanExpression o) {
    visitExpression(o);
  }

  public void visitLetExpression(@NotNull SigmaLetExpression o) {
    visitExpression(o);
  }

  public void visitLetExpressionScopeEntry(@NotNull SigmaLetExpressionScopeEntry o) {
    visitLetExpressionScopeEntryBase(o);
  }

  public void visitMultiplicationExpression(@NotNull SigmaMultiplicationExpression o) {
    visitExpression(o);
  }

  public void visitNamespaceDefinition(@NotNull SigmaNamespaceDefinition o) {
    visitNamespaceEntry(o);
  }

  public void visitOrderedTupleConstructor(@NotNull SigmaOrderedTupleConstructor o) {
    visitPsiElement(o);
  }

  public void visitOrderedTupleTypeConstructor(@NotNull SigmaOrderedTupleTypeConstructor o) {
    visitPsiElement(o);
  }

  public void visitOrderedTupleTypeConstructorEntry(@NotNull SigmaOrderedTupleTypeConstructorEntry o) {
    visitPsiElement(o);
  }

  public void visitParenExpression(@NotNull SigmaParenExpression o) {
    visitExpression(o);
  }

  public void visitReferenceExpression(@NotNull SigmaReferenceExpression o) {
    visitExpression(o);
  }

  public void visitSubtractionExpression(@NotNull SigmaSubtractionExpression o) {
    visitExpression(o);
  }

  public void visitTupleConstructor(@NotNull SigmaTupleConstructor o) {
    visitExpression(o);
  }

  public void visitTupleTypeConstructor(@NotNull SigmaTupleTypeConstructor o) {
    visitExpression(o);
  }

  public void visitTypeAnnotation(@NotNull SigmaTypeAnnotation o) {
    visitPsiElement(o);
  }

  public void visitTypeExpression(@NotNull SigmaTypeExpression o) {
    visitPsiElement(o);
  }

  public void visitUnaryNegationExpression(@NotNull SigmaUnaryNegationExpression o) {
    visitExpression(o);
  }

  public void visitUnorderedTupleConstructor(@NotNull SigmaUnorderedTupleConstructor o) {
    visitPsiElement(o);
  }

  public void visitUnorderedTupleEntry(@NotNull SigmaUnorderedTupleEntry o) {
    visitPsiElement(o);
  }

  public void visitUnorderedTupleTypeConstructor(@NotNull SigmaUnorderedTupleTypeConstructor o) {
    visitPsiElement(o);
  }

  public void visitUnorderedTupleTypeConstructorEntry(@NotNull SigmaUnorderedTupleTypeConstructorEntry o) {
    visitPsiElement(o);
  }

  public void visitExpressionBase(@NotNull SigmaExpressionBase o) {
    visitPsiElement(o);
  }

  public void visitLetExpressionScopeEntryBase(@NotNull SigmaLetExpressionScopeEntryBase o) {
    visitPsiElement(o);
  }

  public void visitNamespaceEntry(@NotNull SigmaNamespaceEntry o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
