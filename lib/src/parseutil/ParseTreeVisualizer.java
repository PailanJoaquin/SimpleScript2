package lib.src.parseutil;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ParseTreeVisualizer extends JPanel {
    private ParseTreeNode root;

    // Constructor with root node
    public ParseTreeVisualizer(ParseTreeNode root) {
        this.root = root;
    }

    // Override paintComponent method to draw the parse tree
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Call the recursive function to draw the tree starting from the root
        drawTree(g, root, getWidth() / 2, 20, getWidth() / 4);
    }

    // Recursive method to draw the tree
    private void drawTree(Graphics g, ParseTreeNode node, int x, int y, int offset) {
        if (node == null) {
            return;
        }

        // Draw the node value
        g.setColor(Color.BLACK);
        g.fillOval(x - 20, y, 40, 40);  // Draw a circle for the node
        g.setColor(Color.WHITE);
        g.drawString(node.value, x - 10, y + 25);  // Draw the text inside the node

        // Draw edges to children and recursively draw each child
        int childX = x - offset;
        for (ParseTreeNode child : node.children) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y + 40, childX, y + 80);  // Line from parent to child
            drawTree(g, child, childX, y + 80, offset / 2);  // Recursive call for the child
            childX += offset * 2;  // Increase x-coordinate for the next child
        }
    }

    // Main method to set up the JFrame
    public static void showParseTree(ParseTreeNode root) {
        // Create a JFrame to hold the visualization
        JFrame frame = new JFrame("Parse Tree Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);  // Set size of the window

        // Create the ParseTreeVisualizer with the root node
        ParseTreeVisualizer treeVisualizer = new ParseTreeVisualizer(root);

        // Add the visualizer panel to the frame
        frame.add(treeVisualizer);

        // Make the frame visible
        frame.setVisible(true);
    }

    // Main entry point for testing
    public static void main(String[] args) {
        // Sample parse tree for testing
        ParseTreeNode root = new ParseTreeNode("Program");
        ParseTreeNode child1 = new ParseTreeNode("Statement");
        ParseTreeNode child2 = new ParseTreeNode("Expression");

        root.addChild(child1);
        root.addChild(child2);

        child1.addChild(new ParseTreeNode("Let"));
        child1.addChild(new ParseTreeNode("ID"));
        child2.addChild(new ParseTreeNode("ID"));

        // Visualize the parse tree
        showParseTree(root);
    }
}
