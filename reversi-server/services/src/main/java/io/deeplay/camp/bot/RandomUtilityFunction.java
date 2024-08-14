package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;

import java.util.Random;

public class RandomUtilityFunction implements UtilityFunction {
    private final Random random;

    public RandomUtilityFunction() {
        this.random = new Random();
    }

    @Override
    public double evaluate(BoardService boardBefore, BoardService boardAfter, int currentPlayerId) {
        return -1 + 2 * random.nextDouble();
    }
}
