package workoutprj.workout.Service;

import workoutprj.workout.DTO.BoardDTO;
import workoutprj.workout.DTO.BoardDetailDTO;
import workoutprj.workout.DTO.BoardViewDTO;

import java.util.List;

public interface IBoardService {

    /**
     * 게시판 등록
     */
    void createBoard(BoardDTO board);

    // 특정 게시물 조회
    BoardDetailDTO findBoard(Long id);

    // 전체 게시물 조회
    List<BoardViewDTO> findAll();

    // 게시판 수정
    void updateBoard(Long id, String boardName, String content);

    // 게시판 삭제
    void deleteBoard(Long id);


    BoardDetailDTO findBoardDetail(Long boardId);

    List<BoardViewDTO> searchBoard(String category, String q);
}

