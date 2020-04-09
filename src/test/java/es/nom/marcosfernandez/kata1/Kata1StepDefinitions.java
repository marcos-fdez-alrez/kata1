package es.nom.marcosfernandez.kata1;

/**   @happy
Scenario: Consume the APIs in a Happy path scenario
Given a list of REST API about Greek, Roman & Nordic
When call and retrieve all API info
Then filter by god starting with `n`
And  convert the names into a decimal format
And  sum

 @latency
 Scenario: Consume the APIs considering some latency in the greek service
 Given a list of REST API about Greek, Roman & Nordic
 When call and retrieve all API info from the god list
 Then filter by god starting with `n`
 And  convert the names into a decimal format
 And  sum */

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.cucumber.java.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

public class Kata1StepDefinitions {

    private static final String GREEK_PATH = "/greek";
    private static final String ROMAN_PATH = "/roman";
    private static final String NORDIC_PATH = "/nordic";
    private static final WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());
    private static final List<String> selectedNames = Arrays.asList("078101109101115105115",
            "078101112116117110",
            "078105107101",
            "078106111114100");

    private List<String> apiRest = null;
    private Kata1Application kata1Application = null;
    private List<String> result = null;

    @Before(order = 1)
    public void initialization() {
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());

        // Roman & Nordic Gods Configuration
        configureGetStubFor(ROMAN_PATH, "json/romanGods.json", null);
        configureGetStubFor(NORDIC_PATH, "json/nordicGods.json", null);

        kata1Application = new Kata1Application();
    }

    @Before(order = 2, value = "@happy")
    public void beforeHappy() {
        // Greek Gods Configuration
        configureGetStubFor(GREEK_PATH, "json/greekGods.json", null);
    }

    @Before(order = 2, value = "@latency")
    public void beforeLatency() {
        // Greek Gods Configuration With Latency
        configureGetStubFor(GREEK_PATH, "json/greekGods.json",5000);
    }

    @BeforeStep
    public void beforeStep() {
        // BeforeStep

    }

    @Given("a list of REST API about Greek, Roman & Nordic")
    public void givenAPIRestList() {
       apiRest = Stream.of(GREEK_PATH,ROMAN_PATH,NORDIC_PATH)
                .map(godPath -> "http://localhost:"+wireMockServer.port()+godPath)
                .collect(Collectors.toList());
    }

    @When("call and retrieve all API info")
    public void whenCallAndRetrieveAllInfo() {
        result = kata1Application.retrieveAllApisInfo(apiRest);
    }

    @When("call and retrieve all API info from the god list")
    public void call_and_retrieve_all_API_info_from_the_god_list() {
        result = kata1Application.retrieveAllApisInfo(apiRest);
    }

    @Then("filter by god starting with {string}")
    public void filterGodByStartingLetter(String letter) {
        result = kata1Application.filterByLetter(result, letter);
        result = result.stream().sorted().collect(Collectors.toList());
        assertLinesMatch(Arrays.asList("Nemesis","Neptun","Nike","Njord"),result);
    }

    @And("convert the names into a decimal format")
    public void convertNamesIntoDecimalFormat() {
        result = kata1Application.convertToDigits(result);
        assertLinesMatch(selectedNames,result);
    }

    @And("sum")
    public void sum() {
        assertEquals(selectedNames.stream().mapToDouble(name -> Double.parseDouble(name)).sum(),
                kata1Application.sum(result));
    }

    @AfterStep
    public void afterStep() {
        // after step
    }

    @After
    public void afterScenario() {
        // after all
        wireMockServer.stop();
    }

    private StubMapping configureGetStubFor(String path, String godFile, Integer milliseconds) {
        ResponseDefinitionBuilder response = Optional.ofNullable(milliseconds)
                .map(mills -> aResponse().withFixedDelay(mills))
                .orElse(aResponse());

        StubMapping stubMapping = stubFor(get(urlEqualTo(path))
                .willReturn(response
                        .withBodyFile(godFile)
                        .withStatus(200)));

        return stubMapping;
    }


}
