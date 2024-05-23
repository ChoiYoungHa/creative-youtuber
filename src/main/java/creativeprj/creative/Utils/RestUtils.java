package creativeprj.creative.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class RestUtils {

    @Value("${openai.api.key}")
    private String apiKey;

    private static String staticApiKey;

    @PostConstruct
    private void init(){
        staticApiKey = apiKey;
    }


    /**
     * @regdate 24.05.22
     * @param responseBody
     * @return Assistants threadId
     * @throws JsonProcessingException
     * @expain String json 데이터의 id 값반환
     */
    public static String jsonNodeGetTarget(String responseBody, String target) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.path(target).asText();
    }


    // 중복되는 헤더 생성
    public static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(staticApiKey);
        headers.set("OpenAI-Beta", "assistants=v1");
        return headers;
    }



}
