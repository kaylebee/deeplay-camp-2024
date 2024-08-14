package io.deeplay.camp.bot;

import io.deeplay.camp.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeStatistics {
    private static final Logger logger = LoggerFactory.getLogger(TreeStatistics.class);

    public void collectStatistics(GameStateNode root) {
        long startTime = System.currentTimeMillis();

        int totalNodes = countNodes(root);
        int terminalNodes = countTerminalNodes(root);
        int maxDepth = findMaxDepth(root);
        double branchingFactor = totalNodes == 1 ? 0 : (double) (totalNodes - 1) / (totalNodes - terminalNodes);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        logger.info("Total Nodes: " + totalNodes);
        logger.info("Terminal Nodes: " + terminalNodes);
        logger.info("Max Depth: " + maxDepth);
        logger.info("Branching Factor: " + branchingFactor);
        logger.info("Time Taken (ms): " + duration);
    }

    private int countNodes(GameStateNode node) {
        int count = 1;
        for (GameStateNode child : node.getChildren()) {
            count += countNodes(child);
        }
        return count;
    }

    private int countTerminalNodes(GameStateNode node) {
        if (node.getChildren().isEmpty()) {
            return 1;
        }
        int count = 0;
        for (GameStateNode child : node.getChildren()) {
            count += countTerminalNodes(child);
        }
        return count;
    }

    private int findMaxDepth(GameStateNode node) {
        if (node.getChildren().isEmpty()) {
            return 0;
        }
        int maxDepth = 0;
        for (GameStateNode child : node.getChildren()) {
            maxDepth = Math.max(maxDepth, findMaxDepth(child));
        }
        return maxDepth + 1;
    }
}