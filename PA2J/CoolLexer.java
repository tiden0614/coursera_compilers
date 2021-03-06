/*
 *  The scanner definition for COOL.
 */
import java_cup.runtime.Symbol;


class CoolLexer implements java_cup.runtime.Scanner {
	private final int YY_BUFFER_SIZE = 512;
	private final int YY_F = -1;
	private final int YY_NO_STATE = -1;
	private final int YY_NOT_ACCEPT = 0;
	private final int YY_START = 1;
	private final int YY_END = 2;
	private final int YY_NO_ANCHOR = 4;
	private final int YY_BOL = 128;
	private final int YY_EOF = 129;

/*  Stuff enclosed in %{ %} is copied verbatim to the lexer class
 *  definition, all the extra variables/functions you want to use in the
 *  lexer actions should go here.  Don't remove or modify anything that
 *  was there initially.  */
    // Max size of string constants
    static int MAX_STR_CONST = 1025;
    // For assembling string constants
    StringBuffer string_buf = new StringBuffer();
	private int block_comment_depth = 0;
    private int curr_lineno = 1;
	private int before_comment_state;
	private boolean string_error_mark = false;
    int get_curr_lineno() {
	return curr_lineno;
    }
    private AbstractSymbol filename;
    void set_filename(String fname) {
	filename = AbstractTable.stringtable.addString(fname);
    }
    AbstractSymbol curr_filename() {
	return filename;
    }
	private IntTable intTable = AbstractTable.inttable;
	private IdTable idTable = AbstractTable.idtable;
	private StringTable stringTable = AbstractTable.stringtable;
	private AbstractSymbol addInt(String text) {
		return intTable.addString(text);
	}
	private AbstractSymbol addId(String text) {
		return idTable.addString(text);
	}
	private AbstractSymbol addStr(String text) {
		return stringTable.addString(text);
	}
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private boolean yy_at_bol;
	private int yy_lexical_state;

