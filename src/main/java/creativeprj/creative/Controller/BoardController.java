package creativeprj.creative.Controller;

import creativeprj.creative.DTO.BoardEditRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import creativeprj.creative.DTO.BoardDTO;
import creativeprj.creative.DTO.BoardDetailDTO;
import creativeprj.creative.DTO.BoardViewDTO;
import creativeprj.creative.Exception.NotFindBoardException;
import creativeprj.creative.Service.Impl.BoardService;
import reactor.netty.http.server.HttpServer;

import javax.naming.NoPermissionException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;

    // 게시판 이동
    @GetMapping("/boardList")
    public String boardList(Model model) {
        log.info("start board list");

        List<BoardViewDTO> all = boardService.findAll();

        for (BoardViewDTO boardViewDTO : all) {
            log.info("getMember_nickname : " + boardViewDTO.getMember_nickname());
            log.info("getBoard_name : " + boardViewDTO.getBoard_name());
            log.info("getBoard_click : " + boardViewDTO.getBoard_click());
            log.info("getBoard_id : " + boardViewDTO.getBoard_id());
        }

        model.addAttribute("boards", all);

        return "board/boardList";
    }

    // 게시물 등록 이동
    @GetMapping("/createBoard")
    public String createBoard() {
        return "board/createBoard";
    }

    // 게시물 등록
    @PostMapping("/write")
    public String insertBoard(HttpServletRequest request) {
        String title = request.getParameter("title");
        String contents = request.getParameter("contents");
        String member_id = request.getParameter("member_id");

        long memberId = Long.parseLong(member_id);

        BoardDTO bDTO = new BoardDTO();
        bDTO.setMember_id(memberId);
        bDTO.setBoard_name(title);
        bDTO.setBoard_content(contents);

        try {
            boardService.createBoard(bDTO);
            log.info("게시물 등록 성공");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("게시물 등록 실패");
        }

        return "redirect:/board/boardList";
    }

    // 게시판 상세페이지 조회
    @GetMapping("/detail/{id}")
    public String boardDetail(@PathVariable("id") Long boardId, Model model) {

        try {
            BoardDetailDTO boardDtail = boardService.findBoardDetail(boardId);
            model.addAttribute("boardDetail", boardDtail);
            log.info("게시물 조회 성공");
            return "board/detail";
        } catch (NotFindBoardException e) {
            log.info("게시물 조회 실패");
            model.addAttribute("error", e.getMessage());
            return "search/findByReference";

        }
    }

    // 게시판 수정 데이터 전달
    @GetMapping("/boardEdit/{boardId}")
    @ResponseBody
    public ResponseEntity<?> boardEdit(@PathVariable Long boardId, HttpServletRequest request, Model model) throws NoPermissionException {

        Long memberId = (Long) request.getAttribute("memberId");
        log.info("memberId : " + memberId);

        // 해당 게시물의 작성자인지 확인하는 벨리데이션 로직이 필요함
        try {
            BoardDetailDTO board = boardService.findBoard(boardId);


            // 게시물의 작성자 ID와 세션 사용자 ID 비교
            if (!board.getMember_id().equals(memberId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
            }
            return ResponseEntity.ok(board);
        } catch (NotFindBoardException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 존재하지 않습니다.");
        }
    }


    // 게시물 수정
    @PostMapping("/edit/{boardId}")
    public ResponseEntity<String> boardEditUpdate(@PathVariable("boardId") Long boardId,
                                                  @RequestBody BoardEditRequestDTO bDTO,
                                                  HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");

        String title = bDTO.getTitle();
        String content = bDTO.getContent();

        BoardDetailDTO board = boardService.findBoard(boardId);
        if (!board.getMember_id().equals(memberId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("게시물을 수정할 권한이 없습니다.");
        }

        try {
            boardService.updateBoard(boardId, title, content);
            log.info("게시판 수정 성공");
            return ResponseEntity.ok("게시판이 수정 되었습니다.");
        } catch (NotFindBoardException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 존재하지 않습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류로 게시판 수정에 실패하였습니다.");
        }
    }

    // 게시물 삭제
    @GetMapping("/delete/{boardId}")
    public ResponseEntity<String> boardDelete(@PathVariable("boardId") Long boardId, HttpServletRequest request) {

        Long memberId = (Long) request.getAttribute("memberId");
        log.info("memberId : " + memberId);

        // 세션이 존재하는데, 해당 게시물의 작성자인지 확인하는 벨리데이션 로직이 필요함
        try {
            BoardDetailDTO board = boardService.findBoard(boardId);
            // 게시물의 작성자 ID와 세션 사용자 ID 비교
            if (!board.getMember_id().equals(memberId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
            }
            // 게시판 삭제
            boardService.deleteBoard(boardId);
            return ResponseEntity.ok("게시물이 삭제되었습니다.");
        } catch (NotFindBoardException e) {
            log.info("존재하지 않는 게시물");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시물이 존재하지 않습니다.");
        }
    }

    // 게시물 검색
    @GetMapping("/search")
    public String searchBoard(HttpServletRequest request,Model model) {
        String q = request.getParameter("keyword");
        String category = request.getParameter("searchType");

        log.info("q: " + q);
        log.info("category : " + category);

        try {
            List<BoardViewDTO> boardViewDTOS = boardService.searchBoard(category, q);
            log.info("검색 성공");
            model.addAttribute("boards", boardViewDTOS);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "board/boardList";
    }
}
