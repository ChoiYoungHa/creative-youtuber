package workoutprj.workout.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import workoutprj.workout.DTO.YoutubeDTO;
import workoutprj.workout.Service.Impl.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/core")
public class CoreController {
    @Resource(name = "CoreService")
    private CoreService coreService;

    private static final Logger logger = LoggerFactory.getLogger(CoreController.class);

    // 유튜브 대본에 핵심 키워드만 추출해주는 어시스턴트
    @PostMapping("/youtube-reference")
    @ResponseBody
    public List<YoutubeDTO> youtubeReference(HttpServletRequest request) throws JsonProcessingException, InterruptedException {
        String content = request.getParameter("content");
        logger.info("content: " + content);
        List<YoutubeDTO> response = coreService.youtubeReference(content);

        return response;
    }


    // 벤치마킹 할 구독자 대비 조회수가 잘 나온 동영상 찾아줌
    @PostMapping("/youtube-benchmarking")
    @ResponseBody
    public List<YoutubeDTO> youtubeBenchmarking(HttpServletRequest request) throws JsonProcessingException {
        String keyword = request.getParameter("keyword");
        List<YoutubeDTO> response = coreService.youtubeBenchmarking(keyword);

        return response;
    }

    // caption 서비스랑 여기로 옮기기




    // 참고자료 찾기 페이지이동
    @GetMapping("/findByReference")
    public String findByReference() {
        return "search/findByReference";
    }


    @GetMapping("/benchmarking")
    public String benchmarking(){
        return "search/benchmarking";
    }


}
