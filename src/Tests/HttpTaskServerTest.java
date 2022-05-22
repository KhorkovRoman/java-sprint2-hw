package Tests;

import HTTP.HTTPTaskManager;
import HTTP.HttpTaskServer;
import HTTP.KVServer;
import Managers.FileBackedTasksManager;
import Managers.Managers;
import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerTest {

    String urlServer = "http://localhost:8078";

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateTimeAdapter())
            // любые другие методы билдера
            .create();

    Task task58 = new Task(58, "Task1", "Task1 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 26, 10, 00), 1);

    Epic epic20 = new Epic(20, "Epic20", "Epic20 description", TaskStatus.NEW,
            null, 0);

    SubTask subTask21 = new SubTask(21, "SubTask21", "SubTask21 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 27, 12, 00), 1, 20);

    KVServer kvServer = new KVServer();
    HTTPTaskManager httpTaskManager = Managers.getDefaultHTTPTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(httpTaskManager);

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    HttpRequest request;
    HttpResponse<String> response;

    public HttpTaskServerTest() throws IOException, InterruptedException {
    }

    @BeforeEach
    public void addAllTasksEpicsAndSubTasks() throws IOException, InterruptedException {
        kvServer.start();
        httpTaskServer.createHTTPServer();


    }

    @AfterEach
    public void clearTaskMapEpicMapSubTaskMap() {
        kvServer.stopKVServer();
        httpTaskServer.stopHttpServer();
    }

    @Test
    public void shouldReturnTaskById() throws IOException, InterruptedException {
        request = requestBuilder
                .uri(URI.create(urlServer + "/tasks/task?id=58"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        response = client.send(request, handler);
        Task taskFromServer = gson.fromJson(response.body(), Task.class);
        assertEquals(task58, taskFromServer);
    }

    @Test
    public void shouldReturnEpicById() throws IOException, InterruptedException {
        request = requestBuilder
                .uri(URI.create(urlServer + "/tasks/epic?id=20"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        response = client.send(request, handler);
        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);
        assertEquals(epic20, epicFromServer);
    }

    @Test
    public void shouldReturnSubTaskById() throws IOException, InterruptedException {
        request = requestBuilder
                .uri(URI.create(urlServer + "/tasks/subtask?id=21"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        response = client.send(request, handler);
        SubTask subTaskFromServer = gson.fromJson(response.body(), SubTask.class);
        assertEquals(subTask21, subTaskFromServer);
    }

    public static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

        private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy(HH:mm)");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            if (localDateTime == null) {
                jsonWriter.nullValue();
                return;
            }
            jsonWriter.value(localDateTime.format(DATE_TIME_FORMATTER));
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            return LocalDateTime.parse(jsonReader.nextString(), DATE_TIME_FORMATTER);
        }
    }
}
