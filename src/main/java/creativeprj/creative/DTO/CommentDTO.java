package creativeprj.creative.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
    private Long board_id;
    private Long member_id;
    private String comment_content;

    public CommentDTO(Long board_id, Long member_id, String comment_content) {
        this.board_id = board_id;
        this.member_id = member_id;
        this.comment_content = comment_content;
    }
}
