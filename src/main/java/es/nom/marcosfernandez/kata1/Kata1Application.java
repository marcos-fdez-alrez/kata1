package es.nom.marcosfernandez.kata1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Kata1Application {

    //private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final String endpoint = "https://my-json-server.typicode.com/jabrena/latency-problems";
    private static final String APPLICATION_JSON = "application/json";
    private static final String GREEK_PATH = "/greek";
    private static final String ROMAN_PATH = "/roman";
    private static final String NORDIC_PATH = "/nordic";

    private List<String> result = null;
    private IntStream intStream = null;

    public List<String> retrieveAllApisInfo(List<String> paths) {

        /* 7s 586ms*/
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // TODO: review stream -> list -> stream ...
        Stream<List<String>> listado = paths.stream().map(path -> CompletableFuture.supplyAsync(() -> obtainApiInfo(path))).map(CompletableFuture::join);
        List<String> result = listado.collect(Collectors.toList()).stream().flatMap(List::stream)
                .collect(Collectors.toList());

         /*previous solution - 8s 161ms
        result = paths.stream().flatMap(path -> obtainApiInfo(path).stream()).collect(Collectors.toList());*/

        return result;
    }

    private List<String> obtainApiInfo(String uri)  {
        // TODO: ugly code
        try {
            HttpGet request = new HttpGet(uri);
            request.addHeader("accept", APPLICATION_JSON);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpResponse httpResponse = httpClient.execute(request);
            InputStream responseStream = httpResponse.getEntity().getContent();
            Scanner scanner = new Scanner(responseStream, "UTF-8");
            scanner.useDelimiter("\\Z");
            String result = scanner.next();
            ObjectMapper mapper = new ObjectMapper();
            scanner.close();
            return (List<String>) mapper.readValue(result,java.util.List.class);
        }catch (IOException e) {
            return new ArrayList<String>();
        }
    }

    public List<String> filterByLetter(List<String> input, String letter) {
        //Function<String,Predicate<String>> predicate = lett -> name -> name.startsWith(lett);
        return input.stream().filter(name -> name.startsWith(letter)).collect(Collectors.toList());
    }

    public List<String>  convertToDigits(List<String> input) {
        return input.stream().map(name -> name.chars().mapToObj(data -> {return String.format("%03d", data);}).collect(Collectors.joining())).collect(Collectors.toList());
    }

    public Double sum(List<String> input) { // TODO: correctly implemented?
        return input.stream().mapToDouble(element -> Double.parseDouble(element)).sum();
    }

}