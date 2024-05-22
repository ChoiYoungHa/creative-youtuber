package creativeprj.creative.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDetailDTO {
    // 게시판에 들어가면 보여야 할 것.
    private Long board_id;
    private String board_title;
    private String board_content;
    private String member_nickname;
    private int click;

    private Long member_id;

    public BoardDetailDTO(Long board_id, String board_title, String board_content, String member_nickname, int click, Long member_id) {
        this.board_id = board_id;
        this.board_title = board_title;
        this.board_content = board_content;
        this.member_nickname = member_nickname;
        this.click = click;
        this.member_id = member_id;
    }
}
