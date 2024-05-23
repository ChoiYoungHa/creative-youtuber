package creativeprj.creative.Service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import creativeprj.creative.Utils.AwsSecretManagerUtil;
import creativeprj.creative.Utils.RestUtils;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;

class CoreServiceTest {


    @Test
    void jsonParsingTest() throws JsonProcessingException {
        //given
        String json = "{\n" +
                "  \"id\": \"thread_abc123\",\n" +
                "  \"object\": \"thread\",\n" +
                "  \"created_at\": 1699012949,\n" +
                "  \"metadata\": {},\n" +
                "  \"tool_resources\": {}\n" +
                "}\n";

        //when
        String id = RestUtils.jsonNodeGetTarget(json, "id");

        //then
        assertEquals("thread_abc123", id);
    }


    @Test
    void requestOpenAiCreateThread() throws JsonProcessingException {
        RestAssured.baseURI = "https://api.openai.com/v1/threads";
        HttpHeaders headers = RestUtils.createHeaders();
        String secretName = "/secret/youtuber_prod_app";
        String secretJson = AwsSecretManagerUtil.getSecret(secretName);
        String apiKey = RestUtils.jsonNodeGetTarget(secretJson, "openai_api_key");

        System.out.println(apiKey);

        given().
                header("Content-Type", headers.getContentType().toString()).
                header("Authorization", "Bearer " + apiKey).
                header("OpenAI-Beta", headers.getFirst("OpenAI-Beta")).
                when().
                    post().
                then().
                    statusCode(200).
                    body("object", equalTo("thread"));
    }




}