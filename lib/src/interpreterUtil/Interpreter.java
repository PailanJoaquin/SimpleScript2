package lib.src.interpreterUtil;

import lib.src.tokenutil.Token;
import lib.src.tokenutil.TokenType;

import java.util.*;

import static lib.src.interpreterUtil.interUtil.*;

public class Interpreter {
    Stack <Token> inputStack;
    SymbolTable symbolTable = new SymbolTable();
    SymbolTable loopTable = new SymbolTable();
    List<SymbolTable>loopTables = new ArrayList<>();
    public Interpreter() {}
    public Interpreter(Stack<Token> inputStack)
    {
        this.inputStack = inputStack;
        evaluate();
    }

    public void evaluate() {
        while (!inputStack.isEmpty()) {
            Token current = inputStack.peek(); // Look at the top
            //System.out.println("Evaluating " + current.getItem());//DEBUG
            switch (current.getItem()) {
                case "let" : handleDeclaration(); break;
                case "IDENTIFIER" : handleAssignmentOrExpression(); break;
                case "show" : handleShow(); break;
                case "give" : handleGive(); break;
                case "repeat" : handleForLoop(); break;

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
        if (symbolTable.getType(identifier.getLexeme()).matches("String"))
        {
            System.out.println("detected : " + identifier.getLexeme());
            value = inputStack.pop().getLexeme();
        }
        else
        {
            value = evaluateExpression(); // Evaluate expr until semicolon
        }
        Token semicolon = inputStack.pop();
        if (semicolon.getType() != TokenType.PUNCTUATION)
            throw new RuntimeException("Expected ';' after expression but get " + semicolon.getLexeme() + " at line " + semicolon.getLineNumber() + " and column " + semicolon.getColumnNumber());

        symbolTable.assign(identifier.getLexeme(), value); // Update value
    }
    private void handleShow() {
        inputStack.pop(); // show
        inputStack.pop(); // (
        Token var = inputStack.pop(); // IDENTIFIER
        inputStack.pop(); // )
        inputStack.pop(); // ;

        Object value = symbolTable.get(var.getLexeme());
        System.out.println(value);
    }

    private void handleGive() {
        inputStack.pop(); // give
        inputStack.pop(); // (
        Token var = inputStack.pop(); // IDENTIFIER
        inputStack.pop(); // )
        inputStack.pop(); // ;

        System.out.print("Enter value for " + var.getLexeme() + ": ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        symbolTable.assign(var.getLexeme(), input); // assume string input
    }
    private Object evaluateExpression() {
        Stack <Token> arithmeticStack = new Stack<>();
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
    private void handleForLoop() {
        System.out.println(inputStack);
        loopTable = this.symbolTable;
        inputStack.pop(); // pop 'repeat'
        inputStack.pop(); // pop '('

        // ---- Initialization: let int i ;
        inputStack.pop(); // pop 'let'
        String dataType = inputStack.pop().getLexeme(); // int, float, etc.
        String identifier = inputStack.pop().getLexeme(); // e.g., iclare variable
        // ---- Initial Assignment: i be <expr> ;
        inputStack.pop(); // pop 'be'
        Object initValue = evaluateExpression(); // evaluate EXPR
        loopTable.define(identifier, initValue, dataType);
        inputStack.pop(); // pop ';'

        // ---- Condition: i be <expr> ;
        String condVar = inputStack.pop().getLexeme(); // i
        List<Token> conditionExpr = readTokensUntil(";"); // <expr>

        // ---- Update: i be <expr> )
        String updateVar = inputStack.pop().getLexeme(); // i
        inputStack.pop(); // pop 'be'
        List<Token> updateExpr = readTokensUntil(")"); //example = "i be i plus 1"

        inputStack.pop(); // pop '{'

        // ---- Extract body tokens until matching '}'
        Stack<Token> bodyTokens = readBlockTokens();
        bodyTokens = reverseStack(bodyTokens);

        // ---- Run the loop
        while (evaluateCondition(condVar, conditionExpr)) {
            Interpreter bodyInterpreter = new Interpreter(bodyTokens);
            bodyInterpreter.symbolTable = loopTable; // share symbol table


            // Re-evaluate updateExpr each loop

            Iterator<Token> iterator = updateExpr.iterator();
            Stack<Token> tempStack = new Stack<>();
            while (iterator.hasNext()) {
                tempStack.push(iterator.next());
            }
            tempStack = reverseStack(tempStack);
            Iterator<Token> tempIterator = tempStack.iterator();
            while (tempIterator.hasNext()) {
                inputStack.push(tempIterator.next());
            }
            System.out.println(inputStack);
            Object updatedValue = evaluateExpression();
            loopTable.assign(updateVar, updatedValue);
        }
    }
    private String infixToPostfix(Stack<Token> infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<String> stack = new Stack<>();

        for (Token token : infix) {
            String lexeme = token.getLexeme();
            if (isFloat(lexeme) || isInteger(lexeme) ||isVariable(lexeme)) {
                postfix.append(lexeme).append(" ");
            } else if (lexeme.equals("(")) {
                stack.push(lexeme);
            } else if (lexeme.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.append(stack.pop()).append(" ");
                }
                if (!stack.isEmpty()) stack.pop(); // Pop '('
            } else if (isArithmeticOp(lexeme) || isComparisonOp(lexeme)) {
                while (!stack.isEmpty() && precedence(lexeme) <= precedence(stack.peek())) {
                    postfix.append(stack.pop()).append(" ");
                }
                stack.push(lexeme);
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop()).append(" ");
        }
        return postfix.toString();
    }
    private boolean isVariable(String lexeme) {
        return symbolTable.contains(lexeme);
    }
    private List<Token> readTokensUntil(String delimiter) {
        System.out.println("Reading tokens until " + delimiter);
        List<Token> tokens = new ArrayList<>();
        while (!inputStack.peek().getLexeme().equals(delimiter)) {
            System.out.println("Popping: " + inputStack.peek().getLexeme());
            tokens.add(inputStack.pop());
        }
        inputStack.pop(); // pop delimiter
        return tokens;
    }

    private Stack<Token> readBlockTokens() {
        Stack<Token> body = new Stack<>();
        int openBraces = 1;
        while (openBraces > 0) {
            Token token = inputStack.pop();
            if (token.getLexeme().equals("{")) openBraces++;
            else if (token.getLexeme().equals("}")) openBraces--;
            if (openBraces > 0) body.add(token);
        }
        return body;
    }

    private boolean evaluateCondition(String varName, List<Token> expr) {
        loopTable.assign(varName, "int"); // just in case
        Object value = evaluateExpression();
        return toBoolean(value);
    }
    public boolean toBoolean(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).doubleValue() != 0;
        return value != null;
    }
    public void printSymbolTable() {
        symbolTable.printSymbols();
    }
    public static Stack<Token> reverseStack(Stack<Token> originalStack) {
        Stack<Token> reversedStack = new Stack<>();
        while (!originalStack.isEmpty()) {
            reversedStack.push(originalStack.pop());
        }
        return reversedStack;
    }
}
