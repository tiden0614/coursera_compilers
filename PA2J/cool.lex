/*
 *  The scanner definition for COOL.
 */

import java_cup.runtime.Symbol;

%%

%{

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
%}

%init{

/*  Stuff enclosed in %init{ %init} is copied verbatim to the lexer
 *  class constructor, all the extra initialization you want to do should
 *  go here.  Don't remove or modify anything that was there initially. */

    // empty for now
%init}

%eofval{

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
	case BLOCKCOM:
	yybegin(YYINITIAL);
	return new Symbol(TokenConstants.ERROR, "EOF in comment");
    }
    return new Symbol(TokenConstants.EOF);
%eofval}

%class CoolLexer
%cup

DIGIT   = [0-9]
LLETTER = [a-z]
ULETTER = [A-Z]
LETTER  = [a-zA-Z]
INT     = {DIGIT}+
FLOAT   = {INT}(\.{INT}+)?
WSPACE  = [ \f\r\t\v]

%state LINECOM, BLOCKCOM, STRING

%%
<YYINITIAL, STRING>"(*"                 { 
											block_comment_depth = 1;
											before_comment_state = yy_lexical_state;
											yybegin(BLOCKCOM); 
										}
<YYINITIAL, STRING>--                   {
											before_comment_state = yy_lexical_state;
											yybegin(LINECOM);
										}

<LINECOM>[^\n]*\n                       {
											curr_lineno++;
											yybegin(before_comment_state);
										}
<BLOCKCOM>"(*"                          {block_comment_depth++;}
<BLOCKCOM>[^\n]							{ }
<BLOCKCOM>\n							{curr_lineno++;}
<BLOCKCOM>"*)"                          { 
											if (--block_comment_depth <= 0) {
												yybegin(before_comment_state);
											}
										}

<STRING>\"                              { 
											yybegin(YYINITIAL);
											return new Symbol(TokenConstants.STR_CONST, 
													stringTable.addString(string_buf.toString()));
										}
<STRING>\n                              { 
											curr_lineno++;
											yybegin(YYINITIAL);
											return new Symbol(TokenConstants.ERROR, "Unterminated string constant");
										}
<STRING>\0                              {
											yybegin(YYINITIAL);
											return new Symbol(TokenConstants.ERROR, "String contains null character");
										}
<STRING>\\.                             {
											String t = yytext();
											if("\\\n".equals(t)) {
												curr_lineno++;
												string_buf.append("\n");
											}
											else if ("\\n".equals(t)) string_buf.append("\n");
											else if ("\\f".equals(t)) string_buf.append("\f");
											else if ("\\t".equals(t)) string_buf.append("\t");
											else if ("\\b".equals(t)) string_buf.append("\b");
											else string_buf.append(t.substring(1));
										}
<STRING>[^\"\n\\]+                      { string_buf.append(yytext()); }



<YYINITIAL>{WSPACE}+                    { }
<YYINITIAL>\n							{curr_lineno++;}
<YYINITIAL>\"                           {
											string_buf.setLength(0);
											yybegin(STRING);
										}
