// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLangIntellijPlugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class SigmaParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return sigma_file(b, l + 1);
  }

  /* ********************************************************** */
  // CONST_KEYWORD IDENTIFIER ASSIGN expression
  public static boolean constant_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constant_definition")) return false;
    if (!nextTokenIs(b, CONST_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CONST_KEYWORD, IDENTIFIER, ASSIGN);
    r = r && expression(b, l + 1);
    exit_section_(b, m, CONSTANT_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // INT_LITERAL
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    if (!nextTokenIs(b, INT_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INT_LITERAL);
    exit_section_(b, m, EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // namespace_body
  public static boolean module(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MODULE, "<module>");
    r = namespace_body(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // namespace_entry*
  public static boolean namespace_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_body")) return false;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_BODY, "<namespace body>");
    while (true) {
      int c = current_position_(b);
      if (!namespace_entry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namespace_body", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // NAMESPACE_KEYWORD IDENTIFIER PAREN_LEFT namespace_body PAREN_RIGHT
  public static boolean namespace_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_definition")) return false;
    if (!nextTokenIs(b, NAMESPACE_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NAMESPACE_KEYWORD, IDENTIFIER, PAREN_LEFT);
    r = r && namespace_body(b, l + 1);
    r = r && consumeToken(b, PAREN_RIGHT);
    exit_section_(b, m, NAMESPACE_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // constant_definition |
  //     namespace_definition
  public static boolean namespace_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_entry")) return false;
    if (!nextTokenIs(b, "<namespace entry>", CONST_KEYWORD, NAMESPACE_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NAMESPACE_ENTRY, "<namespace entry>");
    r = constant_definition(b, l + 1);
    if (!r) r = namespace_definition(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // module
  static boolean sigma_file(PsiBuilder b, int l) {
    return module(b, l + 1);
  }

}
