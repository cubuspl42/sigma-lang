// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLangIntellijPlugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.impl.*;

public interface SigmaTypes {

  IElementType ABSTRACTION_CONSTRUCTOR = new SigmaElementType("ABSTRACTION_CONSTRUCTOR");
  IElementType ADDITION_TERM = new SigmaElementType("ADDITION_TERM");
  IElementType CALL_COMPACT_EXPRESSION = new SigmaElementType("CALL_COMPACT_EXPRESSION");
  IElementType COMPACT_EXPRESSION = new SigmaElementType("COMPACT_EXPRESSION");
  IElementType COMPACT_TERM = new SigmaElementType("COMPACT_TERM");
  IElementType CONSTANT_DEFINITION = new SigmaElementType("CONSTANT_DEFINITION");
  IElementType DIVISION_TERM = new SigmaElementType("DIVISION_TERM");
  IElementType EQUALS_TERM = new SigmaElementType("EQUALS_TERM");
  IElementType EXPRESSION = new SigmaElementType("EXPRESSION");
  IElementType GENERIC_PARAMETERS_TUPLE = new SigmaElementType("GENERIC_PARAMETERS_TUPLE");
  IElementType GENERIC_PARAMETER_DECLARATION = new SigmaElementType("GENERIC_PARAMETER_DECLARATION");
  IElementType IF_EXPRESSION = new SigmaElementType("IF_EXPRESSION");
  IElementType IF_EXPRESSION_BODY = new SigmaElementType("IF_EXPRESSION_BODY");
  IElementType IS_UNDEFINED_EXPRESSION = new SigmaElementType("IS_UNDEFINED_EXPRESSION");
  IElementType LET_EXPRESSION = new SigmaElementType("LET_EXPRESSION");
  IElementType LET_EXPRESSION_SCOPE = new SigmaElementType("LET_EXPRESSION_SCOPE");
  IElementType LET_EXPRESSION_SCOPE_ENTRY = new SigmaElementType("LET_EXPRESSION_SCOPE_ENTRY");
  IElementType LITERAL_COMPACT_EXPRESSION = new SigmaElementType("LITERAL_COMPACT_EXPRESSION");
  IElementType MULTIPLICATION_TERM = new SigmaElementType("MULTIPLICATION_TERM");
  IElementType NAMESPACE_BODY = new SigmaElementType("NAMESPACE_BODY");
  IElementType NAMESPACE_DEFINITION = new SigmaElementType("NAMESPACE_DEFINITION");
  IElementType NAMESPACE_ENTRY = new SigmaElementType("NAMESPACE_ENTRY");
  IElementType ORDERED_TUPLE_CONSTRUCTOR = new SigmaElementType("ORDERED_TUPLE_CONSTRUCTOR");
  IElementType ORDERED_TUPLE_TYPE_CONSTRUCTOR = new SigmaElementType("ORDERED_TUPLE_TYPE_CONSTRUCTOR");
  IElementType ORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY = new SigmaElementType("ORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY");
  IElementType PAREN_COMPACT_EXPRESSION = new SigmaElementType("PAREN_COMPACT_EXPRESSION");
  IElementType REFERENCE_COMPACT_EXPRESSION = new SigmaElementType("REFERENCE_COMPACT_EXPRESSION");
  IElementType SUBTRACTION_TERM = new SigmaElementType("SUBTRACTION_TERM");
  IElementType TERM = new SigmaElementType("TERM");
  IElementType TUPLE_CONSTRUCTOR_COMPACT_EXPRESSION = new SigmaElementType("TUPLE_CONSTRUCTOR_COMPACT_EXPRESSION");
  IElementType TUPLE_CONSTRUCTOR_EXPRESSION = new SigmaElementType("TUPLE_CONSTRUCTOR_EXPRESSION");
  IElementType TUPLE_TYPE_CONSTRUCTOR_COMPACT_EXPRESSION = new SigmaElementType("TUPLE_TYPE_CONSTRUCTOR_COMPACT_EXPRESSION");
  IElementType TYPE_ANNOTATION = new SigmaElementType("TYPE_ANNOTATION");
  IElementType TYPE_EXPRESSION = new SigmaElementType("TYPE_EXPRESSION");
  IElementType UNARY_NEGATION_TERM = new SigmaElementType("UNARY_NEGATION_TERM");
  IElementType UNORDERED_TUPLE_CONSTRUCTOR = new SigmaElementType("UNORDERED_TUPLE_CONSTRUCTOR");
  IElementType UNORDERED_TUPLE_ENTRY = new SigmaElementType("UNORDERED_TUPLE_ENTRY");
  IElementType UNORDERED_TUPLE_TYPE_CONSTRUCTOR = new SigmaElementType("UNORDERED_TUPLE_TYPE_CONSTRUCTOR");
  IElementType UNORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY = new SigmaElementType("UNORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY");

