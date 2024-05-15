package workoutprj.workout.ETC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class MonitoringInterceptor implements HandlerInterceptor {

    @Autowired
    private WebClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MonitoringInterceptor(){
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.info(e.getMessage());
            return "{}";
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        // 정적 리소스 URI인지 확인
        if (uri.matches(".*(css|js|png|jpg|jpeg|gif|woff|woff2|ttf|svg)$") || uri.equals("/error")) {
            return true; // 정적 리소스에 대해서는 처리를 스킵합니다
        }

        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Object startTimeObj = request.getAttribute("startTime");
        if (startTimeObj == null) {
            return;
        }

        long startTime = (Long) startTimeObj;
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;

        Map<String, Object> logMap = new HashMap<>();
        logMap.put("@timestamp", Instant.now().toString());
        logMap.put("method", request.getMethod());
        logMap.put("uri", request.getRequestURI());
        logMap.put("status_code", response.getStatus());
        logMap.put("response_time_ms", executeTime);
        logMap.put("remote_address", request.getRemoteAddr());
        logMap.put("user_agent", request.getHeader("User-Agent"));
        logMap.put("error_message", ex != null ? ex.getMessage() : null);

        // 로그 포맷 통일
        String logMessage = String.format("[%s] %s executed in %dms", request.getMethod(), request.getRequestURI(), executeTime);
        sendLog(toJson(logMap));
        log.info(logMessage);

        // 예외 발생 시 로깅
        if (ex != null) {
            log.error("An exception occurred after handling the request.", ex);
        }
    }


    private void sendLog(String message) {
        webClient.post()
                .uri("/log")
                .body(Mono.just(message), String.class)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    log.error("Failed to send log to server, error: {}", e.getMessage());
                    log.info("error");
                    return Mono.empty();
                })
                .subscribe();
    }
}
