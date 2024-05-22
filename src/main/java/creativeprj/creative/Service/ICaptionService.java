package creativeprj.creative.Service;

import reactor.core.publisher.Mono;

public interface ICaptionService {

    Mono<String> getCaption(String videoId);
}
