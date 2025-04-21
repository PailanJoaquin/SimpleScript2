package lib.src.generators;

import java.io.*;
import java.util.*;

public class FirstFollowSetGenerator {

    private Map<String, List<List>> grammar = new HashMap<>();
    private Set<String> items = new HashSet<>();
    private Map<String, Set<String>> firstSets = new HashMap<>();
    private Map<String, Set<String>> followSets = new HashMap<>();


    public FirstFollowSetGenerator(String fileName) {
        readGrammarFromFile(fileName);
        calculateFirstSets();
//        calculateFollowSets();

        Iterator<String> iterator = items.iterator();
        while (iterator.hasNext())
        {
            String item = iterator.next();
            System.out.println("FIRST("+ item + ") = " + firstSets.get(item));//DEBUG
        }
    }

    // Read the grammar from a file
    private void readGrammarFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.trim().isEmpty()) continue;

                String[] rule = line.split("->");
                String lhs = rule[0].trim();
                items.add(lhs);
                List<List> rhsStack = new ArrayList<>();
                List<String> rhs = new ArrayList<>();
                for (String symbol : rule[1].trim().split("\\s+")) {
                    rhs.add(symbol);
                    items.add(symbol);
                }
                rhsStack.add(rhs);
                if (!grammar.containsKey(lhs)) {
                    grammar.put(lhs,rhsStack);
                }
                else
                    grammar.get(lhs).add(rhsStack);

            }

//            for (String key : grammar.keySet()) //DEBUG
//            {
//                System.out.println("Key: " + key + " -> Values: " + grammar.get(key));
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Calculate the First set for each non-terminal
    private void calculateFirstSets() {
        Iterator<String> iterator = items.iterator();
        Set<String> terminals = new HashSet<>();
        Set<String> nonTerminals = new HashSet<>();
        for(SetTerminals setTerminal : SetTerminals.values()) //Load Terminal Items
        {
            terminals.add(setTerminal.getStringValue());
        }
        while (iterator.hasNext()) {
            String item = iterator.next();
            if(terminals.contains(item)) //Will Check if Terminal, if not continue
            {
                Set<String> terminal = new HashSet<>();
                terminal.add(item);
                firstSets.put(item, terminal);
                //System.out.println("FIRST("+ item + ") = " + firstSets.get(item));//DEBUG
            }
            else
            {
                nonTerminals.add(item);
                //System.out.println("Non Terminal Found : " + item);//DEBUG
            }
        }

        for (String nonTerminal : nonTerminals) {
            firstSets.putIfAbsent(nonTerminal, calculateFirst(nonTerminal));
        }

    }

    // Helper method to calculate First set
    private Set<String> calculateFirst(String symbol) {
        Set<String> firstSet = new HashSet<>();
        //System.out.println("Calculating First of : " + symbol);//DEBUG
        if (!grammar.containsKey(symbol)) {
            firstSet.add(symbol); // Terminal
            return firstSet;
        }
        int stackCounter = 0;
        for (List <String> production : grammar.get(symbol)) {
            Set <String> newProduction = new HashSet<>();
            if(grammar.get(symbol).size()>1)
            {
                if(grammar.get(symbol).get(stackCounter).getFirst().toString().equals(grammar.get(symbol).get(stackCounter).getFirst().toString()))
                    continue;
                else
                {
                    newProduction.add(grammar.get(symbol).get(stackCounter++).getFirst().toString());
                    System.out.println("DEBUGGING : " + newProduction);//DEBUG
                }
            }
            else
                newProduction.add(grammar.get(symbol).getFirst().getFirst().toString());
            firstSet.add(newProduction.toString());

            if ((stackCounter) == grammar.get(symbol).size())
                break;
        }
        return firstSet;
    }
//
//    // Calculate the Follow set for each non-terminal
//    private void calculateFollowSets() {
//        for (String nonTerminal : grammar.keySet()) {
//            followSets.put(nonTerminal, calculateFollow(nonTerminal));
//        }
//    }
//
//    // Helper method to calculate Follow set
//    private Set<String> calculateFollow(String nonTerminal) {
//        Set<String> followSet = new HashSet<>();
//
//        if ("S".equals(nonTerminal)) {
//            followSet.add("$");  // Assuming "S" is the start symbol
//        }
//
//        for (String lhs : grammar.keySet()) {
//            for (String rhs : grammar.get(lhs)) {
//                String[] symbols = rhs.split("\\s+");
//                for (int i = 0; i < symbols.length; i++) {
//                    if (symbols[i].equals(nonTerminal)) {
//                        if (i + 1 < symbols.length) {
//                            followSet.addAll(calculateFirst(symbols[i + 1]));
//                        } else if (!lhs.equals(nonTerminal)) {
//                            followSet.addAll(calculateFollow(lhs));
//                        }
//                    }
//                }
//            }
//        }
//        return followSet;
//    }
//
//    public Map<String, Set<String>> getFirstSets() {
//        return firstSets;
//    }
//
//    public Map<String, Set<String>> getFollowSets() {
//        return followSets;
//    }
//    public Map<String, List<String>> getGrammar() {
//        return grammar;
//    }
}
