package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;

import java.util.Random;

public class RandomUtilityFunction implements UtilityFunction {
    private final Random random;

    public RandomUtilityFunction() {
        this.random = new Random();
    }

    @Override
    public double evaluate(BoardService boardAfter, int currentPlayerId) {
        if (boardAfter.checkForWin().isGameFinished()) {
            if (boardAfter.checkForWin().getUserIdWinner() == 1) {
                return 1;
            } else if (boardAfter.checkForWin().getUserIdWinner() == 2) {
                return -1;
            } else {
                return 0;
            }
        }
        return -1 + 2 * random.nextDouble();
    }
}
