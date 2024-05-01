package workoutprj.workout.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.aspectj.bridge.ILifecycleAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import workoutprj.workout.DTO.CommentDTO;
import workoutprj.workout.DTO.ViewCommentDTO;
import workoutprj.workout.Repositrory.CommentRepository;
import workoutprj.workout.Service.Impl.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentRepository commentRepository;
    private final CommentService commentService;


    // 댓글등록
    @PostMapping("createComment")
    public ResponseEntity<String> createComment(@RequestBody CommentDTO commentDTO, HttpSession session) {

        if (commentDTO.getMember_id() == null) {
            return ResponseEntity.status(403).body("로그인이 필요합니다.");
        }

        try {
            commentRepository.createComment(commentDTO);
            log.info("댓글등록 성공");
            return ResponseEntity.ok("댓글이 등록되었습니다.");
        } catch (RuntimeException e) {
            log.info("댓글등록 실패");
            return ResponseEntity.status(500).body("댓글 등록에 실패했습니다.");
        }
    }

    // 댓글 불러오기
    @GetMapping("getComments/{boardId}")
    public ResponseEntity<?> getComments(@PathVariable("boardId") Long boardId) {

        try {
            List<ViewCommentDTO> comments = commentService.getComments(boardId);
            return ResponseEntity.ok(comments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }

    }

    // 댓글 수정요청
    @PostMapping("editComment/{commentId}")
    public ResponseEntity<?> editComment(@PathVariable("commentId") Long id, @RequestBody ViewCommentDTO pDTO) {

        String commentContent = pDTO.getComment_content();
        log.info("commentContent : " + commentContent);

        try {
            commentRepository.editComment(id, commentContent);
            log.info("댓글수정 성공");
            return ResponseEntity.ok("댓글이 수정 되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("존재하지 않는 댓글은 수정할 수 없습니다.");
        }
    }

    @PostMapping("/deleteComment/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long id) {

        try {
            commentRepository.deleteComment(id);
            log.info("댓글삭제 성공");
            return ResponseEntity.ok("댓글이 삭제되었습니다.");
        } catch (RuntimeException e) {
            log.info("댓글삭제 실패");
            return ResponseEntity.status(500).body("존재하지 않는 댓글은 삭제할 수 없습니다.");
        }
    }


}
