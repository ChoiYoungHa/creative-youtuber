package workoutprj.workout.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import workoutprj.workout.Domain.Member;
import workoutprj.workout.Exception.EmailAlreadyExistsException;
import workoutprj.workout.Service.Impl.MemberService;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 인덱스 페이지 생성
    @GetMapping("/")
    public String index(){
        log.info("index 페이지 연결");
        return "search/findByReference";
    }

    // 로그인 페이지 이동
    @RequestMapping("/login")
    public String login(HttpSession session, Model model) {

        if (session.getAttribute("SS_MEMBER_ID") != null) {
            model.addAttribute("error", "이미 로그인중 입니다.");
            return "/search/findByReference";
        }
        return "user/login";
    }
    // 로그인
    @PostMapping("/getLogin")
    public String getLogin(HttpServletRequest request, Model model, HttpSession session) {
        log.info("getLogin Start!");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        log.info("login start!");
        try {
            Member member = memberService.login(email, password);
            log.info("login success");
            session.setAttribute("SS_MEMBER_ID", member.getMember_id());
            session.setAttribute("SS_MEMBER_NAME", member.getUser_name());
            session.setAttribute("SS_MEMBER_NICK", member.getNickname());
            return "redirect:/core/findByReference";
        } catch (IllegalStateException e) {
            log.info("로그인 실패");
            model.addAttribute("error", e.getMessage());
            return "user/login";
        }
    }

    // 로그아웃 기능
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
    @PostMapping("/joinMember")
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


