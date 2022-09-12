package gatling;

import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static gatling.PerfTestConfig.JSON;
import static gatling.PerfTestConfig.BASE_URL;
import static gatling.PerfTestConfig.USER_AGENT;

public class Protocol {
    public HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl(BASE_URL)
            .acceptHeader(JSON)
            .userAgentHeader(USER_AGENT);
}
