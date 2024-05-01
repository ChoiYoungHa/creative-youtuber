package workoutprj.workout;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import workoutprj.workout.Service.Impl.CoreService;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
class WorkoutApplicationTests {

    @Nested
    @ExtendWith(SpringExtension.class)
	@SpringBootTest
    class OpenAIServiceTest {

		@Autowired
		private CoreService openAIService;

		@Autowired
		private RestTemplate restTemplate;

		@Test
		public void testCreateThread() throws JsonProcessingException {
			MockRestServiceServer mockServer = MockRestServiceServer.createServer(restTemplate);

			String assistantId = "asst_JfxJ0itTR9W5gWTD2bFZcP9R";
			String expectedUrl = "https://api.openai.com/v1/threads";

			mockServer.expect(requestTo(expectedUrl))
					.andExpect(method(HttpMethod.POST))
					.andRespond(withSuccess("{\"thread_id\": \"new_thread_id\"}", MediaType.APPLICATION_JSON));

			String result = openAIService.createThread();
			System.out.println("Thread Created with Response: " + result);

			mockServer.verify();
		}
	}



}
