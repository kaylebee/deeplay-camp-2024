package io.deeplay.camp.bot;

import io.deeplay.camp.entity.Tile;
import io.deeplay.camp.board.BoardService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KaylebeeBot extends BotStrategy {
    private final UtilityFunction utilityFunction;
    private final int maxDepth;

    public KaylebeeBot(int id, String name, int maxDepth) {
        super(id, name);
        this.utilityFunction = new RandomUtilityFunction();
        this.maxDepth = maxDepth;
    }

    @Override
    public Tile getMakeMove(int currentPlayerId, @NotNull BoardService boardLogic) {
        BoardService boardCopy = getBoardCopy(boardLogic);
        return iterativeDeepening(boardCopy, currentPlayerId, maxDepth);
    }

    private Tile iterativeDeepening(BoardService board, int currentPlayerId, int depth) {
        TreeBuilder treeBuilder = new TreeBuilder();
        GameStateNode root = treeBuilder.buildGameTree(board, currentPlayerId, depth);
        TreeStatistics treeStatistics = new TreeStatistics();
        treeStatistics.collectStatistics(root);

        double bestValue = Double.NEGATIVE_INFINITY;
        Tile bestMove = null;

        for (GameStateNode child : root.getChildren()) {
            double nodeValue = minimax(child, depth - 1, false, currentPlayerId);
            if (nodeValue > bestValue) {
                bestValue = nodeValue;
                bestMove = child.getMove();
            }
        }

        return bestMove;
    }

    private double minimax(GameStateNode node, int depth, boolean maximizingPlayer, int currentPlayerId) {
        if (depth == 0 || node.getChildren().isEmpty()) {
            double evaluation = utilityFunction.evaluate(node.getBoard(), currentPlayerId);
            return evaluation;
        }

        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            for (GameStateNode child : node.getChildren()) {
                double eval = minimax(child, depth - 1, false, currentPlayerId);
                maxEval = Math.max(maxEval, eval);
            }

            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (GameStateNode child : node.getChildren()) {
                double eval = minimax(child, depth - 1, true, currentPlayerId);
                minEval = Math.min(minEval, eval);
            }

            return minEval;
        }
    }

    @Override
    List<Tile> getAllValidMoves(int currentPlayerId, @NotNull BoardService boardLogic) {
        return boardLogic.getAllValidTiles(currentPlayerId);
    }

    public BoardService getBoardCopy(BoardService boardService) {
        return boardService.getCopy();
    }
}
