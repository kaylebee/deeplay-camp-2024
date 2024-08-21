package io.deeplay.camp.bot;

import io.deeplay.camp.entity.Tile;
import io.deeplay.camp.board.BoardService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KaylebeeBotMyFunc extends BotStrategy {
    private final UtilityFunction utilityFunction;
    private final int maxDepth;

    public KaylebeeBotMyFunc(int id, String name, int maxDepth) {
        super(id, name);
        this.utilityFunction = new AdvancedUtilityFunction();
        this.maxDepth = maxDepth;
    }

    @Override
    public Tile getMakeMove(int currentPlayerId, @NotNull BoardService boardLogic) {
        Tile bestMove = depthDeepening(boardLogic, currentPlayerId, maxDepth);
        return bestMove;
    }

    private Tile depthDeepening(BoardService board, int currentPlayerId, int depth) {
        double bestValue = Double.NEGATIVE_INFINITY;
        Tile bestMove = null;

        List<Tile> possibleMoves = board.getAllValidTiles(currentPlayerId);

        for (Tile move : possibleMoves) {
            BoardService boardCopy = board.getCopy();
            boardCopy.makeMove(currentPlayerId, move);
            double nodeValue = minimax(boardCopy, depth - 1, false, currentPlayerId, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

            if (nodeValue > bestValue) {
                bestValue = nodeValue;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private double minimax(BoardService board, int depth, boolean maximizingPlayer, int currentPlayerId, double alpha, double beta) {
        if (depth == 0 || board.checkForWin().isGameFinished()) {
            double evaluation = utilityFunction.evaluate(board, currentPlayerId);
            return evaluation;
        }

        int nextPlayerId = getNextPlayer(currentPlayerId);

        if (maximizingPlayer) {
            double maxEval = Double.NEGATIVE_INFINITY;
            if (board.getAllValidTiles(currentPlayerId).isEmpty()) {
                return 0;
            }
            for (Tile move : board.getAllValidTiles(currentPlayerId)) {
                BoardService boardCopy = board.getCopy();
                boardCopy.makeMove(currentPlayerId, move);
                double eval = minimax(boardCopy, depth - 1, false, currentPlayerId, alpha, beta);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            double minEval = Double.POSITIVE_INFINITY;
            for (Tile move : board.getAllValidTiles(nextPlayerId)) {
                BoardService boardCopy = board.getCopy();
                boardCopy.makeMove(nextPlayerId, move);
                double eval = minimax(boardCopy, depth - 1, true, currentPlayerId, alpha, beta);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }

    private int getNextPlayer(int currentPlayerId) {
        return currentPlayerId == 1 ? 2 : 1;
    }

    @Override
    List<Tile> getAllValidMoves(int currentPlayerId, @NotNull BoardService boardLogic) {
        return boardLogic.getAllValidTiles(currentPlayerId);
    }

    public BoardService getBoardCopy(BoardService boardService) {
        return boardService.getCopy();
    }
}
