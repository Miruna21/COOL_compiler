parser grammar CoolParser;

options {
    tokenVocab = CoolLexer;
}

@header {
    package cool.parser;
}

/* Start parser rule
 */
program : (class_ SEMICOLON)* EOF;

class_ : CLASS type=TYPE (INHERITS parent=TYPE)? OPEN_BRACE (features+=feature SEMICOLON)* CLOSE_BRACE;
feature : attribute | method;

formal : name=ID COLON type=TYPE;
attribute : name=ID COLON type=TYPE (ASSIGN init=expr)?;
method : name=ID OPEN_BRACKET (formals+=formal (COMMA formals+=formal)*)? CLOSE_BRACKET COLON type=TYPE OPEN_BRACE body=expr CLOSE_BRACE;

expr : e=expr DOT method_name=ID OPEN_BRACKET (args+=expr (COMMA args+=expr)*)? CLOSE_BRACKET                       # explicit_dispatch
    | method_name=ID OPEN_BRACKET (args+=expr (COMMA args+=expr)*)? CLOSE_BRACKET                                   # simple_dispatch
    | e=expr AT parent=TYPE DOT method_name=ID OPEN_BRACKET (args+=expr (COMMA args+=expr)*)? CLOSE_BRACKET         # at_dispatch
    | IF cond=expr THEN thenBranch=expr ELSE elseBranch=expr FI                                                     # conditional
    | WHILE cond=expr LOOP body=expr POOL                                                                           # loop
    | OPEN_BRACE (e+=expr SEMICOLON)+ CLOSE_BRACE                                                                   # block
    | LET local_ (COMMA local_)* IN body=expr                                                                       # let
    | CASE cond=expr OF branches+=case_branch+ ESAC                                                                 # case
    | NEW type=TYPE                                                                                                 # new
    | ISVOID e=expr                                                                                                 # isvoid
    | NEGATE e=expr                                                                                                 # negate
    | left=expr op=(MULT | DIV) right=expr                                                                          # mult_div
    | left=expr op=(PLUS | MINUS) right=expr                                                                        # plus_minus
    | name=ID ASSIGN e=expr                                                                                         # assignment
    | left=expr op=(LESS | LESS_EQUAL | EQUAL) right=expr                                                           # cmp
    | NOT e=expr                                                                                                    # not
    | OPEN_BRACKET e=expr CLOSE_BRACKET                                                                             # paren
    | ID                                                                                                            # identifier
    | INT                                                                                                           # int
    | STRING                                                                                                        # string
    | TRUE                                                                                                          # true
    | FALSE                                                                                                         # false
    ;

local_ : name=ID COLON type=TYPE (ASSIGN init=expr)?;
case_branch : name=ID COLON type=TYPE IMPLICATION body=expr SEMICOLON;
