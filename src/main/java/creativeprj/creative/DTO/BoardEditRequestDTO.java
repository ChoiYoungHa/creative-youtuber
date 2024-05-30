package creativeprj.creative.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class BoardEditRequestDTO {

    private String title;
    private String content;

}
