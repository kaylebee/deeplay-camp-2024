package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;
import io.deeplay.camp.entity.Tile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The {@code Model} class represents a strategy for a bot player in a game that uses reinforcement learning methods.
 * This class extends the {@code BotStrategy} class, incorporating an AI agent that utilizes Deep Q-Learning to make
 * decisions based on the current state of the game. It also employs a utility function to evaluate the game state and reward the agent.
 */
public class Model extends BotStrategy {

    /**
     * The Deep Q-Learning agent that decides on which action to take based on the current game state.
     */
    private final DeepQLearningAgent agent;

    /**
     * The utility function used to evaluate the game state and calculate rewards for the agent.
     */
    private final UtilityFunction utilityFunction;

    /**
     * Constructs an instance of the {@code Model} class with the specified player identifier and name.
     * A default {@link DeepQLearningAgent} is used for decision-making processes.
     *
     * @param id the unique identifier for the bot player.
     * @param name the name assigned to the bot player.
     */
    public Model(int id, String name) {
        super(id, name);
        this.utilityFunction = new AdvancedUtilityFunction();
        this.agent = new DeepQLearningAgent();
    }

    /**
     * Constructs an instance of the {@code Model} class with the specified player identifier, name, and a custom
     * Deep Q-Learning agent for decision-making processes.
     *
     * @param id the unique identifier for the bot player.
     * @param name the name assigned to the bot player.
     * @param agent a custom {@link DeepQLearningAgent} to be used by the bot for making decisions.
     */
    public Model(int id, String name, DeepQLearningAgent agent) {
        super(id, name);
        this.agent = agent;
        this.utilityFunction = new AdvancedUtilityFunction();
    }

    /**
     * Determines the next move for the bot given the current player ID and the game board's current state.
     * It makes a move, updates the board state, calculates a reward, and trains the agent on the new state.
     *
     * @param currentPlayerId the ID of the player making the move.
     * @param boardLogic the current state of the game board.
     * @return the {@link Tile} representing the move the agent decides to make, or {@code null} if no move is possible.
     */
    @Override
    public Tile getMakeMove(int currentPlayerId, @NotNull BoardService boardLogic) {
        BoardService boardCopy = getBoardCopy(boardLogic);
        Tile selectedMove = selectBestMove(boardCopy, currentPlayerId);
        if (selectedMove == null) {
            return null;
        }

        int[] action = {selectedMove.getX(), selectedMove.getY()};

        boardLogic.makeMove(currentPlayerId, selectedMove);

        double reward = calculateReward(boardLogic, currentPlayerId);
        BoardService nextBoardState = getBoardCopy(boardLogic);
        boolean done = boardLogic.checkForWin().isGameFinished();

        trainAgent(boardCopy, action, reward, nextBoardState, done);

        return selectedMove;
    }

    /**
     * Selects the best move for the current player by invoking the agent's decision-making process.
     *
     * @param board the current game board state.
     * @param currentPlayerId the ID of the player for whom the move is being selected.
     * @return the {@link Tile} representing the best move for the current player.
     */
    private Tile selectBestMove(BoardService board, int currentPlayerId) {
        return agent.selectAction(board, currentPlayerId);
    }

    /**
     * Retrieves all valid moves that the current player can make on the given game board.
     *
     * @param currentPlayerId the ID of the player whose valid moves are being fetched.
     * @param boardLogic the current game board state.
     * @return a {@link List} of {@link Tile} objects representing all valid moves.
     */
    @Override
    List<Tile> getAllValidMoves(int currentPlayerId, @NotNull BoardService boardLogic) {
        return boardLogic.getAllValidTiles(currentPlayerId);
    }

    /**
     * Produces a copy of the current game board state. This method facilitates state observation without
     * altering the actual game board.
     *
     * @param boardService the original game board service that needs to be copied.
     * @return a new instance of {@link BoardService} representing the copied state of the game board.
     */
    public BoardService getBoardCopy(BoardService boardService) {
        return boardService.getCopy();
    }

    /**
     * Trains the agent by providing feedback from the environment, including the performed action's effectiveness
     * and if the game has concluded.
     *
     * @param game the current state of the game before the action is taken.
     * @param action the action taken by the agent, represented by an array containing x and y coordinates.
     * @param reward the numeric reward assigned for the action.
     * @param nextGame the state of the game following the action.
     * @param done a boolean indicating whether the action has resulted in the game's termination.
     */
    public void trainAgent(BoardService game, int[] action, double reward, BoardService nextGame, boolean done) {
        agent.train(game, action, reward, nextGame, done);
    }

    /**
     * Calculates and returns the reward for the agent based on the current state of the board and the given player's
     * performance.
     *
     * @param boardLogic the current game board state.
     * @param currentPlayerId the ID of the player for whom the reward is being calculated.
     * @return the calculated reward as a double value reflecting the player's performance.
     */
    private double calculateReward(BoardService boardLogic, int currentPlayerId) {
        return utilityFunction.evaluate(boardLogic, currentPlayerId);
    }
}
