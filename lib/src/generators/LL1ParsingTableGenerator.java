package lib.src.generators;

import java.io.*;
import java.util.*;

public class LL1ParsingTableGenerator {

    private Map<String, Set<String>> firstSets;
    private Map<String, Set<String>> followSets;
    private Map<String, List<String>> grammar;
    private Map<String, Map<String, String>> parsingTable;

    public LL1ParsingTableGenerator(Map<String, Set<String>> firstSets, Map<String, Set<String>> followSets, Map<String, List<String>> grammar) {
        this.firstSets = firstSets;
        this.followSets = followSets;
        this.grammar = grammar;
        this.parsingTable = new HashMap<>();
        generateParsingTable();
    }

    // Generate the LL(1) Parsing Table
    private void generateParsingTable() {
        for (String nonTerminal : grammar.keySet()) {
            Map<String, String> row = new HashMap<>();
            for (String production : grammar.get(nonTerminal)) {
                String[] symbols = production.split("\\s+");
                Set<String> firstSetForProduction = calculateFirst(symbols[0]);

                for (String terminal : firstSetForProduction) {
                    if (!terminal.equals("ε")) {
                        row.put(terminal, nonTerminal + " -> " + production);
                    }
                }

                // If ε is in the First set, add to Follow set as well
                if (firstSetForProduction.contains("ε")) {
                    for (String follow : followSets.get(nonTerminal)) {
                        row.put(follow, nonTerminal + " -> " + production);
                    }
                }
            }
            parsingTable.put(nonTerminal, row);
        }
    }

    // Helper method to calculate the First set
    private Set<String> calculateFirst(String symbol) {
        Set<String> firstSet = new HashSet<>();

        if (!grammar.containsKey(symbol)) {
            firstSet.add(symbol); // Terminal
            return firstSet;
        }

        for (String production : grammar.get(symbol)) {
            String[] symbols = production.split("\\s+");
            if (symbols[0].equals(symbol)) continue;  // Avoid recursion

            firstSet.addAll(calculateFirst(symbols[0]));
        }
        return firstSet;
    }

    // Output the LL(1) Parsing Table as CSV
    public void generateCSVOutput(String outputFileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            Set<String> terminals = new HashSet<>();
            for (Map.Entry<String, Set<String>> entry : firstSets.entrySet()) {
                terminals.addAll(entry.getValue());
            }
            terminals.add("$");

            // Header (Terminals)
            writer.write("Non-Terminals,");
            for (String terminal : terminals) {
                writer.write(terminal + ",");
            }
            writer.write("\n");

            // Write the table rows
            for (String nonTerminal : parsingTable.keySet()) {
                writer.write(nonTerminal + ",");
                Map<String, String> row = parsingTable.get(nonTerminal);
                for (String terminal : terminals) {
                    if (row.containsKey(terminal)) {
                        writer.write(row.get(terminal) + ",");
                    } else {
                        writer.write(","); // Empty cell
                    }
                }
                writer.write("\n");
            }

            System.out.println("LL(1) Parsing Table output generated in " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
