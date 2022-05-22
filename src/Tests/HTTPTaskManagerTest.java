package Tests;

import HTTP.HTTPTaskManager;
import Managers.Managers;
import Managers.TaskManager;
import HTTP.HttpTaskServer;
import HTTP.KVServer;
import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskManagerTest {

//    private TaskManager taskManager = Managers.getDefaultHTTPTaskManager();
//    private KVServer kvServer = new KVServer();
//    private HttpTaskServer httpTaskServer = new HttpTaskServer();

    public HTTPTaskManager httpTaskManager;
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;

    String urlServer = "http://localhost:8078";

    public HTTPTaskManagerTest() throws IOException, InterruptedException {
        this.kvServer = new KVServer();
        this.httpTaskManager = Managers.getDefaultHTTPTaskManager();
        this.httpTaskServer = new HttpTaskServer(this.httpTaskManager);
    }

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new HttpTaskServer.LocalDateTimeAdapter())
            // любые другие методы билдера
            .create();

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    HttpRequest request;
    HttpResponse<String> response;

    Task task1 = new Task(1, "Task1", "Task1 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 26, 10, 00), 1);
    Task task2 = new Task(2, "Task2", "Task2 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 26, 12, 00), 1);

    Epic epic1 = new Epic(3, "Epic1", "Epic1 description", TaskStatus.NEW,
            null, 0);

    SubTask subTask1 = new SubTask(4, "SubTask1", "SubTask1 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 27, 12, 00), 1, 3);
    SubTask subTask2 = new SubTask(5, "SubTask2", "SubTask2 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 28, 12, 00), 1, 3);
    SubTask subTask3 = new SubTask(6, "SubTask3", "SubTask3 description", TaskStatus.DONE,
            LocalDateTime.of(2022, Month.APRIL, 29, 12, 00), 1, 3);

    Epic epic2 = new Epic(7, "Epic2", "Epic2 description", TaskStatus.NEW,
            null, 0);



    @BeforeEach
    public void addAllTasksEpicsAndSubTasks() throws IOException, InterruptedException {
        //httpTaskManager = Managers.getDefaultHTTPTaskManager();
        //kvServer = new KVServer();
        kvServer.start();
        //httpTaskServer = new HttpTaskServer(httpTaskManager);
        httpTaskServer.createHTTPServer();

        httpTaskManager.addTask(task1);
        httpTaskManager.addTask(task2);
        httpTaskManager.addEpic(epic1);
        httpTaskManager.addSubTask(subTask1);
        httpTaskManager.addSubTask(subTask2);
        httpTaskManager.addSubTask(subTask3);
        httpTaskManager.addEpic(epic2);

        httpTaskManager.getTask(task1.getId());
        httpTaskManager.getEpic(epic1.getId());
        httpTaskManager.getSubTask(subTask3.getId());
    }

    @AfterEach
    public void clearTaskMapEpicMapSubTaskMap() {
//        httpTaskManager.deleteTasksEpicsSubTasks();


        kvServer.stopKVServer();
        httpTaskServer.stopHttpServer();
    }

    @Test
    public void shouldReturnTaskById() throws IOException, InterruptedException {
        request = requestBuilder
                .uri(URI.create(urlServer + "/tasks/task?id=1"))
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        response = client.send(request, handler);
        Task taskFromServer = gson.fromJson(response.body(), Task.class);
        assertEquals(task1, taskFromServer);
    }

//    @Test
//    public void shouldReturnEpicById() throws IOException, InterruptedException {
//        request = requestBuilder
//                .uri(URI.create(urlServer + "/tasks/epic?id=7"))
//                .version(HttpClient.Version.HTTP_1_1)
//                .GET()
//                .build();
//        response = client.send(request, handler);
//        Epic epicFromServer = gson.fromJson(response.body(), Epic.class);
//        assertEquals(epic2, epicFromServer);
//    }
//
//    @Test
//    public void shouldReturnSubTaskById() throws IOException, InterruptedException {
//        request = requestBuilder
//                .uri(URI.create(urlServer + "/tasks/subtask?id=4"))
//                .version(HttpClient.Version.HTTP_1_1)
//                .GET()
//                .build();
//        response = client.send(request, handler);
//        SubTask subTaskFromServer = gson.fromJson(response.body(), SubTask.class);
//        assertEquals(subTask1, subTaskFromServer);
//    }

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
