grammar FunLang;

program : statement* EOF ;

statement
    : letDecl ';'
    | assignment ';'
    | printStmt ';'
    | ifStmt    // NOTe - no semicolon
    ;

letDecl    : LET ID ( '=' expr )? ;
assignment : ID '=' expr ;
printStmt  : PRINT '(' expr ')' ;

ifStmt     : IF '(' expr ')' statement (ELSE statement)? ; // NOTE - for now we support single statement

expr
    : INT
    | ID
    ;

// ---- Lexer ----
LET    : 'let' ;
PRINT  : 'print' ;
IF    : 'if' ;
ELSE  : 'else' ;

ID     : [a-zA-Z_][a-zA-Z_0-9]* ; //NOTE - must be after keywords
INT    : [0-9]+ ; // NOTE - negative numbers not supported

COMMENT: ( '//' ~[\r\n]* | '/*' .*? '*/' ) -> skip ;
WS     : [ \t\r\n]+ -> skip ;

// NOTE - you may not skip comments/whitespace, or use channel(HIDDEN)
// to get hidden tokens, use:
// List<Token> hidden = tokens.getHiddenTokensToLeft(index);
