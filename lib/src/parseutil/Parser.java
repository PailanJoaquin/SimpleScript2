package lib.src.parseutil;

import lib.src.tokenutil.*;
import java.util.Stack;

public class Parser {
    private final Stack<Token> tokens;
    private int current = 0;

    public Parser(Stack<Token> tokens) {
        this.tokens = tokens;
    }

    public void Parse() {

    }

    public ItemType getItemType(Token token)
    {
        Items item = new Items(token);
        return item.getType();
    }

    private boolean isAtEnd() {
        return current >= tokens.size() || tokens.get(current).getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void parseStmt() {
        ItemType type = getItemType(peek());
        if (type == null) {
            error("Unknown token: " + peek().getLexeme());
            return;
        }
        switch (type) {
            case DECL_STMT:
            case ASSIGN_STMT:
            case RETURN_STMT:
            case IO_STMT:
            case LITERAL:
            case STOP_STMT:
            case SKIP_STMT:
                //parseSimpleStmt();
                break;
            case CONDITIONAL_STMT:
            case LOOP_STMT:
            case FUNC_DEC:
                //parseCompoundStmt();
                break;
            default:
                error("Unexpected statement: " + peek().getLexeme());
        }
    }

    private void error(String message) {
        throw new RuntimeException(message);
    }

    public void printCurrentToken() {
        System.out.println(tokens.peek());
    }
}
