package creativeprj.creative.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import creativeprj.creative.DTO.YoutubeDTO;
import creativeprj.creative.Service.Impl.CaptionService;
import creativeprj.creative.Service.Impl.CoreService;

import java.util.List;

@Controller
@RequestMapping("/core")
@RequiredArgsConstructor
@Slf4j
public class CoreController {


    private final CoreService coreService;
    private final CaptionService captionService;

    

    // 유튜브 대본에 핵심 키워드만 추출해주는 어시스턴트
    @PostMapping("/youtube-reference")
    @ResponseBody
    public List<YoutubeDTO> youtubeReference(HttpServletRequest request) throws JsonProcessingException, InterruptedException {
        String content = request.getParameter("content");
        log.info("content: " + content);
        List<YoutubeDTO> response = coreService.youtubeReference(content);

        return response;
    }


    // 벤치마킹 할 구독자 대비 조회수가 잘 나온 동영상 찾아줌
    @PostMapping("/youtube-benchmarking")
    @ResponseBody
    public List<YoutubeDTO> youtubeBenchmarking(HttpServletRequest request) throws JsonProcessingException {
        String keyword = request.getParameter("keyword");
        log.info("keyword : " + keyword);
        List<YoutubeDTO> response = coreService.youtubeBenchmarking(keyword);

        return response;
    }

    @PostMapping("/getCaption")
    @ResponseBody
    public String getCaption(HttpServletRequest request) {
        String youtube_url = request.getParameter("youtube_url");

        String videoId = youtube_url.split("v=")[1].trim();

        log.info("videoId: " + videoId);
        return captionService.getCaption(videoId).block();
    }


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
