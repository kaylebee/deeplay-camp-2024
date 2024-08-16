package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;
import io.deeplay.camp.entity.Tile;

import java.util.ArrayList;
import java.util.List;

public class GameStateNode {
    private final BoardService board;
    private final Tile move;
    private final int currentPlayer;
    private final List<GameStateNode> children;
    private final GameStateNode parent;

    public GameStateNode(BoardService board, Tile move, int currentPlayer, GameStateNode parent) {
        this.board = board;
        this.move = move;
        this.currentPlayer = currentPlayer;
        this.children = new ArrayList<>();
        this.parent = parent;
    }

    public BoardService getBoard() {
        return board;
    }

    public Tile getMove() {
        return move;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public List<GameStateNode> getChildren() {
        return children;
    }

    public GameStateNode getParent() {
        return parent;
    }

    public void addChild(GameStateNode child) {
        children.add(child);
    }
}