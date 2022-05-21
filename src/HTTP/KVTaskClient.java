package HTTP;

import java.io.IOException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private String url;
    private String apiKey;

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    HttpRequest request;
    HttpResponse<String> response;

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.url = url;
        request = requestBuilder
                .uri(URI.create(url + "/register"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .GET()
                .build();
        response = client.send(request, handler);
        this.apiKey = response.body();
    }

    public String getApiKey() {
        return apiKey;
    }

    void put(String key, String json) {

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);

        request = requestBuilder
                .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + apiKey))
                .POST(body)
                .build();
        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    String load(String key) {

        request = requestBuilder
                .uri(URI.create(url + "/load/" + key + "?API_TOKEN=" + apiKey))
                .GET()
                .build();
        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return response.body();
    }
}