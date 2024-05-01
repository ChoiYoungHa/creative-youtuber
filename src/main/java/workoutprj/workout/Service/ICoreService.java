package workoutprj.workout.Service;


import com.fasterxml.jackson.core.JsonProcessingException;
import workoutprj.workout.DTO.YoutubeDTO;

import java.util.List;

public interface ICoreService {
    public List<YoutubeDTO> youtubeReference(String ask) throws JsonProcessingException, InterruptedException;

    public List<YoutubeDTO> youtubeBenchmarking(String keyword) throws JsonProcessingException;

}
