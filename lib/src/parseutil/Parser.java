package lib.src.parseutil;

import lib.src.tokenutil.*;

import java.util.*;

public class Parser {
    private Map<String, Map<String, String>> parsingTable;
    private Stack<String> parseStack;
    private Stack<Token> input;
    private Stack<Token> newInput = new Stack<>();
    private ASTNode root;
    private Stack<ASTNode> astNodeStack = new Stack<>();

    public Parser(Map<String, Map<String, String>> parsingTable, Stack<Token> input) {
        this.parsingTable = parsingTable;
        this.input = input;
        this.parseStack = new Stack<>();
    }

    public void parse() {
        // Push the start symbol onto the stack
            parseStack.push("START_PRIME");
        Iterator<Token> iterator = input.iterator();
        while (iterator.hasNext()) {
            Token token = iterator.next();
            newInput.push(token);
        }
        newInput.push(new Token(TokenType.EOF, "$", 0, 0));
        newInput = reverseStack(newInput);

        // Process input string
        int index = 0;
        while (!parseStack.isEmpty()) {
            System.out.println("Parsing Stack: " + parseStack);
            String top = parseStack.peek();

            //Generates Input Stack for debug only
            Iterator<Token> iterator2 = newInput.iterator();
            Stack<String> stringInputStack = new Stack<>();
            while (iterator2.hasNext()) {
                Token token = iterator2.next();
                stringInputStack.push(token.getItem());
            }
            System.out.println("Input Stack: " + stringInputStack);

            String currentInput = newInput.peek().getItem();
            System.out.println("Top of the Parsing Stack: " + top);
            System.out.println("Top of the Input Stack: " + currentInput);

            if (isTerminal(top)) {
                // If top of the stack is terminal, match it with input
                if (currentInput.equals("$") && top.equals("START_PRIME")){
                    parseStack.pop();
                    newInput.pop();
                } else if (top.equals("ε")) {
                    System.out.println("Removing ε : " + top);
                    parseStack.pop();
                }else if (top.equals(currentInput)) {
                    System.out.println("Match: " + top);
                    parseStack.pop();
                    newInput.pop();// Remove terminal from stack
                }else{
                    // Syntax error if the terminal doesn't match
                    System.out.println("Syntax Error: Expected " + top + " but found " + currentInput + " at line " + newInput.peek().getLineNumber()
                            + " at column " + newInput.peek().getColumnNumber());
                    return;
                }
            } else {
                // If top of the stack is non-terminal, consult the parsing table
                Map<String, String> row = parsingTable.get(top);
                if (row != null && row.containsKey(String.valueOf(currentInput)))
                {
                    String [] tempProd = row.get(String.valueOf(currentInput)).toString().split("->");
                    String prod = tempProd[1].trim();
                    String [] production = prod.split(" ");

                    //String production = row.get(String.valueOf(currentInput));
                    System.out.println("Applying rule: " + top + " -> " + prod);

                    ASTNode currentNode = new ASTNode(top);
                    ASTNode children = null;
                    boolean isRoot = false;
                    if (root==null){
                        System.out.println("New Root = " + top);
                        isRoot = true;
                        root = currentNode;
                    }

                    parseStack.pop();
                    for (int i = production.length - 1; i >= 0; i--)
                    {
                        parseStack.push(production[i].trim().toString());
                        if (isRoot){
                            children = new ASTNode(production[i].trim().toString());
                            currentNode.addChild(children);
                            astNodeStack.push(children);
                        }
                        else 
                        {
                            children = new ASTNode(production[i].trim().toString());
                            astNodeStack.peek().addChild(children);
                        }
                    }
                    if(!isRoot)
                    {
                        astNodeStack.pop();
                        astNodeStack.push(children);
                    }
                    

                } else {
                    // Syntax error if no rule exists
                    System.out.println("Syntax Error: No rule for " + top + " with input " + currentInput + " at line " + newInput.peek().getLineNumber()
                    + " at column " + newInput.peek().getColumnNumber());
                    return;
                }
            }
        }

        // Check if the entire input has been consumed
        if (index == newInput.size()-1) {
            System.out.println("Input parsed successfully!");
        } else {
            System.out.println("Syntax Error: Extra input remaining.");
        }
    }

    // Helper method to check if a symbol is terminal
    private boolean isTerminal(String symbol) {
        return !parsingTable.containsKey(symbol);
    }

    public static Stack<Token> reverseStack(Stack<Token> originalStack) {
        Stack<Token> reversedStack = new Stack<>();

        // Pop elements from the original stack and push them to the reversed stack
        while (!originalStack.isEmpty()) {
            reversedStack.push(originalStack.pop());
        }
        return reversedStack;
    }
    public void visualizeAST() {
        ASTVisualizer.visualizeAST(root);
    }
}
