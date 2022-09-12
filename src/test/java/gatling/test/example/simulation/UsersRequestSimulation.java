package gatling.test.example.simulation;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.*;
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
                    -> Collections.singletonMap("name", UUID.randomUUID().toString())).iterator();

    private ScenarioBuilder createUsers = CoreDsl.scenario("Load Test Creating users")
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

    private ScenarioBuilder getUser = CoreDsl.scenario("Load Test get user")
            .feed(
                    Stream.generate((Supplier<Map<String, Object>>) ()
                    -> Collections.singletonMap("id", new Random().nextInt(20))).iterator()
            )
            .during(10).on(
                    exec(http("get-user-request")
                            .get(pathUsers + "/" )
                            .header("Content-Type", "application/json")
                            .check(status().is(200))
                            .check(jsonPath("$.support.url").ofString().is("https://reqres.in/#support-heading"))
                            .check(jsonPath("$.support.text").ofString().is("To keep ReqRes free, contributions towards server costs are appreciated!")))
            );

    public UsersRequestSimulation() {
        this.setUp(

                createUsers.injectOpen(rampUsers(REQUEST_PER_SECOND)
                .during(Duration.ofSeconds(DURATION_MIN))),

                getUser.injectOpen(rampUsers(REQUEST_PER_SECOND)
                .during(Duration.ofSeconds(DURATION_MIN))))

                .protocols(httpProtocol)
                .assertions(global().failedRequests().count().is(0L));
    }
}