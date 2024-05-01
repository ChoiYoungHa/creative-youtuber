package workoutprj.workout.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import workoutprj.workout.DTO.BoardDTO;
import workoutprj.workout.DTO.BoardDetailDTO;
import workoutprj.workout.DTO.BoardViewDTO;
import workoutprj.workout.Domain.Board;
import workoutprj.workout.Repositrory.BoardRepository;
import workoutprj.workout.Service.IBoardService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService implements IBoardService {

    private final BoardRepository boardRepository;

    // 게시물 등록
    @Override
    public void createBoard(BoardDTO board) {
        boardRepository.createBoard(board);
    }

    // 특정 게시물 찾기
    @Override
    public BoardDetailDTO findBoard(Long id) {
        // DTO로 변환
        Board board = boardRepository.findOneBoard(id);
        return new BoardDetailDTO(board.getBoard_id(), board.getBoard_name(), board.getBoard_content(),
                board.getMember().getNickname(), board.getBoard_click(), board.getMember().getMember_id());
    }

    // 전체 게시물 조회
    @Override
    public List<BoardViewDTO> findAll() {
        List<Board> boards = boardRepository.findAllBoard();
        return boards.stream()
                .map(board -> new BoardViewDTO(
                        board.getBoard_id(),
                        board.getBoard_name(),
                        board.getMember().getNickname(),
                        board.getBoard_click()))
                .collect(Collectors.toList());
    }

    // 게시물 수정
    @Override
    public void updateBoard(Long id, String boardName, String content) {
        boardRepository.updateBoard(id, boardName, content);
    }

    // 게시물 삭제
    @Override
    public void deleteBoard(Long id) {
        boardRepository.deleteBoard(id);
    }


    // 상세 페이지 조회 시 조회수 증가로직
    @Override
    @Transactional
    public BoardDetailDTO findBoardDetail(Long boardId) {
        Board board = boardRepository.findOneBoard(boardId);
        board.incrementClick();

        return new BoardDetailDTO(board.getBoard_id(), board.getBoard_name(), board.getBoard_content(),
                board.getMember().getNickname(), board.getBoard_click(), board.getMember().getMember_id());
    }


    // 게시판 검색 후 boards -> boardDTO(게시물번호, 맴버번호, 게시물이름, 게시물 조회수, 작성자)
    @Override
    public List<BoardViewDTO> searchBoard(String category, String q) {
        List<Board> boards = boardRepository.searchBoard(category, q);
        return boards.stream()
                .map(board -> new BoardViewDTO(
                        board.getBoard_id(),
                        board.getBoard_name(),
                        board.getMember().getNickname(),
                        board.getBoard_click()))
                .collect(Collectors.toList());
    }


}
