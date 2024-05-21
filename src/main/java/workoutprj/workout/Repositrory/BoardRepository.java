package workoutprj.workout.Repositrory;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import workoutprj.workout.DTO.BoardDTO;
import workoutprj.workout.Domain.Board;
import workoutprj.workout.Domain.Member;
import workoutprj.workout.Exception.NotFindBoardException;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardRepository {

    private final EntityManager em;


    /**
     * 게시판 등록
     */
    @Transactional
    public void createBoard(BoardDTO board) {
        Long memberId = board.getMember_id();
        Member member = em.find(Member.class, memberId);

        Board entity = new Board();
        entity.setBoard_name(board.getBoard_name());
        entity.setBoard_content(board.getBoard_content());
        entity.setBoard_click(0);
        entity.setMember(member);

        em.persist(entity);
    }


    /**
     * 게시물 조회, 수정 시 게시물 데이터 불러오기
     */
    public Board findOneBoard(Long boardId) {
        Board board = em.find(Board.class, boardId);
        if (board == null) {
            throw new NotFindBoardException("해당 게시물을 찾을 수 없습니다.");
        }
        return board;
    }

    /**
     * 게시물 리스트 조회
     */
    public List<Board> findAllBoard() {
        // jqpl 쿼리 조회
        return em.createQuery("select b from Board b join fetch b.member order by b.board_id", Board.class)
                .getResultList();
    }

    /**
     * 게시판 수정
     */
    @Transactional
    public void updateBoard(Long boardId, String boardName, String content) {
        Board board = em.find(Board.class, boardId);
        if (board != null){
            board.setBoard_content(content);
            board.setBoard_name(boardName);
        }
    }

    /**
     * 게시판 삭제
     */
    @Transactional
    public void deleteBoard(Long id) {
        Board board = em.find(Board.class, id);
        if (board == null) {
            throw new IllegalStateException("해당 게시물은 존재하지 않습니다.");
        }

        em.createQuery("delete from Board b where b.board_id = :boardId")
                .setParameter("boardId", id)
                .executeUpdate();
    }


    /**
     * 제목, 글쓴이 검색어로 게시글 조회
     */
    public List<Board> searchBoard(String category, String q) {
        List<Board> boards = new ArrayList<>();
        try {

            if (category.equals("nickname")) {
                return em.createQuery("select b from Board b join fetch b.member where b.member.nickname like :nickname", Board.class)
                        .setParameter("nickname", "%" + q + "%")
                        .getResultList();
            } else if (category.equals("title")) {
                return em.createQuery("select b from Board b join fetch b.member where b.board_name like :title", Board.class)
                        .setParameter("title", "%" + q + "%")
                        .getResultList();
            }
        } catch (NoResultException e) {
            throw new RuntimeException("검색결과가 없습니다.");
        }

        return boards;
    }



}
