package lib.src.parseutil;

import java.util.*;

public class Grammar {
    private final Map<ItemType, List<ProductionRule>> rules;

    public Grammar() {
        rules = new HashMap<>();
        defineGrammar();
    }

    private void defineGrammar() {
        // START_PRIME → STMT PROGRAM_PRIME
        addRule(ItemType.START_PRIME, list(ItemType.STMT, ItemType.PROGRAM_PRIME));

        // PROGRAM_PRIME → STMT PROGRAM_PRIME | ε
        addRule(ItemType.PROGRAM_PRIME, list(ItemType.STMT, ItemType.PROGRAM_PRIME));
        addRule(ItemType.PROGRAM_PRIME, list());

        // STMT → SIMPLE_STMT | COMPOUND_STMT
        addRule(ItemType.STMT, list(ItemType.SIMPLE_STMT));
        addRule(ItemType.STMT, list(ItemType.COMPOUND_STMT));

        // SIMPLE_STMT rules
        addRule(ItemType.SIMPLE_STMT, list(ItemType.DECL_STMT));
        addRule(ItemType.SIMPLE_STMT, list(ItemType.ASSIGN_STMT));
        addRule(ItemType.SIMPLE_STMT, list(ItemType.RETURN_STMT));
        addRule(ItemType.SIMPLE_STMT, list(ItemType.IO_STMT));
        addRule(ItemType.SIMPLE_STMT, list(ItemType.LITERAL));
        addRule(ItemType.SIMPLE_STMT, list(ItemType.STOP_STMT));
        addRule(ItemType.SIMPLE_STMT, list(ItemType.SKIP_STMT));

        // DECL_STMT → let DATA_TYPE IDENTIFIER ;
        addRule(ItemType.DECL_STMT, list(ItemType.let, ItemType.DATA_TYPE, ItemType.id, ItemType.semicolon));

        // ASSIGN_STMT → IDENTIFIER be LITERAL ; | ε
        addRule(ItemType.ASSIGN_STMT, list(ItemType.id, ItemType.be, ItemType.LITERAL, ItemType.semicolon));
        addRule(ItemType.ASSIGN_STMT, list());

        // RETURN_STMT → return LITERAL ;
        addRule(ItemType.RETURN_STMT, list(ItemType.return_, ItemType.LITERAL, ItemType.semicolon));

        // DATA_TYPE → string | int | float | bool
        addRule(ItemType.DATA_TYPE, list(ItemType.string));
        addRule(ItemType.DATA_TYPE, list(ItemType.int_));
        addRule(ItemType.DATA_TYPE, list(ItemType.float_));
        addRule(ItemType.DATA_TYPE, list(ItemType.bool));

        // IO_STMT → SHOW_FUNC | GIVE_FUNC
        addRule(ItemType.IO_STMT, list(ItemType.SHOW_FUNC));
        addRule(ItemType.IO_STMT, list(ItemType.GIVE_FUNC));

        // SHOW_FUNC → show ( SIMPLE_STMT ) ;
        addRule(ItemType.SHOW_FUNC, list(ItemType.show, ItemType.lparen, ItemType.SIMPLE_STMT, ItemType.rparen, ItemType.semicolon));

        // GIVE_FUNC → give ( SIMPLE_STMT ) ;
        addRule(ItemType.GIVE_FUNC, list(ItemType.give, ItemType.lparen, ItemType.SIMPLE_STMT, ItemType.rparen, ItemType.semicolon));

        // LITERAL → STRING | INT | FLOAT | BOOL
        addRule(ItemType.LITERAL, list(ItemType.STRING));
        addRule(ItemType.LITERAL, list(ItemType.INT));
        addRule(ItemType.LITERAL, list(ItemType.FLOAT));
        addRule(ItemType.LITERAL, list(ItemType.BOOL));

        // BOOL → yes | no
        addRule(ItemType.BOOL, list(ItemType.yes));
        addRule(ItemType.BOOL, list(ItemType.no));

        // STOP_STMT → stop ;
        addRule(ItemType.STOP_STMT, list(ItemType.stop, ItemType.semicolon));

        // SKIP_STMT → ε (define explicitly if needed)
        addRule(ItemType.SKIP_STMT, list());

        // COMPOUND_STMT → CONDITIONAL_STMT | LOOP_STMT | FUNC_DEC
        addRule(ItemType.COMPOUND_STMT, list(ItemType.CONDITIONAL_STMT));
        addRule(ItemType.COMPOUND_STMT, list(ItemType.LOOP_STMT));
        addRule(ItemType.COMPOUND_STMT, list(ItemType.FUNC_DEC));

        // CONDITIONAL_STMT → IF_STMT | IF_ELSE_STMT | ELSE_IF_STMT
        addRule(ItemType.CONDITIONAL_STMT, list(ItemType.IF_STMT));
        addRule(ItemType.CONDITIONAL_STMT, list(ItemType.IF_ELSE_STMT));
        addRule(ItemType.CONDITIONAL_STMT, list(ItemType.ELSE_IF_STMT));

        // IF_STMT → check ( SIMPLE_STMT ) { SIMPLE_STMT } IF_ELSE_STMT ELSE_IF_STMT
        addRule(ItemType.IF_STMT, list(ItemType.check, ItemType.lparen, ItemType.SIMPLE_STMT, ItemType.rparen,
                ItemType.lbrace, ItemType.SIMPLE_STMT, ItemType.rbrace, ItemType.IF_ELSE_STMT, ItemType.ELSE_IF_STMT));

        // IF_ELSE_STMT → orcheck ( SIMPLE_STMT ) { SIMPLE_STMT } IF_ELSE_STMT ELSE_IF_STMT | ε
        addRule(ItemType.IF_ELSE_STMT, list(ItemType.orcheck, ItemType.lparen, ItemType.SIMPLE_STMT, ItemType.rparen,
                ItemType.lbrace, ItemType.SIMPLE_STMT, ItemType.rbrace, ItemType.IF_ELSE_STMT, ItemType.ELSE_IF_STMT));
        addRule(ItemType.IF_ELSE_STMT, list());

        // ELSE_IF_STMT → otherwise { SIMPLE_STMT } | ε
        addRule(ItemType.ELSE_IF_STMT, list(ItemType.otherwise, ItemType.lbrace, ItemType.SIMPLE_STMT, ItemType.rbrace));
        addRule(ItemType.ELSE_IF_STMT, list());

        // LOOP_STMT → FOR_STMT | WHILE_STMT | STOP_STMT | SKIP_STMT
        addRule(ItemType.LOOP_STMT, list(ItemType.FOR_STMT));
        addRule(ItemType.LOOP_STMT, list(ItemType.WHILE_STMT));
        addRule(ItemType.LOOP_STMT, list(ItemType.STOP_STMT));
        addRule(ItemType.LOOP_STMT, list(ItemType.SKIP_STMT));

        // FUNC_DEC → task RETURN_TYPE IDENTIFIER ( PARAMS ) { SIMPLE_STMT | COMPOUND_STMT }
        addRule(ItemType.FUNC_DEC, list(ItemType.task, ItemType.RETURN_TYPE, ItemType.id,
                ItemType.lparen, ItemType.PARAMS, ItemType.rparen, ItemType.lbrace, ItemType.SIMPLE_STMT, ItemType.rbrace));
        addRule(ItemType.FUNC_DEC, list(ItemType.task, ItemType.RETURN_TYPE, ItemType.id,
                ItemType.lparen, ItemType.PARAMS, ItemType.rparen, ItemType.lbrace, ItemType.COMPOUND_STMT, ItemType.rbrace));

        // RETURN_TYPE → void | DATA_TYPE
        addRule(ItemType.RETURN_TYPE, list(ItemType.void_));
        addRule(ItemType.RETURN_TYPE, list(ItemType.DATA_TYPE));

        // PARAMS → DATA_TYPE IDENTIFIER PARAMS_PRIME
        addRule(ItemType.PARAMS, list(ItemType.DATA_TYPE, ItemType.id, ItemType.PARAMS_PRIME));

        // PARAMS_PRIME → , DATA_TYPE IDENTIFIER PARAMS_PRIME | ε
        addRule(ItemType.PARAMS_PRIME, list(ItemType.comma, ItemType.DATA_TYPE, ItemType.id, ItemType.PARAMS_PRIME));
        addRule(ItemType.PARAMS_PRIME, list());

        // ARITHMETIC_OP
        addRule(ItemType.ARITHMETIC_OP, list(ItemType.plus));
        addRule(ItemType.ARITHMETIC_OP, list(ItemType.minus));
        addRule(ItemType.ARITHMETIC_OP, list(ItemType.times));
        addRule(ItemType.ARITHMETIC_OP, list(ItemType.over));
        addRule(ItemType.ARITHMETIC_OP, list(ItemType.mod));

        // COMPARISON_OP
        addRule(ItemType.COMPARISON_OP, list(ItemType.is));
        addRule(ItemType.COMPARISON_OP, list(ItemType.isnt));
        addRule(ItemType.COMPARISON_OP, list(ItemType.less));
        addRule(ItemType.COMPARISON_OP, list(ItemType.more));
        addRule(ItemType.COMPARISON_OP, list(ItemType.lesseq));
        addRule(ItemType.COMPARISON_OP, list(ItemType.moreeq));

        // LOGICAL_OP
        addRule(ItemType.LOGICAL_OP, list(ItemType.and));
        addRule(ItemType.LOGICAL_OP, list(ItemType.or));
        addRule(ItemType.LOGICAL_OP, list(ItemType.not));
    }

    private void addRule(ItemType lhs, List<ItemType> rhs) {
        rules.computeIfAbsent(lhs, k -> new ArrayList<>()).add(new ProductionRule(lhs, rhs));
    }

    public List<ProductionRule> getRules(ItemType nonTerminal) {
        return rules.getOrDefault(nonTerminal, Collections.emptyList());
    }

    public List<ProductionRule> getAllRules() {
        List<ProductionRule> all = new ArrayList<>();
        for (List<ProductionRule> rlist : rules.values()) {
            all.addAll(rlist);
        }
        return all;
    }

    private List<ItemType> list(ItemType... symbols) {
        return Arrays.asList(symbols);
    }
}
