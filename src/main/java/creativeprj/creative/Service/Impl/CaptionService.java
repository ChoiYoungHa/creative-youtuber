package creativeprj.creative.Service.Impl;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import creativeprj.creative.Service.ICaptionService;


@Service("CationService")
public class CaptionService implements ICaptionService {

    @Value("${ec2.ip}")
    private String ec2_ip;

    private WebClient webClient;

    private final WebClient.Builder webClientBuilder;

    public CaptionService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @PostConstruct
    private void init() {
        this.webClient = webClientBuilder.baseUrl("http://" + ec2_ip + ":5000").build();
    }


    // webClient로 flask server rest 요청하기
    @Override
    public Mono<String> getCaption(String videoId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/getSubtitle")
                        .queryParam("videoId", videoId)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }
}



