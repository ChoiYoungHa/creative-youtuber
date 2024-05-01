package workoutprj.workout.Service;

import workoutprj.workout.DTO.ViewCommentDTO;

import java.util.List;

public interface ICommentService {
    List<ViewCommentDTO> getComments(Long boardId);
}
