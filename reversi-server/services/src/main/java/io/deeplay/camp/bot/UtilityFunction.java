package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;

public interface UtilityFunction {
    double evaluate(BoardService board, int currentPlayerId);
}
