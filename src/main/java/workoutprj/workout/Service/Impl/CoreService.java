package workoutprj.workout.Service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import workoutprj.workout.DTO.YoutubeDTO;
import workoutprj.workout.Service.ICoreService;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service("CoreService")
public class CoreService implements ICoreService {

    private static final Logger logger = LoggerFactory.getLogger(CoreService.class);
    private static final String CORE_ASSISTANTS = "asst_JfxJ0itTR9W5gWTD2bFZcP9R";

    @Autowired
    private RestTemplate restTemplate;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${youtube.api.key}")
    private String youtubeApiKey;



    // 중복되는 헤더 생성
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("OpenAI-Beta", "assistants=v1");
        return headers;
    }


    // 어시스턴트 스레드 생성
    public String createThread() throws JsonProcessingException {

        // 스레드 id 생성
        HttpHeaders headers = createHeaders();

        String requestBody = "{}";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.openai.com/v1/threads",
                HttpMethod.POST,
                entity,
                String.class);

        // Parse the JSON response to extract the "id"
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        return rootNode.path("id").asText();

    }

    // 어시스턴트 스레드 공간에서 메시지를 생성함.
    public String createMessage(String threadId, String content) throws JsonProcessingException {

        HttpHeaders headers = createHeaders();

        // 어시스턴트가 요청한 것인지, 유저가 요청한 것인지 구분
        String role = "user";

        // Building the request body with ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(new MessageRequest(role, content));

        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

        // API endpoint with thread ID
        String url = "https://api.openai.com/v1/threads/" + threadId + "/messages";

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, requestEntity, String.class
        );

        // Parse the JSON response to extract the "message_id"
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        return rootNode.path("id").asText();  // Extracts and returns

    }



    // run 객체 응답상태 확인(status="completed" or "queued") gpt가 메시지 생성중
    public String getRunStatus(String threadId, String runId) throws JsonProcessingException {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        logger.info(response.getBody());
        String status = new ObjectMapper().readTree(response.getBody()).path("status").asText();
        logger.info(status);
        return status;
    }

    // 메시지 응답을 가져옴
    public String fetchMessages(String threadId) throws JsonProcessingException {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = "https://api.openai.com/v1/threads/" + threadId + "/messages";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        // 응답 본문에서 첫번째 메시지를 추출
        JsonNode firstMessage = new ObjectMapper().readTree(response.getBody()).path("data").get(0);
        // 첫 번째 메시지의 'content' 배열의 첫 번째 요소에서 'text' 객체의 'value'를 가져옵니다.
        String value = firstMessage.path("content").get(0).path("text").path("value").asText();
        return value;
    }


    // thread 실행 후 2초마다 답변완료를 확인함. 30초 제한시간. thread 안에 요청한 message가 있음.
    public String checkRunCompletion(String threadId, String assistantId) throws InterruptedException, JsonProcessingException {

        // 스레드 실행
        String runId = startThreadRun(threadId, assistantId);
        logger.info("runId : " + runId);

        // 답변이 완성 됐는지 2초마다 30초 체크
        boolean isCompleted = awaitRunCompletion(threadId, runId);
        if (isCompleted) {
            return fetchMessages(threadId);
        } else {
            return "false";
        }
    }

    // 생성한 스레드 실행 (메시지와 함께 전송)
    private String startThreadRun(String threadId, String assistantId) throws JsonProcessingException {
        HttpHeaders headers = createHeaders();
        String json = new ObjectMapper().writeValueAsString(new RunRequest(assistantId));
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        String url = "https://api.openai.com/v1/threads/" + threadId + "/runs";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.getBody());
        String runId = rootNode.path("id").asText();
        logger.info("runId : " + runId);
        return runId;
    }

    // GPT 답변이 완성될 때까지 기다림 (제한시간 30초, 2초마다 확인)
    private boolean awaitRunCompletion(String threadId, String runId) throws InterruptedException, JsonProcessingException {
        String status;
        long startTime = System.currentTimeMillis();
        long timeout = 30000; // 30 seconds timeout

        // 최소 1번 실행보장
        do {
            if (System.currentTimeMillis() - startTime > timeout) {
                logger.info("Timeout exceeded while waiting for run to complete.");
                return false; // Exit loop after timeout
            }

            Thread.sleep(2000); // 2초마다 실행
            status = getRunStatus(threadId, runId);
        } while (!"completed".equals(status));

        return "completed".equals(status);
    }


    // 유튜브 조회수 가져오기
    public String getViewCount(String searchResult) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> videoIdList = new ArrayList<>();

        mapper.readTree(searchResult).path("items").forEach(item -> {
            videoIdList.add(item.path("id").path("videoId").asText());
        });
        return null;

    }

    // 유튜브 검색결과 json -> dto로 매핑
    public List<YoutubeDTO> parseYoutubeSearchResult(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);
        JsonNode items = rootNode.path("items");

        List<YoutubeDTO> videos = new ArrayList<>();
        for (JsonNode item : items) {
            YoutubeDTO video = new YoutubeDTO();
            JsonNode idNode = item.path("id");
            JsonNode snippet = item.path("snippet");

            video.setVideoId(idNode.path("videoId").asText());
            video.setChannelId(snippet.path("channelId").asText());
            video.setTitle(snippet.path("title").asText());
            video.setDescription(snippet.path("description").asText());
            video.setThumbnailUrl(snippet.path("thumbnails").path("default").path("url").asText());
            video.setChannelTitle(snippet.path("channelTitle").asText());
            video.setUrl("https://www.youtube.com/watch?v=" + idNode.path("videoId").asText());

            videos.add(video);
        }

        return videos;
    }

    // 유튜브 api로 검색
    public String searchVideos(String query) {
        String youtubeSearchUrl = "https://www.googleapis.com/youtube/v3/search";

        URI uri = UriComponentsBuilder.fromHttpUrl(youtubeSearchUrl)
                .queryParam("part", "snippet")
                .queryParam("type", "video")
                .queryParam("q", query)
                .queryParam("order", "viewCount")
                .queryParam("key", youtubeApiKey)
                .queryParam("maxResults", 10)
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, String.class);
    }

    // 유튜브 api 결과값 50개 받아오기
    public String searchVideosBulk(String query) {
        String youtubeSearchUrl = "https://www.googleapis.com/youtube/v3/search";

        URI uri = UriComponentsBuilder.fromHttpUrl(youtubeSearchUrl)
                .queryParam("part", "snippet")
                .queryParam("type", "video")
                .queryParam("q", query)
                .queryParam("order", "viewCount")
                .queryParam("key", youtubeApiKey)
                .queryParam("maxResults", 50)
                .build()
                .encode()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(uri, String.class);
    }

    public Map<String, Integer> fetchVideoStatistics(List<String> videoIds) throws JsonProcessingException {
        String ids = String.join(",", videoIds);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/videos")
                .queryParam("part", "statistics")
                .queryParam("id", ids)
                .queryParam("key", youtubeApiKey);

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(builder.toUriString(), String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response);
        JsonNode items = rootNode.path("items");

        Map<String, Integer> statisticsMap = new HashMap<>();
        for (JsonNode item : items) {
            String videoId = item.path("id").asText();
            int viewCount = item.path("statistics").path("viewCount").asInt();
            statisticsMap.put(videoId, viewCount);
        }
        return statisticsMap;
    }

    // 채널 구독자수 조회
    public Map<String, Integer> fetchChannelSubscribers(List<String> channelIds) throws JsonProcessingException {
        String ids = String.join(",", channelIds); // 채널 ID들을 콤마로 구분하여 하나의 문자열로 결합
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/channels")
                .queryParam("part", "statistics") // 'statistics' 파트 요청
                .queryParam("id", ids) // 채널 ID들
                .queryParam("key", youtubeApiKey); // API 키

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(builder.toUriString(), String.class);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response);
        JsonNode items = rootNode.path("items");

        Map<String, Integer> statisticsMap = new HashMap<>();
        for (JsonNode item : items) {
            String channelId = item.path("id").asText(); // 채널 ID 추출
            int subscriberCount = item.path("statistics").path("subscriberCount").asInt(); // 구독자 수 추출
            statisticsMap.put(channelId, subscriberCount); // Map에 채널 ID와 구독자 수 매핑하여 저장
        }
        return statisticsMap; // 결과 Map 반환
    }


    // 구독자 대비 조회수가 많이 나온 동영상 매핑해서 DTO반환
    public List<YoutubeDTO> parseYoutubeSearchResult(String json, Map<String, Integer> highPerformingVideos) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);
        JsonNode items = rootNode.path("items");

        List<YoutubeDTO> videos = new ArrayList<>();
        for (JsonNode item : items) {
            JsonNode idNode = item.path("id");
            String videoId = idNode.path("videoId").asText();

            // Check if the videoId is in the list of high performing videos
            if (highPerformingVideos.containsKey(videoId)) {
                YoutubeDTO video = new YoutubeDTO();
                JsonNode snippet = item.path("snippet");

                video.setVideoId(videoId);
                video.setChannelId(snippet.path("channelId").asText());
                video.setTitle(snippet.path("title").asText());
                video.setDescription(snippet.path("description").asText());
                video.setThumbnailUrl(snippet.path("thumbnails").path("default").path("url").asText());
                video.setChannelTitle(snippet.path("channelTitle").asText());
                video.setUrl("https://www.youtube.com/watch?v=" + videoId);
                video.setViewCount(highPerformingVideos.get(videoId));  // Set the view count from highPerformingVideos

                videos.add(video);
            }
        }

        return videos;
    }



    // youtube 대본을 넣으면 레퍼런스 찾아주는 기능
    @Override
    public List<YoutubeDTO> youtubeReference(String ask) throws JsonProcessingException, InterruptedException {
        // openai rest 요청해서 대본 또는 요청사항을 맥락에 따라 핵심 키워드 1개 선정
        // 어시스턴트 스레드 생성
        String threadId = createThread();
        logger.info("threadId : " + threadId);
        // 메시지가 생성된 스레드에 추가됐고, 메시지id를 받아옴
        String messageId = createMessage(threadId, ask);
        logger.info("messageId : " + messageId);

        // run thread를 실행시키고, 메시지 응답을 2초마다 기다림.
        // 응답이 완료되면 답변을 반환
        String result_response = checkRunCompletion(threadId, CORE_ASSISTANTS);
        logger.info("result_response: " + result_response);

        String q = result_response.split(":")[1].trim();
        q = q.replaceAll("[^a-zA-Z0-9가-힣 ]", "");
        logger.info("q:" + q);

        //키워드로 youtube api에 대본의 핵심 키워드로 유튜브 검색
        String result = searchVideos(q);
        logger.info(result);

        // 유튜브 검색결과를 DTO로 매핑
        List<YoutubeDTO> youtubeDTOS = parseYoutubeSearchResult(result);

        // 유튜브 id 추출
        List<String> videoIds = youtubeDTOS.stream().map(YoutubeDTO::getVideoId).collect(Collectors.toList());
        // rest 재요청 후 각각의 동영상 조회수 알아내기
        Map<String, Integer> viewCounts = fetchVideoStatistics(videoIds);

        // youtubeDTOS에 rest로 알아낸 조회수 set
        for (YoutubeDTO video : youtubeDTOS) {
            if (viewCounts.containsKey(video.getVideoId())) {
                video.setViewCount(viewCounts.get(video.getVideoId()));
            }
        }

        return youtubeDTOS;
    }

    // 키워드를 넣으면 구독자대비 조회수가 높은 벤치마킹 영상 찾아줌
    @Override
    public List<YoutubeDTO> youtubeBenchmarking(String keyword) throws JsonProcessingException {
        // 키워드 검색을 통한 비디오 목록 수집

        double threshold = 10.0;
        String result = searchVideosBulk(keyword);

        ArrayList<String> videoIdList = new ArrayList<>();
        ArrayList<String> channelIdList = new ArrayList<>();
        Map<String, String> videoToChannelMap = new HashMap<>(); // 비디오 ID와 채널 ID 매핑

        ObjectMapper mapper = new ObjectMapper();
        JsonNode items = mapper.readTree(result).path("items");
        for (JsonNode item : items) {
            String videoId = item.path("id").path("videoId").asText();
            String channelId = item.path("snippet").path("channelId").asText();

            if (!videoId.isEmpty() && !channelId.isEmpty()) {
                videoIdList.add(videoId);
                channelIdList.add(channelId);
                videoToChannelMap.put(videoId, channelId); // 비디오 ID에 채널 ID 매핑
            }
        }


        // 유튜브 동영상 id와 조회수 매핑
        Map<String, Integer> videoViewsMap = fetchVideoStatistics(videoIdList);


        // 유튜브 채널 id와 구독자수 매핑
        Map<String, Integer> channelSubscribersMap = fetchChannelSubscribers(channelIdList);


        // 조회수와 구독자수 비율을 만족하는 비디오의 ID와 조회수만 매핑
        Map<String, Integer> highPerformingVideos = new HashMap<>();
        videoViewsMap.forEach((videoId, views) -> {
            String channelId = videoToChannelMap.get(videoId);
            Integer subscribers = channelSubscribersMap.get(channelId);
            if (subscribers != null && subscribers > 0) {
                double ratio = (double) views / subscribers;
                if (ratio >= threshold) {  // 임계값 비교
                    highPerformingVideos.put(videoId, views);
                }
            }
        });

        List<YoutubeDTO> benchmarking = parseYoutubeSearchResult(result, highPerformingVideos);



        return benchmarking;
    }

    // Object Mapper와 매핑하기 위해서 클래스화
    @Setter
    @Getter
    static class MessageRequest {
        private String role;
        private String content;

        public MessageRequest(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    @Setter
    @Getter
    static class RunRequest {
        private String assistant_id;

        public RunRequest(String assistantId) {
            this.assistant_id = assistantId;
        }

    }
}
