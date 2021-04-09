package jdk11.httpclient;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java11HttpClientExample
 * <p>
 * https://mkyong.com/java/java-11-httpclient-examples
 *
 * @author no-today
 * @date 2020/12/05 1:48 PM
 */
public class Java11HttpClientExample {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .executor(Executors.newFixedThreadPool(5))
//            .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 1087)))
//            .authenticator(Authenticator.getDefault())
            .build();

    @Test
    void testAsync() throws Exception {
        var futures = Stream.of(get(), postForm(), postJson())
                .map(Java11HttpClientExample::async)
                .collect(Collectors.toList());

        for (CompletableFuture<String> future : futures) {
            System.out.println(future.get());
        }
    }

    @Test
    void testSync() {
        Stream.of(get(), postForm(), postJson())
                .map(Java11HttpClientExample::sync)
                .collect(Collectors.toList())
                .forEach(System.out::println);
    }

    private static CompletableFuture<String> async(HttpRequest request) {
        return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

    private static String sync(HttpRequest request) {
        try {
            return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void print(HttpResponse<String> response) {
        // print status code
        System.out.println(response.statusCode());

        // print response body
        System.out.println(response.body());
    }

    private static HttpRequest get() {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("https://httpbin.org/get"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")
                .build();
    }

    private static HttpRequest postForm() {
        //
        var data = Map.of("username", "notoday", "password", "changeme");

        // Sample: 'username=notoday&password=changeme'
        var formData = data.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");

        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .uri(URI.create("https://httpbin.org/post"))
                .header("User-Agent", "Java 11 HttpClient Bot")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
    }

    private static HttpRequest postJson() {
        // json formatted data
        var json = "{" +
                "\"username\":\"notoday\"," +
                "\"password\":\"changeme\"" +
                "}";

        return HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create("https://httpbin.org/post"))
                .setHeader("User-Agent", "Java 11 HttpClient Bot")  // add request header
                .header("Content-Type", "application/json")         // add json header
                .build();
    }


}
