package lib.src.interpreterUtil;

import lib.src.tokenutil.Token;
import java.util.Stack;
import lib.src.interpreterUtil.SymbolTable;

public class interpreter {
    Stack <Token> inputStack;
    SymbolTable symbolTable = new SymbolTable();
    interpreter (Stack<Token> inputStack)
    {
        this.inputStack = inputStack;
    }

    public void evaluate ()
    {

    }

    public void printSymbolTable() {
        symbolTable.printSymbols();
    }
}
