package gatling.test.example.simulation;

public class PerfTestConfig {
    public static final String BASE_URL = "https://reqres.in/";
    public static final int REQUEST_PER_SECOND = 20;
    public static final long DURATION_MIN = 1;
    public static final int P95_RESPONSE_TIME_MS = 1000;
}
