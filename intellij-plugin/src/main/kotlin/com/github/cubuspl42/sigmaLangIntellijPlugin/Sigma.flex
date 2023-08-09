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

WHITE_SPACE=[\ \n\t\f]
INT_LITERAL=[0-9]+
//SYMBOL=[a-zA-Z0-9]+
SPACE=' '
NEWLINE='\n'
//BACKTICK='`'
//COMMENT="//"[^\r\n]*

%%

{WHITE_SPACE}+                  { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

"const"                         { yybegin(YYINITIAL); return SigmaTypes.CONST_KEYWORD; }
[a-zA-Z][a-zA-Z0-9]*            { yybegin(YYINITIAL); return SigmaTypes.IDENTIFIER; }
//"("                           { yybegin(YYINITIAL); return TokenType.PAREN_LEFT; }
//")"                           { yybegin(YYINITIAL); return TokenType.PAREN_RIGHT; }
//"["                           { yybegin(YYINITIAL); return TokenType.BRACKET_LEFT; }
//"]"                           { yybegin(YYINITIAL); return TokenType.BRACKET_RIGHT; }
//"{"                           { yybegin(YYINITIAL); return TokenType.BRACE_LEFT; }
//"}"                           { yybegin(YYINITIAL); return TokenType.BRACE_RIGHT; }
"="                             { yybegin(YYINITIAL); return SigmaTypes.ASSIGN; }
{SPACE} | {NEWLINE}             { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }
//{COMMENT}                     { yybegin(YYINITIAL); return SigmaTypes.COMMENT; }
//"let"                         { yybegin(YYINITIAL); return SigmaTypes.LET_KEYWORD; }
//"in"                          { yybegin(YYINITIAL); return SigmaTypes.IN_KEYWORD; }
//"import"                      { yybegin(YYINITIAL); return SigmaTypes.IMPORT_KEYWORD; }
//"isUndefined"                 { yybegin(YYINITIAL); return SigmaTypes.ISUNDEFINED_KEYWORD; }
//"typeAlias"                   { yybegin(YYINITIAL); return SigmaTypes.TYPEALIAS_KEYWORD; }
//"class"                       { yybegin(YYINITIAL); return SigmaTypes.CLASS_KEYWORD; }
//"def"                         { yybegin(YYINITIAL); return SigmaTypes.DEF_KEYWORD; }
//"if"                          { yybegin(YYINITIAL); return SigmaTypes.IF_KEYWORD; }
//"namespace"                   { yybegin(YYINITIAL); return SigmaTypes.NAMESPACE_KEYWORD; }
//"%fields"                     { yybegin(YYINITIAL); return SigmaTypes.FIELDS_DIRECTIVE; }
//"%method"                     { yybegin(YYINITIAL); return SigmaTypes.METHOD_DIRECTIVE; }
//"%then"                       { yybegin(YYINITIAL); return SigmaTypes.THEN_DIRECTIVE; }
//"%else"                       { yybegin(YYINITIAL); return SigmaTypes.ELSE_DIRECTIVE; }
//"%"{IDENTIFIER}               { yybegin(YYINITIAL); return SigmaTypes.UNRECOGNIZED_DIRECTIVE; }
//"("<IDENTIFIER>{SPACE}*")"    { yybegin(YYINITIAL); return SigmaTypes.PAREN_EXPRESSION; }
//{IDENTIFIER}                  { yybegin(YYINITIAL); return SigmaTypes.IDENTIFIER; }
{INT_LITERAL}                   { yybegin(YYINITIAL); return SigmaTypes.INT_LITERAL; }
[^]                             { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER; }
