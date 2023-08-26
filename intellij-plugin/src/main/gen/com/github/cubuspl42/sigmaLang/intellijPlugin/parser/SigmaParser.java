// This is a generated file. Not intended for manual editing.
package com.github.cubuspl42.sigmaLang.intellijPlugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaTypes.*;
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
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return module(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ABSTRACTION_CONSTRUCTOR, ADDITION_EXPRESSION, DIVISION_EXPRESSION, EQUALS_EXPRESSION,
      EXPRESSION, GREATER_THAN_EQUALS_EXPRESSION, GREATER_THAN_EXPRESSION, IF_EXPRESSION,
      INT_LITERAL, IS_UNDEFINED_EXPRESSION, LESS_THAN_EQUALS_EXPRESSION, LESS_THAN_EXPRESSION,
      LET_EXPRESSION, MULTIPLICATION_EXPRESSION, ORDERED_TUPLE_CONSTRUCTOR, ORDERED_TUPLE_TYPE_CONSTRUCTOR,
      PAREN_EXPRESSION, POSTFIX_CALL_EXPRESSION, REFERENCE_EXPRESSION, SUBTRACTION_EXPRESSION,
      TUPLE_CONSTRUCTOR, TUPLE_TYPE_CONSTRUCTOR, UNORDERED_TUPLE_CONSTRUCTOR, UNORDERED_TUPLE_TYPE_CONSTRUCTOR),
  };

  /* ********************************************************** */
  // BRACE_LEFT <<p>> BRACE_RIGHT
  static boolean brace_wrapped(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "brace_wrapped")) return false;
    if (!nextTokenIs(b, BRACE_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BRACE_LEFT);
    r = r && _p.parse(b, l);
    r = r && consumeToken(b, BRACE_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BRACKET_LEFT <<p>> BRACKET_RIGHT
  static boolean bracket_wrapped(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "bracket_wrapped")) return false;
    if (!nextTokenIs(b, BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BRACKET_LEFT);
    r = r && _p.parse(b, l);
    r = r && consumeToken(b, BRACKET_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONST_KEYWORD IDENTIFIER ASSIGN expression
  public static boolean constant_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constant_definition")) return false;
    if (!nextTokenIs(b, CONST_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, CONST_KEYWORD, IDENTIFIER, ASSIGN);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, CONSTANT_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean generic_parameter_declaration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_parameter_declaration")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, GENERIC_PARAMETER_DECLARATION, r);
    return r;
  }

  /* ********************************************************** */
  // DASH <<bracket_wrapped
  //         <<list generic_parameter_declaration>>
  //     >>
  public static boolean generic_parameters_tuple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "generic_parameters_tuple")) return false;
    if (!nextTokenIs(b, DASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DASH);
    r = r && bracket_wrapped(b, l + 1, generic_parameters_tuple_1_0_parser_);
    exit_section_(b, m, GENERIC_PARAMETERS_TUPLE, r);
    return r;
  }

  /* ********************************************************** */
  // THEN_KEYWORD expression COMMA
  //     ELSE_KEYWORD expression COMMA?
  public static boolean if_expression_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_expression_body")) return false;
    if (!nextTokenIs(b, THEN_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, THEN_KEYWORD);
    r = r && expression(b, l + 1, -1);
    r = r && consumeTokens(b, 0, COMMA, ELSE_KEYWORD);
    r = r && expression(b, l + 1, -1);
    r = r && if_expression_body_5(b, l + 1);
    exit_section_(b, m, IF_EXPRESSION_BODY, r);
    return r;
  }

  // COMMA?
  private static boolean if_expression_body_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_expression_body_5")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // <<list let_expression_scope_entry>>
  static boolean let_expression_scope(PsiBuilder b, int l) {
    return list(b, l + 1, SigmaParser::let_expression_scope_entry);
  }

  /* ********************************************************** */
  // IDENTIFIER type_annotation? ASSIGN expression
  public static boolean let_expression_scope_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_expression_scope_entry")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && let_expression_scope_entry_1(b, l + 1);
    r = r && consumeToken(b, ASSIGN);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, LET_EXPRESSION_SCOPE_ENTRY, r);
    return r;
  }

  // type_annotation?
  private static boolean let_expression_scope_entry_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_expression_scope_entry_1")) return false;
    type_annotation(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  static Parser list_$(Parser _p) {
    return (b, l) -> list(b, l + 1, _p);
  }

  // (<<p>> (COMMA <<p>>)* COMMA?)?
  static boolean list(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "list")) return false;
    list_0(b, l + 1, _p);
    return true;
  }

  // <<p>> (COMMA <<p>>)* COMMA?
  private static boolean list_0(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "list_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = _p.parse(b, l);
    r = r && list_0_1(b, l + 1, _p);
    r = r && list_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA <<p>>)*
  private static boolean list_0_1(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "list_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!list_0_1_0(b, l + 1, _p)) break;
      if (!empty_element_parsed_guard_(b, "list_0_1", c)) break;
    }
    return true;
  }

  // COMMA <<p>>
  private static boolean list_0_1_0(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "list_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && _p.parse(b, l);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean list_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_0_2")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // namespace_body
  static boolean module(PsiBuilder b, int l) {
    return namespace_body(b, l + 1);
  }

  /* ********************************************************** */
  // namespace_entry*
  static boolean namespace_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_body")) return false;
    while (true) {
      int c = current_position_(b);
      if (!namespace_entry(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "namespace_body", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // NAMESPACE_KEYWORD IDENTIFIER <<paren_wrapped namespace_body>>
  public static boolean namespace_definition(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_definition")) return false;
    if (!nextTokenIs(b, NAMESPACE_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, NAMESPACE_KEYWORD, IDENTIFIER);
    r = r && paren_wrapped(b, l + 1, SigmaParser::namespace_body);
    exit_section_(b, m, NAMESPACE_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // constant_definition |
  //     namespace_definition
  static boolean namespace_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_entry")) return false;
    if (!nextTokenIs(b, "", CONST_KEYWORD, NAMESPACE_KEYWORD)) return false;
    boolean r;
    r = constant_definition(b, l + 1);
    if (!r) r = namespace_definition(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // <<bracket_wrapped
  //         <<list expression>>
  //     >>
  public static boolean ordered_tuple_constructor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_tuple_constructor")) return false;
    if (!nextTokenIs(b, BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bracket_wrapped(b, l + 1, ordered_tuple_constructor_0_0_parser_);
    exit_section_(b, m, ORDERED_TUPLE_CONSTRUCTOR, r);
    return r;
  }

  /* ********************************************************** */
  // DASH <<bracket_wrapped
  //         <<list ordered_tuple_type_constructor_entry>>
  //     >>
  public static boolean ordered_tuple_type_constructor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_tuple_type_constructor")) return false;
    if (!nextTokenIs(b, DASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DASH);
    r = r && bracket_wrapped(b, l + 1, ordered_tuple_type_constructor_1_0_parser_);
    exit_section_(b, m, ORDERED_TUPLE_TYPE_CONSTRUCTOR, r);
    return r;
  }

  /* ********************************************************** */
  // (IDENTIFIER COLON)? type_expression
  public static boolean ordered_tuple_type_constructor_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_tuple_type_constructor_entry")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY, "<ordered tuple type constructor entry>");
    r = ordered_tuple_type_constructor_entry_0(b, l + 1);
    r = r && type_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (IDENTIFIER COLON)?
  private static boolean ordered_tuple_type_constructor_entry_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_tuple_type_constructor_entry_0")) return false;
    ordered_tuple_type_constructor_entry_0_0(b, l + 1);
    return true;
  }

  // IDENTIFIER COLON
  private static boolean ordered_tuple_type_constructor_entry_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_tuple_type_constructor_entry_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENTIFIER, COLON);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // PAREN_LEFT <<p>> PAREN_RIGHT
  static boolean paren_wrapped(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "paren_wrapped")) return false;
    if (!nextTokenIs(b, PAREN_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PAREN_LEFT);
    r = r && _p.parse(b, l);
    r = r && consumeToken(b, PAREN_RIGHT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ordered_tuple_constructor |
  //     unordered_tuple_constructor
  static boolean tuple_constructor_raw(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_constructor_raw")) return false;
    if (!nextTokenIs(b, "", BRACE_LEFT, BRACKET_LEFT)) return false;
    boolean r;
    r = ordered_tuple_constructor(b, l + 1);
    if (!r) r = unordered_tuple_constructor(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // COLON type_expression
  public static boolean type_annotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_annotation")) return false;
    if (!nextTokenIs(b, COLON)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON);
    r = r && type_expression(b, l + 1);
    exit_section_(b, m, TYPE_ANNOTATION, r);
    return r;
  }

  /* ********************************************************** */
  // expression
  public static boolean type_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_EXPRESSION, "<type expression>");
    r = expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // <<brace_wrapped
  //         <<list unordered_tuple_constructor_entry>>
  //     >>
  public static boolean unordered_tuple_constructor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unordered_tuple_constructor")) return false;
    if (!nextTokenIs(b, BRACE_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = brace_wrapped(b, l + 1, unordered_tuple_constructor_0_0_parser_);
    exit_section_(b, m, UNORDERED_TUPLE_CONSTRUCTOR, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER COLON expression
  public static boolean unordered_tuple_constructor_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unordered_tuple_constructor_entry")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENTIFIER, COLON);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, UNORDERED_TUPLE_CONSTRUCTOR_ENTRY, r);
    return r;
  }

  /* ********************************************************** */
  // DASH <<brace_wrapped
  //         <<list unordered_tuple_type_constructor_entry>>
  //     >>
  public static boolean unordered_tuple_type_constructor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unordered_tuple_type_constructor")) return false;
    if (!nextTokenIs(b, DASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DASH);
    r = r && brace_wrapped(b, l + 1, unordered_tuple_type_constructor_1_0_parser_);
    exit_section_(b, m, UNORDERED_TUPLE_TYPE_CONSTRUCTOR, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER COLON type_expression
  public static boolean unordered_tuple_type_constructor_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unordered_tuple_type_constructor_entry")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENTIFIER, COLON);
    r = r && type_expression(b, l + 1);
    exit_section_(b, m, UNORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY, r);
    return r;
  }

  /* ********************************************************** */
  // Expression root: expression
  // Operator priority table:
  // 0: BINARY(multiplication_expression) BINARY(division_expression)
  // 1: BINARY(addition_expression) BINARY(subtraction_expression)
  // 2: BINARY(equals_expression)
  // 3: BINARY(less_than_expression) BINARY(less_than_equals_expression) BINARY(greater_than_expression) BINARY(greater_than_equals_expression)
  // 4: POSTFIX(postfix_call_expression) PREFIX(if_expression) PREFIX(is_undefined_expression) PREFIX(let_expression)
  //    ATOM(abstraction_constructor) ATOM(reference_expression) PREFIX(paren_expression) ATOM(tuple_constructor)
  //    ATOM(tuple_type_constructor) ATOM(int_literal)
  public static boolean expression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression")) return false;
    addVariant(b, "<expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expression>");
    r = tuple_constructor(b, l + 1);
    if (!r) r = if_expression(b, l + 1);
    if (!r) r = is_undefined_expression(b, l + 1);
    if (!r) r = let_expression(b, l + 1);
    if (!r) r = abstraction_constructor(b, l + 1);
    if (!r) r = reference_expression(b, l + 1);
    if (!r) r = paren_expression(b, l + 1);
    if (!r) r = tuple_type_constructor(b, l + 1);
    if (!r) r = int_literal(b, l + 1);
    p = r;
    r = r && expression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean expression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 0 && consumeTokenSmart(b, ASTERISK)) {
        r = expression(b, l, 0);
        exit_section_(b, l, m, MULTIPLICATION_EXPRESSION, r, true, null);
      }
      else if (g < 0 && consumeTokenSmart(b, SLASH)) {
        r = expression(b, l, 0);
        exit_section_(b, l, m, DIVISION_EXPRESSION, r, true, null);
      }
      else if (g < 1 && consumeTokenSmart(b, PLUS)) {
        r = expression(b, l, 1);
        exit_section_(b, l, m, ADDITION_EXPRESSION, r, true, null);
      }
      else if (g < 1 && consumeTokenSmart(b, MINUS)) {
        r = expression(b, l, 1);
        exit_section_(b, l, m, SUBTRACTION_EXPRESSION, r, true, null);
      }
      else if (g < 2 && consumeTokenSmart(b, EQUALS)) {
        r = expression(b, l, 2);
        exit_section_(b, l, m, EQUALS_EXPRESSION, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, LESS_THAN)) {
        r = expression(b, l, 3);
        exit_section_(b, l, m, LESS_THAN_EXPRESSION, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, LESS_THAN_EQUALS)) {
        r = expression(b, l, 3);
        exit_section_(b, l, m, LESS_THAN_EQUALS_EXPRESSION, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, GREATER_THAN)) {
        r = expression(b, l, 3);
        exit_section_(b, l, m, GREATER_THAN_EXPRESSION, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, GREATER_THAN_EQUALS)) {
        r = expression(b, l, 3);
        exit_section_(b, l, m, GREATER_THAN_EQUALS_EXPRESSION, r, true, null);
      }
      else if (g < 4 && tuple_constructor_raw(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, POSTFIX_CALL_EXPRESSION, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  // tuple_constructor_raw
  public static boolean tuple_constructor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_constructor")) return false;
    if (!nextTokenIsSmart(b, BRACE_LEFT, BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, TUPLE_CONSTRUCTOR, "<tuple constructor>");
    r = tuple_constructor_raw(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  public static boolean if_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_expression")) return false;
    if (!nextTokenIsSmart(b, IF_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, IF_KEYWORD);
    p = r;
    r = p && expression(b, l, -1);
    r = p && report_error_(b, paren_wrapped(b, l + 1, SigmaParser::if_expression_body)) && r;
    exit_section_(b, l, m, IF_EXPRESSION, r, p, null);
    return r || p;
  }

  public static boolean is_undefined_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_undefined_expression")) return false;
    if (!nextTokenIsSmart(b, IS_UNDEFINED_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, IS_UNDEFINED_KEYWORD);
    p = r;
    r = p && expression(b, l, -1);
    exit_section_(b, l, m, IS_UNDEFINED_EXPRESSION, r, p, null);
    return r || p;
  }

  public static boolean let_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_expression")) return false;
    if (!nextTokenIsSmart(b, LET_KEYWORD)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = let_expression_0(b, l + 1);
    p = r;
    r = p && expression(b, l, -1);
    exit_section_(b, l, m, LET_EXPRESSION, r, p, null);
    return r || p;
  }

  // LET_KEYWORD <<brace_wrapped let_expression_scope>> IN_KEYWORD
  private static boolean let_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_expression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, LET_KEYWORD);
    r = r && brace_wrapped(b, l + 1, SigmaParser::let_expression_scope);
    r = r && consumeToken(b, IN_KEYWORD);
    exit_section_(b, m, null, r);
    return r;
  }

  // generic_parameters_tuple? tuple_type_constructor (THIN_ARROW type_expression)? FAT_ARROW expression
  public static boolean abstraction_constructor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstraction_constructor")) return false;
    if (!nextTokenIsSmart(b, DASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = abstraction_constructor_0(b, l + 1);
    r = r && tuple_type_constructor(b, l + 1);
    r = r && abstraction_constructor_2(b, l + 1);
    r = r && consumeToken(b, FAT_ARROW);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, ABSTRACTION_CONSTRUCTOR, r);
    return r;
  }

  // generic_parameters_tuple?
  private static boolean abstraction_constructor_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstraction_constructor_0")) return false;
    generic_parameters_tuple(b, l + 1);
    return true;
  }

  // (THIN_ARROW type_expression)?
  private static boolean abstraction_constructor_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstraction_constructor_2")) return false;
    abstraction_constructor_2_0(b, l + 1);
    return true;
  }

  // THIN_ARROW type_expression
  private static boolean abstraction_constructor_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstraction_constructor_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, THIN_ARROW);
    r = r && type_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENTIFIER
  public static boolean reference_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reference_expression")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, IDENTIFIER);
    exit_section_(b, m, REFERENCE_EXPRESSION, r);
    return r;
  }

  public static boolean paren_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_expression")) return false;
    if (!nextTokenIsSmart(b, PAREN_LEFT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, PAREN_LEFT);
    p = r;
    r = p && expression(b, l, -1);
    r = p && report_error_(b, consumeToken(b, PAREN_RIGHT)) && r;
    exit_section_(b, l, m, PAREN_EXPRESSION, r, p, null);
    return r || p;
  }

  // ordered_tuple_type_constructor |
  //     unordered_tuple_type_constructor
  public static boolean tuple_type_constructor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_type_constructor")) return false;
    if (!nextTokenIsSmart(b, DASH)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, TUPLE_TYPE_CONSTRUCTOR, null);
    r = ordered_tuple_type_constructor(b, l + 1);
    if (!r) r = unordered_tuple_type_constructor(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // INT
  public static boolean int_literal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "int_literal")) return false;
    if (!nextTokenIsSmart(b, INT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, INT);
    exit_section_(b, m, INT_LITERAL, r);
    return r;
  }

  static final Parser expression_parser_ = (b, l) -> expression(b, l + 1, -1);

  private static final Parser generic_parameters_tuple_1_0_parser_ = list_$(SigmaParser::generic_parameter_declaration);
  private static final Parser ordered_tuple_constructor_0_0_parser_ = list_$(expression_parser_);
  private static final Parser ordered_tuple_type_constructor_1_0_parser_ = list_$(SigmaParser::ordered_tuple_type_constructor_entry);
  private static final Parser unordered_tuple_constructor_0_0_parser_ = list_$(SigmaParser::unordered_tuple_constructor_entry);
  private static final Parser unordered_tuple_type_constructor_1_0_parser_ = list_$(SigmaParser::unordered_tuple_type_constructor_entry);
}
