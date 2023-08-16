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
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
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

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(CALL_COMPACT_EXPRESSION, COMPACT_EXPRESSION, LITERAL_COMPACT_EXPRESSION, PAREN_COMPACT_EXPRESSION,
      REFERENCE_COMPACT_EXPRESSION, TUPLE_CONSTRUCTOR_COMPACT_EXPRESSION, TUPLE_TYPE_CONSTRUCTOR_COMPACT_EXPRESSION),
    create_token_set_(ADDITION_TERM, COMPACT_TERM, DIVISION_TERM, EQUALS_TERM,
      MULTIPLICATION_TERM, SUBTRACTION_TERM, TERM, UNARY_NEGATION_TERM),
  };

  /* ********************************************************** */
  // generic_parameters_tuple? tuple_type_constructor_compact_expression (THIN_ARROW type_expression)? FAT_ARROW expression
  public static boolean abstraction_constructor(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "abstraction_constructor")) return false;
    if (!nextTokenIs(b, DASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = abstraction_constructor_0(b, l + 1);
    r = r && tuple_type_constructor_compact_expression(b, l + 1);
    r = r && abstraction_constructor_2(b, l + 1);
    r = r && consumeToken(b, FAT_ARROW);
    r = r && expression(b, l + 1);
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
    r = consumeToken(b, THIN_ARROW);
    r = r && type_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

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
    r = r && expression(b, l + 1);
    exit_section_(b, m, CONSTANT_DEFINITION, r);
    return r;
  }

  /* ********************************************************** */
  // if_expression |
  //     is_undefined_expression |
  //     let_expression |
  //     abstraction_constructor | // Must be before tuple_constructor
  //     term
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION, "<expression>");
    r = if_expression(b, l + 1);
    if (!r) r = is_undefined_expression(b, l + 1);
    if (!r) r = let_expression(b, l + 1);
    if (!r) r = abstraction_constructor(b, l + 1);
    if (!r) r = term(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
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
  // IF_KEYWORD expression <<paren_wrapped if_expression_body>>
  public static boolean if_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_expression")) return false;
    if (!nextTokenIs(b, IF_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IF_KEYWORD);
    r = r && expression(b, l + 1);
    r = r && paren_wrapped(b, l + 1, SigmaParser::if_expression_body);
    exit_section_(b, m, IF_EXPRESSION, r);
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
    r = r && expression(b, l + 1);
    r = r && consumeTokens(b, 0, COMMA, ELSE_KEYWORD);
    r = r && expression(b, l + 1);
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
  // IS_UNDEFINED_KEYWORD expression
  public static boolean is_undefined_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "is_undefined_expression")) return false;
    if (!nextTokenIs(b, IS_UNDEFINED_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IS_UNDEFINED_KEYWORD);
    r = r && expression(b, l + 1);
    exit_section_(b, m, IS_UNDEFINED_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // LET_KEYWORD <<brace_wrapped let_expression_scope>> IN_KEYWORD expression
  public static boolean let_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_expression")) return false;
    if (!nextTokenIs(b, LET_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LET_KEYWORD);
    r = r && brace_wrapped(b, l + 1, SigmaParser::let_expression_scope);
    r = r && consumeToken(b, IN_KEYWORD);
    r = r && expression(b, l + 1);
    exit_section_(b, m, LET_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // <<list let_expression_scope_entry>>
  public static boolean let_expression_scope(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "let_expression_scope")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = list(b, l + 1, SigmaParser::let_expression_scope_entry);
    exit_section_(b, m, LET_EXPRESSION_SCOPE, r);
    return r;
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
    r = r && expression(b, l + 1);
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

  // <<p>> (COMMA <<p>>)* COMMA?
  static boolean list(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "list")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = _p.parse(b, l);
    r = r && list_1(b, l + 1, _p);
    r = r && list_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMA <<p>>)*
  private static boolean list_1(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "list_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!list_1_0(b, l + 1, _p)) break;
      if (!empty_element_parsed_guard_(b, "list_1", c)) break;
    }
    return true;
  }

  // COMMA <<p>>
  private static boolean list_1_0(PsiBuilder b, int l, Parser _p) {
    if (!recursion_guard_(b, l, "list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && _p.parse(b, l);
    exit_section_(b, m, null, r);
    return r;
  }

  // COMMA?
  private static boolean list_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_2")) return false;
    consumeToken(b, COMMA);
    return true;
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
  // IDENTIFIER type_annotation
  public static boolean ordered_tuple_type_constructor_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ordered_tuple_type_constructor_entry")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    r = r && type_annotation(b, l + 1);
    exit_section_(b, m, ORDERED_TUPLE_TYPE_CONSTRUCTOR_ENTRY, r);
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
  // namespace_body
  static boolean sigma_file(PsiBuilder b, int l) {
    return namespace_body(b, l + 1);
  }

  /* ********************************************************** */
  // ordered_tuple_constructor |
  //     unordered_tuple_constructor
  public static boolean tuple_constructor_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_constructor_expression")) return false;
    if (!nextTokenIs(b, "<tuple constructor expression>", BRACE_LEFT, BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TUPLE_CONSTRUCTOR_EXPRESSION, "<tuple constructor expression>");
    r = ordered_tuple_constructor(b, l + 1);
    if (!r) r = unordered_tuple_constructor(b, l + 1);
    exit_section_(b, l, m, r, false, null);
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
    r = expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // <<brace_wrapped
  //         <<list unordered_tuple_entry>>
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
  public static boolean unordered_tuple_entry(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unordered_tuple_entry")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENTIFIER, COLON);
    r = r && expression(b, l + 1);
    exit_section_(b, m, UNORDERED_TUPLE_ENTRY, r);
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
  // Expression root: compact_expression
  // Operator priority table:
  // 0: ATOM(reference_compact_expression)
  // 1: ATOM(tuple_constructor_compact_expression)
  // 2: ATOM(tuple_type_constructor_compact_expression)
  // 3: ATOM(literal_compact_expression)
  // 4: POSTFIX(call_compact_expression)
  // 5: ATOM(paren_compact_expression)
  public static boolean compact_expression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "compact_expression")) return false;
    addVariant(b, "<compact expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<compact expression>");
    r = reference_compact_expression(b, l + 1);
    if (!r) r = tuple_constructor_compact_expression(b, l + 1);
    if (!r) r = tuple_type_constructor_compact_expression(b, l + 1);
    if (!r) r = literal_compact_expression(b, l + 1);
    if (!r) r = paren_compact_expression(b, l + 1);
    p = r;
    r = r && compact_expression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean compact_expression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "compact_expression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 4 && tuple_constructor_expression(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, CALL_COMPACT_EXPRESSION, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  // IDENTIFIER
  public static boolean reference_compact_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reference_compact_expression")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, IDENTIFIER);
    exit_section_(b, m, REFERENCE_COMPACT_EXPRESSION, r);
    return r;
  }

  // tuple_constructor_expression
  public static boolean tuple_constructor_compact_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_constructor_compact_expression")) return false;
    if (!nextTokenIsSmart(b, BRACE_LEFT, BRACKET_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TUPLE_CONSTRUCTOR_COMPACT_EXPRESSION, "<tuple constructor compact expression>");
    r = tuple_constructor_expression(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ordered_tuple_type_constructor |
  //     unordered_tuple_type_constructor
  public static boolean tuple_type_constructor_compact_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tuple_type_constructor_compact_expression")) return false;
    if (!nextTokenIsSmart(b, DASH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ordered_tuple_type_constructor(b, l + 1);
    if (!r) r = unordered_tuple_type_constructor(b, l + 1);
    exit_section_(b, m, TUPLE_TYPE_CONSTRUCTOR_COMPACT_EXPRESSION, r);
    return r;
  }

  // INT_LITERAL
  public static boolean literal_compact_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_compact_expression")) return false;
    if (!nextTokenIsSmart(b, INT_LITERAL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, INT_LITERAL);
    exit_section_(b, m, LITERAL_COMPACT_EXPRESSION, r);
    return r;
  }

  // <<paren_wrapped expression>>
  public static boolean paren_compact_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "paren_compact_expression")) return false;
    if (!nextTokenIsSmart(b, PAREN_LEFT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = paren_wrapped(b, l + 1, SigmaParser::expression);
    exit_section_(b, m, PAREN_COMPACT_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // Expression root: term
  // Operator priority table:
  // 0: PREFIX(unary_negation_term)
  // 1: BINARY(multiplication_term) BINARY(division_term)
  // 2: BINARY(addition_term) BINARY(subtraction_term)
  // 3: BINARY(equals_term)
  // 4: ATOM(compact_term)
  public static boolean term(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "term")) return false;
    addVariant(b, "<term>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<term>");
    r = unary_negation_term(b, l + 1);
    if (!r) r = compact_term(b, l + 1);
    p = r;
    r = r && term_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean term_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "term_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 2 && consumeTokenSmart(b, MINUS)) {
        r = term(b, l, 2);
        exit_section_(b, l, m, SUBTRACTION_TERM, r, true, null);
      }
      else if (g < 1 && consumeTokenSmart(b, ASTERISK)) {
        r = term(b, l, 1);
        exit_section_(b, l, m, MULTIPLICATION_TERM, r, true, null);
      }
      else if (g < 1 && consumeTokenSmart(b, SLASH)) {
        r = term(b, l, 1);
        exit_section_(b, l, m, DIVISION_TERM, r, true, null);
      }
      else if (g < 2 && consumeTokenSmart(b, PLUS)) {
        r = term(b, l, 2);
        exit_section_(b, l, m, ADDITION_TERM, r, true, null);
      }
      else if (g < 3 && consumeTokenSmart(b, EQUALS)) {
        r = term(b, l, 3);
        exit_section_(b, l, m, EQUALS_TERM, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  public static boolean unary_negation_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unary_negation_term")) return false;
    if (!nextTokenIsSmart(b, MINUS)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, null);
    r = consumeTokenSmart(b, MINUS);
    p = r;
    r = p && term(b, l, 0);
    exit_section_(b, l, m, UNARY_NEGATION_TERM, r, p, null);
    return r || p;
  }

  // compact_expression
  public static boolean compact_term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "compact_term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPACT_TERM, "<compact term>");
    r = compact_expression(b, l + 1, -1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  private static final Parser generic_parameters_tuple_1_0_parser_ = list_$(SigmaParser::generic_parameter_declaration);
  private static final Parser ordered_tuple_constructor_0_0_parser_ = list_$(SigmaParser::expression);
  private static final Parser ordered_tuple_type_constructor_1_0_parser_ = list_$(SigmaParser::ordered_tuple_type_constructor_entry);
  private static final Parser unordered_tuple_constructor_0_0_parser_ = list_$(SigmaParser::unordered_tuple_entry);
  private static final Parser unordered_tuple_type_constructor_1_0_parser_ = list_$(SigmaParser::unordered_tuple_type_constructor_entry);
}
