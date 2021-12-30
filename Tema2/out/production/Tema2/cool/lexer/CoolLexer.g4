lexer grammar CoolLexer;

tokens { ERROR } 

@header {
    package cool.lexer;
}

@members {
    private void raiseError(String msg) {
        setText(msg);
        setType(ERROR);
    }
}

/* Keywords
 */
CLASS : [cC][lL][aA][sS][sS];
INHERITS : [iI][nN][hH][eE][rR][iI][tT][sS];
NEW : [nN][eE][wW];

CASE : [cC][aA][sS][eE];
OF : [oO][fF];
ESAC : [eE][sS][aA][cC];

IF : [iI][fF];
THEN : [tT][hH][eE][nN];
ELSE : [eE][lL][sS][eE];
FI : [fF][iI];

WHILE : [wW][hH][iI][lL][eE];
LOOP : [lL][oO][oO][pP];
POOL : [pP][oO][oO][lL];

LET : [lL][eE][tT];
IN : [iI][nN];

ISVOID : [iI][sS][vV][oO][iI][dD];
NOT : [nN][oO][tT];

TRUE : 't'[rR][uU][eE];
FALSE : 'f'[aA][lL][sS][eE];

/* Symbols
*/
AT : '@';
IMPLICATION : '=>';
DOT : '.';
COMMA : ',';
COLON : ':';
SEMICOLON : ';';
OPEN_BRACKET : '(';
CLOSE_BRACKET : ')';
OPEN_BRACE : '{';
CLOSE_BRACE : '}';

/* Arithmetic operators
*/
PLUS : '+';
MINUS : '-';
MULT : '*';
DIV : '/';
NEGATE : '~';

/* Comparison operators
*/
EQUAL : '=';
LESS : '<';
LESS_EQUAL : '<=';

/* Assign operator
*/
ASSIGN : '<-';

/* Integer
 */
fragment DIGIT : [0-9];
INT : DIGIT+;

/* Identifier
 */
fragment LETTER: [a-zA-Z];
fragment LOWER_LETTER : [a-z];
fragment UPPER_LETTER : [A-Z];

ID : (LOWER_LETTER | '_')(LETTER | '_' | DIGIT)*;

/* Type
*/
TYPE : UPPER_LETTER (LETTER | '_' | DIGIT)*;

/* String
 */
STRING : '"' ('\\"' | '\\' NEW_LINE
             | '\u0000' { raiseError(" String contains null character "); }
             | .)*? ('"' {
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
                        }
                    | NEW_LINE { raiseError("Unterminated string constant"); }
                    | EOF { raiseError("EOF in string constant"); });

/* Comment
*/
fragment NEW_LINE : '\r'? '\n';

LINE_COMMENT : '--' .*? (NEW_LINE | EOF) -> skip;

UNMATCHED_COMMENT : '*)' { raiseError("Unmatched *)"); };

BLOCK_COMMENT : '(*' (BLOCK_COMMENT | .)*? ('*)' { skip(); } | EOF { raiseError("EOF in comment"); });

/* Whitespaces
 */
WS : [ \n\f\r\t]+ -> skip;

INVALID_CHARACTER : . { raiseError("Invalid character: " + getText()); };