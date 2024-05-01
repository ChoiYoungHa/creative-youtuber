package workoutprj.workout.Service;

import reactor.core.publisher.Mono;

public interface ICaptionService {

    Mono<String> getCaption(String videoId);
}
