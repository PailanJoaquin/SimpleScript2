package lib.src.tokenutil;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Symbol> symbols;

    public SymbolTable() {
        symbols = new HashMap<>();
    }

    public void add(String lexeme, String tokenType) {
        symbols.put(lexeme, new Symbol(lexeme, tokenType));
    }

    public boolean contains(String lexeme) {
        return symbols.containsKey(lexeme);
    }

    public void printSymbols() {
        System.out.println("\n--- Symbol Table ---");
        for (Symbol symbol : symbols.values()) {
            System.out.println(symbol);
        }
    }
}