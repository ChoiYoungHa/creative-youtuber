package workoutprj.workout.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardViewDTO {
    private Long board_id;
    private String board_name;
    private String member_nickname;
    private int board_click;

    public BoardViewDTO(Long boardId, String board_name, String member_nickname, int board_click) {
        this.board_id = boardId;
        this.board_name = board_name;
        this.member_nickname = member_nickname;
        this.board_click = board_click;
    }
}
