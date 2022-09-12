package gatling.scenarios;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static gatling.PerfTestConfig.CONTENT_TYPE;
import static gatling.PerfTestConfig.JSON;
import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class Users {
    private String pathUsers = "api/users";

    private Iterator<Map<String, Object>> feeder =
            Stream.generate((Supplier<Map<String, Object>>) ()
                    -> Collections.singletonMap("name", UUID.randomUUID().toString())).iterator();

    public ScenarioBuilder createUsers = CoreDsl.scenario("Load Test Creating users")
            .feed(feeder)
            .exec(http("create-user-request")
                    .post(pathUsers)
                    .header(CONTENT_TYPE, JSON)
                    .body(StringBody("{ \"name\": \"${name}\" , \"job\": \"test\"}"))
                    .check(status().is(201))
                    .check(jsonPath("$.name").ofString().exists())
                    .check(jsonPath("$.job").ofString().is("test"))
                    .check(jsonPath("$.id").ofString().exists())
                    .check(jsonPath("$.createdAt").ofString().exists())
            );

    public ScenarioBuilder getUser = CoreDsl.scenario("Load Test get user")
            .during(10).on(
                    feed(Stream.generate((Supplier<Map<String, Object>>) ()
                            -> Collections.singletonMap("id", new Random().nextInt(20))).iterator())

                            .exec(http("get-user-request")
                                    .get(pathUsers + "/${id}")
                                    .header(CONTENT_TYPE, JSON)
                                    .check(status().is(200))
                                    .check(jsonPath("$.support.url").ofString().is("https://reqres.in/#support-heading"))
                                    .check(jsonPath("$.support.text").ofString().is("To keep ReqRes free, contributions towards server costs are appreciated!")))
            );

}
