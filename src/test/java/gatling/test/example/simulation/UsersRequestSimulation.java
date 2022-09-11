package gatling.test.example.simulation;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static gatling.test.example.simulation.PerfTestConfig.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class UsersRequestSimulation extends Simulation {

    private String pathUsers = "api/users";

    private HttpProtocolBuilder httpProtocol = HttpDsl.http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .userAgentHeader("Gatling/Performance Test");

    private Iterator<Map<String, Object>> feeder =
            Stream.generate((Supplier<Map<String, Object>>) ()
                    -> Collections.singletonMap("name", UUID.randomUUID().toString())
            ).iterator();

    private ScenarioBuilder scn = CoreDsl.scenario("Load Test Creating users")
            .feed(feeder)
            .exec(http("create-user-request")
                    .post(pathUsers)
                    .header("Content-Type", "application/json")
                    .body(StringBody("{ \"name\": \"${name}\" , \"job\": \"test\"}"))
                    .check(status().is(201))
                    .check(jsonPath("$.name").ofString().exists())
                    .check(jsonPath("$.job").ofString().is("test"))
                    .check(jsonPath("$.id").ofString().exists())
                    .check(jsonPath("$.createdAt").ofString().exists())
            );

    public UsersRequestSimulation() {
        this.setUp(scn.injectOpen(constantUsersPerSec(REQUEST_PER_SECOND).during(Duration.ofSeconds(DURATION_MIN))))
                .protocols(httpProtocol);
    }
}