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
    @PostMapping("/youtubeReference")
    @ResponseBody
    public List<YoutubeDTO> youtubeReference(HttpServletRequest request) throws JsonProcessingException, InterruptedException {
        // 메시지를 처리하기 위한 스레드 생성
        // 사용자 메시지 처리
        String content = request.getParameter("content");
        logger.info("content: " + content);
        List<YoutubeDTO> response = coreService.youtubeReference(content);

        return response;
    }

    // 벤치마킹 할 구독자 대비 조회수가 잘 나온 동영상 찾아줌
    @PostMapping("/youtubeBenchmarking")
    @ResponseBody
    public List<YoutubeDTO> youtubeBenchmarking(HttpServletRequest request) throws JsonProcessingException {
        String keyword = request.getParameter("keyword");
        List<YoutubeDTO> response = coreService.youtubeBenchmarking(keyword);

        return response;
    }


    @GetMapping("/youtubeSearchTest")
    @ResponseBody
    public List<YoutubeDTO> youtubeSearchTest() throws JsonProcessingException {
        String youtubeResult = coreService.searchVideos("왈라비 키우기");
        List<YoutubeDTO> youtubeDTOS = coreService.parseYoutubeSearchResult(youtubeResult);

        List<String> videoIds = youtubeDTOS.stream().map(YoutubeDTO::getVideoId).collect(Collectors.toList());
        Map<String, Integer> viewCounts = coreService.fetchVideoStatistics(videoIds);

        for (YoutubeDTO video : youtubeDTOS) {
            if (viewCounts.containsKey(video.getVideoId())) {
                video.setViewCount(viewCounts.get(video.getVideoId()));
            }
        }

        return youtubeDTOS;
    }


    @GetMapping("/youtubeSearchTest2")
    @ResponseBody
    public List<YoutubeDTO> youtubeSearchTest2(HttpServletRequest request) throws JsonProcessingException {
        String keyword = request.getParameter("keyword");
        List<YoutubeDTO> s = coreService.youtubeBenchmarking(keyword);

        return s;
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
