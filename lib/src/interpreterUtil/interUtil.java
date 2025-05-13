package lib.src.interpreterUtil;

import lib.src.tokenutil.Token;


import java.util.Stack;

public class interUtil {
    public static boolean isString(String word) {
        return word.matches("[^\"]*\"");
    }

    public static boolean isBoolean(String word) {
        return word.matches("yes|no");
    }
    public static boolean isVariable(String word) {
        return word.matches("[a-zA-Z_][a-zA-Z0-9_]*");
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
            case "is":
            case "isnt":
            case "less":
            case "more":
            case "lesseq":
            case "moreeq":
                return 1;
            case "plus":
            case "minus":
                return 2;
            case "times":
            case "mod":
            case "over":
                return 3;
            default:
                return -1;
        }
    }

    public static boolean isComparisonOp(String op)
    {
        return op.equals("is") || op.equals("isnt") ||
                op.equals("less") || op.equals("more") ||
                op.equals("lesseq") || op.equals("moreeq");
    }
    public static String evaluatePostfix(String postfix) {
        Stack<String> stack = new Stack<>();
        String[] tokens = postfix.split("\\s+"); // Split by spaces to handle multi-digit numbers
        //System.out.println("Evaluating postfix expression: " + postfix);
        String result="";

        for (String token : tokens) {
            if (isArithmeticOp(token)) {
                // Pop two operands
                String a = stack.pop();
                String b = stack.pop();

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
}

