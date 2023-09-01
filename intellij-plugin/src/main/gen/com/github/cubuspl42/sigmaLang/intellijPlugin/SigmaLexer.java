// Generated by JFlex 1.9.1 http://jflex.de/  (tweaked for IntelliJ platform)
// source: Sigma.flex

package com.github.cubuspl42.sigmaLang.intellijPlugin;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.github.cubuspl42.sigmaLang.intellijPlugin.psi.SigmaTypes;
import com.intellij.psi.TokenType;


class SigmaLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = {
     0, 0
  };

  /**
   * Top-level table for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_TOP = zzUnpackcmap_top();

  private static final String ZZ_CMAP_TOP_PACKED_0 =
    "\1\0\u10ff\u0100";

  private static int [] zzUnpackcmap_top() {
    int [] result = new int[4352];
    int offset = 0;
    offset = zzUnpackcmap_top(ZZ_CMAP_TOP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_top(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Second-level tables for translating characters to character classes
   */
  private static final int [] ZZ_CMAP_BLOCKS = zzUnpackcmap_blocks();

  private static final String ZZ_CMAP_BLOCKS_PACKED_0 =
    "\11\0\1\1\1\2\1\0\1\1\1\3\22\0\1\1"+
    "\4\0\1\4\2\0\1\5\1\6\1\7\1\10\1\11"+
    "\1\12\1\13\1\14\12\15\1\16\1\0\1\17\1\20"+
    "\1\21\2\0\24\22\1\23\5\22\1\24\1\0\1\25"+
    "\1\26\2\0\1\27\1\22\1\30\1\31\1\32\1\33"+
    "\1\22\1\34\1\35\2\22\1\36\1\37\1\40\1\41"+
    "\1\42\2\22\1\43\1\44\6\22\1\45\1\0\1\46"+
    "\u0182\0";

  private static int [] zzUnpackcmap_blocks() {
    int [] result = new int[512];
    int offset = 0;
    offset = zzUnpackcmap_blocks(ZZ_CMAP_BLOCKS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackcmap_blocks(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /**
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\1\1\3\1\4\1\5\1\6"+
    "\1\7\1\10\1\1\1\11\1\12\1\13\1\14\1\15"+
    "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\6\0"+
    "\1\25\1\0\1\26\1\27\1\30\1\31\1\32\2\0"+
    "\1\33\1\34\4\0\1\35\3\0\1\36\3\0\1\37"+
    "\2\0\1\40\1\41\11\0\1\42\1\0\1\43";

  private static int [] zzUnpackAction() {
    int [] result = new int[69];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\47\0\116\0\165\0\47\0\47\0\47\0\47"+
    "\0\47\0\234\0\303\0\352\0\u0111\0\47\0\u0138\0\u015f"+
    "\0\u0186\0\u01ad\0\47\0\47\0\47\0\47\0\47\0\u01d4"+
    "\0\u01fb\0\u0222\0\u0249\0\u0270\0\u0297\0\47\0\u02be\0\u02e5"+
    "\0\47\0\47\0\47\0\47\0\u030c\0\u0333\0\47\0\47"+
    "\0\u035a\0\u0381\0\u03a8\0\u03cf\0\47\0\u03f6\0\u041d\0\u0444"+
    "\0\47\0\u046b\0\u0492\0\u04b9\0\47\0\u04e0\0\u0507\0\47"+
    "\0\47\0\u052e\0\u0555\0\u057c\0\u05a3\0\u05ca\0\u05f1\0\u0618"+
    "\0\u063f\0\u0666\0\47\0\u068d\0\47";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[69];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length() - 1;
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /**
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpacktrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\2\3\1\2\1\4\1\5\1\6\1\7\1\10"+
    "\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20"+
    "\1\21\2\22\1\23\1\24\1\25\16\22\1\26\1\27"+
    "\50\0\2\3\74\0\1\30\1\0\1\31\2\0\1\32"+
    "\1\33\1\0\1\34\3\0\1\35\23\0\1\36\40\0"+
    "\1\37\47\0\1\40\47\0\1\15\51\0\1\41\46\0"+
    "\1\42\1\43\45\0\1\44\43\0\1\22\4\0\2\22"+
    "\3\0\16\22\43\0\1\45\43\0\1\46\43\0\1\47"+
    "\4\0\1\50\2\0\1\51\35\0\1\52\43\0\1\53"+
    "\53\0\1\54\25\0\1\55\33\0\2\40\2\0\43\40"+
    "\40\0\1\56\51\0\1\57\26\0\1\60\67\0\1\61"+
    "\41\0\1\62\41\0\1\63\57\0\1\64\35\0\1\65"+
    "\54\0\1\66\40\0\1\67\54\0\1\70\52\0\1\71"+
    "\33\0\1\72\60\0\1\73\35\0\1\74\56\0\1\75"+
    "\37\0\1\76\42\0\1\77\54\0\1\100\41\0\1\101"+
    "\56\0\1\102\40\0\1\103\46\0\1\104\45\0\1\105"+
    "\15\0";

  private static int [] zzUnpacktrans() {
    int [] result = new int[1716];
    int offset = 0;
    offset = zzUnpacktrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpacktrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String[] ZZ_ERROR_MSG = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state {@code aState}
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\2\1\5\11\4\1\1\11\4\1\5\11"+
    "\6\0\1\11\1\0\1\1\4\11\2\0\2\11\4\0"+
    "\1\11\3\0\1\11\3\0\1\11\2\0\2\11\11\0"+
    "\1\11\1\0\1\11";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[69];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** Number of newlines encountered up to the start of the matched text. */
  @SuppressWarnings("unused")
  private int yyline;

  /** Number of characters from the last newline up to the start of the matched text. */
  @SuppressWarnings("unused")
  protected int yycolumn;

  /** Number of characters up to the start of the matched text. */
  @SuppressWarnings("unused")
  private long yychar;

  /** Whether the scanner is currently at the beginning of a line. */
  @SuppressWarnings("unused")
  private boolean zzAtBOL = true;

  /** Whether the user-EOF-code has already been executed. */
  private boolean zzEOFDone;


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  SigmaLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** Returns the maximum size of the scanner buffer, which limits the size of tokens. */
  private int zzMaxBufferLen() {
    return Integer.MAX_VALUE;
  }

  /**  Whether the scanner buffer can grow to accommodate a larger token. */
  private boolean zzCanGrow() {
    return true;
  }

  /**
   * Translates raw input code points to DFA table row
   */
  private static int zzCMap(int input) {
    int offset = input & 255;
    return offset == input ? ZZ_CMAP_BLOCKS[offset] : ZZ_CMAP_BLOCKS[ZZ_CMAP_TOP[input >> 8] | offset];
  }

  public final int getTokenStart() {
    return zzStartRead;
  }

  public final int getTokenEnd() {
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end, int initialState) {
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      {@code false}, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position {@code pos} from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Contains user EOF-code, which will be executed exactly once,
   * when the end of file is reached
   */
  private void zzDoEOF() {
    if (!zzEOFDone) {
      zzEOFDone = true;
    
    }
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException
  {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + zzCMap(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
            zzDoEOF();
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1:
            { yybegin(YYINITIAL); return TokenType.BAD_CHARACTER;
            }
          // fall through
          case 36: break;
          case 2:
            { yybegin(YYINITIAL); return TokenType.WHITE_SPACE;
            }
          // fall through
          case 37: break;
          case 3:
            { yybegin(YYINITIAL); return SigmaTypes.PAREN_LEFT;
            }
          // fall through
          case 38: break;
          case 4:
            { yybegin(YYINITIAL); return SigmaTypes.PAREN_RIGHT;
            }
          // fall through
          case 39: break;
          case 5:
            { yybegin(YYINITIAL); return SigmaTypes.ASTERISK;
            }
          // fall through
          case 40: break;
          case 6:
            { yybegin(YYINITIAL); return SigmaTypes.PLUS;
            }
          // fall through
          case 41: break;
          case 7:
            { yybegin(YYINITIAL); return SigmaTypes.COMMA;
            }
          // fall through
          case 42: break;
          case 8:
            { yybegin(YYINITIAL); return SigmaTypes.MINUS;
            }
          // fall through
          case 43: break;
          case 9:
            { yybegin(YYINITIAL); return SigmaTypes.SLASH;
            }
          // fall through
          case 44: break;
          case 10:
            { yybegin(YYINITIAL); return SigmaTypes.INT;
            }
          // fall through
          case 45: break;
          case 11:
            { yybegin(YYINITIAL); return SigmaTypes.COLON;
            }
          // fall through
          case 46: break;
          case 12:
            { yybegin(YYINITIAL); return SigmaTypes.LESS_THAN;
            }
          // fall through
          case 47: break;
          case 13:
            { yybegin(YYINITIAL); return SigmaTypes.ASSIGN;
            }
          // fall through
          case 48: break;
          case 14:
            { yybegin(YYINITIAL); return SigmaTypes.GREATER_THAN;
            }
          // fall through
          case 49: break;
          case 15:
            { yybegin(YYINITIAL); return SigmaTypes.IDENTIFIER;
            }
          // fall through
          case 50: break;
          case 16:
            { yybegin(YYINITIAL); return SigmaTypes.BRACKET_LEFT;
            }
          // fall through
          case 51: break;
          case 17:
            { yybegin(YYINITIAL); return SigmaTypes.BRACKET_RIGHT;
            }
          // fall through
          case 52: break;
          case 18:
            { yybegin(YYINITIAL); return SigmaTypes.DASH;
            }
          // fall through
          case 53: break;
          case 19:
            { yybegin(YYINITIAL); return SigmaTypes.BRACE_LEFT;
            }
          // fall through
          case 54: break;
          case 20:
            { yybegin(YYINITIAL); return SigmaTypes.BRACE_RIGHT;
            }
          // fall through
          case 55: break;
          case 21:
            { yybegin(YYINITIAL); return SigmaTypes.THIN_ARROW;
            }
          // fall through
          case 56: break;
          case 22:
            { yybegin(YYINITIAL); return SigmaTypes.LINE_COMMENT;
            }
          // fall through
          case 57: break;
          case 23:
            { yybegin(YYINITIAL); return SigmaTypes.LESS_THAN_EQUALS;
            }
          // fall through
          case 58: break;
          case 24:
            { yybegin(YYINITIAL); return SigmaTypes.EQUALS;
            }
          // fall through
          case 59: break;
          case 25:
            { yybegin(YYINITIAL); return SigmaTypes.FAT_ARROW;
            }
          // fall through
          case 60: break;
          case 26:
            { yybegin(YYINITIAL); return SigmaTypes.GREATER_THAN_EQUALS;
            }
          // fall through
          case 61: break;
          case 27:
            { yybegin(YYINITIAL); return SigmaTypes.IF_KEYWORD;
            }
          // fall through
          case 62: break;
          case 28:
            { yybegin(YYINITIAL); return SigmaTypes.IN_KEYWORD;
            }
          // fall through
          case 63: break;
          case 29:
            { yybegin(YYINITIAL); return SigmaTypes.ELLIPSIS;
            }
          // fall through
          case 64: break;
          case 30:
            { yybegin(YYINITIAL); return SigmaTypes.LET_KEYWORD;
            }
          // fall through
          case 65: break;
          case 31:
            { yybegin(YYINITIAL); return SigmaTypes.ELSE_KEYWORD;
            }
          // fall through
          case 66: break;
          case 32:
            { yybegin(YYINITIAL); return SigmaTypes.THEN_KEYWORD;
            }
          // fall through
          case 67: break;
          case 33:
            { yybegin(YYINITIAL); return SigmaTypes.CONST_KEYWORD;
            }
          // fall through
          case 68: break;
          case 34:
            { yybegin(YYINITIAL); return SigmaTypes.NAMESPACE_KEYWORD;
            }
          // fall through
          case 69: break;
          case 35:
            { yybegin(YYINITIAL); return SigmaTypes.IS_UNDEFINED_KEYWORD;
            }
          // fall through
          case 70: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
