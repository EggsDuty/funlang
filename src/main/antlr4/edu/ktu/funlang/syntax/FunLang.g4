grammar FunLang;

program : statement* EOF ;

statement
    : varDecl
    | assignment
    | exprStmt
    | ifStmt
    | whileStmt
    | forStmt
    | funcDef
    | returnStmt
    | systemCall
    | block
    ;

// ---------------------- Declarations ----------------------
systemCall
    : CONSOLE '->' expr ';'
    ;

block
    : '{' statement* '}'
    ;

varDecl
    : type ID ( '=' expr )? ';'
    ;

assignment
    : ID '=' expr ';'
    ;

returnStmt
    : RETURN expr ';'
    ;

exprStmt
    : expr
    ;


// ---------------------- Control Flow ----------------------

ifStmt
    : IF expr THEN block ( ELSE IF expr THEN block )* ( ELSE block )?
    ;

whileStmt
    : WHILE expr DO block
    ;

forStmt
    : FOR ID IN expr ':' expr ( STEP expr )? DO block
    ;


// ---------------------- Functions ----------------------

funcDef
    : FUNCTION ID '(' paramList? ')' block
    ;

paramList
    : param (',' param)*
    ;

param
    : type ID
    ;

functionCall
    : ID '(' argList? ')'
    ;

argList
    : expr (',' expr)*
    ;


// ---------------------- Expressions ----------------------

expr
    : chainExpr
    ;

// chaining operators: =>, <=, <=>
chainExpr
    : binaryExpr ( chainOp binaryExpr )*
    ;

chainOp
    : '=>'
    | '<='
    | '<=>'
    ;

// arithmetic & comparison
binaryExpr
    : binaryExpr ('+'|'-') binaryExpr
    | binaryExpr ('*'|'/') binaryExpr
    | binaryExpr ('<'|'>'|'=='|'!=' ) binaryExpr
    | basicExpr
    ;

basicExpr
    : literal
    | ID
    | functionCall
    | '(' expr ')'
    ;

literal
    : INT
    | DECIMAL
    | STRING
    | TRUE
    | FALSE
    | listLiteral
    ;

listLiteral
    : '[' (expr (',' expr)*)? ']'
    ;


// ---------------------- Types ----------------------

type
    : 'integer'
    | 'decimal'
    | 'text'
    | 'boolean'
    | 'list' '(' type ')'
    ;


// ---------------------- Lexer ----------------------

IF       : 'IF' ;
THEN     : 'THEN' ;
ELSE     : 'ELSE' ;
WHILE    : 'WHILE' ;
FOR      : 'FOR' ;
IN       : 'IN' ;
STEP     : 'STEP' ;
DO       : 'DO' ;
RETURN   : 'RETURN' ;
FUNCTION : 'FUNCTION' ;
TRUE     : 'TRUE' ;
FALSE    : 'FALSE' ;

// I/O keywords
CONSOLE  : 'CONSOLE' ;
FILEFN   : 'FILE' ;

ID       : [a-z][a-zA-Z0-9_]* | [A-Z][a-zA-Z0-9_]* ;
INT      : [0-9]+ ;
DECIMAL  : [0-9]+ '.' [0-9]+ ;
STRING   : '"' .*? '"' ;

// Operators
COMMENT: ( '//' ~[\r\n]* | '/*' .*? '*/' ) -> skip ;
WS       : [ \t\r\n]+ -> skip ;
