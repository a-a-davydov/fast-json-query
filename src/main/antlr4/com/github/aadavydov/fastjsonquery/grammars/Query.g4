grammar Query;
   
GT : '>' ;
GE : '>=' ;
LT : '<' ;
LE : '<=' ;
EQ : '==' ;
NEQ : '!=' ;
 
LPAREN : '(' ;
RPAREN : ')' ;

NUM
   : '-'? INT ('.' [0-9] +)? EXP?
   ;

fragment INT
   : '0' | [1-9] [0-9]*
   ;
// no leading zeros

fragment EXP
   : [Ee] [+\-]? INT
   ;
// \- since - means "range" inside [...]

STRING
   : '"' (ESC | ~ ["\\])* '"'
   ;
fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;
fragment UNICODE
   : 'u' HEX HEX HEX HEX
   ;
fragment HEX
   : [0-9a-fA-F]
   ;

fragment IDENTIFIER 
    : [a-zA-Z_][a-zA-Z_0-9]*
    ;

INDEX
    : '.'? '[' INT ']'
    ;

CHILD_NODE
    : '.' (STRING | IDENTIFIER)
    ;

path
    : '@' (INDEX | CHILD_NODE)+
    ;

filter : logical_expr EOF;

logical_expr
    : logical_expr and logical_expr
    | logical_expr or logical_expr
    | comparison_expr      
    | path
    | LPAREN logical_expr RPAREN
    ;

and : '&&';
or :  '||';

comparison_expr
    : comparison_operand comp_operator comparison_operand
    ;

null_operand : 'null';
bool_operand : 'true' | 'false';
num_operand : NUM;
str_operand : STRING;

comparison_operand
    : path
    | null_operand
    | bool_operand
    | num_operand
    | str_operand;

comp_operator
    : GT
    | GE
    | LT
    | LE
    | EQ
    | NEQ
    ;

WS : [ \t\n\r]+ -> skip ;


