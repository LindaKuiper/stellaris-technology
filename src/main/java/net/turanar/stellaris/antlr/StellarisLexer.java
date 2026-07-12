// Generated from Stellaris.g4 by ANTLR 4.7.1
package net.turanar.stellaris.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class StellarisLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, BOOLEAN=3, VARIABLE=4, SPECIFIER=5, NUMBER=6, DATE=7, 
		BAREWORD=8, STRING=9, WS=10, LINE_COMMENT=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "BOOLEAN", "VARIABLE", "SPECIFIER", "NUMBER", "DATE", 
		"BAREWORD", "STRING", "WS", "LINE_COMMENT"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'{'", "'}'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "BOOLEAN", "VARIABLE", "SPECIFIER", "NUMBER", "DATE", 
		"BAREWORD", "STRING", "WS", "LINE_COMMENT"
	};
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


	public StellarisLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Stellaris.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\r\u0098\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\3\2\3\2\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\5\4,\n\4\3\5\3\5\3\5\7\5\61\n\5\f\5\16\5\64\13\5\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6B\n\6\3\7\5\7E\n\7\3\7"+
		"\6\7H\n\7\r\7\16\7I\3\7\3\7\5\7N\n\7\3\7\6\7Q\n\7\r\7\16\7R\3\7\3\7\6"+
		"\7W\n\7\r\7\16\7X\3\7\5\7\\\n\7\3\7\6\7_\n\7\r\7\16\7`\5\7c\n\7\3\b\6"+
		"\bf\n\b\r\b\16\bg\3\b\3\b\6\bl\n\b\r\b\16\bm\3\b\3\b\6\br\n\b\r\b\16\b"+
		"s\3\t\3\t\7\tx\n\t\f\t\16\t{\13\t\3\t\5\t~\n\t\3\n\3\n\7\n\u0082\n\n\f"+
		"\n\16\n\u0085\13\n\3\n\3\n\3\13\6\13\u008a\n\13\r\13\16\13\u008b\3\13"+
		"\3\13\3\f\3\f\7\f\u0092\n\f\f\f\16\f\u0095\13\f\3\f\3\f\2\2\r\3\3\5\4"+
		"\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\3\2\n\4\2C\\c|\b\2\'\'/\60"+
		"\62;C\\aac|\4\2>>@@\3\2\62;\t\2\'\'))/<B\\aac|~~\3\2$$\5\2\13\f\17\17"+
		"\"\"\4\2\f\f\17\17\2\u00b2\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2"+
		"\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2"+
		"\25\3\2\2\2\2\27\3\2\2\2\3\31\3\2\2\2\5\33\3\2\2\2\7+\3\2\2\2\t-\3\2\2"+
		"\2\13A\3\2\2\2\rb\3\2\2\2\17e\3\2\2\2\21u\3\2\2\2\23\177\3\2\2\2\25\u0089"+
		"\3\2\2\2\27\u008f\3\2\2\2\31\32\7}\2\2\32\4\3\2\2\2\33\34\7\177\2\2\34"+
		"\6\3\2\2\2\35\36\7{\2\2\36\37\7g\2\2\37,\7u\2\2 !\7p\2\2!,\7q\2\2\"#\7"+
		"v\2\2#$\7t\2\2$%\7w\2\2%,\7g\2\2&\'\7h\2\2\'(\7c\2\2()\7n\2\2)*\7u\2\2"+
		"*,\7g\2\2+\35\3\2\2\2+ \3\2\2\2+\"\3\2\2\2+&\3\2\2\2,\b\3\2\2\2-.\7B\2"+
		"\2.\62\t\2\2\2/\61\t\3\2\2\60/\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62"+
		"\63\3\2\2\2\63\n\3\2\2\2\64\62\3\2\2\2\65B\7?\2\2\66\67\7>\2\2\67B\7@"+
		"\2\28B\t\4\2\29:\7>\2\2:B\7?\2\2;<\7@\2\2<B\7?\2\2=>\7#\2\2>B\7?\2\2?"+
		"@\7A\2\2@B\7?\2\2A\65\3\2\2\2A\66\3\2\2\2A8\3\2\2\2A9\3\2\2\2A;\3\2\2"+
		"\2A=\3\2\2\2A?\3\2\2\2B\f\3\2\2\2CE\7/\2\2DC\3\2\2\2DE\3\2\2\2EG\3\2\2"+
		"\2FH\t\5\2\2GF\3\2\2\2HI\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JK\3\2\2\2Kc\7\'\2"+
		"\2LN\7/\2\2ML\3\2\2\2MN\3\2\2\2NP\3\2\2\2OQ\t\5\2\2PO\3\2\2\2QR\3\2\2"+
		"\2RP\3\2\2\2RS\3\2\2\2ST\3\2\2\2TV\7\60\2\2UW\t\5\2\2VU\3\2\2\2WX\3\2"+
		"\2\2XV\3\2\2\2XY\3\2\2\2Yc\3\2\2\2Z\\\7/\2\2[Z\3\2\2\2[\\\3\2\2\2\\^\3"+
		"\2\2\2]_\t\5\2\2^]\3\2\2\2_`\3\2\2\2`^\3\2\2\2`a\3\2\2\2ac\3\2\2\2bD\3"+
		"\2\2\2bM\3\2\2\2b[\3\2\2\2c\16\3\2\2\2df\t\5\2\2ed\3\2\2\2fg\3\2\2\2g"+
		"e\3\2\2\2gh\3\2\2\2hi\3\2\2\2ik\7\60\2\2jl\t\5\2\2kj\3\2\2\2lm\3\2\2\2"+
		"mk\3\2\2\2mn\3\2\2\2no\3\2\2\2oq\7\60\2\2pr\t\5\2\2qp\3\2\2\2rs\3\2\2"+
		"\2sq\3\2\2\2st\3\2\2\2t\20\3\2\2\2uy\t\2\2\2vx\t\6\2\2wv\3\2\2\2x{\3\2"+
		"\2\2yw\3\2\2\2yz\3\2\2\2z}\3\2\2\2{y\3\2\2\2|~\7A\2\2}|\3\2\2\2}~\3\2"+
		"\2\2~\22\3\2\2\2\177\u0083\7$\2\2\u0080\u0082\n\7\2\2\u0081\u0080\3\2"+
		"\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2\2\u0084"+
		"\u0086\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0087\7$\2\2\u0087\24\3\2\2\2"+
		"\u0088\u008a\t\b\2\2\u0089\u0088\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u0089"+
		"\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008e\b\13\2\2"+
		"\u008e\26\3\2\2\2\u008f\u0093\7%\2\2\u0090\u0092\n\t\2\2\u0091\u0090\3"+
		"\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094"+
		"\u0096\3\2\2\2\u0095\u0093\3\2\2\2\u0096\u0097\b\f\3\2\u0097\30\3\2\2"+
		"\2\26\2+\62ADIMRX[`bgmsy}\u0083\u008b\u0093\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}