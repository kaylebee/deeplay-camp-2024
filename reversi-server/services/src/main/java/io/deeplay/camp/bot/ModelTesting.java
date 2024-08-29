package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;
import io.deeplay.camp.entity.Tile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ModelTesting extends BotStrategy {
    private final DeepQLearningAgent agent;
    private final UtilityFunction utilityFunction;

    public ModelTesting(int id, String name) {
        super(id, name);
        this.utilityFunction = new AdvancedUtilityFunction();
        this.agent = new DeepQLearningAgent();
    }

    public ModelTesting(int id, String name, DeepQLearningAgent agent) {
        super(id, name);
        this.agent = agent;
        this.utilityFunction = new AdvancedUtilityFunction();
    }

    @Override
    public Tile getMakeMove(int currentPlayerId, @NotNull BoardService boardLogic) {
        BoardService boardCopy = getBoardCopy(boardLogic);
        Tile selectedMove = selectBestMove(boardCopy, currentPlayerId);
        if (selectedMove == null)
            return null;

        int[] action = {selectedMove.getX(), selectedMove.getY()};

        boardLogic.makeMove(currentPlayerId, selectedMove);

        double reward = calculateReward(boardLogic, currentPlayerId);
        BoardService nextBoardState = getBoardCopy(boardLogic);
        boolean done = boardLogic.checkForWin().isGameFinished();

        trainAgent(boardCopy, action, reward, nextBoardState, done);

        return selectedMove;
    }

    private Tile selectBestMove(BoardService board, int currentPlayerId) {
        return agent.selectAction(board, currentPlayerId);
    }

    @Override
    List<Tile> getAllValidMoves(int currentPlayerId, @NotNull BoardService boardLogic) {
        return boardLogic.getAllValidTiles(currentPlayerId);
    }

    public BoardService getBoardCopy(BoardService boardService) {
        return boardService.getCopy();
    }

    public void trainAgent(BoardService game, int[] action, double reward, BoardService nextGame, boolean done) {
        agent.train(game, action, reward, nextGame, done);
    }

    private double calculateReward(BoardService boardLogic, int currentPlayerId) {
        return utilityFunction.evaluate(boardLogic, currentPlayerId);
    }
}
