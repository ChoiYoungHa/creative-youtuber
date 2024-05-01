package workoutprj.workout.Service.Impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import workoutprj.workout.Service.ICaptionService;

@Service("CationService")
public class CaptionService implements ICaptionService {


    private final WebClient webClient;

    public CaptionService(WebClient.Builder webClientBuilder) {
        //this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:5000").build();
        this.webClient = webClientBuilder.baseUrl("http://172.31.12.131:5000").build();
    }


    // webClient로 rest 요청하기, oAuth 받고 캡션 요청하기
    public Mono<String> getCaption(String videoId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getSubtitle")
                        .queryParam("videoId", videoId)
                        .build())
                .retrieve() // Trigger the HTTP request and retrieve the response body
                .bodyToMono(String.class); // Convert the response body to String
    }

    // youtubeUrl로 youtube api 요청 후 videoId 추출하기



}
