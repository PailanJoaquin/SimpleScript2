package lib.src.interpreterUtil;

import lib.src.tokenutil.Token;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class interUtil {
    public static boolean isString(String word) {
        return word.matches("[^\"]*\"");
    }

    public static boolean isBoolean(String word) {
        return word.matches("yes|no");
    }

    public static boolean isInteger(String word) {
        return word.matches("\\d+");
    }

    public static boolean isFloat(String word) {
        return word.matches("[+-]?\\d+\\.\\d+");
    }

    public static boolean isArithmeticOp(String word) {
        return word.matches("plus|minus|times|over|mod");
    }

    public static int precedence(String op) {
        switch (op) {
            case "plus":
            case "minus":
                return 1;
            case "times":
            case "mod":
            case "over":
                return 2;
            default:
                return -1;
        }
    }

    public static String infixToPostfix(Stack<Token> infix) {
        StringBuilder postfix = new StringBuilder();
        Stack<String> stack = new Stack<>();

        for (Token token : infix) {
            if (isFloat(token.getLexeme()) || isInteger(token.getLexeme())) {
                postfix.append(token.getLexeme()+" "); // Append operand
            } else if (token.getLexeme() == "(") {
                stack.push(token.getLexeme());
            } else if (token.getLexeme() == ")") {
                while (!stack.isEmpty() && stack.peek() != "(") {
                    postfix.append(stack.pop()+" ");
                }
                stack.pop(); // Pop '('
            } else if (isArithmeticOp(token.getLexeme())) {
                while (!stack.isEmpty() && precedence(token.getLexeme()) <= precedence(stack.peek())) {
                    postfix.append(stack.pop()+" ");
                }
                stack.push(token.getLexeme());
            }
        }

        while (!stack.isEmpty()) {
            postfix.append(stack.pop()+" ");
        }
        return postfix.toString();
    }

    public static String evaluatePostfix(String postfix) {
        Stack<String> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+"); // Split by spaces to handle multi-digit numbers
        //System.out.println("Evaluating postfix expression: " + postfix);



        for (String token : tokens) {
            if (isArithmeticOp(token)) {
                // Pop two operands
                String a = stack.pop();
                String b = stack.pop();

                // Apply the operator and push the result back
                //System.out.println("Applying operator: " + a + " " + token + " " + b);
                String result = applyOperatorInteger(a, b, token);
                stack.push(result);
            } else {
                // It's an operand (number), push it onto the stack
                stack.push(token);
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
}

