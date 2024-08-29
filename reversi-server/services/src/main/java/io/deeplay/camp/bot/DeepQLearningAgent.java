package io.deeplay.camp.bot;

import java.io.IOException;
import java.util.List;

import io.deeplay.camp.board.BoardService;
import io.deeplay.camp.entity.Tile;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.deeplearning4j.util.ModelSerializer;

import java.util.Random;

public class DeepQLearningAgent {
    private MultiLayerNetwork model;
    private double epsilon;
    private double gamma;
    private double alpha;
    private Random random;
    private final ReplayBuffer replayBuffer;
    private final DataBase experienceDatabase;
    private final int batchSize;
    private double minEpsilon = 0.1;
    private double decayRate = 0.9999;

    public DeepQLearningAgent() {
        this.epsilon = 0.1;
        this.gamma = 0.99;
        this.alpha = 0.01;
        this.random = new Random();
        this.batchSize = 128;
        this.replayBuffer = new ReplayBuffer(100000);
        this.experienceDatabase = new DataBase();

        List<Experience> experiences = experienceDatabase.getAllExperiences();
        for (Experience experience : experiences) {
            replayBuffer.add(experience);
        }

        initModel();
    }

    public void saveModel(String filePath) {
        try {
            ModelSerializer.writeModel(model, filePath, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadModel(String filePath) {
        try {
            model = ModelSerializer.restoreMultiLayerNetwork(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            initModel();
        }
    }

    private void initModel() {
        int inputSize = 64;
        int outputSize = 64;

        NeuralNetConfiguration.ListBuilder builder = new NeuralNetConfiguration.Builder()
                .seed(7997) // Полностю случайный сид. Просто для одинаковой инициализации
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT) // Для обновления параметров.
                // Общая логика, книжный вариант, может давать шумы, но они по идее нивелируются через Adam.
                .updater(new Adam(alpha)) // Доработка общей оптимизации
                // Досаточно мощный, работает через градиенты и их квадраты (поизменять alpha, желательно динамически.)
                .weightInit(WeightInit.XAVIER) // задает начальные веса на основе количества входов и выходов для каждого узла.
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0)
                .list();

        builder.layer(new DenseLayer.Builder().nIn(inputSize).nOut(128) // Оптимально? Есть вариант повысить при большом
                // колличестве параметров для обучения
                .activation(Activation.RELU) // Новая функция активации. Позволяет избежать затухание грдиента.
                .build());

        builder.layer(new DenseLayer.Builder().nIn(128).nOut(128)
                .activation(Activation.RELU)
                .build());

        builder.layer(new OutputLayer.Builder(LossFunctions.LossFunction.L2)
                .activation(Activation.TANH) // Линейная функция активации, ради получения точного значения.
                .nIn(128).nOut(outputSize)
                .build());

        model = new MultiLayerNetwork(builder.build());
        model.init();
    }

    private double updateEpsilon() {
        int currentIteration = model.getIterationCount();
//        System.out.println(Math.max(minEpsilon, Math.pow(decayRate, currentIteration)));
        return Math.max(minEpsilon, Math.pow(decayRate, currentIteration));
    }

    public Tile selectAction(BoardService game, int currentPlayer) {
        double currentEpsilon = updateEpsilon();
        if (random.nextDouble() < currentEpsilon) {
            List<Tile> validMoves = game.getAllValidTiles(currentPlayer);
            if (validMoves != null && validMoves.size() > 0) {
                return validMoves.get(random.nextInt(validMoves.size()));
            } else {
                return null;
            }
        } else {
            INDArray input = gameToINDArray(game);
            INDArray output = model.output(input);
            return getBestMove(output, game, currentPlayer);
        }
    }

    public void storeExperience(BoardService game, int[] action, double reward, BoardService nextGame, boolean done) {
        INDArray state = gameToINDArray(game);
        INDArray nextState = gameToINDArray(nextGame);
        Experience experience = new Experience(state, action[0] * 8 + action[1], reward, nextState, done);
        replayBuffer.add(experience);
        experienceDatabase.addExperience(experience);
    }

    private INDArray gameToINDArray(BoardService game) {
        double[] flatBoard = new double[64];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                flatBoard[i * 8 + j] = game.hasPieceBlack(i, j) ? 1 : game.hasPieceWhite(i, j) ? -1 : 0;
            }
        }
        return Nd4j.create(flatBoard, new int[]{1, 64});
    }

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

    private double updateGamma(int iterationCount, double winRate) {
        double iterationMaxGamma = 0.5;
        double finalMaxGamma = 0.9;
        double initialGamma = 0.1;

        double gammaBasedOnIterations = initialGamma + ((iterationMaxGamma - initialGamma) * Math.min(1.0, iterationCount / 100000.0));

        if (gammaBasedOnIterations >= 0.5) {
            double gammaAdj = (winRate - 0.5) * (finalMaxGamma - iterationMaxGamma);
            double gammaBasedOnPerf = iterationMaxGamma + gammaAdj;
            return Math.min(finalMaxGamma, Math.max(iterationMaxGamma, gammaBasedOnPerf));
        }
//        System.out.println(gammaBasedOnIterations);
        return gammaBasedOnIterations;
    }

    public void replay(int batchSize) {
        int iterationCount = model.getIterationCount();
        experienceDatabase.calculateAndStoreWinRate();
        double check = experienceDatabase.getLatestWinRate();
        double winRate = check != -1.0 ? check : 0.0;
//        System.out.println("winrate: " + winRate);
        gamma = updateGamma(iterationCount, winRate);

        List<Experience> experiences = replayBuffer.sample(batchSize);
        for (Experience experience : experiences) {
            INDArray state = experience.getState();
            int action = experience.getAction();
            double reward = experience.getReward();
            INDArray nextState = experience.getNextState();
            boolean done = experience.isDone();

            INDArray target = model.output(state);
            double targetValue = reward;
            if (!done) {
                targetValue += gamma * model.output(nextState).max(1).getDouble(0);
            }
            target.putScalar(action, targetValue);

//            System.out.println("gamma: " + gamma + " targetValue: " + targetValue);

            model.fit(new DataSet(state, target));
        }
    }

    public void train(BoardService game, int[] action, double reward, BoardService nextGame, boolean done) {
        storeExperience(game, action, reward, nextGame, done);
        if (replayBuffer.size() >= batchSize) {
            replay(batchSize);
        }
    }
}