  IElementType ASSIGN = new SigmaTokenType("ASSIGN");
  IElementType ASTERISK = new SigmaTokenType("ASTERISK");
  IElementType BRACE_LEFT = new SigmaTokenType("BRACE_LEFT");
  IElementType BRACE_RIGHT = new SigmaTokenType("BRACE_RIGHT");
  IElementType BRACKET_LEFT = new SigmaTokenType("BRACKET_LEFT");
  IElementType BRACKET_RIGHT = new SigmaTokenType("BRACKET_RIGHT");
  IElementType COLON = new SigmaTokenType("COLON");
  IElementType COMMA = new SigmaTokenType("COMMA");
  IElementType CONST_KEYWORD = new SigmaTokenType("CONST_KEYWORD");
  IElementType DASH = new SigmaTokenType("DASH");
  IElementType ELSE_KEYWORD = new SigmaTokenType("ELSE_KEYWORD");
  IElementType EQUALS = new SigmaTokenType("EQUALS");
  IElementType FAT_ARROW = new SigmaTokenType("FAT_ARROW");
  IElementType IDENTIFIER = new SigmaTokenType("IDENTIFIER");
  IElementType IF_KEYWORD = new SigmaTokenType("IF_KEYWORD");
  IElementType INT_LITERAL = new SigmaTokenType("INT_LITERAL");
  IElementType IN_KEYWORD = new SigmaTokenType("IN_KEYWORD");
  IElementType IS_UNDEFINED_KEYWORD = new SigmaTokenType("IS_UNDEFINED_KEYWORD");
  IElementType LET_KEYWORD = new SigmaTokenType("LET_KEYWORD");
  IElementType MINUS = new SigmaTokenType("MINUS");
  IElementType NAMESPACE_KEYWORD = new SigmaTokenType("NAMESPACE_KEYWORD");
  IElementType PAREN_LEFT = new SigmaTokenType("PAREN_LEFT");
  IElementType PAREN_RIGHT = new SigmaTokenType("PAREN_RIGHT");
  IElementType PLUS = new SigmaTokenType("PLUS");
  IElementType SLASH = new SigmaTokenType("SLASH");
  IElementType THEN_KEYWORD = new SigmaTokenType("THEN_KEYWORD");
  IElementType THIN_ARROW = new SigmaTokenType("THIN_ARROW");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ABSTRACTION_CONSTRUCTOR) {
        return new SigmaAbstractionConstructorImpl(node);
      }
      else if (type == ADDITION_TERM) {
        return new SigmaAdditionTermImpl(node);
      }
      else if (type == CALL_COMPACT_EXPRESSION) {
        return new SigmaCallCompactExpressionImpl(node);
      }
      else if (type == COMPACT_TERM) {
        return new SigmaCompactTermImpl(node);
      }
      else if (type == CONSTANT_DEFINITION) {
        return new SigmaConstantDefinitionImpl(node);
      }
      else if (type == DIVISION_TERM) {
        return new SigmaDivisionTermImpl(node);
      }
      else if (type == EQUALS_TERM) {
        return new SigmaEqualsTermImpl(node);
      }
      else if (type == EXPRESSION) {
        return new SigmaExpressionImpl(node);
      }
      else if (type == GENERIC_PARAMETERS_TUPLE) {
        return new SigmaGenericParametersTupleImpl(node);
      }
      else if (type == GENERIC_PARAMETER_DECLARATION) {
        return new SigmaGenericParameterDeclarationImpl(node);
      }
      else if (type == IF_EXPRESSION) {
        return new SigmaIfExpressionImpl(node);
      }
      else if (type == IF_EXPRESSION_BODY) {
        return new SigmaIfExpressionBodyImpl(node);
      }
      else if (type == IS_UNDEFINED_EXPRESSION) {
        return new SigmaIsUndefinedExpressionImpl(node);
      }
      else if (type == LET_EXPRESSION) {
        return new SigmaLetExpressionImpl(node);
      }
      else if (type == LET_EXPRESSION_SCOPE) {
        return new SigmaLetExpressionScopeImpl(node);
      }
      else if (type == LET_EXPRESSION_SCOPE_ENTRY) {
        return new SigmaLetExpressionScopeEntryImpl(node);
      }
      else if (type == LITERAL_COMPACT_EXPRESSION) {
        return new SigmaLiteralCompactExpressionImpl(node);
      }
      else if (type == MULTIPLICATION_TERM) {
        return new SigmaMultiplicationTermImpl(node);
      }
      else if (type == NAMESPACE_BODY) {
        return new SigmaNamespaceBodyImpl(node);
      }
      else if (type == NAMESPACE_DEFINITION) {
        return new SigmaNamespaceDefinitionImpl(node);
      }
      else if (type == NAMESPACE_ENTRY) {
        return new SigmaNamespaceEntryImpl(node);
      }
      else if (type == ORDERED_TUPLE_CONSTRUCTOR) {
        return new SigmaOrderedTupleConstructorImpl(node);
      }
      else if (type == ORDERED_TUPLE_TYPE_CONSTRUCTOR) {
        return new SigmaOrderedTupleTypeConstructorImpl(node);
      }
      else if (type == ORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY) {
        return new SigmaOrderedTupleTypeConstructorEntryImpl(node);
      }
      else if (type == PAREN_COMPACT_EXPRESSION) {
        return new SigmaParenCompactExpressionImpl(node);
      }
      else if (type == REFERENCE_COMPACT_EXPRESSION) {
        return new SigmaReferenceCompactExpressionImpl(node);
      }
      else if (type == SUBTRACTION_TERM) {
        return new SigmaSubtractionTermImpl(node);
      }
      else if (type == TUPLE_CONSTRUCTOR_COMPACT_EXPRESSION) {
        return new SigmaTupleConstructorCompactExpressionImpl(node);
      }
      else if (type == TUPLE_CONSTRUCTOR_EXPRESSION) {
        return new SigmaTupleConstructorExpressionImpl(node);
      }
      else if (type == TUPLE_TYPE_CONSTRUCTOR_COMPACT_EXPRESSION) {
        return new SigmaTupleTypeConstructorCompactExpressionImpl(node);
      }
      else if (type == TYPE_ANNOTATION) {
        return new SigmaTypeAnnotationImpl(node);
      }
      else if (type == TYPE_EXPRESSION) {
        return new SigmaTypeExpressionImpl(node);
      }
      else if (type == UNARY_NEGATION_TERM) {
        return new SigmaUnaryNegationTermImpl(node);
      }
      else if (type == UNORDERED_TUPLE_CONSTRUCTOR) {
        return new SigmaUnorderedTupleConstructorImpl(node);
      }
      else if (type == UNORDERED_TUPLE_ENTRY) {
        return new SigmaUnorderedTupleEntryImpl(node);
      }
      else if (type == UNORDERED_TUPLE_TYPE_CONSTRUCTOR) {
        return new SigmaUnorderedTupleTypeConstructorImpl(node);
      }
      else if (type == UNORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY) {
        return new SigmaUnorderedTupleTypeConstructorEntryImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
