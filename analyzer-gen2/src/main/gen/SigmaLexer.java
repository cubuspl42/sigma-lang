// Generated from /Users/jakub/Projects/sigma-lang/analyzer-gen2/src/main/antlr/SigmaLexer.g4 by ANTLR 4.13.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class SigmaLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LetKeyword=1, InKeyword=2, ValKeyword=3, FunKeyword=4, LeftBrace=5, RightBrace=6, 
		Dash=7, Colon=8, Comma=9, Equals=10, FatArrow=11, Identifier=12, IntLiteral=13, 
		Whitespace=14;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"LetKeyword", "InKeyword", "ValKeyword", "FunKeyword", "LeftBrace", "RightBrace", 
			"Dash", "Colon", "Comma", "Equals", "FatArrow", "Identifier", "IntLiteral", 
			"Whitespace"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'%let'", "'%in'", "'%val'", "'%fun'", "'{'", "'}'", "'^'", "':'", 
			"','", "'='", "'=>'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LetKeyword", "InKeyword", "ValKeyword", "FunKeyword", "LeftBrace", 
			"RightBrace", "Dash", "Colon", "Comma", "Equals", "FatArrow", "Identifier", 
			"IntLiteral", "Whitespace"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SigmaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SigmaLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u000eO\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0001\u000b"+
		"\u0001\u000b\u0005\u000bB\b\u000b\n\u000b\f\u000bE\t\u000b\u0001\f\u0004"+
		"\fH\b\f\u000b\f\f\fI\u0001\r\u0001\r\u0001\r\u0001\r\u0000\u0000\u000e"+
		"\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006\r"+
		"\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e"+
		"\u0001\u0000\u0004\u0002\u0000AZaz\u0003\u000009AZaz\u0001\u000009\u0002"+
		"\u0000\n\n  P\u0000\u0001\u0001\u0000\u0000\u0000\u0000\u0003\u0001\u0000"+
		"\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000\u0007\u0001\u0000"+
		"\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b\u0001\u0000\u0000"+
		"\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001\u0000\u0000\u0000"+
		"\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001\u0000\u0000\u0000"+
		"\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001\u0000\u0000\u0000"+
		"\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001\u0000\u0000\u0000"+
		"\u0001\u001d\u0001\u0000\u0000\u0000\u0003\"\u0001\u0000\u0000\u0000\u0005"+
		"&\u0001\u0000\u0000\u0000\u0007+\u0001\u0000\u0000\u0000\t0\u0001\u0000"+
		"\u0000\u0000\u000b2\u0001\u0000\u0000\u0000\r4\u0001\u0000\u0000\u0000"+
		"\u000f6\u0001\u0000\u0000\u0000\u00118\u0001\u0000\u0000\u0000\u0013:"+
		"\u0001\u0000\u0000\u0000\u0015<\u0001\u0000\u0000\u0000\u0017?\u0001\u0000"+
		"\u0000\u0000\u0019G\u0001\u0000\u0000\u0000\u001bK\u0001\u0000\u0000\u0000"+
		"\u001d\u001e\u0005%\u0000\u0000\u001e\u001f\u0005l\u0000\u0000\u001f "+
		"\u0005e\u0000\u0000 !\u0005t\u0000\u0000!\u0002\u0001\u0000\u0000\u0000"+
		"\"#\u0005%\u0000\u0000#$\u0005i\u0000\u0000$%\u0005n\u0000\u0000%\u0004"+
		"\u0001\u0000\u0000\u0000&\'\u0005%\u0000\u0000\'(\u0005v\u0000\u0000("+
		")\u0005a\u0000\u0000)*\u0005l\u0000\u0000*\u0006\u0001\u0000\u0000\u0000"+
		"+,\u0005%\u0000\u0000,-\u0005f\u0000\u0000-.\u0005u\u0000\u0000./\u0005"+
		"n\u0000\u0000/\b\u0001\u0000\u0000\u000001\u0005{\u0000\u00001\n\u0001"+
		"\u0000\u0000\u000023\u0005}\u0000\u00003\f\u0001\u0000\u0000\u000045\u0005"+
		"^\u0000\u00005\u000e\u0001\u0000\u0000\u000067\u0005:\u0000\u00007\u0010"+
		"\u0001\u0000\u0000\u000089\u0005,\u0000\u00009\u0012\u0001\u0000\u0000"+
		"\u0000:;\u0005=\u0000\u0000;\u0014\u0001\u0000\u0000\u0000<=\u0005=\u0000"+
		"\u0000=>\u0005>\u0000\u0000>\u0016\u0001\u0000\u0000\u0000?C\u0007\u0000"+
		"\u0000\u0000@B\u0007\u0001\u0000\u0000A@\u0001\u0000\u0000\u0000BE\u0001"+
		"\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000CD\u0001\u0000\u0000\u0000"+
		"D\u0018\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000FH\u0007\u0002"+
		"\u0000\u0000GF\u0001\u0000\u0000\u0000HI\u0001\u0000\u0000\u0000IG\u0001"+
		"\u0000\u0000\u0000IJ\u0001\u0000\u0000\u0000J\u001a\u0001\u0000\u0000"+
		"\u0000KL\u0007\u0003\u0000\u0000LM\u0001\u0000\u0000\u0000MN\u0006\r\u0000"+
		"\u0000N\u001c\u0001\u0000\u0000\u0000\u0003\u0000CI\u0001\u0006\u0000"+
		"\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}