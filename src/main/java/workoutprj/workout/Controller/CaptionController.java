package workoutprj.workout.Controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import workoutprj.workout.Service.ICaptionService;

@Controller
@RequestMapping("/caption")
public class CaptionController {
    @Resource(name = "CationService")
    private ICaptionService captionService;

    private static final Logger logger = LoggerFactory.getLogger(CaptionController.class);

    @PostMapping("/getCaption")
    @ResponseBody
    public String getCaption(HttpServletRequest request) {
        String youtubeUrl = request.getParameter("youtubeUrl");

        String videoId = youtubeUrl.split("v=")[1].trim();

        logger.info("videoId: " + videoId);
        return captionService.getCaption(videoId).block();
    }

    @GetMapping("/subtitle")
    public String youtubeScript() {
        return "search/subtitle";
    }

}
