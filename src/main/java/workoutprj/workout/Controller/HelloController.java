package workoutprj.workout.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("basic")
public class HelloController {

    @GetMapping("hello")
    public String hello(Model model) {
        model.addAttribute("data", "오야스 스프링부트");
        return "hello";
    }

    @GetMapping("text-basic")
    public String textBasic(Model model){
        model.addAttribute("basicData", "basic test");
        return "basic/text-basic";
    }

    @GetMapping("text-unescape")
    public String textUnescape(Model model){
        model.addAttribute("basicData", "<b>basic test</b>");
        return "basic/text-unescape";
    }
}


