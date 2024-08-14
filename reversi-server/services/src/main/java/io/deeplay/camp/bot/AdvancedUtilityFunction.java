package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;
import io.deeplay.camp.entity.Tile;

import java.util.List;

public class AdvancedUtilityFunction implements UtilityFunction {
    private static final int[][] WEIGHTS = {
            {100, -10, 10, 10, 10, 10, -10, 100},
            {-10, -20, 1, 1, 1, 1, -20, -10},
            {10, 1, 5, 5, 5, 5, 1, 10},
            {10, 1, 5, 5, 5, 5, 1, 10},
            {10, 1, 5, 5, 5, 5, 1, 10},
            {10, 1, 5, 5, 5, 5, 1, 10},
            {-10, -20, 1, 1, 1, 1, -20, -10},
            {100, -10, 10, 10, 10, 10, -10, 100}
    };

    @Override
    public double evaluate(BoardService boardBefore, BoardService boardAfter, int currentPlayerId) {
        int opponentId = (currentPlayerId == 1) ? 2 : 1;

        int currentPlayerScore = 0;
        int opponentScore = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (currentPlayerId == 1) {
                    if (boardAfter.hasPieceBlack(x, y)) {
                        currentPlayerScore += WEIGHTS[x][y];
                    } else if (boardAfter.hasPieceWhite(x, y)) {
                        opponentScore += WEIGHTS[x][y];
                    }
                } else {
                    if (boardAfter.hasPieceBlack(x, y)) {
                        opponentScore += WEIGHTS[x][y];
                    } else if (boardAfter.hasPieceWhite(x, y)) {
                        currentPlayerScore += WEIGHTS[x][y];
                    }
                }
            }
        }

        int currentPlayerPieces = boardAfter.getAllValidTiles(currentPlayerId).size();
        int opponentPieces = boardAfter.getAllValidTiles(opponentId).size();
        int pieceDifference = currentPlayerPieces - opponentPieces;

        int currentPlayerMobility = calculateMobility(boardAfter, currentPlayerId);
        int opponentMobility = calculateMobility(boardAfter, opponentId);
        int mobilityDifference = currentPlayerMobility - opponentMobility;

        int flipScore = countFlippedPieces(boardBefore, boardAfter);

        return currentPlayerScore - opponentScore
                + 10 * pieceDifference
                + 5 * mobilityDifference
                + 2 * flipScore;
    }

    private int calculateMobility(BoardService board, int currentPlayerId) {
        List<Tile> validMoves = board.getAllValidTiles(currentPlayerId);
        return validMoves.size();
    }

    private int countFlippedPieces(BoardService boardBefore, BoardService boardAfter) {
        int flippedPieces = 0;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (boardAfter.hasPieceBlack(x, y) && !boardBefore.hasPieceBlack(x, y)) {
                    flippedPieces++;
                } else if (boardAfter.hasPieceWhite(x, y) && !boardBefore.hasPieceWhite(x, y)) {
                    flippedPieces++;
                }
            }
        }

        return flippedPieces;
    }
}
