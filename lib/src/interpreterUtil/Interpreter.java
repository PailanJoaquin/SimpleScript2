package lib.src.interpreterUtil;

import lib.src.tokenutil.Token;
import lib.src.tokenutil.TokenType;

import java.util.Stack;

public class Interpreter {
    Stack <Token> inputStack;
    Stack <Token> tempStack = new Stack<>();
    Stack <Token> outputStack = new Stack<>();
    SymbolTable symbolTable = new SymbolTable();
    public Interpreter() {}
    public Interpreter(Stack<Token> inputStack)
    {
        this.inputStack = inputStack;
        evaluate();
    }

    public void evaluate() {
        while (!inputStack.isEmpty()) {
            Token current = inputStack.peek(); // Look at the top
            System.out.println("Evaluating " + current.getItem());
            switch (current.getItem()) {
                case "let" : handleDeclaration(); break;
                case "IDENTIFIER" : handleAssignmentOrExpression(); break;

                default:
                    throw new RuntimeException("Unexpected token: " + current.getLexeme() + " at line" + current.getLineNumber() + "and column" + current.getColumnNumber());
            }
        }
    }
    private void handleDeclaration() {
        inputStack.pop(); // consume 'let'
        Token dataType = inputStack.pop(); // string, int, etc.
        Token identifier = inputStack.pop(); // variable name
        Token semicolon = inputStack.pop(); // ;

        if (semicolon.getType() != TokenType.PUNCTUATION)
            throw new RuntimeException("Expected ';' after declaration at " + semicolon.getLineNumber() + ":" + semicolon.getColumnNumber());

        symbolTable.define(identifier.getLexeme(), null, dataType.getType()); // Save as null
    }
    private void handleAssignmentOrExpression() {
        Token identifier = inputStack.pop(); // variable name
        Token be = inputStack.pop(); // "be"
        if (be.getType() != TokenType.ASSIGNMENT)
            throw new RuntimeException("Expected 'be' at line" + be.getLineNumber() + " and column" + be.getColumnNumber());

        Object value = evaluateExpression(); // Evaluate expr until semicolon
        Token semicolon = inputStack.pop();
        if (semicolon.getType() != TokenType.PUNCTUATION)
            throw new RuntimeException("Expected ';' after expression at line" + semicolon.getLineNumber() + " and column" + semicolon.getColumnNumber());

        symbolTable.assign(identifier.getLexeme(), value); // Update value
    }
    private Object evaluateExpression() {
        Token token = inputStack.pop();
        return Integer.parseInt(token.getLexeme());
        //throw new RuntimeException("Invalid expression token: " + token.getLexeme()+" at line" + token.getLineNumber() + "and column" + token.getColumnNumber());

    }




    public void printSymbolTable() {
        symbolTable.printSymbols();
    }
}
