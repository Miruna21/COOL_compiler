// Generated from /home/miruna/Desktop/CPL/Tema2/Schelet + checker - IntelliJ/Tema2/src/cool/lexer/CoolLexer.g4 by ANTLR 4.9.1

    package cool.lexer;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CoolLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ERROR=1, CLASS=2, INHERITS=3, NEW=4, CASE=5, OF=6, ESAC=7, IF=8, THEN=9, 
		ELSE=10, FI=11, WHILE=12, LOOP=13, POOL=14, LET=15, IN=16, ISVOID=17, 
		NOT=18, TRUE=19, FALSE=20, AT=21, IMPLICATION=22, DOT=23, COMMA=24, COLON=25, 
		SEMICOLON=26, OPEN_BRACKET=27, CLOSE_BRACKET=28, OPEN_BRACE=29, CLOSE_BRACE=30, 
		PLUS=31, MINUS=32, MULT=33, DIV=34, NEGATE=35, EQUAL=36, LESS=37, LESS_EQUAL=38, 
		ASSIGN=39, INT=40, ID=41, TYPE=42, STRING=43, LINE_COMMENT=44, UNMATCHED_COMMENT=45, 
		BLOCK_COMMENT=46, WS=47, INVALID_CHARACTER=48;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"CLASS", "INHERITS", "NEW", "CASE", "OF", "ESAC", "IF", "THEN", "ELSE", 
			"FI", "WHILE", "LOOP", "POOL", "LET", "IN", "ISVOID", "NOT", "TRUE", 
			"FALSE", "AT", "IMPLICATION", "DOT", "COMMA", "COLON", "SEMICOLON", "OPEN_BRACKET", 
			"CLOSE_BRACKET", "OPEN_BRACE", "CLOSE_BRACE", "PLUS", "MINUS", "MULT", 
			"DIV", "NEGATE", "EQUAL", "LESS", "LESS_EQUAL", "ASSIGN", "DIGIT", "INT", 
			"LETTER", "LOWER_LETTER", "UPPER_LETTER", "ID", "TYPE", "STRING", "NEW_LINE", 
			"LINE_COMMENT", "UNMATCHED_COMMENT", "BLOCK_COMMENT", "WS", "INVALID_CHARACTER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "'@'", "'=>'", 
			"'.'", "','", "':'", "';'", "'('", "')'", "'{'", "'}'", "'+'", "'-'", 
			"'*'", "'/'", "'~'", "'='", "'<'", "'<='", "'<-'", null, null, null, 
			null, null, "'*)'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ERROR", "CLASS", "INHERITS", "NEW", "CASE", "OF", "ESAC", "IF", 
			"THEN", "ELSE", "FI", "WHILE", "LOOP", "POOL", "LET", "IN", "ISVOID", 
			"NOT", "TRUE", "FALSE", "AT", "IMPLICATION", "DOT", "COMMA", "COLON", 
			"SEMICOLON", "OPEN_BRACKET", "CLOSE_BRACKET", "OPEN_BRACE", "CLOSE_BRACE", 
			"PLUS", "MINUS", "MULT", "DIV", "NEGATE", "EQUAL", "LESS", "LESS_EQUAL", 
			"ASSIGN", "INT", "ID", "TYPE", "STRING", "LINE_COMMENT", "UNMATCHED_COMMENT", 
			"BLOCK_COMMENT", "WS", "INVALID_CHARACTER"
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


	    private void raiseError(String msg) {
	        setText(msg);
	        setType(ERROR);
	    }


	public CoolLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CoolLexer.g4"; }

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

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 45:
			STRING_action((RuleContext)_localctx, actionIndex);
			break;
		case 48:
			UNMATCHED_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 49:
			BLOCK_COMMENT_action((RuleContext)_localctx, actionIndex);
			break;
		case 51:
			INVALID_CHARACTER_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void STRING_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			 raiseError(" String contains null character "); 
			break;
		case 1:

			                            int len = getText().length();
			                            String crt_string = getText().substring(1, len - 1);

			                            len -= 2;
			                            StringBuilder new_string = new StringBuilder();

			                            for (int i = 0; i < len; i++) {
			                                Character c = crt_string.charAt(i);
			                                if (crt_string.charAt(i) == '\\' && i < len - 1) {
			                                    Character next_c = crt_string.charAt(i + 1);
			                                    i++;
			                                    switch(next_c) {
			                                         case 'n': new_string.append('\n');
			                                                   break;
			                                         case 't': new_string.append('\t');
			                                                   break;
			                                         case 'b': new_string.append('\b');
			                                                   break;
			                                         case 'f': new_string.append('\f');
			                                                   break;
			                                         case 'r': new_string.append('\r');
			                                                   break;
			                                         default: new_string.append(next_c);
			                                    }
			                                } else {
			                                    new_string.append(c);
			                                }
			                            }

			                            setText(new_string.toString());

			                            if (new_string.length() > 1024)
			                                raiseError("String constant too long");
			                        
			break;
		case 2:
			 raiseError("Unterminated string constant"); 
			break;
		case 3:
			 raiseError("EOF in string constant"); 
			break;
		}
	}
	private void UNMATCHED_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4:
			 raiseError("Unmatched *)"); 
			break;
		}
	}
	private void BLOCK_COMMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 5:
			 skip(); 
			break;
		case 6:
			 raiseError("EOF in comment"); 
			break;
		}
	}
	private void INVALID_CHARACTER_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 7:
			 raiseError("Invalid character: " + getText()); 
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\62\u015e\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\7\3\7"+
		"\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16"+
		"\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24"+
		"\3\24\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32"+
		"\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\""+
		"\3#\3#\3$\3$\3%\3%\3&\3&\3&\3\'\3\'\3\'\3(\3(\3)\6)\u00f5\n)\r)\16)\u00f6"+
		"\3*\3*\3+\3+\3,\3,\3-\3-\5-\u0101\n-\3-\3-\3-\7-\u0106\n-\f-\16-\u0109"+
		"\13-\3.\3.\3.\3.\7.\u010f\n.\f.\16.\u0112\13.\3/\3/\3/\3/\3/\3/\3/\3/"+
		"\7/\u011c\n/\f/\16/\u011f\13/\3/\3/\3/\3/\3/\3/\3/\5/\u0128\n/\3\60\5"+
		"\60\u012b\n\60\3\60\3\60\3\61\3\61\3\61\3\61\7\61\u0133\n\61\f\61\16\61"+
		"\u0136\13\61\3\61\3\61\5\61\u013a\n\61\3\61\3\61\3\62\3\62\3\62\3\62\3"+
		"\62\3\63\3\63\3\63\3\63\3\63\7\63\u0148\n\63\f\63\16\63\u014b\13\63\3"+
		"\63\3\63\3\63\3\63\3\63\3\63\5\63\u0153\n\63\3\64\6\64\u0156\n\64\r\64"+
		"\16\64\u0157\3\64\3\64\3\65\3\65\3\65\5\u011d\u0134\u0149\2\66\3\4\5\5"+
		"\7\6\t\7\13\b\r\t\17\n\21\13\23\f\25\r\27\16\31\17\33\20\35\21\37\22!"+
		"\23#\24%\25\'\26)\27+\30-\31/\32\61\33\63\34\65\35\67\369\37; =!?\"A#"+
		"C$E%G&I\'K(M)O\2Q*S\2U\2W\2Y+[,]-_\2a.c/e\60g\61i\62\3\2\30\4\2EEee\4"+
		"\2NNnn\4\2CCcc\4\2UUuu\4\2KKkk\4\2PPpp\4\2JJjj\4\2GGgg\4\2TTtt\4\2VVv"+
		"v\4\2YYyy\4\2QQqq\4\2HHhh\4\2RRrr\4\2XXxx\4\2FFff\4\2WWww\3\2\62;\4\2"+
		"C\\c|\3\2c|\3\2C\\\5\2\13\f\16\17\"\"\2\u016d\2\3\3\2\2\2\2\5\3\2\2\2"+
		"\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3"+
		"\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2"+
		"\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2"+
		"\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2"+
		"\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2"+
		"\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2"+
		"\2M\3\2\2\2\2Q\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2a\3\2\2\2\2c"+
		"\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\3k\3\2\2\2\5q\3\2\2\2\7z\3\2"+
		"\2\2\t~\3\2\2\2\13\u0083\3\2\2\2\r\u0086\3\2\2\2\17\u008b\3\2\2\2\21\u008e"+
		"\3\2\2\2\23\u0093\3\2\2\2\25\u0098\3\2\2\2\27\u009b\3\2\2\2\31\u00a1\3"+
		"\2\2\2\33\u00a6\3\2\2\2\35\u00ab\3\2\2\2\37\u00af\3\2\2\2!\u00b2\3\2\2"+
		"\2#\u00b9\3\2\2\2%\u00bd\3\2\2\2\'\u00c2\3\2\2\2)\u00c8\3\2\2\2+\u00ca"+
		"\3\2\2\2-\u00cd\3\2\2\2/\u00cf\3\2\2\2\61\u00d1\3\2\2\2\63\u00d3\3\2\2"+
		"\2\65\u00d5\3\2\2\2\67\u00d7\3\2\2\29\u00d9\3\2\2\2;\u00db\3\2\2\2=\u00dd"+
		"\3\2\2\2?\u00df\3\2\2\2A\u00e1\3\2\2\2C\u00e3\3\2\2\2E\u00e5\3\2\2\2G"+
		"\u00e7\3\2\2\2I\u00e9\3\2\2\2K\u00eb\3\2\2\2M\u00ee\3\2\2\2O\u00f1\3\2"+
		"\2\2Q\u00f4\3\2\2\2S\u00f8\3\2\2\2U\u00fa\3\2\2\2W\u00fc\3\2\2\2Y\u0100"+
		"\3\2\2\2[\u010a\3\2\2\2]\u0113\3\2\2\2_\u012a\3\2\2\2a\u012e\3\2\2\2c"+
		"\u013d\3\2\2\2e\u0142\3\2\2\2g\u0155\3\2\2\2i\u015b\3\2\2\2kl\t\2\2\2"+
		"lm\t\3\2\2mn\t\4\2\2no\t\5\2\2op\t\5\2\2p\4\3\2\2\2qr\t\6\2\2rs\t\7\2"+
		"\2st\t\b\2\2tu\t\t\2\2uv\t\n\2\2vw\t\6\2\2wx\t\13\2\2xy\t\5\2\2y\6\3\2"+
		"\2\2z{\t\7\2\2{|\t\t\2\2|}\t\f\2\2}\b\3\2\2\2~\177\t\2\2\2\177\u0080\t"+
		"\4\2\2\u0080\u0081\t\5\2\2\u0081\u0082\t\t\2\2\u0082\n\3\2\2\2\u0083\u0084"+
		"\t\r\2\2\u0084\u0085\t\16\2\2\u0085\f\3\2\2\2\u0086\u0087\t\t\2\2\u0087"+
		"\u0088\t\5\2\2\u0088\u0089\t\4\2\2\u0089\u008a\t\2\2\2\u008a\16\3\2\2"+
		"\2\u008b\u008c\t\6\2\2\u008c\u008d\t\16\2\2\u008d\20\3\2\2\2\u008e\u008f"+
		"\t\13\2\2\u008f\u0090\t\b\2\2\u0090\u0091\t\t\2\2\u0091\u0092\t\7\2\2"+
		"\u0092\22\3\2\2\2\u0093\u0094\t\t\2\2\u0094\u0095\t\3\2\2\u0095\u0096"+
		"\t\5\2\2\u0096\u0097\t\t\2\2\u0097\24\3\2\2\2\u0098\u0099\t\16\2\2\u0099"+
		"\u009a\t\6\2\2\u009a\26\3\2\2\2\u009b\u009c\t\f\2\2\u009c\u009d\t\b\2"+
		"\2\u009d\u009e\t\6\2\2\u009e\u009f\t\3\2\2\u009f\u00a0\t\t\2\2\u00a0\30"+
		"\3\2\2\2\u00a1\u00a2\t\3\2\2\u00a2\u00a3\t\r\2\2\u00a3\u00a4\t\r\2\2\u00a4"+
		"\u00a5\t\17\2\2\u00a5\32\3\2\2\2\u00a6\u00a7\t\17\2\2\u00a7\u00a8\t\r"+
		"\2\2\u00a8\u00a9\t\r\2\2\u00a9\u00aa\t\3\2\2\u00aa\34\3\2\2\2\u00ab\u00ac"+
		"\t\3\2\2\u00ac\u00ad\t\t\2\2\u00ad\u00ae\t\13\2\2\u00ae\36\3\2\2\2\u00af"+
		"\u00b0\t\6\2\2\u00b0\u00b1\t\7\2\2\u00b1 \3\2\2\2\u00b2\u00b3\t\6\2\2"+
		"\u00b3\u00b4\t\5\2\2\u00b4\u00b5\t\20\2\2\u00b5\u00b6\t\r\2\2\u00b6\u00b7"+
		"\t\6\2\2\u00b7\u00b8\t\21\2\2\u00b8\"\3\2\2\2\u00b9\u00ba\t\7\2\2\u00ba"+
		"\u00bb\t\r\2\2\u00bb\u00bc\t\13\2\2\u00bc$\3\2\2\2\u00bd\u00be\7v\2\2"+
		"\u00be\u00bf\t\n\2\2\u00bf\u00c0\t\22\2\2\u00c0\u00c1\t\t\2\2\u00c1&\3"+
		"\2\2\2\u00c2\u00c3\7h\2\2\u00c3\u00c4\t\4\2\2\u00c4\u00c5\t\3\2\2\u00c5"+
		"\u00c6\t\5\2\2\u00c6\u00c7\t\t\2\2\u00c7(\3\2\2\2\u00c8\u00c9\7B\2\2\u00c9"+
		"*\3\2\2\2\u00ca\u00cb\7?\2\2\u00cb\u00cc\7@\2\2\u00cc,\3\2\2\2\u00cd\u00ce"+
		"\7\60\2\2\u00ce.\3\2\2\2\u00cf\u00d0\7.\2\2\u00d0\60\3\2\2\2\u00d1\u00d2"+
		"\7<\2\2\u00d2\62\3\2\2\2\u00d3\u00d4\7=\2\2\u00d4\64\3\2\2\2\u00d5\u00d6"+
		"\7*\2\2\u00d6\66\3\2\2\2\u00d7\u00d8\7+\2\2\u00d88\3\2\2\2\u00d9\u00da"+
		"\7}\2\2\u00da:\3\2\2\2\u00db\u00dc\7\177\2\2\u00dc<\3\2\2\2\u00dd\u00de"+
		"\7-\2\2\u00de>\3\2\2\2\u00df\u00e0\7/\2\2\u00e0@\3\2\2\2\u00e1\u00e2\7"+
		",\2\2\u00e2B\3\2\2\2\u00e3\u00e4\7\61\2\2\u00e4D\3\2\2\2\u00e5\u00e6\7"+
		"\u0080\2\2\u00e6F\3\2\2\2\u00e7\u00e8\7?\2\2\u00e8H\3\2\2\2\u00e9\u00ea"+
		"\7>\2\2\u00eaJ\3\2\2\2\u00eb\u00ec\7>\2\2\u00ec\u00ed\7?\2\2\u00edL\3"+
		"\2\2\2\u00ee\u00ef\7>\2\2\u00ef\u00f0\7/\2\2\u00f0N\3\2\2\2\u00f1\u00f2"+
		"\t\23\2\2\u00f2P\3\2\2\2\u00f3\u00f5\5O(\2\u00f4\u00f3\3\2\2\2\u00f5\u00f6"+
		"\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7R\3\2\2\2\u00f8"+
		"\u00f9\t\24\2\2\u00f9T\3\2\2\2\u00fa\u00fb\t\25\2\2\u00fbV\3\2\2\2\u00fc"+
		"\u00fd\t\26\2\2\u00fdX\3\2\2\2\u00fe\u0101\5U+\2\u00ff\u0101\7a\2\2\u0100"+
		"\u00fe\3\2\2\2\u0100\u00ff\3\2\2\2\u0101\u0107\3\2\2\2\u0102\u0106\5S"+
		"*\2\u0103\u0106\7a\2\2\u0104\u0106\5O(\2\u0105\u0102\3\2\2\2\u0105\u0103"+
		"\3\2\2\2\u0105\u0104\3\2\2\2\u0106\u0109\3\2\2\2\u0107\u0105\3\2\2\2\u0107"+
		"\u0108\3\2\2\2\u0108Z\3\2\2\2\u0109\u0107\3\2\2\2\u010a\u0110\5W,\2\u010b"+
		"\u010f\5S*\2\u010c\u010f\7a\2\2\u010d\u010f\5O(\2\u010e\u010b\3\2\2\2"+
		"\u010e\u010c\3\2\2\2\u010e\u010d\3\2\2\2\u010f\u0112\3\2\2\2\u0110\u010e"+
		"\3\2\2\2\u0110\u0111\3\2\2\2\u0111\\\3\2\2\2\u0112\u0110\3\2\2\2\u0113"+
		"\u011d\7$\2\2\u0114\u0115\7^\2\2\u0115\u011c\7$\2\2\u0116\u0117\7^\2\2"+
		"\u0117\u011c\5_\60\2\u0118\u0119\7\2\2\2\u0119\u011c\b/\2\2\u011a\u011c"+
		"\13\2\2\2\u011b\u0114\3\2\2\2\u011b\u0116\3\2\2\2\u011b\u0118\3\2\2\2"+
		"\u011b\u011a\3\2\2\2\u011c\u011f\3\2\2\2\u011d\u011e\3\2\2\2\u011d\u011b"+
		"\3\2\2\2\u011e\u0127\3\2\2\2\u011f\u011d\3\2\2\2\u0120\u0121\7$\2\2\u0121"+
		"\u0128\b/\3\2\u0122\u0123\5_\60\2\u0123\u0124\b/\4\2\u0124\u0128\3\2\2"+
		"\2\u0125\u0126\7\2\2\3\u0126\u0128\b/\5\2\u0127\u0120\3\2\2\2\u0127\u0122"+
		"\3\2\2\2\u0127\u0125\3\2\2\2\u0128^\3\2\2\2\u0129\u012b\7\17\2\2\u012a"+
		"\u0129\3\2\2\2\u012a\u012b\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012d\7\f"+
		"\2\2\u012d`\3\2\2\2\u012e\u012f\7/\2\2\u012f\u0130\7/\2\2\u0130\u0134"+
		"\3\2\2\2\u0131\u0133\13\2\2\2\u0132\u0131\3\2\2\2\u0133\u0136\3\2\2\2"+
		"\u0134\u0135\3\2\2\2\u0134\u0132\3\2\2\2\u0135\u0139\3\2\2\2\u0136\u0134"+
		"\3\2\2\2\u0137\u013a\5_\60\2\u0138\u013a\7\2\2\3\u0139\u0137\3\2\2\2\u0139"+
		"\u0138\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u013c\b\61\6\2\u013cb\3\2\2\2"+
		"\u013d\u013e\7,\2\2\u013e\u013f\7+\2\2\u013f\u0140\3\2\2\2\u0140\u0141"+
		"\b\62\7\2\u0141d\3\2\2\2\u0142\u0143\7*\2\2\u0143\u0144\7,\2\2\u0144\u0149"+
		"\3\2\2\2\u0145\u0148\5e\63\2\u0146\u0148\13\2\2\2\u0147\u0145\3\2\2\2"+
		"\u0147\u0146\3\2\2\2\u0148\u014b\3\2\2\2\u0149\u014a\3\2\2\2\u0149\u0147"+
		"\3\2\2\2\u014a\u0152\3\2\2\2\u014b\u0149\3\2\2\2\u014c\u014d\7,\2\2\u014d"+
		"\u014e\7+\2\2\u014e\u014f\3\2\2\2\u014f\u0153\b\63\b\2\u0150\u0151\7\2"+
		"\2\3\u0151\u0153\b\63\t\2\u0152\u014c\3\2\2\2\u0152\u0150\3\2\2\2\u0153"+
		"f\3\2\2\2\u0154\u0156\t\27\2\2\u0155\u0154\3\2\2\2\u0156\u0157\3\2\2\2"+
		"\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158\u0159\3\2\2\2\u0159\u015a"+
		"\b\64\6\2\u015ah\3\2\2\2\u015b\u015c\13\2\2\2\u015c\u015d\b\65\n\2\u015d"+
		"j\3\2\2\2\23\2\u00f6\u0100\u0105\u0107\u010e\u0110\u011b\u011d\u0127\u012a"+
		"\u0134\u0139\u0147\u0149\u0152\u0157\13\3/\2\3/\3\3/\4\3/\5\b\2\2\3\62"+
		"\6\3\63\7\3\63\b\3\65\t";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}