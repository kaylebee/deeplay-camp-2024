package io.deeplay.camp.bot;

import io.deeplay.camp.board.BoardService;

public interface UtilityFunction {
    double evaluate(GameStateNode node, BoardService boardBefore, BoardService boardAfter, int currentPlayerId);
}
