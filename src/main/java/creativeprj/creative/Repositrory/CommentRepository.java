package creativeprj.creative.Repositrory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import creativeprj.creative.DTO.CommentDTO;
import creativeprj.creative.Domain.Board;
import creativeprj.creative.Domain.Comment;
import creativeprj.creative.Domain.Member;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class CommentRepository {
    private final EntityManager em;

    // 사용자가 게시판에 댓글을 단다. 등록.
    // 누군지? 어떤 게시판에?
    public void createComment(CommentDTO cDTO) {
        Comment comment = new Comment();

        // 어차피 form에서 validation 처리
        comment.setComment_content(cDTO.getComment_content());


        Member member = em.find(Member.class, cDTO.getMember_id());
        if (member == null) {
            throw new IllegalStateException("존재하지 않는 회원입니다.");
        }

        Board board = em.find(Board.class, cDTO.getBoard_id());
        if (board == null) {
            throw new IllegalStateException("존재하지 않는 게시물입니다.");
        }

        comment.setMember(member);
        comment.setBoard(board);

        try {
            em.persist(comment);
        } catch (PersistenceException e) {
            throw new RuntimeException("댓글 저장중 문제가 생겼습니다.");
        }
    }

    // 특정 게시물의 댓글 조회
    @Transactional(readOnly = true)
    public List<Comment> getComments(Long boardId) {
        return em.createQuery("select c from Comment c join fetch c.member m where c.board.board_id = :boardId",
                        Comment.class)
                .setParameter("boardId", boardId)
                .getResultList();
    }

    // 댓글 수정
    public void editComment(Long commentId, String comment_content) {

        // 컨트롤러에서 catch
        Comment comment = em.find(Comment.class, commentId);
        if (commentId == null) {
            throw new IllegalStateException("존재하지 않는 댓글입니다.");
        }else {
            comment.setComment_content(comment_content);
        }
    }

    // 댓글삭제
    public void deleteComment(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        if (comment == null) {
            throw new IllegalStateException("존재하지 않는 댓글입니다.");
        }

        em.createQuery("delete from Comment c where c.comment_id = :comment_id")
                .setParameter("comment_id", commentId)
                .executeUpdate();
    }



}
