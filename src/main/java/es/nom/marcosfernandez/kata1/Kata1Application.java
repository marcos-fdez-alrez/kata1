package es.nom.marcosfernandez.kata1;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Kata1Application {

    private static final String endpoint = "https://my-json-server.typicode.com/jabrena/latency-problems";
    private static final String APPLICATION_JSON = "application/json";
    private static final String GREEK_PATH = "/greek";
    private static final String ROMAN_PATH = "/roman";
    private static final String NORDIC_PATH = "/nordic";

    public Stream<String> retrieveAllApisInfo(String endpoint, String... paths) {
        // TODO: pending
        return null;
    }

    public Stream<String> filterByLetter(Stream<String> input, Predicate predicate) {
        // TODO: pending
        return null;
    }

    public IntStream convertToDigits(Stream<String> input) {
        // TODO: pending
        return null;
    }

    public Long sum(IntStream input) {
        // TODO: pending
        return null;
    }



}
