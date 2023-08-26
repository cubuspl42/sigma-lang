// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLang.intellijPlugin.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.impl.*;

public interface SigmaTypes {

  IElementType ABSTRACTION_CONSTRUCTOR = new SigmaElementType("ABSTRACTION_CONSTRUCTOR");
  IElementType ADDITION_EXPRESSION = new SigmaElementType("ADDITION_EXPRESSION");
  IElementType CALL_EXPRESSION = new SigmaElementType("CALL_EXPRESSION");
  IElementType CONSTANT_DEFINITION = new SigmaElementType("CONSTANT_DEFINITION");
  IElementType DIVISION_EXPRESSION = new SigmaElementType("DIVISION_EXPRESSION");
  IElementType EQUALS_EXPRESSION = new SigmaElementType("EQUALS_EXPRESSION");
  IElementType EXPRESSION = new SigmaElementType("EXPRESSION");
  IElementType GENERIC_PARAMETERS_TUPLE = new SigmaElementType("GENERIC_PARAMETERS_TUPLE");
  IElementType GENERIC_PARAMETER_DECLARATION = new SigmaElementType("GENERIC_PARAMETER_DECLARATION");
  IElementType GREATER_THAN_EQUALS_EXPRESSION = new SigmaElementType("GREATER_THAN_EQUALS_EXPRESSION");
  IElementType GREATER_THAN_EXPRESSION = new SigmaElementType("GREATER_THAN_EXPRESSION");
  IElementType IF_EXPRESSION = new SigmaElementType("IF_EXPRESSION");
  IElementType IF_EXPRESSION_BODY = new SigmaElementType("IF_EXPRESSION_BODY");
  IElementType INT_LITERAL = new SigmaElementType("INT_LITERAL");
  IElementType IS_UNDEFINED_EXPRESSION = new SigmaElementType("IS_UNDEFINED_EXPRESSION");
  IElementType LESS_THAN_EQUALS_EXPRESSION = new SigmaElementType("LESS_THAN_EQUALS_EXPRESSION");
  IElementType LESS_THAN_EXPRESSION = new SigmaElementType("LESS_THAN_EXPRESSION");
  IElementType LET_EXPRESSION = new SigmaElementType("LET_EXPRESSION");
  IElementType LET_EXPRESSION_SCOPE_ENTRY = new SigmaElementType("LET_EXPRESSION_SCOPE_ENTRY");
  IElementType MULTIPLICATION_EXPRESSION = new SigmaElementType("MULTIPLICATION_EXPRESSION");
  IElementType NAMESPACE_DEFINITION = new SigmaElementType("NAMESPACE_DEFINITION");
  IElementType ORDERED_TUPLE_CONSTRUCTOR = new SigmaElementType("ORDERED_TUPLE_CONSTRUCTOR");
  IElementType ORDERED_TUPLE_TYPE_CONSTRUCTOR = new SigmaElementType("ORDERED_TUPLE_TYPE_CONSTRUCTOR");
  IElementType ORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY = new SigmaElementType("ORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY");
  IElementType PAREN_EXPRESSION = new SigmaElementType("PAREN_EXPRESSION");
  IElementType REFERENCE_EXPRESSION = new SigmaElementType("REFERENCE_EXPRESSION");
  IElementType SUBTRACTION_EXPRESSION = new SigmaElementType("SUBTRACTION_EXPRESSION");
  IElementType TUPLE_CONSTRUCTOR = new SigmaElementType("TUPLE_CONSTRUCTOR");
  IElementType TUPLE_TYPE_CONSTRUCTOR = new SigmaElementType("TUPLE_TYPE_CONSTRUCTOR");
  IElementType TYPE_ANNOTATION = new SigmaElementType("TYPE_ANNOTATION");
  IElementType TYPE_EXPRESSION = new SigmaElementType("TYPE_EXPRESSION");
  IElementType UNARY_NEGATION_EXPRESSION = new SigmaElementType("UNARY_NEGATION_EXPRESSION");
  IElementType UNORDERED_TUPLE_CONSTRUCTOR = new SigmaElementType("UNORDERED_TUPLE_CONSTRUCTOR");
  IElementType UNORDERED_TUPLE_CONSTRUCTOR_ENTRY = new SigmaElementType("UNORDERED_TUPLE_CONSTRUCTOR_ENTRY");
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
  IElementType GREATER_THAN = new SigmaTokenType("GREATER_THAN");
  IElementType GREATER_THAN_EQUALS = new SigmaTokenType("GREATER_THAN_EQUALS");
  IElementType IDENTIFIER = new SigmaTokenType("IDENTIFIER");
  IElementType IF_KEYWORD = new SigmaTokenType("IF_KEYWORD");
  IElementType INT = new SigmaTokenType("INT");
  IElementType IN_KEYWORD = new SigmaTokenType("IN_KEYWORD");
  IElementType IS_UNDEFINED_KEYWORD = new SigmaTokenType("IS_UNDEFINED_KEYWORD");
  IElementType LESS_THAN = new SigmaTokenType("LESS_THAN");
  IElementType LESS_THAN_EQUALS = new SigmaTokenType("LESS_THAN_EQUALS");
  IElementType LET_KEYWORD = new SigmaTokenType("LET_KEYWORD");
  IElementType LINE_COMMENT = new SigmaTokenType("LINE_COMMENT");
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
      else if (type == ADDITION_EXPRESSION) {
        return new SigmaAdditionExpressionImpl(node);
      }
      else if (type == CALL_EXPRESSION) {
        return new SigmaCallExpressionImpl(node);
      }
      else if (type == CONSTANT_DEFINITION) {
        return new SigmaConstantDefinitionImpl(node);
      }
      else if (type == DIVISION_EXPRESSION) {
        return new SigmaDivisionExpressionImpl(node);
      }
      else if (type == EQUALS_EXPRESSION) {
        return new SigmaEqualsExpressionImpl(node);
      }
      else if (type == GENERIC_PARAMETERS_TUPLE) {
        return new SigmaGenericParametersTupleImpl(node);
      }
      else if (type == GENERIC_PARAMETER_DECLARATION) {
        return new SigmaGenericParameterDeclarationImpl(node);
      }
      else if (type == GREATER_THAN_EQUALS_EXPRESSION) {
        return new SigmaGreaterThanEqualsExpressionImpl(node);
      }
      else if (type == GREATER_THAN_EXPRESSION) {
        return new SigmaGreaterThanExpressionImpl(node);
      }
      else if (type == IF_EXPRESSION) {
        return new SigmaIfExpressionImpl(node);
      }
      else if (type == IF_EXPRESSION_BODY) {
        return new SigmaIfExpressionBodyImpl(node);
      }
      else if (type == INT_LITERAL) {
        return new SigmaIntLiteralImpl(node);
      }
      else if (type == IS_UNDEFINED_EXPRESSION) {
        return new SigmaIsUndefinedExpressionImpl(node);
      }
      else if (type == LESS_THAN_EQUALS_EXPRESSION) {
        return new SigmaLessThanEqualsExpressionImpl(node);
      }
      else if (type == LESS_THAN_EXPRESSION) {
        return new SigmaLessThanExpressionImpl(node);
      }
      else if (type == LET_EXPRESSION) {
        return new SigmaLetExpressionImpl(node);
      }
      else if (type == LET_EXPRESSION_SCOPE_ENTRY) {
        return new SigmaLetExpressionScopeEntryImpl(node);
      }
      else if (type == MULTIPLICATION_EXPRESSION) {
        return new SigmaMultiplicationExpressionImpl(node);
      }
      else if (type == NAMESPACE_DEFINITION) {
        return new SigmaNamespaceDefinitionImpl(node);
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
      else if (type == PAREN_EXPRESSION) {
        return new SigmaParenExpressionImpl(node);
      }
      else if (type == REFERENCE_EXPRESSION) {
        return new SigmaReferenceExpressionImpl(node);
      }
      else if (type == SUBTRACTION_EXPRESSION) {
        return new SigmaSubtractionExpressionImpl(node);
      }
      else if (type == TYPE_ANNOTATION) {
        return new SigmaTypeAnnotationImpl(node);
      }
      else if (type == TYPE_EXPRESSION) {
        return new SigmaTypeExpressionImpl(node);
      }
      else if (type == UNARY_NEGATION_EXPRESSION) {
        return new SigmaUnaryNegationExpressionImpl(node);
      }
      else if (type == UNORDERED_TUPLE_CONSTRUCTOR) {
        return new SigmaUnorderedTupleConstructorImpl(node);
      }
      else if (type == UNORDERED_TUPLE_CONSTRUCTOR_ENTRY) {
        return new SigmaUnorderedTupleConstructorEntryImpl(node);
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
