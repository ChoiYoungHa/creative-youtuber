package creativeprj.creative.Controller;

import creativeprj.creative.Service.Impl.CustomUserDetailService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import creativeprj.creative.Domain.Member;
import creativeprj.creative.Exception.EmailAlreadyExistsException;
import creativeprj.creative.Service.Impl.MemberService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {

    private final MemberService memberService;

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService customUserDetailService;

    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    // 로그인 페이지 이동
    @GetMapping("/login")
    public String login(HttpSession session, Model model) {

        if (session.getAttribute("SS_MEMBER_ID") != null) {
            model.addAttribute("error", "이미 로그인중 입니다.");
            return "/search/findByReference";
        }
        return "user/login";
    }

    // 세션 로그인
//    @PostMapping("/login")
//    public String getLogin(HttpServletRequest request, Model model, HttpSession session) {
//        log.info("getLogin Start!");
//        String email = request.getParameter("email");
//        String password = request.getParameter("password");
//
//        log.info("login start!");
//        try {
//            Member member = memberService.login(email, password);
//            log.info("login success");
//            session.setAttribute("SS_MEMBER_ID", member.getMember_id());
//            session.setAttribute("SS_MEMBER_NAME", member.getUser_name());
//            session.setAttribute("SS_MEMBER_NICK", member.getNickname());
//            return "redirect:/core/findByReference";
//        } catch (IllegalStateException e) {
//            log.info("로그인 실패");
//            model.addAttribute("error", e.getMessage());
//            return "user/login";
//        }
//    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, String> login(@RequestParam String email, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
            String token = Jwts.builder()
                    .setSubject(email)
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(key)
                    .compact();
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return response;
        } catch (AuthenticationException e) {
            throw new RuntimeException("존재하지 않는 회원이거나 이메일 패스워드가 틀렸습니다.");
        }
    }



    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("SS_MEMBER_ID");
        session.removeAttribute("SS_MEMBER_NICK");
        session.removeAttribute("SS_MEMBER_NAME");
        return "redirect:core/findByReference";
    }

    // 회원가입 페이지 이동
    @GetMapping("/signup")
    public String signup(){
        return "user/signup";
    }

    // 회원가입
    @PostMapping("/signup")
    public String joinMember(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        String memberName = request.getParameter("member_name");
        String memberNick = request.getParameter("member_nick");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // MemberDTO 만들어서 보낼 것;;
        Member member = new Member();
        member.setUser_name(memberName);
        member.setNickname(memberNick);
        member.setEmail(email);
        member.setPassword(password);

        try {
            memberService.join(member);
            log.info("회원가입 성공");
            redirectAttributes.addFlashAttribute("msg", "회원가입이 성공적으로 완료되었습니다.");
            return "redirect:login?success=true";
        } catch (IllegalStateException e) {
            log.info("회원가입 실패");
            return "user/login";
        }
    }

    // 로그인 패스워드 틀릴 때
    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public String InvaliDataHandleException(InvalidDataAccessApiUsageException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "user/login"; // 에러 메시지와 함께 로그인 페이지로 리턴
    }


    // 회원가입 이메일중복
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String EmailDuplicateException(EmailAlreadyExistsException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "user/signup"; // 에러 메시지와 함께 로그인 페이지로 리턴
    }

}


