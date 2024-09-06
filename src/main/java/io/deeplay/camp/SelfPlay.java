package io.deeplay.camp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.deeplay.camp.bot.*;
import io.deeplay.camp.entity.Board;
import io.deeplay.camp.entity.Tile;
import io.deeplay.camp.board.BoardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles self-play games between bots.
 * <p>
 * This class manages the execution of multiple games between two bots, collects the results, and saves them to a JSON file.
 * It uses a scheduled executor service to manage the game threads and a fixed thread pool for game execution.
 * </p>
 */
public class SelfPlay {
    private static final Logger logger = LoggerFactory.getLogger(SelfPlay.class);
    private static final int GAME_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 10;
    private static final int SCHEDULER_THREAD_COUNT = 10;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(SCHEDULER_THREAD_COUNT);
    private static final ExecutorService gameExecutor = Executors.newFixedThreadPool(GAME_THREAD_COUNT);
    private final int gameCount;
    private final AtomicInteger firstBotWins = new AtomicInteger(0);
    private final AtomicInteger secondBotWins = new AtomicInteger(0);
    private final AtomicInteger draws = new AtomicInteger(0);
    private final AtomicInteger totalGamesCompleted = new AtomicInteger(0);

    public SelfPlay(int gameCount) {
        this.gameCount = gameCount;
    }

    public void startBotGame() {
        String modelPath = "C:/Users/filiy/model_v2.zip";
        DeepQLearningAgent agent = new DeepQLearningAgent();
        agent.loadModel(modelPath);

        long startTime = System.currentTimeMillis();

        int totalBatches = (int) Math.ceil((double) gameCount / 1);

        for (int batch = 0; batch < totalBatches; batch++) {
            int gamesInBatch = Math.min(1, gameCount - batch * 1);
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < gamesInBatch; i++) {
                int gameIndex = batch * 1 + i;
                futures.add(gameExecutor.submit(() -> playSingleGame(gameIndex % 2 == 0, agent)));
            }

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error(e.toString());
                }
            }

            logger.info("Batch {} completed. Total games: {}. Bot 1 wins: {}. Bot 2 wins: {}. Draws: {}", batch + 1, totalGamesCompleted.get(), firstBotWins.get(), secondBotWins.get(), draws.get());
        }

        logger.info("Total games: {}. Bot 1 wins: {}. Bot 2 wins: {}. Draws: {}", gameCount, firstBotWins.get(), secondBotWins.get(), draws.get());
        gameExecutor.shutdown();
        scheduler.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        saveResultsToJson(duration);

        agent.saveModel(modelPath);
    }

    private Void playSingleGame(boolean firstBotStarts, DeepQLearningAgent agent) {
        BotStrategy secondRandomBot = new Model(1, "ViolaBot", agent);
        BotStrategy firstRandomBot = new RandomBot(2, "DarlingBot");
        Board board = new Board();
        BoardService boardLogic = new BoardService(board);
        BotStrategy currentBot = firstBotStarts ? firstRandomBot : secondRandomBot;

        while (!boardLogic.checkForWin().isGameFinished()) {
            executeBotMove(currentBot, boardLogic);
            currentBot = currentBot.id == firstRandomBot.id ? secondRandomBot : firstRandomBot;
        }

        if (boardLogic.checkForWin().getUserIdWinner() == 1) {
            firstBotWins.incrementAndGet();
        } else if (boardLogic.checkForWin().getUserIdWinner() == 2) {
            secondBotWins.incrementAndGet();
        } else {
            draws.incrementAndGet();
        }

        totalGamesCompleted.incrementAndGet();
        return null;
    }

    private void executeBotMove(BotStrategy botService, BoardService boardLogic) {
        Callable<Tile> botMoveTask = () -> botService.getMakeMove(botService.id, boardLogic);
        Future<Tile> futureMove = scheduler.schedule(botMoveTask, 0, TimeUnit.SECONDS);

        try {
            var tile = futureMove.get(5000, TimeUnit.SECONDS);
            if (tile != null) boardLogic.makeMove(botService.id, tile);
        } catch (TimeoutException e) {
            logger.error("Bot {} move timed out.", botService.id);
            gameFinished(botService.id);
        } catch (Exception e) {
            logger.error("Error during bot {} move", botService.id, e);
            logger.error(e.toString());
            gameFinished(botService.id);
        }
    }

    private void gameFinished(int botNumberLose) {
        if (botNumberLose == 1) {
            secondBotWins.incrementAndGet();
        } else {
            firstBotWins.incrementAndGet();
        }
        totalGamesCompleted.incrementAndGet();
    }

    private void saveResultsToJson(long duration) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        File file = new File("game_results.json");

        List<Results> resultsList;

        if (file.exists()) {
            try {
                resultsList = mapper.readValue(file, new TypeReference<List<Results>>() {});
            } catch (IOException e) {
                logger.error("Error reading existing game results from JSON", e);
                resultsList = new ArrayList<>();
            }
        } else {
            resultsList = new ArrayList<>();
        }

        Results newResults = new Results(gameCount, firstBotWins.get(), secondBotWins.get(), draws.get(), duration);
        resultsList.add(newResults);

        try {
            writer.writeValue(file, resultsList);
            logger.info("Game results saved to game_results.json");
        } catch (IOException e) {
            logger.error("Error saving game results to JSON", e);
        }
    }

    private static class Results {
        public int totalGames;
        public int firstBotWins;
        public int secondBotWins;
        public int draws;
        public long duration; // Новое поле для времени выполнения

        public Results() {}

        public Results(int totalGames, int firstBotWins, int secondBotWins, int draws, long duration) {
            this.totalGames = totalGames;
            this.firstBotWins = firstBotWins;
            this.secondBotWins = secondBotWins;
            this.draws = draws;
            this.duration = duration;
        }
    }
}