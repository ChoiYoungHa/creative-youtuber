package workoutprj.workout.ETC;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebConfig implements WebMvcConfigurer {

    private final MonitoringInterceptor monitoringInterceptor;

    public WebConfig(MonitoringInterceptor monitoringInterceptor) {
        this.monitoringInterceptor = monitoringInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(monitoringInterceptor);
    }
}
