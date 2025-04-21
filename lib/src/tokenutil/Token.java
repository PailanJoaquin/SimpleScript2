package lib.src.tokenutil;

import lib.src.tokenutil.TokenType;

public class Token {
    private TokenType type;
    private String lexeme;
    private int lineNumber;
    private int columnNumber;

    public Token(TokenType type, String lexeme, int lineNumber, int columnNumber) {
        this.type = type;
        this.lexeme = lexeme;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }


    public String toString() {
        switch (type) {
            case IDENTIFIER:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case ARITHMETIC_OP:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case COMPARISON_OP:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case LOGICAL_OP:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case ASSIGNMENT:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case CONDITIONAL:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case LOOP:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case FUNCTION:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case DATA_TYPE:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case LITERAL:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case PUNCTUATION:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            case EOF:
                return "Word = [" + lexeme + "] Type = [" + type + "] at (Line"   + lineNumber + ", Column " + columnNumber + ") ";
            default:
                return "non-token";
        }
    }
}