<YYINITIAL>"*)"                         {return new Symbol(TokenConstants.ERROR, "Unmatched *)");}
<YYINITIAL>{INT}						{return new Symbol(TokenConstants.INT_CONST, intTable.addString(yytext()));}
<YYINITIAL>t[rR][uU][eE]        		{return new Symbol(TokenConstants.BOOL_CONST, true);}
<YYINITIAL>f[aA][lL][sS][eE]    		{return new Symbol(TokenConstants.BOOL_CONST, false);}
<YYINITIAL>[oO][fF]                     {return new Symbol(TokenConstants.OF);}
<YYINITIAL>[iI][nN]                     {return new Symbol(TokenConstants.IN);}
<YYINITIAL>[iI][fF]                     {return new Symbol(TokenConstants.IF);}
<YYINITIAL>[fF][iI]                     {return new Symbol(TokenConstants.FI);}
<YYINITIAL>[nN][eE][wW]                 {return new Symbol(TokenConstants.NEW);}
<YYINITIAL>[nN][oO][tT]                 {return new Symbol(TokenConstants.NOT);}
<YYINITIAL>[lL][eE][tT]                 {return new Symbol(TokenConstants.LET);}
<YYINITIAL>[eE][lL][sS][eE]             {return new Symbol(TokenConstants.ELSE);}
<YYINITIAL>[tT][hH][eE][nN]             {return new Symbol(TokenConstants.THEN);}
<YYINITIAL>[cC][aA][sS][eE]             {return new Symbol(TokenConstants.CASE);}
<YYINITIAL>[eE][sS][aA][cC]             {return new Symbol(TokenConstants.ESAC);}
<YYINITIAL>[lL][oO][oO][pP]             {return new Symbol(TokenConstants.LOOP);}
<YYINITIAL>[pP][oO][oO][lL]             {return new Symbol(TokenConstants.POOL);}
<YYINITIAL>[wW][hH][iI][lL][eE]         {return new Symbol(TokenConstants.WHILE);}
<YYINITIAL>[cC][lL][aA][sS][sS]         {return new Symbol(TokenConstants.CLASS);}
<YYINITIAL>[iI][sS][vV][oO][iI][dD]     {return new Symbol(TokenConstants.ISVOID);}
<YYINITIAL>[iI][nN][hH][eE][rR][iI][tT][sS]     {return new Symbol(TokenConstants.INHERITS);}
<YYINITIAL>[A-Z][_a-zA-Z0-9]*           {return new Symbol(TokenConstants.TYPEID, idTable.addString(yytext()));}
<YYINITIAL>[_a-z][_a-zA-Z0-9]*          {return new Symbol(TokenConstants.OBJECTID, idTable.addString(yytext()));}
<YYINITIAL>"@"							{return new Symbol(TokenConstants.AT);}
<YYINITIAL>"~"							{return new Symbol(TokenConstants.NEG);}
<YYINITIAL>";"							{return new Symbol(TokenConstants.SEMI);}
<YYINITIAL>":"							{return new Symbol(TokenConstants.COLON);}
<YYINITIAL>"{"							{return new Symbol(TokenConstants.LBRACE);}
<YYINITIAL>"}"							{return new Symbol(TokenConstants.RBRACE);}
<YYINITIAL>"("							{return new Symbol(TokenConstants.LPAREN);}
<YYINITIAL>")"							{return new Symbol(TokenConstants.RPAREN);}
<YYINITIAL>"+"							{return new Symbol(TokenConstants.PLUS);}
<YYINITIAL>"-"							{return new Symbol(TokenConstants.MINUS);}
<YYINITIAL>"*"							{return new Symbol(TokenConstants.MULT);}
<YYINITIAL>"/"							{return new Symbol(TokenConstants.DIV);}
<YYINITIAL>","							{return new Symbol(TokenConstants.COMMA);}
<YYINITIAL>"."							{return new Symbol(TokenConstants.DOT);}
<YYINITIAL>"="							{return new Symbol(TokenConstants.EQ);}
<YYINITIAL>"<"							{return new Symbol(TokenConstants.LT);}
<YYINITIAL>"<="							{return new Symbol(TokenConstants.LE);}
<YYINITIAL>"<-"							{return new Symbol(TokenConstants.ASSIGN);}
<YYINITIAL>"=>"			                { /* Sample lexical rule for "=>" arrow.
                                             Further lexical rules should be defined
                                             here, after the last %% separator */
                                             return new Symbol(TokenConstants.DARROW); }

.                                       { /* This rule should be the very last
                                             in your lexical specification and
                                             will match match everything not
                                             matched by other lexical rules. */
                                             System.err.println("LEXER BUG - UNMATCHED: " + yytext()); 
											 return new Symbol(TokenConstants.ERROR, 
													 "Unmatched character: " + yytext());
										}
