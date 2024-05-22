package creativeprj.creative.Controller;

import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import creativeprj.creative.Service.ICaptionService;

@Controller
@RequestMapping("/caption")
public class CaptionController {
    @Resource(name = "CationService")
    private ICaptionService captionService;

    private static final Logger logger = LoggerFactory.getLogger(CaptionController.class);


    @GetMapping("/subtitle")
    public String youtubeScript() {
        return "search/subtitle";
    }

    @GetMapping("/test")
    @ResponseBody
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Test endpoint");
    }
}
