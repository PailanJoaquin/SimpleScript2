package lib.src.tokenutil;

public class Symbol {
    private String lexeme;
    private String value;

    public Symbol(String lexeme, String value) {
        this.lexeme = lexeme;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Symbol: " + lexeme + " ( Value = " + value + ")";
    }
}