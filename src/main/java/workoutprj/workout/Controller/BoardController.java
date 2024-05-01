package workoutprj.workout.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import workoutprj.workout.DTO.BoardDTO;
import workoutprj.workout.DTO.BoardDetailDTO;
import workoutprj.workout.DTO.BoardViewDTO;
import workoutprj.workout.Exception.NotFindBoardException;
import workoutprj.workout.Service.Impl.BoardService;

import javax.naming.NoPermissionException;
import java.util.List;

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
    @PostMapping("/insertBorad")
    public String insertBoard(HttpServletRequest request, HttpSession session, Model model) {
        String title = request.getParameter("title");
        String contents = request.getParameter("contents");

        // memberId를 repository에 넘긴 후 member 찾아서 jpql을 해야하나 아님 find를해서 name을 넣어줘야하나 물어보자.
        Long ssMemberId = (Long) session.getAttribute("SS_MEMBER_ID");

        BoardDTO bDTO = new BoardDTO();
        bDTO.setMember_id(ssMemberId);
        bDTO.setBoard_name(title);
        bDTO.setBoard_content(contents);

        try {
            boardService.createBoard(bDTO);
            log.info("게시물 등록 성공");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("게시물 등록 실패");
        }

        return "redirect:board/boardList";
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

    // 게시판 수정 페이지 이동
    @GetMapping("/boardEdit/{boardId}")
    public String boardEdit(@PathVariable("boardId") Long boardId, Model model, HttpSession session) throws NoPermissionException {

        // 비로그인 유저가 게시물 수정 url 요청을 보냈을 때.
        Long userId = (Long) session.getAttribute("SS_MEMBER_ID");
        if (userId == null) {
            model.addAttribute("error", "로그인이 필요한 기능입니다.");
            return "user/login";
        }


        // 세션이 존재하는데, 해당 게시물의 작성자인지 확인하는 벨리데이션 로직이 필요함
        try {
            BoardDetailDTO board = boardService.findBoard(boardId);
            // 게시물의 작성자 ID와 세션 사용자 ID 비교
            if (!board.getMember_id().equals(userId)) {
                throw new NoPermissionException("수정 권한이 없습니다.");
            }
            model.addAttribute("boardDetail", board);
            log.info("게시물 내용 불러오기 성공");
            return "board/boardEdit";  // 게시물 수정 페이지로 이동
        } catch (NotFindBoardException e) {
            model.addAttribute("error", e.getMessage());
            log.info("존재하지 않는 게시물");
            return "search/findByReference";  // 기본 페이지로 리다이렉트
        } catch (NoPermissionException e) {
            model.addAttribute("error", e.getMessage());
            log.info("수정 권한 없음");
            return "search/findByReference";  // 기본 페이지로 리다이렉트
        }
    }


    // 게시물 수정
    @PostMapping("/boardEditUpdate/{boardId}")
    public String boardEditUpdate(@PathVariable("boardId") Long boardId, HttpServletRequest request, Model model) {
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        try {
            boardService.updateBoard(boardId, title, content);
            log.info("게시판 수정 성공");
            return "redirect:board/detail/" + boardId;
        } catch (Exception e) {
            model.addAttribute("error", "게시물 수정 중 오류 발생");
            return "redirect:board/boardEdit" + boardId; // 에러 발생 시 수정 페이지로 다시 이동
        }
    }

    // 게시물 삭제
    @GetMapping("/boardDelete/{boardId}")
    public String boardDelete(@PathVariable("boardId") Long boardId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("SS_MEMBER_ID");
        if (userId == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            return "user/login";
        }

        // 세션이 존재하는데, 해당 게시물의 작성자인지 확인하는 벨리데이션 로직이 필요함
        try {
            BoardDetailDTO board = boardService.findBoard(boardId);
            // 게시물의 작성자 ID와 세션 사용자 ID 비교
            if (!board.getMember_id().equals(userId)) {
                throw new NoPermissionException("삭제 권한이 없습니다.");
            }
            // 게시판 삭제
            boardService.deleteBoard(boardId);

            // 게시판 전체 불러오기
            List<BoardViewDTO> all = boardService.findAll();

            // model 로 넘기면서 boardLsit 이동
            model.addAttribute("boards", all);
            log.info("게시물 삭제 성공");
            return "board/boardList";  // 게시판 페이지로 이동
        } catch (NotFindBoardException e) {
            model.addAttribute("error", e.getMessage());
            log.info("존재하지 않는 게시물");
            return "search/findByReference";  // 기본 페이지로 리다이렉트
        } catch (NoPermissionException e) {
            model.addAttribute("error", e.getMessage());
            log.info("삭제 권한 없음");
            return "search/findByReference";  // 기본 페이지로 리다이렉트
        }
    }

    @GetMapping("/searchBoard")
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
