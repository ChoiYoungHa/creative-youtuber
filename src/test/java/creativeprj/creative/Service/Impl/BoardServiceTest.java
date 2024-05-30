package creativeprj.creative.Service.Impl;

import creativeprj.creative.DTO.BoardDetailDTO;
import creativeprj.creative.Domain.Board;
import creativeprj.creative.Domain.Member;
import creativeprj.creative.Repositrory.BoardRepository;
import creativeprj.creative.Service.Impl.BoardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    // boardId를 주면 게시물 상세 내용을 불러온다.
    @Test
    public void find_board_test(){

        // 가짜 맴버, 게시판 생성
        Member member = new Member();
        member.setNickname("하이요");
        member.setMember_id(1L);

        Board mockBoard = new Board();
        mockBoard.setBoard_id(1L);
        mockBoard.setBoard_content("안냐세요");
        mockBoard.setBoard_name("테스트입니당.");
        mockBoard.setBoard_click(1);
        mockBoard.setMember(member);


        when(boardRepository.findOneBoard(1L)).thenReturn(mockBoard);

        // 테스트 대상 메서드 호출
        BoardDetailDTO result = boardService.findBoard(1L);

        // 검증
        assertNotNull(result);
        assertEquals(1L, result.getBoard_id());

    }




}