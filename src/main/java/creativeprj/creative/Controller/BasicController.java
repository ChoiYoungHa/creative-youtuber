package creativeprj.creative.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class BasicController {

    // 인덱스 페이지 생성
    @GetMapping("/")
    public String index(){
        log.info("index 페이지 연결");
        return "search/findByReference";
    }
}
