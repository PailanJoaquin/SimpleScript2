package lib.src.parseutil;

import java.util.ArrayList;
import java.util.List;

class ParseTreeNode {
    String value;  // The value of the node (could be a terminal or non-terminal)
    List<ParseTreeNode> children;

    public ParseTreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    // Add a child node
    public void addChild(ParseTreeNode child) {
        children.add(child);
    }

    // Print the tree structure
    public void printTree(String prefix) {
        System.out.println(prefix + value);
        for (ParseTreeNode child : children) {
            child.printTree(prefix + "  ");
        }
    }
}

