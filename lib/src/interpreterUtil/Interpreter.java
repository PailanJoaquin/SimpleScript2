package lib.src.interpreterUtil;

import lib.src.tokenutil.Token;
import lib.src.tokenutil.TokenType;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static lib.src.interpreterUtil.interUtil.evaluatePostfix;
import static lib.src.interpreterUtil.interUtil.infixToPostfix;

public class Interpreter {
    Stack <Token> inputStack;
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
        String type;

        if (dataType.getLexeme().equals("string"))
            type = "String";
        else if (dataType.getLexeme().equals("int"))
            type = "Integer";
        else if (dataType.getLexeme().equals("float"))
            type = "Float";
        else if (dataType.getLexeme().equals("bool"))
            type = "Boolean";
        else
            throw new RuntimeException("Unknown data type: " + dataType.getLexeme() + " at line " + dataType.getLineNumber() + "and column " + dataType.getColumnNumber());
        if (semicolon.getType() != TokenType.PUNCTUATION)
            throw new RuntimeException("Expected ';' after declaration at " + semicolon.getLineNumber() + ":" + semicolon.getColumnNumber());

        symbolTable.define(identifier.getLexeme(), null, type); // Save as null
    }
    private void handleAssignmentOrExpression() {
        Token identifier = inputStack.pop(); // variable name
        Token be = inputStack.pop(); // "be"
        Object value = null;
        if (be.getType() != TokenType.ASSIGNMENT)
            throw new RuntimeException("Expected 'be' at line" + be.getLineNumber() + " and column" + be.getColumnNumber());
        if (symbolTable.getType(identifier.getLexeme()).matches("String|Boolean"))
        {
            value = inputStack.pop().getLexeme();
        }
        else
        {
            value = evaluateExpression(); // Evaluate expr until semicolon
        }
        Token semicolon = inputStack.pop();
        if (semicolon.getType() != TokenType.PUNCTUATION)
            throw new RuntimeException("Expected ';' after expression at line " + semicolon.getLineNumber() + " and column " + semicolon.getColumnNumber());

        symbolTable.assign(identifier.getLexeme(), value); // Update value
    }
    private Object evaluateExpression() {
        Stack <Token> arithmeticStack = new Stack<>();
        Stack <Token> tempStack = new Stack<>();
        String expression = "";
        while (inputStack.peek().getLexeme().matches("[^;]+"))
            {
                arithmeticStack.push(inputStack.pop());
            }
        expression = evaluatePostfix(infixToPostfix(arithmeticStack));
        //EVALUATION
        return expression;
        //throw new RuntimeException("Invalid expression token: " + token.getLexeme()+" at line" + token.getLineNumber() + "and column" + token.getColumnNumber());
    }





    private List<String> reverseStack(Stack<Token> stack) {
        List<String> tokens = new ArrayList<>();
        Stack<Token> copy = (Stack<Token>) stack.clone();
        while (!copy.isEmpty()) {
            tokens.add(0, copy.pop().getLexeme());
        }
        return tokens;
    }
    public void printSymbolTable() {
        symbolTable.printSymbols();
    }
}
