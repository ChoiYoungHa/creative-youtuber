package workoutprj.workout.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ViewCommentDTO {

    // 댓글 표시용 DTO
    private Long comment_id;
    private String comment_content;

    private String member_nickname;
    private Long member_id;

    public ViewCommentDTO(Long comment_id, String comment_content, String member_nickname, Long member_id) {
        this.comment_id = comment_id;
        this.comment_content = comment_content;
        this.member_nickname = member_nickname;
        this.member_id = member_id;
    }
}
