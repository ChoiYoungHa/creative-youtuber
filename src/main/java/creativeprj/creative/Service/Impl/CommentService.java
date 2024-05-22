package creativeprj.creative.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import creativeprj.creative.DTO.ViewCommentDTO;
import creativeprj.creative.Domain.Comment;
import creativeprj.creative.Repositrory.CommentRepository;
import creativeprj.creative.Service.ICommentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private final CommentRepository commentRepository;

    // Entity -> DTO변환
    @Override
    public List<ViewCommentDTO> getComments(Long boardId) {
        List<Comment> comments = commentRepository.getComments(boardId);
        return comments.stream()
                .map(comment -> new ViewCommentDTO(
                        comment.getComment_id(),
                        comment.getComment_content(),
                        comment.getMember().getNickname(),
                        comment.getMember().getMember_id()
                )).collect(Collectors.toList());
    }

}
