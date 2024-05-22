package creativeprj.creative.Service;

import creativeprj.creative.DTO.ViewCommentDTO;

import java.util.List;

public interface ICommentService {
    List<ViewCommentDTO> getComments(Long boardId);
}
