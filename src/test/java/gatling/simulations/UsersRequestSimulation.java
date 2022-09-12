package gatling.simulations;

import gatling.Protocol;
import gatling.scenarios.Users;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static gatling.PerfTestConfig.DURATION_MIN;
import static gatling.PerfTestConfig.REQUEST_PER_SECOND;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;

public class UsersRequestSimulation extends Simulation {

    public UsersRequestSimulation() {
        HttpProtocolBuilder httpProtocol = new Protocol().httpProtocol;
        ScenarioBuilder createUsers = new Users().createUsers;
        ScenarioBuilder getUser = new Users().getUser;

        this.setUp(

                createUsers.injectOpen(rampUsers(REQUEST_PER_SECOND)
                .during(Duration.ofSeconds(DURATION_MIN))),

                getUser.injectOpen(rampUsers(REQUEST_PER_SECOND)
                .during(Duration.ofSeconds(DURATION_MIN))))

                .protocols(httpProtocol)
                .assertions(global().failedRequests().count().is(0L));
    }
}