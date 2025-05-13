package lib.src;

import lib.src.generators.LL1ParsingTableGenerator;
import lib.src.parseutil.ASTNode;
import lib.src.parseutil.Parser;
import lib.src.generators.FirstFollowSetGenerator;
import lib.src.tokenutil.*;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class  Tester {

    public static void main(String[] args) throws IOException {
        // Specify the filename directly
        String filePath = "lib/src/source_file_03.simp";  // Replace this with your desired .simp file path

        //Parsing Table

        //Generate First and Follow Sets
        FirstFollowSetGenerator firstFollowSetGenerator = new FirstFollowSetGenerator("lib/src/parseutil/newgrammar.txt");

        //Generate Parsing Table
        Map<String, Set<String>> firstSets = firstFollowSetGenerator.getFirstSets();
        Map<String, Set<String>> followSets = firstFollowSetGenerator.getFollowSets();
        Map<String, List<List>> grammar = firstFollowSetGenerator.getGrammar();
        LL1ParsingTableGenerator table = new LL1ParsingTableGenerator(firstSets, followSets, grammar);
        table.generateTable();



        // Initializing and Declaration
        SimpleScanner scanner = new SimpleScanner(filePath);
        Stack<Token> tokens = new Stack<>();

        // Print the tokens for debugging
        //System.out.println("Tokens:");
        Token token;
        int counter = 0;

        while ((token = scanner.getNextToken()) != null) {
            //System.out.println(token.toString() + "Token #"+counter++);
            tokens.add(token);

        }
        System.out.println("============OUTPUT============");
        Parser parser = new Parser(table.getTable(),tokens);
        parser.parse();
        parser.visualizeAST();
        parser.printSymbolTable();


    }
}

