package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;
import io.deeplay.camp.entity.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@code GameStateNode} class represents a node in the game state tree for board games.
 * Each node encapsulates a board state after a particular move, the player making that move,
 * its parent node, and any potential child nodes representing future game states.
 * Additionally, this node keeps track of visit and win statistics to aid in game tree exploration.
 */
public class GameStateNode {

    /**
     * The current state of the board at this node.
     */
    private final BoardService board;

    /**
     * The move leading to this board state.
     */
    private final Tile move;

    /**
     * The player number whose turn is reflected in this node.
     */
    private final int currentPlayer;

    /**
     * The parent node of this game state node. If this node
     * is the root of the tree, then {@code parent} is {@code null}.
     */
    private final GameStateNode parent;

    /**
     * A map of child nodes, where each key is a {@code Tile} representing a move,
     * and each value is a {@code GameStateNode} representing the resulting game state.
     */
    private final Map<Tile, GameStateNode> children;

    /**
     * An atomic counter holding the number of times this node has been visited
     * during a search or evaluation process.
     */
    private final AtomicInteger visits;

    /**
     * An atomic counter holding the count of wins associated with this node
     * after simulation or actual gameplay.
     */
    private final AtomicInteger wins;

    /**
     * Constructs a new {@code GameStateNode} with the specified board, move, current player,
     * and parent node. Initializes the children map to be empty and sets visit and win counts
     * to zero.
     *
     * @param board the current board state
     * @param move the move leading to this board state
     * @param currentPlayer the player whose move led to this state
     * @param parent the parent node preceding this state in the game tree
     */
    public GameStateNode(BoardService board, Tile move, int currentPlayer, GameStateNode parent) {
        this.board = board;
        this.move = move;
        this.currentPlayer = currentPlayer;
        this.parent = parent;
        this.children = new ConcurrentHashMap<>();
        this.visits = new AtomicInteger(0);
        this.wins = new AtomicInteger(0);
    }

    /**
     * Returns the board state associated with this node.
     *
     * @return the board state
     */
    public BoardService getBoard() {
        return board;
    }

    /**
     * Returns the move that was executed to reach this node's board state.
     *
     * @return the move leading to this state
     */
    public Tile getMove() {
        return move;
    }

    /**
     * Returns the player number whose move is reflected in this node.
     *
     * @return the current player number
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the parent node of this game state node.
     *
     * @return the parent node, or {@code null} if this node is the root
     */
    public GameStateNode getParent() {
        return parent;
    }

    /**
     * Returns a collection of child nodes representing possible subsequent game states from this node.
     *
     * @return a collection of child game state nodes
     */
    public Collection<GameStateNode> getChildren() {
        return children.values();
    }

    /**
     * Adds a child node to this node's collection of children. The child node is associated
     * with the move leading to its board state.
     *
     * @param child the child game state node to be added
     */
    public void addChild(GameStateNode child) {
        children.put(child.getMove(), child);
    }

    /**
     * Increments the visit count for this node. This should be called each time this
     * game state is encountered in a search or simulation.
     */
    public void incrementVisits() {
        visits.incrementAndGet();
    }

    /**
     * Increments the win count for this node. This should be called each time this
     * game state results in a simulated or actual win.
     */
    public void incrementWins() {
        wins.incrementAndGet();
    }

    /**
     * Returns the total number of visits to this node.
     *
     * @return the visit count
     */
    public int getVisits() {
        return visits.get();
    }

    /**
     * Returns the total number of wins associated with this node.
     *
     * @return the win count
     */
    public int getWins() {
        return wins.get();
    }
}