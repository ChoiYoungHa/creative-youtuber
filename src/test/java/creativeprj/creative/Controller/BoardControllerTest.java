package creativeprj.creative.Controller;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import creativeprj.creative.DTO.BoardDetailDTO;
import creativeprj.creative.Domain.Board;
import creativeprj.creative.Domain.Member;
import creativeprj.creative.ETC.MonitoringInterceptor;
import creativeprj.creative.Repositrory.BoardRepository;
import creativeprj.creative.Security.JwtAuthenticationFilter;
import creativeprj.creative.Security.JwtTokenProvider;
import creativeprj.creative.Service.Impl.BoardService;
import creativeprj.creative.Service.Impl.CustomUserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;


    // 스프링 컨텍스트 자체에 종속되어 있어서 어쩔 수 없이 등록
    @MockBean
    private MonitoringInterceptor monitoringInterceptor;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private BoardDetailDTO boardDetailDTO;

    private String jwtToken;

    // 인터셉터에 종속되어 있어서 어쩔 수 없이 로드
    @Configuration
    static class TestConfig{
        @Bean
        public WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }
    }


    @BeforeEach
    public void setUp(){
        // 임의의 게시물 생성 회원번호 1L, 게시물 번호1L
         boardDetailDTO = new BoardDetailDTO(1L, "글제목",
                "글 내용","사용자 닉네임",1, 1L);
        jwtToken = "Bearer " + jwtTokenProvider.createToken("test@example.com", "1L");

    }
    private Claims createClaims(String email, String memberId) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("memberId", memberId);
        return claims;
    }


    // 로그인을 하지 않았을 경우
    @Test
    public void testBoardEdit_Unauthorized() throws Exception{
        mockMvc.perform(get("/board/boardEdit/{boardId}", 1L))
                .andExpect(status().isUnauthorized());
    }








}