	CoolLexer (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	CoolLexer (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private CoolLexer () {
		yy_buffer = new char[YY_BUFFER_SIZE];
		yy_buffer_read = 0;
		yy_buffer_index = 0;
		yy_buffer_start = 0;
		yy_buffer_end = 0;
		yy_at_bol = true;
		yy_lexical_state = YYINITIAL;

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */
    // empty for now
	}

	private boolean yy_eof_done = false;
	private final int STRING = 3;
	private final int BLOCKCOM = 2;
	private final int YYINITIAL = 0;
	private final int LINECOM = 1;
	private final int yy_state_dtrans[] = {
		0,
		50,
		63,
		85
	};
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
		throws java.io.IOException {
		int next_read;
		int i;
		int j;

		if (yy_buffer_index < yy_buffer_read) {
			return yy_buffer[yy_buffer_index++];
		}

		if (0 != yy_buffer_start) {
			i = yy_buffer_start;
			j = 0;
			while (i < yy_buffer_read) {
				yy_buffer[j] = yy_buffer[i];
				++i;
				++j;
			}
			yy_buffer_end = yy_buffer_end - yy_buffer_start;
			yy_buffer_start = 0;
			yy_buffer_read = j;
			yy_buffer_index = j;
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}

		while (yy_buffer_index >= yy_buffer_read) {
			if (yy_buffer_index >= yy_buffer.length) {
				yy_buffer = yy_double(yy_buffer);
			}
			next_read = yy_reader.read(yy_buffer,
					yy_buffer_read,
					yy_buffer.length - yy_buffer_read);
			if (-1 == next_read) {
				return YY_EOF;
			}
			yy_buffer_read = yy_buffer_read + next_read;
		}
		return yy_buffer[yy_buffer_index++];
	}
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
		for (i = 0; i < buf.length; ++i) {
			newbuf[i] = buf[i];
		}
		return newbuf;
	}
	private final int YY_E_INTERNAL = 0;
	private final int YY_E_MATCH = 1;
	private java.lang.String yy_error_string[] = {
		"Error: Internal error.\n",
		"Error: Unmatched input.\n"
	};
	private void yy_error (int code,boolean fatal) {
		java.lang.System.out.print(yy_error_string[code]);
		java.lang.System.out.flush();
		if (fatal) {
			throw new Error("Fatal Error.\n");
		}
	}
	private int[][] unpackFromString(int size1, int size2, String st) {
		int colonIndex = -1;
		String lengthString;
		int sequenceLength = 0;
		int sequenceInteger = 0;

		int commaIndex;
		String workString;

		int res[][] = new int[size1][size2];
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
				res[i][j] = sequenceInteger;
				sequenceLength--;
			}
		}
		return res;
	}
	private int yy_acpt[] = {
		/* 0 */ YY_NOT_ACCEPT,
		/* 1 */ YY_NO_ANCHOR,
		/* 2 */ YY_NO_ANCHOR,
		/* 3 */ YY_NO_ANCHOR,
		/* 4 */ YY_NO_ANCHOR,
		/* 5 */ YY_NO_ANCHOR,
		/* 6 */ YY_NO_ANCHOR,
		/* 7 */ YY_NO_ANCHOR,
		/* 8 */ YY_NO_ANCHOR,
		/* 9 */ YY_NO_ANCHOR,
		/* 10 */ YY_NO_ANCHOR,
		/* 11 */ YY_NO_ANCHOR,
		/* 12 */ YY_NO_ANCHOR,
		/* 13 */ YY_NO_ANCHOR,
		/* 14 */ YY_NO_ANCHOR,
		/* 15 */ YY_NO_ANCHOR,
		/* 16 */ YY_NO_ANCHOR,
		/* 17 */ YY_NO_ANCHOR,
		/* 18 */ YY_NO_ANCHOR,
		/* 19 */ YY_NO_ANCHOR,
		/* 20 */ YY_NO_ANCHOR,
		/* 21 */ YY_NO_ANCHOR,
		/* 22 */ YY_NO_ANCHOR,
		/* 23 */ YY_NO_ANCHOR,
		/* 24 */ YY_NO_ANCHOR,
		/* 25 */ YY_NO_ANCHOR,
		/* 26 */ YY_NO_ANCHOR,
		/* 27 */ YY_NO_ANCHOR,
		/* 28 */ YY_NO_ANCHOR,
		/* 29 */ YY_NO_ANCHOR,
		/* 30 */ YY_NO_ANCHOR,
		/* 31 */ YY_NO_ANCHOR,
		/* 32 */ YY_NO_ANCHOR,
		/* 33 */ YY_NO_ANCHOR,
		/* 34 */ YY_NO_ANCHOR,
		/* 35 */ YY_NO_ANCHOR,
		/* 36 */ YY_NO_ANCHOR,
		/* 37 */ YY_NO_ANCHOR,
		/* 38 */ YY_NO_ANCHOR,
		/* 39 */ YY_NO_ANCHOR,
		/* 40 */ YY_NO_ANCHOR,
		/* 41 */ YY_NO_ANCHOR,
		/* 42 */ YY_NO_ANCHOR,
		/* 43 */ YY_NO_ANCHOR,
		/* 44 */ YY_NO_ANCHOR,
		/* 45 */ YY_NO_ANCHOR,
		/* 46 */ YY_NO_ANCHOR,
		/* 47 */ YY_NO_ANCHOR,
		/* 48 */ YY_NO_ANCHOR,
		/* 49 */ YY_NO_ANCHOR,
		/* 50 */ YY_NO_ANCHOR,
		/* 51 */ YY_NO_ANCHOR,
		/* 52 */ YY_NO_ANCHOR,
		/* 53 */ YY_NO_ANCHOR,
		/* 54 */ YY_NO_ANCHOR,
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NOT_ACCEPT,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NO_ANCHOR,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR,
		/* 132 */ YY_NO_ANCHOR,
		/* 133 */ YY_NO_ANCHOR,
		/* 134 */ YY_NO_ANCHOR,
		/* 135 */ YY_NO_ANCHOR,
		/* 136 */ YY_NO_ANCHOR,
		/* 137 */ YY_NO_ANCHOR,
		/* 138 */ YY_NO_ANCHOR,
		/* 139 */ YY_NO_ANCHOR,
		/* 140 */ YY_NO_ANCHOR,
		/* 141 */ YY_NO_ANCHOR,
		/* 142 */ YY_NO_ANCHOR,
		/* 143 */ YY_NO_ANCHOR,
		/* 144 */ YY_NO_ANCHOR,
		/* 145 */ YY_NO_ANCHOR,
		/* 146 */ YY_NO_ANCHOR,
		/* 147 */ YY_NO_ANCHOR,
		/* 148 */ YY_NO_ANCHOR,
		/* 149 */ YY_NO_ANCHOR,
		/* 150 */ YY_NO_ANCHOR,
		/* 151 */ YY_NO_ANCHOR,
		/* 152 */ YY_NO_ANCHOR,
		/* 153 */ YY_NO_ANCHOR,
		/* 154 */ YY_NO_ANCHOR,
		/* 155 */ YY_NO_ANCHOR,
		/* 156 */ YY_NO_ANCHOR,
		/* 157 */ YY_NO_ANCHOR,
		/* 158 */ YY_NO_ANCHOR,
		/* 159 */ YY_NO_ANCHOR,
		/* 160 */ YY_NO_ANCHOR,
		/* 161 */ YY_NO_ANCHOR,
		/* 162 */ YY_NO_ANCHOR,
		/* 163 */ YY_NO_ANCHOR,
		/* 164 */ YY_NO_ANCHOR,
		/* 165 */ YY_NO_ANCHOR,
		/* 166 */ YY_NO_ANCHOR,
		/* 167 */ YY_NO_ANCHOR,
		/* 168 */ YY_NO_ANCHOR,
		/* 169 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,130,
"1,8:8,10,2,10:3,8:18,10,8,6,8:5,3,5,4,55,57,9,58,56,11:10,52,51,60,59,61,8," +
"49,31,32,33,34,35,21,32,36,37,32:2,38,32,39,40,41,32,42,43,25,44,45,46,32:3" +
",8,7,8:2,47,8,17,48,27,30,15,16,48,26,22,48:2,18,48,23,20,28,48,13,19,12,14" +
",29,24,48:3,53,8,54,50,8,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,170,
"0,1:3,2,3,1:2,4,1,5,6,7,1:10,8,9,1:3,10:3,11,1:3,10:15,12,1:9,13,1:2,14,15," +
"16,17:3,18,17:13,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,3" +
"8,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,6" +
"3,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,8" +
"8,89,90,91,92,93,94,95,10,17,96,97,98,99,100,101,102,103")[0];

	private int yy_nxt[][] = unpackFromString(104,62,
"1,2,3,4,5,6,7,2:2,8,9,10,11,160:2,162,64,160,119,160,86,12,89,121,164,65,16" +
"0,166,168,160:2,161:2,163,161,165,161,87,120,122,90,167,161:4,169,2,160,13," +
"14,15,16,17,18,19,20,21,22,23,24,2,-1:66,25,-1:62,26,-1:65,27,-1:63,10,-1:6" +
"1,160:2,123,160:12,125,160:9,125,160:5,123,160:6,-1:24,161:11,66,161:14,66," +
"161:11,-1:74,32,-1:9,33,-1:49,34,-1:13,160:38,-1:24,160:15,149,160:9,149,16" +
"0:12,-1:13,1,83,51,83:59,-1,56,61,62:59,1,52,53,84,88,52:57,-1:11,160:6,131" +
",160:4,28,160:8,131,160:5,28,160:11,-1:24,161:15,124,161:9,124,161:12,-1:24" +
",161:38,-1:24,161:15,146,161:9,146,161:12,-1:14,83,-1,83:59,-1:4,54,-1:57,1" +
",56,57,58:3,59,60,58:54,-1:11,160:5,29,160:4,29,160:27,-1:24,161:5,68,161:2" +
",134,161,68,161,69,161:15,69,161:3,134,161:5,-1:18,55,-1:67,160:5,30,160:2," +
"135,160,30,160,31,160:15,31,160:3,135,160:5,-1:24,161:5,67,161:4,67,161:27," +
"-1:24,160,35,160:12,35,160:23,-1:24,161,70,161:12,70,161:23,-1:24,160:13,36" +
",160:21,36,160:2,-1:24,161:13,71,161:21,71,161:2,-1:24,160,37,160:12,37,160" +
":23,-1:24,161,72,161:12,72,161:23,-1:24,160:4,38,160:19,38,160:13,-1:24,161" +
":12,73,161:15,73,161:9,-1:24,160:12,39,160:15,39,160:9,-1:24,161:4,77,161:1" +
"9,77,161:13,-1:24,160:4,40,160:19,40,160:13,-1:24,161:4,74,161:19,74,161:13" +
",-1:24,160:16,41,160:5,41,160:15,-1:24,161:16,75,161:5,75,161:15,-1:24,160:" +
"17,42,160:12,42,160:7,-1:24,161:17,76,161:12,76,161:7,-1:24,160:4,43,160:19" +
",43,160:13,-1:24,161:7,78,161:19,78,161:10,-1:24,160:7,44,160:19,44,160:10," +
"-1:24,161:8,80,161:23,80,161:5,-1:24,160:4,45,160:19,45,160:13,-1:24,161:4," +
"79,161:19,79,161:13,-1:24,160:4,46,160:19,46,160:13,-1:24,161:19,81,161:3,8" +
"1,161:14,-1:24,160:8,47,160:23,47,160:5,-1:24,161:8,82,161:23,82,161:5,-1:2" +
"4,160:19,48,160:3,48,160:14,-1:24,160:8,49,160:23,49,160:5,-1:24,160:4,91,1" +
"60:4,133,160:14,91,160:4,133,160:8,-1:24,161:4,92,161:4,136,161:14,92,161:4" +
",136,161:8,-1:24,160:4,93,160:4,95,160:14,93,160:4,95,160:8,-1:24,161:4,94," +
"161:4,96,161:14,94,161:4,96,161:8,-1:24,160:3,97,160:29,97,160:4,-1:24,161:" +
"4,98,161:19,98,161:13,-1:24,160:4,99,160:19,99,160:13,-1:24,161:8,100,161:2" +
"3,100,161:5,-1:24,160:8,101,160:23,101,160:5,-1:24,161:6,142,161:13,142,161" +
":17,-1:24,160:6,103,160:13,103,160:17,-1:24,161:8,102,161:23,102,161:5,-1:2" +
"4,160:7,145,160:19,145,160:10,-1:24,161:6,104,161:13,104,161:17,-1:24,160:9" +
",105,160:19,105,160:8,-1:24,161:18,144,161:15,144,161:3,-1:24,160:18,147,16" +
"0:15,147,160:3,-1:24,161:9,106,161:19,106,161:8,-1:24,160:11,151,160:14,151" +
",160:11,-1:24,161:9,108,161:19,108,161:8,-1:24,160:8,107,160:23,107,160:5,-" +
"1:24,161:11,148,161:14,148,161:11,-1:24,160:6,153,160:13,153,160:17,-1:24,1" +
"61:8,110,161:23,110,161:5,-1:24,160:9,109,160:19,109,160:8,-1:24,161:9,150," +
"161:19,150,161:8,-1:24,160:8,111,160:23,111,160:5,-1:24,161:4,152,161:19,15" +
"2,161:13,-1:24,160:9,155,160:19,155,160:8,-1:24,161:7,112,161:19,112,161:10" +
",-1:24,160:4,157,160:19,157,160:13,-1:24,161:11,114,161:14,114,161:11,-1:24" +
",160:7,113,160:19,113,160:10,-1:24,161:2,154,161:28,154,161:6,-1:24,160:8,1" +
"15,160:23,115,160:5,-1:24,161:11,156,161:14,156,161:11,-1:24,160:11,117,160" +
":14,117,160:11,-1:24,161,116,161:12,116,161:23,-1:24,160:2,158,160:28,158,1" +
"60:6,-1:24,160:11,159,160:14,159,160:11,-1:24,160,118,160:12,118,160:23,-1:" +
"24,160:7,127,129,160:18,127,160:4,129,160:5,-1:24,161:6,126,128,161:12,126," +
"161:6,128,161:10,-1:24,160:15,137,160:9,137,160:12,-1:24,161:7,130,132,161:" +
"18,130,161:4,132,161:5,-1:24,160:6,139,141,160:12,139,160:6,141,160:10,-1:2" +
"4,161:9,138,161:19,138,161:8,-1:24,160:9,143,160:19,143,160:8,-1:24,161:15," +
"140,161:9,140,161:12,-1:13");

	public java_cup.runtime.Symbol next_token ()
		throws java.io.IOException {
		int yy_lookahead;
		int yy_anchor = YY_NO_ANCHOR;
		int yy_state = yy_state_dtrans[yy_lexical_state];
		int yy_next_state = YY_NO_STATE;
		int yy_last_accept_state = YY_NO_STATE;
		boolean yy_initial = true;
		int yy_this_accept;

		yy_mark_start();
		yy_this_accept = yy_acpt[yy_state];
		if (YY_NOT_ACCEPT != yy_this_accept) {
			yy_last_accept_state = yy_state;
			yy_mark_end();
		}
		while (true) {
			if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
			else yy_lookahead = yy_advance();
			yy_next_state = YY_F;
			yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
			if (YY_EOF == yy_lookahead && true == yy_initial) {

/*  Stuff enclosed in %eofval{ %eofval} specifies java code that is
 *  executed when end-of-file is reached.  If you use multiple lexical
 *  states and want to do something special if an EOF is encountered in
 *  one of those states, place your code in the switch statement.
 *  Ultimately, you should return the EOF symbol, or your lexer won't
 *  work.  */
    switch(yy_lexical_state) {
    case YYINITIAL:
	/* nothing special to do in the initial state */
	break;
	/* If necessary, add code for other states here, e.g:
	   case COMMENT:
	   ...
	   break;
	*/
	case STRING:
	yybegin(YYINITIAL);
	return new Symbol(TokenConstants.ERROR, "EOF in string constant");
	case LINECOM:
	break;
	case BLOCKCOM:
	yybegin(YYINITIAL);
	return new Symbol(TokenConstants.ERROR, "EOF in comment");
    }
    return new Symbol(TokenConstants.EOF);
			}
			if (YY_F != yy_next_state) {
				yy_state = yy_next_state;
				yy_initial = false;
				yy_this_accept = yy_acpt[yy_state];
				if (YY_NOT_ACCEPT != yy_this_accept) {
					yy_last_accept_state = yy_state;
					yy_mark_end();
				}
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{return new Symbol(TokenConstants.ERROR, yytext());}
					case -3:
						break;
					case 3:
						{curr_lineno++;}
					case -4:
						break;
					case 4:
						{return new Symbol(TokenConstants.LPAREN);}
					case -5:
						break;
					case 5:
						{return new Symbol(TokenConstants.MULT);}
					case -6:
						break;
					case 6:
						{return new Symbol(TokenConstants.RPAREN);}
					case -7:
						break;
					case 7:
						{
											string_buf.setLength(0);
											string_error_mark = false;
											yybegin(STRING);
										}
					case -8:
						break;
					case 8:
						{return new Symbol(TokenConstants.MINUS);}
					case -9:
						break;
					case 9:
						{}
					case -10:
						break;
					case 10:
						{return new Symbol(TokenConstants.INT_CONST, addInt(yytext()));}
					case -11:
						break;
					case 11:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -12:
						break;
					case 12:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -13:
						break;
					case 13:
						{return new Symbol(TokenConstants.AT);}
					case -14:
						break;
					case 14:
						{return new Symbol(TokenConstants.NEG);}
					case -15:
						break;
					case 15:
						{return new Symbol(TokenConstants.SEMI);}
					case -16:
						break;
					case 16:
						{return new Symbol(TokenConstants.COLON);}
					case -17:
						break;
					case 17:
						{return new Symbol(TokenConstants.LBRACE);}
					case -18:
						break;
					case 18:
						{return new Symbol(TokenConstants.RBRACE);}
					case -19:
						break;
					case 19:
						{return new Symbol(TokenConstants.PLUS);}
					case -20:
						break;
					case 20:
						{return new Symbol(TokenConstants.DIV);}
					case -21:
						break;
					case 21:
						{return new Symbol(TokenConstants.COMMA);}
					case -22:
						break;
					case 22:
						{return new Symbol(TokenConstants.DOT);}
					case -23:
						break;
					case 23:
						{return new Symbol(TokenConstants.EQ);}
					case -24:
						break;
					case 24:
						{return new Symbol(TokenConstants.LT);}
					case -25:
						break;
					case 25:
						{ 
											block_comment_depth = 1;
											before_comment_state = yy_lexical_state;
											yybegin(BLOCKCOM); 
										}
					case -26:
						break;
					case 26:
						{return new Symbol(TokenConstants.ERROR, "Unmatched *)");}
					case -27:
						break;
					case 27:
						{
											before_comment_state = yy_lexical_state;
											yybegin(LINECOM);
										}
					case -28:
						break;
					case 28:
						{return new Symbol(TokenConstants.FI);}
					case -29:
						break;
					case 29:
						{return new Symbol(TokenConstants.OF);}
					case -30:
						break;
					case 30:
						{return new Symbol(TokenConstants.IF);}
					case -31:
						break;
					case 31:
						{return new Symbol(TokenConstants.IN);}
					case -32:
						break;
					case 32:
						{return new Symbol(TokenConstants.DARROW);}
					case -33:
						break;
					case 33:
						{return new Symbol(TokenConstants.ASSIGN);}
					case -34:
						break;
					case 34:
						{return new Symbol(TokenConstants.LE);}
					case -35:
						break;
					case 35:
						{return new Symbol(TokenConstants.LET);}
					case -36:
						break;
					case 36:
						{return new Symbol(TokenConstants.NEW);}
					case -37:
						break;
					case 37:
						{return new Symbol(TokenConstants.NOT);}
					case -38:
						break;
					case 38:
						{return new Symbol(TokenConstants.BOOL_CONST, true);}
					case -39:
						break;
					case 39:
						{return new Symbol(TokenConstants.THEN);}
					case -40:
						break;
					case 40:
						{return new Symbol(TokenConstants.ELSE);}
					case -41:
						break;
					case 41:
						{return new Symbol(TokenConstants.ESAC);}
					case -42:
						break;
					case 42:
						{return new Symbol(TokenConstants.LOOP);}
					case -43:
						break;
					case 43:
						{return new Symbol(TokenConstants.CASE);}
					case -44:
						break;
					case 44:
						{return new Symbol(TokenConstants.POOL);}
					case -45:
						break;
					case 45:
						{return new Symbol(TokenConstants.BOOL_CONST, false);}
					case -46:
						break;
					case 46:
						{return new Symbol(TokenConstants.WHILE);}
					case -47:
						break;
					case 47:
						{return new Symbol(TokenConstants.CLASS);}
					case -48:
						break;
					case 48:
						{return new Symbol(TokenConstants.ISVOID);}
					case -49:
						break;
					case 49:
						{return new Symbol(TokenConstants.INHERITS);}
					case -50:
						break;
					case 50:
						{}
					case -51:
						break;
					case 51:
						{
											curr_lineno++;
											yybegin(before_comment_state);
										}
					case -52:
						break;
					case 52:
						{}
					case -53:
						break;
					case 53:
						{curr_lineno++;}
					case -54:
						break;
					case 54:
						{block_comment_depth++;}
					case -55:
						break;
					case 55:
						{ 
											if (--block_comment_depth <= 0) {
												yybegin(before_comment_state);
											}
										}
					case -56:
						break;
					case 56:
						{
											if (!string_error_mark) {
												string_error_mark = true;
												return new Symbol(TokenConstants.ERROR, "String contains null character");
											}
										}
					case -57:
						break;
					case 57:
						{ 
											curr_lineno++;
											yybegin(YYINITIAL);
											if (!string_error_mark) {
												string_error_mark = true;
												return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
											}
										}
					case -58:
						break;
					case 58:
						{ string_buf.append(yytext()); }
					case -59:
						break;
					case 59:
						{ 
											yybegin(YYINITIAL);
											if (string_buf.length() >= MAX_STR_CONST && !string_error_mark)
												return new Symbol(TokenConstants.ERROR, "String constant too long");
											if (!string_error_mark)
												return new Symbol(TokenConstants.STR_CONST, addStr(string_buf.toString()));
										}
					case -60:
						break;
					case 60:
						{ 
											if (!string_error_mark) {
												string_error_mark = true;
												return new Symbol(TokenConstants.ERROR, "Single backslash"); 
											}
										}
					case -61:
						break;
					case 61:
						{
											curr_lineno++;
											string_buf.append("\n");
										}
					case -62:
						break;
					case 62:
						{
											String t = yytext();
											if ("\\n".equals(t)) string_buf.append("\n");
											else if ("\\f".equals(t)) string_buf.append("\f");
											else if ("\\t".equals(t)) string_buf.append("\t");
											else if ("\\b".equals(t)) string_buf.append("\b");
											else string_buf.append(t.substring(1));
										}
					case -63:
						break;
					case 64:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -64:
						break;
					case 65:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -65:
						break;
					case 66:
						{return new Symbol(TokenConstants.FI);}
					case -66:
						break;
					case 67:
						{return new Symbol(TokenConstants.OF);}
					case -67:
						break;
					case 68:
						{return new Symbol(TokenConstants.IF);}
					case -68:
						break;
					case 69:
						{return new Symbol(TokenConstants.IN);}
					case -69:
						break;
					case 70:
						{return new Symbol(TokenConstants.LET);}
					case -70:
						break;
					case 71:
						{return new Symbol(TokenConstants.NEW);}
					case -71:
						break;
					case 72:
						{return new Symbol(TokenConstants.NOT);}
					case -72:
						break;
					case 73:
						{return new Symbol(TokenConstants.THEN);}
					case -73:
						break;
					case 74:
						{return new Symbol(TokenConstants.ELSE);}
					case -74:
						break;
					case 75:
						{return new Symbol(TokenConstants.ESAC);}
					case -75:
						break;
					case 76:
						{return new Symbol(TokenConstants.LOOP);}
					case -76:
						break;
					case 77:
						{return new Symbol(TokenConstants.CASE);}
					case -77:
						break;
					case 78:
						{return new Symbol(TokenConstants.POOL);}
					case -78:
						break;
					case 79:
						{return new Symbol(TokenConstants.WHILE);}
					case -79:
						break;
					case 80:
						{return new Symbol(TokenConstants.CLASS);}
					case -80:
						break;
					case 81:
						{return new Symbol(TokenConstants.ISVOID);}
					case -81:
						break;
					case 82:
						{return new Symbol(TokenConstants.INHERITS);}
					case -82:
						break;
					case 83:
						{}
					case -83:
						break;
					case 84:
						{}
					case -84:
						break;
					case 86:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -85:
						break;
					case 87:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -86:
						break;
					case 88:
						{}
					case -87:
						break;
					case 89:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -88:
						break;
					case 90:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -89:
						break;
					case 91:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -90:
						break;
					case 92:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -91:
						break;
					case 93:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -92:
						break;
					case 94:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -93:
						break;
					case 95:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -94:
						break;
					case 96:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -95:
						break;
					case 97:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -96:
						break;
					case 98:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -97:
						break;
					case 99:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -98:
						break;
					case 100:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -99:
						break;
					case 101:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -100:
						break;
					case 102:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -101:
						break;
					case 103:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -102:
						break;
					case 104:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -103:
						break;
					case 105:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -104:
						break;
					case 106:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -105:
						break;
					case 107:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -106:
						break;
					case 108:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -107:
						break;
					case 109:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -108:
						break;
					case 110:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -109:
						break;
					case 111:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -110:
						break;
					case 112:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -111:
						break;
					case 113:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -112:
						break;
					case 114:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -113:
						break;
					case 115:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -114:
						break;
					case 116:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -115:
						break;
					case 117:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -116:
						break;
					case 118:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -117:
						break;
					case 119:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -118:
						break;
					case 120:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -119:
						break;
					case 121:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -120:
						break;
					case 122:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -121:
						break;
					case 123:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -122:
						break;
					case 124:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -123:
						break;
					case 125:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -124:
						break;
					case 126:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -125:
						break;
					case 127:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -126:
						break;
					case 128:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -127:
						break;
					case 129:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -128:
						break;
					case 130:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -129:
						break;
					case 131:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -130:
						break;
					case 132:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -131:
						break;
					case 133:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -132:
						break;
					case 134:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -133:
						break;
					case 135:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -134:
						break;
					case 136:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -135:
						break;
					case 137:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -136:
						break;
					case 138:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -137:
						break;
					case 139:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -138:
						break;
					case 140:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -139:
						break;
					case 141:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -140:
						break;
					case 142:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -141:
						break;
					case 143:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -142:
						break;
					case 144:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -143:
						break;
					case 145:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -144:
						break;
					case 146:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -145:
						break;
					case 147:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -146:
						break;
					case 148:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -147:
						break;
					case 149:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -148:
						break;
					case 150:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -149:
						break;
					case 151:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -150:
						break;
					case 152:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -151:
						break;
					case 153:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -152:
						break;
					case 154:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -153:
						break;
					case 155:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -154:
						break;
					case 156:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -155:
						break;
					case 157:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -156:
						break;
					case 158:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -157:
						break;
					case 159:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -158:
						break;
					case 160:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -159:
						break;
					case 161:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -160:
						break;
					case 162:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -161:
						break;
					case 163:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -162:
						break;
					case 164:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -163:
						break;
					case 165:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -164:
						break;
					case 166:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -165:
						break;
					case 167:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -166:
						break;
					case 168:
						{return new Symbol(TokenConstants.OBJECTID, addId(yytext()));}
					case -167:
						break;
					case 169:
						{return new Symbol(TokenConstants.TYPEID, addId(yytext()));}
					case -168:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
					case -1:
					}
					yy_initial = true;
					yy_state = yy_state_dtrans[yy_lexical_state];
					yy_next_state = YY_NO_STATE;
					yy_last_accept_state = YY_NO_STATE;
					yy_mark_start();
					yy_this_accept = yy_acpt[yy_state];
					if (YY_NOT_ACCEPT != yy_this_accept) {
						yy_last_accept_state = yy_state;
						yy_mark_end();
					}
				}
			}
		}
	}
}
