package workoutprj.workout.ETC;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${ec2.ip}")
    private String e2_ip;

    @Bean
    public WebClient webClient() {
        String baseUrl = String.format("http://%s:8081", e2_ip);
        return WebClient.builder()
                        .baseUrl(baseUrl)
                        .build();
    }
}
