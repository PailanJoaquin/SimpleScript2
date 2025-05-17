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
            // System.out.println("Evaluating " + current.getItem());//DEBUG
            switch (current.getItem()) {
                case "let" : handleDeclaration(); break;
                case "IDENTIFIER" : handleAssignmentOrExpression(); break;
                case "show" : handleShow(); break;
                case "give" : handleGive(); break;
                case "repeat" : handleForLoop(); break;
                case "check": handleIfStatement(); break;
                case "orcheck": handleOrcheckStatement(); break;
                case "otherwise": handleOtherwiseStatement(); break;
                case "while": handleWhileStatement(); break;

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
            Object updatedValue = evaluateExpression();
            loopTable.assign(updateVar, updatedValue);
        }
    }
    private void handleIfStatement() {

        // CHECK STATEMENT HANDLER
        inputStack.pop(); // pop 'check'
        inputStack.pop(); // pop '('

        List<Token> checkConditionTokens = readTokensUntil(")");// read condition
        Iterator<Token> checkConditionIterator = checkConditionTokens.iterator();
        Stack<Token> checkConditionStack = new Stack<>();
        while (checkConditionIterator.hasNext()) {
            checkConditionStack.push(checkConditionIterator.next());
        }
        checkConditionStack = reverseStack(checkConditionStack);
        inputStack.pop(); // pop '{'
        Stack<Token> checkBody = readBlockTokens();

        // Evaluate 'check' condition
        boolean checkCondition = toBoolean(evaluatePostfix(infixToPostfix(reverseStack(checkConditionStack))));
        if (checkCondition) {
            Interpreter checkInterpreter = new Interpreter();
            checkInterpreter.symbolTable = this.symbolTable;
            checkInterpreter.inputStack = reverseStack(checkBody);
            checkInterpreter.evaluate();
        }

        if (!checkCondition && inputStack.peek().getLexeme().equals("orcheck")) {
                handleOrcheckStatement();
        }


    }

    private void handleOrcheckStatement() {
        inputStack.pop(); // pop 'orcheck'
        inputStack.pop(); // pop '('
        List<Token> orcheckConditionTokens = readTokensUntil(")");
        Iterator<Token> orcheckConditionIterator = orcheckConditionTokens.iterator();
        Stack<Token> orcheckConditionStack = new Stack<>();
        while (orcheckConditionIterator.hasNext()) {
            orcheckConditionStack.push(orcheckConditionIterator.next());
        }
        orcheckConditionStack = reverseStack(orcheckConditionStack);
        inputStack.pop(); // pop '{'
        Stack<Token> orcheckBody = readBlockTokens();

        // Evaluate 'orcheck' condition
        boolean orcheckCondition = toBoolean(evaluatePostfix(infixToPostfix(orcheckConditionStack)));
        if (orcheckCondition) {
            Interpreter orcheckInterpreter = new Interpreter();
            orcheckInterpreter.symbolTable = this.symbolTable;
            orcheckInterpreter.inputStack = reverseStack(orcheckBody);
            orcheckInterpreter.evaluate();
        }

        if (!orcheckCondition && inputStack.peek().getLexeme().equals("otherwise")) handleOtherwiseStatement();
    }

    private void handleOtherwiseStatement() {
        inputStack.pop(); // pop 'otherwise'
        inputStack.pop(); // pop '{'

        Stack<Token> otherwiseBody = readBlockTokens();

        Interpreter otherwiseInterpreter = new Interpreter();
        otherwiseInterpreter.symbolTable = this.symbolTable;
        otherwiseInterpreter.inputStack = reverseStack(otherwiseBody);
        otherwiseInterpreter.evaluate();
    }

    private void handleWhileStatement() {
        inputStack.pop(); // pop 'while'
        inputStack.pop(); // pop '('

        // Read condition tokens until ')'
        List<Token> conditionTokens = readTokensUntil(")");

        // Prepare the condition stack (reverse for correct order)
        Stack<Token> conditionStack = new Stack<>();
        for (Token t : conditionTokens) {
            conditionStack.push(t);
        }
        conditionStack = reverseStack(conditionStack);

        inputStack.pop(); // pop '{'
        Stack<Token> bodyTokens = readBlockTokens(); // get loop body
        bodyTokens = reverseStack(bodyTokens);

        while (toBoolean(evaluatePostfix(infixToPostfix(conditionStack)))) {
            // Clone body tokens each time to avoid exhaustion
            Stack<Token> loopBodyClone = new Stack<>();
            loopBodyClone.addAll(bodyTokens);

            Interpreter bodyInterpreter = new Interpreter();
            bodyInterpreter.symbolTable = this.symbolTable; // share same symbol table
            bodyInterpreter.inputStack = loopBodyClone;
            bodyInterpreter.evaluate();

            // Reset condition stack for re-evaluation
            conditionStack = new Stack<>();
            for (Token t : conditionTokens) {
                conditionStack.push(t);
            }
            conditionStack = reverseStack(conditionStack);
        }
    }


    private String infixToPostfix(Stack<Token> infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<String> stack = new Stack<>();

        for (Token token : infix) {
            String lexeme = token.getLexeme();
            if (isFloat(lexeme) || isInteger(lexeme) ||isVariable(lexeme) ||isBoolean(lexeme)||isString(lexeme)) {
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
        //System.out.println("Reading tokens until " + delimiter);
        List<Token> tokens = new ArrayList<>();
        while (!inputStack.peek().getLexeme().equals(delimiter)) {
            //System.out.println("Popping: " + inputStack.peek().getLexeme());
            tokens.add(inputStack.pop());
        }
        inputStack.pop(); // pop delimiter
        return tokens;
    }
    private Stack<Token> readBlockTokens() {
        Stack<Token> body = new Stack<>();
        int openBraces = 1;
        while (openBraces > 0) {
            //System.out.println("Reading block tokens: " + inputStack.peek().getLexeme());
            Token token = inputStack.pop();
            if (token.getLexeme().equals("{"))
            {
                openBraces++;
            }
            else if (token.getLexeme().equals("}"))
            {
                openBraces--;
            }
            if (openBraces > 0) body.push(token);
        }
        return body;
    }
    private boolean evaluateCondition(String varName, List<Token> expr) {
        loopTable.assign(varName, "int"); // just in case
        return false;
    }
    public boolean toBoolean(String value) {
        if (value.equals("yes")) return true;
        else if (value.equals("no"))
            return false;
        else
            System.err.println("Error: Invalid value '" + value + "'");
        return false;
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
    public String evaluatePostfix(String postfix) {
        Stack<String> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+"); // Split by spaces to handle multi-digit numbers
        //System.out.println("Evaluating postfix expression: " + postfix);
        String result="";

        for (String token : tokens) {
            if (isArithmeticOp(token)) {
                // Pop two operands
                String a = stack.pop();
                String b = stack.pop();

                if (isVariable(a)){
                    a = (String)symbolTable.get(a);
                }
                if (isVariable(b)){
                    b = (String)symbolTable.get(b);
                }
                // Apply the operator and push the result back
                if(isInteger(a) && isInteger(b)){
                    result = applyOperatorInteger(a, b, token);
                }
                else if(isFloat(a) || isFloat(b)){
                    result = applyOperatorFloat(a, b, token);
                }
                else {
                    throw new IllegalArgumentException("Type mismatch");
                }
                stack.push(result);
            } else if (isComparisonOp(token)) {
                String b = stack.pop();
                String a = stack.pop();
                if (isVariable(a)){
                    a = (String)symbolTable.get(a);
                    if (a == "yes")
                        a = "true";
                }
                if (isVariable(b)){
                    b = (String)symbolTable.get(b);
                    if (b == "no")
                        b = "false";
                }
                result = applyComparison(a, b, token);
                if(result.equals("true")) result="yes";
                else if(result.equals("false")) result="no";
                else throw new IllegalArgumentException("Type mismatch");
                stack.push(result); // Push result as string "true"/"false"

            } else {
                stack.push(token); // Operand
            }
        }

        // The final result will be the only element left in the stack
        return stack.pop();
    }
    private static String applyOperatorInteger(String b, String a, String operator) {
        switch (operator) {
            case "plus":
                return String.valueOf(Integer.parseInt(a) + Integer.parseInt(b));
            case "minus":
                return String.valueOf(Integer.parseInt(a) - Integer.parseInt(b));
            case "times":
                return String.valueOf(Integer.parseInt(a) * Integer.parseInt(b));
            case "over":
                return String.valueOf(Integer.parseInt(a) / Integer.parseInt(b));
            case "mod":
                return String.valueOf(Integer.parseInt(a) % Integer.parseInt(b));
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
    private static String applyOperatorFloat(String b, String a, String operator) {
        switch (operator) {
            case "plus":
                return String.valueOf(Float.parseFloat(a) + Float.parseFloat(b));
            case "minus":
                return String.valueOf(Float.parseFloat(a) - Float.parseFloat(b));
            case "times":
                return String.valueOf(Float.parseFloat(a) * Float.parseFloat(b));
            case "over":
                return String.valueOf(Float.parseFloat(a) / Float.parseFloat(b));
            case "mod":
                return String.valueOf(Float.parseFloat(a) % Float.parseFloat(b));
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
    public static String applyComparison(String a, String b, String op) {
        if (!a.matches("true|false")&&!b.matches("true|false"))
        {
            double left = Double.parseDouble(a);
            double right = Double.parseDouble(b);

            switch (op) {
                case "is":
                    return String.valueOf(left == right);
                case "isnt":
                    return String.valueOf(left != right);
                case "less":
                    return String.valueOf(left < right);
                case "more":
                    return String.valueOf(left > right);
                case "lesseq":
                    return String.valueOf(left <= right);
                case "moreeq":
                    return String.valueOf(left >= right);
                default:
                    throw new IllegalArgumentException("Unknown comparison operator: " + op);
            }
        }
        else
        {
            boolean left = Boolean.getBoolean(a);
            boolean right = Boolean.getBoolean(b);

            switch (op) {
                case "is":
                    return left == right ? "true" : "false";
                case "isnt":
                    return left == right ? "true" : "false";
                default:
                    throw new IllegalArgumentException("Unknown comparison operator: " + op);
            }
        }
    }
}
