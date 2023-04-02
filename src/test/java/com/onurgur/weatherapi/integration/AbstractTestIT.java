package com.onurgur.weatherapi.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractTestIT {

    @Autowired
    public TestRestTemplate testRestTemplate;

    @LocalServerPort
    public Integer port;

    // @Container //-> start & stop container for your test
    //if it's a static field -> they will start the containers once per all the tests method in the class
    //if it's an instance  field -> they will start the new container for every test method in your class
    protected static final MySQLContainer mySql = new MySQLContainer<>("mysql:8.0.26")
            .withReuse(true);
            /*.withDatabaseName("weather")
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema.sql"),"/docker-entry-initdb.d/") -> initialize sql script
            .withUsername("root")
            .withPassword("12345");*/

    protected static final MockServerContainer mockServer =
            new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.13.2"));
    protected static MockServerClient mockServerClient;

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        Startables.deepStart(mySql, mockServer).join();

        registry.add("spring.datasource.url", mySql::getJdbcUrl);
        registry.add("spring.datasource.username", mySql::getUsername);
        registry.add("spring.datasource.password", mySql::getPassword);
        registry.add("weather-stack.api-url", mockServer::getEndpoint);

        mockServerClient = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    }

    @BeforeEach
    public void setUp() {
        //RestAssured.baseURI = "http://localhost:" + port;
        testRestTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList(((request, body, execution) -> execution.execute(request, body))));
    }

    protected static void mockGetWeatherForIstanbul() {
        mockServerClient.when(
                        request().withMethod("GET").withPath("?access_key=f8073fd22b5b3c24d4080708659fd5e7&query=.*"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(new Header("Content-Type", "application/json;"))
                                .withBody(json(
                                        """
                                                {
                                                     "request": {
                                                         "type": "City",
                                                         "query": "Istanbul, Turkey",
                                                         "language": "en",
                                                         "unit": "m"
                                                     },
                                                     "location": {
                                                         "name": "Istanbul",
                                                         "country": "Turkey",
                                                         "region": "Istanbul",
                                                         "lat": "41.019",
                                                         "lon": "28.965",
                                                         "timezone_id": "Europe/Istanbul",
                                                         "localtime": "2023-04-02 02:52",
                                                         "localtime_epoch": 1680403920,
                                                         "utc_offset": "3.0"
                                                     },
                                                     "current": {
                                                         "observation_time": "11:52 PM",
                                                         "temperature": 13,
                                                         "weather_code": 116,
                                                         "weather_icons": [
                                                             "https://cdn.worldweatheronline.com/images/wsymbols01_png_64/wsymbol_0004_black_low_cloud.png"
                                                         ],
                                                         "weather_descriptions": [
                                                             "Partly cloudy"
                                                         ],
                                                         "wind_speed": 17,
                                                         "wind_degree": 240,
                                                         "wind_dir": "WSW",
                                                         "pressure": 1006,
                                                         "precip": 1.3,
                                                         "humidity": 82,
                                                         "cloudcover": 75,
                                                         "feelslike": 11,
                                                         "uv_index": 1,
                                                         "visibility": 10,
                                                         "is_day": "no"
                                                     }
                                                 }
                                                """
                                ))
                );
    }

    @Test
    public void test() {
        assertThat(mySql.isRunning()).isTrue();
        assertThat(mockServer.isRunning()).isTrue();
    }
}
