package com.github.cubuspl42.sigmaLangIntellijPlugin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.github.cubuspl42.sigmaLangIntellijPlugin.psi.SigmaTypes;
import com.intellij.psi.TokenType;

%%
%class SigmaLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

%%

[\ \n\t\f]+                   { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

("//")[^\r\n]*                { yybegin(YYINITIAL); return SigmaTypes.LINE_COMMENT; }

","                           { yybegin(YYINITIAL); return SigmaTypes.COMMA; }
"="                           { yybegin(YYINITIAL); return SigmaTypes.ASSIGN; }
":"                           { yybegin(YYINITIAL); return SigmaTypes.COLON; }
"^"                           { yybegin(YYINITIAL); return SigmaTypes.DASH; }
"->"                          { yybegin(YYINITIAL); return SigmaTypes.THIN_ARROW; }
"=>"                          { yybegin(YYINITIAL); return SigmaTypes.FAT_ARROW; }

"("                           { yybegin(YYINITIAL); return SigmaTypes.PAREN_LEFT; }
")"                           { yybegin(YYINITIAL); return SigmaTypes.PAREN_RIGHT; }
"["                           { yybegin(YYINITIAL); return SigmaTypes.BRACKET_LEFT; }
"]"                           { yybegin(YYINITIAL); return SigmaTypes.BRACKET_RIGHT; }
"{"                           { yybegin(YYINITIAL); return SigmaTypes.BRACE_LEFT; }
"}"                           { yybegin(YYINITIAL); return SigmaTypes.BRACE_RIGHT; }

"*"                           { yybegin(YYINITIAL); return SigmaTypes.ASTERISK; }
"/"                           { yybegin(YYINITIAL); return SigmaTypes.SLASH; }
"+"                           { yybegin(YYINITIAL); return SigmaTypes.PLUS; }
"-"                           { yybegin(YYINITIAL); return SigmaTypes.MINUS; }

"=="                          { yybegin(YYINITIAL); return SigmaTypes.EQUALS; }
"<"                           { yybegin(YYINITIAL); return SigmaTypes.LESS_THAN; }
"<="                          { yybegin(YYINITIAL); return SigmaTypes.LESS_THAN_EQUALS; }
">"                           { yybegin(YYINITIAL); return SigmaTypes.GREATER_THAN; }
">="                          { yybegin(YYINITIAL); return SigmaTypes.GREATER_THAN_EQUALS; }

[a-zA-Z][a-zA-Z0-9]*          { yybegin(YYINITIAL); return SigmaTypes.IDENTIFIER; }

"%if"                         { yybegin(YYINITIAL); return SigmaTypes.IF_KEYWORD; }
"%then"                       { yybegin(YYINITIAL); return SigmaTypes.THEN_KEYWORD; }
"%else"                       { yybegin(YYINITIAL); return SigmaTypes.ELSE_KEYWORD; }
"%isUndefined"                { yybegin(YYINITIAL); return SigmaTypes.IS_UNDEFINED_KEYWORD; }
"%let"                        { yybegin(YYINITIAL); return SigmaTypes.LET_KEYWORD; }
"%in"                         { yybegin(YYINITIAL); return SigmaTypes.IN_KEYWORD; }
"%namespace"                  { yybegin(YYINITIAL); return SigmaTypes.NAMESPACE_KEYWORD; }
"%const"                      { yybegin(YYINITIAL); return SigmaTypes.CONST_KEYWORD; }

[0-9]+                        { yybegin(YYINITIAL); return SigmaTypes.INT_LITERAL; }
[^]                           { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
