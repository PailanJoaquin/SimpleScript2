package lib.src.interpreterUtil;

import lib.src.tokenutil.Token;
import lib.src.tokenutil.TokenType;

import java.util.*;

public class SymbolTable {
    private Map<String, Object> variables = new HashMap<>();
    private Map<String, String> types = new HashMap<>();
    private Map<String, Stack<Token>> functionsStack = new HashMap<>();
    private List<String> functions = new ArrayList<>();

    public void define(String name, Object value, String type) {
        variables.put(name, value);
        types.put(name, type);
    }

    public void assign(String name, Object value) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not declared: " + name);
        }
        variables.put(name, value);
    }
    public void assignFunction(String name, List<String> body, String type) {
        variables.put(name, type);
        types.put(name, "function");
    }

    public Object get(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not declared: " + name);
        }
        return variables.get(name);
    }
    public String getType(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable not declared: " + name);
        }
        return types.get(name);
    }
    public boolean contains(String name) {
        return variables.containsKey(name);
    }

    public void printSymbols() {
        System.out.println("=======Symbol Table=======");
        for (String key : variables.keySet()) {
            System.out.println(key + " = " + variables.get(key) + " (" + types.get(key) + ")");
        }
    }
}