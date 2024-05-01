package workoutprj.workout.DTO;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BoardDTO {
    private Long board_id;
    private Long member_id;
    private String board_name;
    private int board_click;
    private String member_nick;
    private String board_content;

}
