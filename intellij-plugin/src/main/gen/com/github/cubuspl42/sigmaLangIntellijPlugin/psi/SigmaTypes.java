// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLangIntellijPlugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl.*;

public interface SigmaTypes {

  IElementType CONSTANT_DEFINITION = new SigmaElementType("CONSTANT_DEFINITION");
  IElementType EXPRESSION = new SigmaElementType("EXPRESSION");
  IElementType MODULE = new SigmaElementType("MODULE");
  IElementType NAMESPACE_BODY = new SigmaElementType("NAMESPACE_BODY");
  IElementType NAMESPACE_DEFINITION = new SigmaElementType("NAMESPACE_DEFINITION");
  IElementType STATIC_STATEMENT = new SigmaElementType("STATIC_STATEMENT");

  IElementType ASSIGN = new SigmaTokenType("ASSIGN");
  IElementType CONST_KEYWORD = new SigmaTokenType("CONST_KEYWORD");
  IElementType IDENTIFIER = new SigmaTokenType("IDENTIFIER");
  IElementType INT_LITERAL = new SigmaTokenType("INT_LITERAL");
  IElementType NAMESPACE_KEYWORD = new SigmaTokenType("NAMESPACE_KEYWORD");
  IElementType PAREN_LEFT = new SigmaTokenType("PAREN_LEFT");
  IElementType PAREN_RIGHT = new SigmaTokenType("PAREN_RIGHT");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == CONSTANT_DEFINITION) {
        return new SigmaConstantDefinitionImpl(node);
      }
      else if (type == EXPRESSION) {
        return new SigmaExpressionImpl(node);
      }
      else if (type == MODULE) {
        return new SigmaModuleImpl(node);
      }
      else if (type == NAMESPACE_BODY) {
        return new SigmaNamespaceBodyImpl(node);
      }
      else if (type == NAMESPACE_DEFINITION) {
        return new SigmaNamespaceDefinitionImpl(node);
      }
      else if (type == STATIC_STATEMENT) {
        return new SigmaStaticStatementImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
