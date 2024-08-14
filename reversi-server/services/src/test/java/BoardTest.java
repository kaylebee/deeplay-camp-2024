import io.deeplay.camp.board.BoardService;
import io.deeplay.camp.bot.KaylebeeBotMyFunc;
import io.deeplay.camp.entity.Board;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    public void testBoardCopy() {
        BoardService originalBoard = new BoardService(new Board());
        originalBoard.setPiece(3, 3, 1);
        originalBoard.setPiece(4, 4, 2);

        KaylebeeBotMyFunc bot = new KaylebeeBotMyFunc(1, "Kaylebee", 4);
        BoardService copiedBoard = bot.getBoardCopy(originalBoard);

        String originalBoardState = originalBoard.getBoardStateDTOWithoutValidMoves();
        String copiedBoardState = copiedBoard.getBoardStateDTOWithoutValidMoves();
        System.out.println(originalBoardState);
        System.out.println(copiedBoardState);

        assertEquals(originalBoardState, copiedBoardState);

        copiedBoard.setWhiteChips(0);
        copiedBoardState = copiedBoard.getBoardStateDTOWithoutValidMoves();

        originalBoardState = originalBoard.getBoardStateDTOWithoutValidMoves();

        assertNotEquals(originalBoardState, copiedBoardState);

        originalBoard.setBlackChips(0);
        originalBoardState = originalBoard.getBoardStateDTOWithoutValidMoves();

        copiedBoardState = copiedBoard.getBoardStateDTOWithoutValidMoves();

        assertNotEquals(originalBoardState, copiedBoardState);
    }
}