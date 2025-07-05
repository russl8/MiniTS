// Generated from Expr.g4 by ANTLR 4.13.2

	package antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class ExprLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, INT_TYPE=12, ID=13, NUM=14, COMMENT=15, WS=16;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "INT_TYPE", "ID", "NUM", "COMMENT", "WS"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'class'", "'{'", "'}'", "'attributes'", "':'", "'operations'", 
			"'do'", "'end'", "'='", "'*'", "'+'", "'INT'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"INT_TYPE", "ID", "NUM", "COMMENT", "WS"
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


	public ExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Expr.g4"; }

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
		"\u0004\u0000\u0010z\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0005\fW\b\f\n\f\f\fZ\t"+
		"\f\u0001\r\u0001\r\u0003\r^\b\r\u0001\r\u0001\r\u0005\rb\b\r\n\r\f\re"+
		"\t\r\u0003\rg\b\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0005"+
		"\u000em\b\u000e\n\u000e\f\u000ep\t\u000e\u0001\u000e\u0001\u000e\u0001"+
		"\u000f\u0004\u000fu\b\u000f\u000b\u000f\f\u000fv\u0001\u000f\u0001\u000f"+
		"\u0000\u0000\u0010\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005"+
		"\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019"+
		"\r\u001b\u000e\u001d\u000f\u001f\u0010\u0001\u0000\u0006\u0002\u0000A"+
		"Zaz\u0004\u000009AZ__az\u0001\u000019\u0001\u000009\u0002\u0000\n\n\r"+
		"\r\u0003\u0000\t\n\r\r  \u007f\u0000\u0001\u0001\u0000\u0000\u0000\u0000"+
		"\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000"+
		"\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b"+
		"\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001"+
		"\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001"+
		"\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001"+
		"\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001"+
		"\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001"+
		"\u0000\u0000\u0000\u0001!\u0001\u0000\u0000\u0000\u0003\'\u0001\u0000"+
		"\u0000\u0000\u0005)\u0001\u0000\u0000\u0000\u0007+\u0001\u0000\u0000\u0000"+
		"\t6\u0001\u0000\u0000\u0000\u000b8\u0001\u0000\u0000\u0000\rC\u0001\u0000"+
		"\u0000\u0000\u000fF\u0001\u0000\u0000\u0000\u0011J\u0001\u0000\u0000\u0000"+
		"\u0013L\u0001\u0000\u0000\u0000\u0015N\u0001\u0000\u0000\u0000\u0017P"+
		"\u0001\u0000\u0000\u0000\u0019T\u0001\u0000\u0000\u0000\u001bf\u0001\u0000"+
		"\u0000\u0000\u001dh\u0001\u0000\u0000\u0000\u001ft\u0001\u0000\u0000\u0000"+
		"!\"\u0005c\u0000\u0000\"#\u0005l\u0000\u0000#$\u0005a\u0000\u0000$%\u0005"+
		"s\u0000\u0000%&\u0005s\u0000\u0000&\u0002\u0001\u0000\u0000\u0000\'(\u0005"+
		"{\u0000\u0000(\u0004\u0001\u0000\u0000\u0000)*\u0005}\u0000\u0000*\u0006"+
		"\u0001\u0000\u0000\u0000+,\u0005a\u0000\u0000,-\u0005t\u0000\u0000-.\u0005"+
		"t\u0000\u0000./\u0005r\u0000\u0000/0\u0005i\u0000\u000001\u0005b\u0000"+
		"\u000012\u0005u\u0000\u000023\u0005t\u0000\u000034\u0005e\u0000\u0000"+
		"45\u0005s\u0000\u00005\b\u0001\u0000\u0000\u000067\u0005:\u0000\u0000"+
		"7\n\u0001\u0000\u0000\u000089\u0005o\u0000\u00009:\u0005p\u0000\u0000"+
		":;\u0005e\u0000\u0000;<\u0005r\u0000\u0000<=\u0005a\u0000\u0000=>\u0005"+
		"t\u0000\u0000>?\u0005i\u0000\u0000?@\u0005o\u0000\u0000@A\u0005n\u0000"+
		"\u0000AB\u0005s\u0000\u0000B\f\u0001\u0000\u0000\u0000CD\u0005d\u0000"+
		"\u0000DE\u0005o\u0000\u0000E\u000e\u0001\u0000\u0000\u0000FG\u0005e\u0000"+
		"\u0000GH\u0005n\u0000\u0000HI\u0005d\u0000\u0000I\u0010\u0001\u0000\u0000"+
		"\u0000JK\u0005=\u0000\u0000K\u0012\u0001\u0000\u0000\u0000LM\u0005*\u0000"+
		"\u0000M\u0014\u0001\u0000\u0000\u0000NO\u0005+\u0000\u0000O\u0016\u0001"+
		"\u0000\u0000\u0000PQ\u0005I\u0000\u0000QR\u0005N\u0000\u0000RS\u0005T"+
		"\u0000\u0000S\u0018\u0001\u0000\u0000\u0000TX\u0007\u0000\u0000\u0000"+
		"UW\u0007\u0001\u0000\u0000VU\u0001\u0000\u0000\u0000WZ\u0001\u0000\u0000"+
		"\u0000XV\u0001\u0000\u0000\u0000XY\u0001\u0000\u0000\u0000Y\u001a\u0001"+
		"\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000[g\u00050\u0000\u0000\\^\u0005"+
		"-\u0000\u0000]\\\u0001\u0000\u0000\u0000]^\u0001\u0000\u0000\u0000^_\u0001"+
		"\u0000\u0000\u0000_c\u0007\u0002\u0000\u0000`b\u0007\u0003\u0000\u0000"+
		"a`\u0001\u0000\u0000\u0000be\u0001\u0000\u0000\u0000ca\u0001\u0000\u0000"+
		"\u0000cd\u0001\u0000\u0000\u0000dg\u0001\u0000\u0000\u0000ec\u0001\u0000"+
		"\u0000\u0000f[\u0001\u0000\u0000\u0000f]\u0001\u0000\u0000\u0000g\u001c"+
		"\u0001\u0000\u0000\u0000hi\u0005-\u0000\u0000ij\u0005-\u0000\u0000jn\u0001"+
		"\u0000\u0000\u0000km\b\u0004\u0000\u0000lk\u0001\u0000\u0000\u0000mp\u0001"+
		"\u0000\u0000\u0000nl\u0001\u0000\u0000\u0000no\u0001\u0000\u0000\u0000"+
		"oq\u0001\u0000\u0000\u0000pn\u0001\u0000\u0000\u0000qr\u0006\u000e\u0000"+
		"\u0000r\u001e\u0001\u0000\u0000\u0000su\u0007\u0005\u0000\u0000ts\u0001"+
		"\u0000\u0000\u0000uv\u0001\u0000\u0000\u0000vt\u0001\u0000\u0000\u0000"+
		"vw\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000xy\u0006\u000f\u0000"+
		"\u0000y \u0001\u0000\u0000\u0000\u0007\u0000X]cfnv\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}