package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;
import io.deeplay.camp.entity.Tile;

import java.util.List;

public class TreeBuilder {

    public GameStateNode buildGameTree(BoardService initialBoard, int currentPlayer, int maxDepth) {
        GameStateNode root = new GameStateNode(initialBoard, null, currentPlayer, null);
        buildGameTree(root, currentPlayer, maxDepth, 0);
        return root;
    }

    private void buildGameTree(GameStateNode node, int currentPlayer, int maxDepth, int currentDepth) {
        if (currentDepth >= maxDepth) {
            return;
        }

        BoardService board = node.getBoard();
        List<Tile> validMoves = getAllValidMoves(currentPlayer, board);

        if (validMoves.isEmpty()) {
            return;
        }

        for (Tile move : validMoves) {
            BoardService newBoard = board.getCopy();
            makeMove(newBoard, move, currentPlayer);
            int nextPlayer = (currentPlayer == 1) ? 2 : 1;
            GameStateNode childNode = new GameStateNode(newBoard, move, nextPlayer, node);
            node.addChild(childNode);
            buildGameTree(childNode, nextPlayer, maxDepth, currentDepth + 1);
        }
    }

    private List<Tile> getAllValidMoves(int currentPlayer, BoardService board) {
        return board.getAllValidTiles(currentPlayer);
    }

    private void makeMove(BoardService board, Tile move, int currentPlayer) {
        board.setPiece(move.getX(), move.getY(), currentPlayer);
    }
}