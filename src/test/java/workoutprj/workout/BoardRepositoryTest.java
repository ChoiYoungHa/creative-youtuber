package workoutprj.workout;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import workoutprj.workout.Domain.Board;
import workoutprj.workout.Repositrory.BoardRepository;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;


    /**
     * 게시판 등록 테스트
     */
    @Test
    @Transactional
    public void testCreateBoard() {
        Board board = new Board();
        board.setBoard_name("Test Board");
        board.setBoard_content("Test Content");
//        boardRepository.createBoard(board);

        Board foundBoard = boardRepository.findOneBoard(board.getBoard_id());
        assertNotNull(foundBoard);
        assertEquals("Test Board", foundBoard.getBoard_name());
        assertEquals("Test Content", foundBoard.getBoard_content());
    }

    /**
     * 게시물 조회 테스트
     */
    @Test
    public void testFindOneBoard() {
        //given
        Board board = new Board();
        board.setBoard_name("test");
        board.setBoard_content("test");
//        boardRepository.createBoard(board);

        //when
        Board findBoard = boardRepository.findOneBoard(board.getBoard_id());

        //then
        assertNotNull(board);
    }

    /**
     * 게시물 리스트 조회 테스트
     */
    @Test
    public void testFindAllBoard() {

        //given
        Board board = new Board();
        board.setBoard_name("test");
        board.setBoard_content("test");
//        boardRepository.createBoard(board);

        Board board1 = new Board();
        board1.setBoard_name("test_test");
        board1.setBoard_content("test");
//        boardRepository.createBoard(board1);

        //when
        List<Board> boards = boardRepository.findAllBoard();


        //then
        for (Board board2 : boards) {
            System.out.println("board2.getBoard_name() = " + board2.getBoard_name());
        }
        assertNotNull(boards);
        assertFalse(boards.isEmpty());
    }

    /**
     * 게시판 수정 테스트
     */
    @Test
    @Transactional
    public void testUpdateBoard() {
        //given
        Board board = new Board();
        board.setBoard_name("test");
        board.setBoard_content("test");
//        boardRepository.createBoard(board);
        String newName = "Updated Name";
        String newContent = "Updated Content";

        //when
        boardRepository.updateBoard(board.getBoard_id(), newName, newContent);

        //then
        Board updatedBoard = boardRepository.findOneBoard(board.getBoard_id());
        assertEquals(newName, updatedBoard.getBoard_name());
        assertEquals(newContent, updatedBoard.getBoard_content());
    }

    /**
     * 게시판 삭제 테스트
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void testDeleteBoard() {
        //given
        Board board = new Board();
        board.setBoard_name("test");
        board.setBoard_content("test");
//        boardRepository.createBoard(board);

        //when
        boardRepository.deleteBoard(board.getBoard_id());

        //then
        boardRepository.findOneBoard(board.getBoard_id());
        Assert.fail();
    }

}
