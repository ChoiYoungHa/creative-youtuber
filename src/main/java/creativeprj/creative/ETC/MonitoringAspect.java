package creativeprj.creative.ETC;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Slf4j
@Component
public class MonitoringAspect {

    @Autowired
    private WebClient webClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MonitoringAspect(){
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

    @Pointcut("execution(* creativeprj.creative.Controller..*(..))")
    public void networkRequestMethods(){}

    @Pointcut("execution(* creativeprj.creative.Controller..*(..))")
    public void databaseTransactionMethod(){}


    // 네트워크 요청 처리 전 로그 기록
    @Before("networkRequestMethods()")
    public void beforeMethodCall(JoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("event", "request_received");
        logMap.put("method", request.getMethod());
        logMap.put("uri", request.getRequestURI());
        logMap.put("remoteAddr", request.getRemoteAddr());
        logMap.put("timestamp", Instant.now());
    //    sendLog(toJson(logMap));
    }

    // 요청 처리 후 성공적 완료 로그
    @AfterReturning(pointcut = "networkRequestMethods()", returning = "result")
    public void afterReturningMethodCall(JoinPoint joinPoint, Object result) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("event", "request_completed");
        logMap.put("signature", joinPoint.getSignature().getName());
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> response = (ResponseEntity<?>) result;
            logMap.put("status", response.getStatusCode().value());
            logMap.put("responseBody", response.getBody());
        }
        logMap.put("timestamp", Instant.now());
    //    sendLog(toJson(logMap));
    }

    // 예외 발생 시 로그 기록
    @AfterThrowing(pointcut = "networkRequestMethods()", throwing = "ex")
    public void afterThrowingMethodCall(JoinPoint joinPoint, Throwable ex) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("event", "request_error");
        logMap.put("signature", joinPoint.getSignature().getName());
        logMap.put("error", ex.getMessage());
        logMap.put("timestamp", Instant.now());
    //    sendLog(toJson(logMap));
    }

    // DB 트랜잭션 시작 전 로그 기록
    @Before("databaseTransactionMethod()")
    public void beforeDbTransaction(JoinPoint joinPoint) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("event", "transaction_start");
        logMap.put("signature", joinPoint.getSignature().toShortString());
        logMap.put("arguments", Arrays.toString(joinPoint.getArgs()));
        logMap.put("timestamp", Instant.now());

    //    sendLog(toJson(logMap));
    }

    // DB 트랜잭션 완료 후 로그 기록
    @AfterReturning(pointcut = "databaseTransactionMethod()", returning = "result")
    public void afterDbTransaction(JoinPoint joinPoint, Object result) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("event", "transaction_complete");
        logMap.put("signature", joinPoint.getSignature().toShortString());
        logMap.put("result", result);
        logMap.put("timestamp", Instant.now());
    //    sendLog(toJson(logMap));
    }

    // DB 트랜잭션 예외 시 로그 기록
    @AfterThrowing(pointcut = "databaseTransactionMethod()", throwing = "ex")
    public void errorDbTransaction(JoinPoint joinPoint, Throwable ex) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("event", "transaction_error");
        logMap.put("signature", joinPoint.getSignature().toShortString());
        logMap.put("error", ex.getMessage());
        logMap.put("timestamp", Instant.now());

    //    sendLog(toJson(logMap));
    }

//    private void sendLog(String message) {
//        webClient.post()
//                .uri("/log")
//                .body(Mono.just(message), String.class)
//                .retrieve()
//                .bodyToMono(Void.class)
//                .onErrorResume(e -> {
//                    log.error("fail to send log to server, saving locally", e);
//                    return Mono.empty();
//                })
//                .subscribe();
//    }
}
