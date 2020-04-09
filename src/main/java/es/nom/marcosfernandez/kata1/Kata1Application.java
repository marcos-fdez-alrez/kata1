package es.nom.marcosfernandez.kata1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Kata1Application {

    private static final String endpoint = "https://my-json-server.typicode.com/jabrena/latency-problems";
    private static final String APPLICATION_JSON = "application/json";
    private static final String GREEK_PATH = "/greek";
    private static final String ROMAN_PATH = "/roman";
    private static final String NORDIC_PATH = "/nordic";

    private List<String> result = null;
    private IntStream intStream = null;

    public List<String> retrieveAllApisInfo(List<String> paths) {

        // TODO: review stream -> list -> stream ...

        //ExecutorService executorService = Executors.newFixedThreadPool(10);
        Stream<List<String>> listado = paths.stream()
                                        .map(path -> CompletableFuture.supplyAsync(() -> obtainApiInfo(path)))
                                        .map(CompletableFuture::join);

        /* 7 s 118ms  */
        List<String> result = listado.flatMap(List::stream)
                                        .collect(Collectors.toList());

        /* 7s - 540ms
        List<String> result = listado.collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList());*/

         /*previous solution - 8s 161ms
        result = paths.stream().flatMap(path -> obtainApiInfo(path).stream()).collect(Collectors.toList());*/

        return result;
    }

    public List<String> filterByLetter(List<String> input, String letter) {
        return input.stream()
                .filter(name -> name.startsWith(letter))
                .collect(Collectors.toList());
    }

    public List<String>  convertToDigits(List<String> input) {
        return input.stream()
                .map(name -> name.chars()
                                .mapToObj(data -> {return String.format("%03d", data);})
                                .collect(Collectors.joining()))
                .collect(Collectors.toList());
    }

    public Double sum(List<String> input) { // TODO: correctly implemented?
        return input.stream().mapToDouble(element -> Double.parseDouble(element)).sum();
    }

    private List<String> obtainApiInfo(String uri)  {
        // TODO: ugly code
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            // Call API REST
            HttpGet request = new HttpGet(uri);
            request.addHeader("accept", APPLICATION_JSON);
            HttpResponse httpResponse = httpClient.execute(request);

            // Process response
            InputStream responseStream = httpResponse.getEntity().getContent();
            Scanner scanner = new Scanner(responseStream, "UTF-8");
            scanner.useDelimiter("\\Z");
            String result = scanner.next();
            ObjectMapper mapper = new ObjectMapper();
            scanner.close();

            return (List<String>) mapper.readValue(result,java.util.List.class);
        }catch (IOException e) {
            System.out.println("Exception processing information from: "+ uri);
            return null;
        }
    }

}