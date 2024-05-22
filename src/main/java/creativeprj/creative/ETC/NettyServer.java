package creativeprj.creative.ETC;

import io.netty.handler.codec.http.HttpResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.netty.http.server.HttpServer;


@Component
public class NettyServer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void startNettyServer() {
        HttpServer server = HttpServer.create().port(8081);
        server.route(routes ->
                routes.post("/log", (request, response) ->
                        request.receive().asString().flatMap(log -> {
                            kafkaTemplate.send("logTopic", log);
                            return response.status(HttpResponseStatus.ACCEPTED).send();
                        })
                )
        ).bindNow().onDispose().block();
    }
}
