package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;
import io.deeplay.camp.entity.Tile;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.List;

public class DeepQLearningAgentUsable {

    /**
     * The deep learning model represented as a MultiLayerNetwork.
     */
    private MultiLayerNetwork model;

    /**
     * Constructs a DeepQLearningAgentUsable instance and loads a model from the specified file path.
     *
     * @param filePath the path to the model file, typically in a serialized format.
     */
    public DeepQLearningAgentUsable(String filePath) {
        loadModel(filePath);
    }

    /**
     * Loads a deep learning model from the specified file path.
     *
     * @param filePath the path to the model file, which should be serialized.
     *        The method attempts to restore the model into the MultiLayerNetwork.
     */
    public void loadModel(String filePath) {
        try {
            model = ModelSerializer.restoreMultiLayerNetwork(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Selects an action by evaluating the game state for the current player using the neural network.
     *
     * @param game          the current state of the board represented by the BoardService object.
     * @param currentPlayer the identifier of the current player whose move is to be selected.
     * @return a Tile object representing the selected move.
     */
    public Tile selectAction(BoardService game, int currentPlayer) {
        INDArray input = gameToINDArray(game);
        INDArray output = model.output(input);
        return getBestMove(output, game, currentPlayer);
    }

    /**
     * Converts the current game state into an INDArray format suitable for input into the neural network.
     * This method represents the board as a 1D array, encoding each tile as follows:
     * 1 for a black piece, -1 for a white piece, and 0 for an empty tile.
     *
     * @param game the current state of the board represented by the BoardService object.
     * @return an INDArray that serves as the neural network input.
     */
    private INDArray gameToINDArray(BoardService game) {
        double[] flatBoard = new double[64];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                flatBoard[i * 8 + j] = game.hasPieceBlack(i, j) ? 1 : game.hasPieceWhite(i, j) ? -1 : 0;
            }
        }
        return Nd4j.create(flatBoard, new int[]{1, 64});
    }

    /**
     * Determines the best possible move for the current player by evaluating the neural network's output.
     * It considers all valid moves and selects the one with the highest predicted value.
     *
     * @param output        the neural network's output for the given board state.
     * @param game          the current state of the board represented by the BoardService object.
     * @param currentPlayer the identifier of the current player for whom the move is being determined.
     * @return a Tile object representing the optimal move as per the neural network's evaluation.
     */
    private Tile getBestMove(INDArray output, BoardService game, int currentPlayer) {
        List<Tile> validMoves = game.getAllValidTiles(currentPlayer);
        Tile bestMove = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Tile move : validMoves) {
            int index = move.getX() * 8 + move.getY();
            double value = output.getDouble(index);
            if (value > bestValue) {
                bestValue = value;
                bestMove = move;
            }
        }

        return bestMove;
    }
}