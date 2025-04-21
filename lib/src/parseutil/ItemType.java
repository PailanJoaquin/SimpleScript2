package lib.src.parseutil;

public enum ItemType {
    //NON TERMINAL
    START_PRIME, PROGRAM_PRIME, STMT, SIMPLE_STMT, COMPOUND_STMT,
    DECL_STMT, ASSIGN_STMT, RETURN_STMT, DATA_TYPE, IO_STMT,
    SHOW_FUNC, GIVE_FUNC, LITERAL, BOOL, FUNC_DEC, RETURN_TYPE,
    PARAMS, PARAMS_PRIME, ARITHMETIC_OP, COMPARISON_OP, LOGICAL_OP,
    STOP_STMT, SKIP_STMT, CONDITIONAL_STMT, IF_STMT, IF_ELSE_STMT,
    ELSE_IF_STMT, LOOP_STMT, FOR_STMT, WHILE_STMT,
    EXP, EXP_PRIME, LOG, LOG_PRIME, REL, REL_OP, ADD, ADD_PRIME,
    ADDOP, MULTI, MULTI_PRIME, MULTIOP, UNARY, NOT_PRIME, PRIMARY,
    TYPE, INT, FLOAT, STRING, CHARACTERS, DIGIT, ID, ID_PRIME,
    LETTER, FUNCDEC, FUNCCALL, ARG, ARG_PRIME, PARAM, PARAM_PRIME,

    //TERMINAL

    let, be, return_, stop, skip, show, give, check, orcheck, otherwise,
    task, void_, string, int_, float_, bool, plus, minus, times, over,
    mod, is, isnt, less, more, lesseq, moreeq, and, or, not, yes, no,
    lparen, rparen, lbrace, rbrace, comma, semicolon, dot,
    squote, dquote, type_int, type_float, type_string, type_bool,
    int_literal, id, string_literal, float_literal, bool_literal,

    }
