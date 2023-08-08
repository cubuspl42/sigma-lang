// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLangIntellijPlugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl.*;

public interface SigmaTypes {

  IElementType PROPERTY = new SigmaElementType("PROPERTY");

  IElementType COMMENT = new SigmaTokenType("COMMENT");
  IElementType CRLF = new SigmaTokenType("CRLF");
  IElementType KEY = new SigmaTokenType("KEY");
  IElementType SEPARATOR = new SigmaTokenType("SEPARATOR");
  IElementType VALUE = new SigmaTokenType("VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PROPERTY) {
        return new SigmaPropertyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
