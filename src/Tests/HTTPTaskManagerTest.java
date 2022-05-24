package Tests;

import Managers.HTTPTaskManager;
import Managers.Managers;
import HTTP.HttpTaskServer;
import HTTP.KVServer;
import TaskStructure.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskManagerTest {

    KVServer kvServer;
    HTTPTaskManager httpTaskManager;
    HttpTaskServer httpTaskServer;

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
        LocalDateTime.of(2022, Month.APRIL, 30, 12, 00), 1);

    @BeforeEach
    public void addAllTasksEpicsAndSubTasks() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = Managers.getDefaultHTTPTaskManager();
        httpTaskServer = new HttpTaskServer(httpTaskManager);
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
        httpTaskManager.deleteTasksEpicsSubTasks();

        kvServer.stopKVServer();
        httpTaskServer.stopHttpServer();
    }

    @Test
    public void shouldReturnTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromServer = httpTaskServer.getGson().fromJson(response.body(), Task.class);
        assertEquals(task1, taskFromServer);
    }

    @Test
    public void shouldReturnTasksList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<HashMap<Integer, Task>>(){}.getType();
        HashMap taskMap = httpTaskServer.getGson().fromJson(response.body(), type);
        assertEquals(httpTaskManager.getTaskMap(), taskMap);
    }

    @Test
    public void shouldReturnEpicById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/epic/?id=7"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicFromServer = httpTaskServer.getGson().fromJson(response.body(), Epic.class);
        assertEquals(epic2, epicFromServer);
    }

    @Test
    public void shouldReturnEpicsList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<HashMap<Integer, Epic>>() {}.getType();
        HashMap epicMap = httpTaskServer.getGson().fromJson(response.body(), type);
        assertEquals(httpTaskManager.getEpicMap(), epicMap);
    }

    @Test
    public void shouldReturnSubTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/subtask?id=4"))
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask subTaskFromServer = httpTaskServer.getGson().fromJson(response.body(), SubTask.class);
        assertEquals(subTask1, subTaskFromServer);
    }

    @Test
    public void shouldReturnSubTasksList() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<HashMap<Integer, SubTask>>() {}.getType();
        HashMap subtaskMap = httpTaskServer.getGson().fromJson(response.body(), type);
        assertEquals(httpTaskManager.getSubTaskMap(), subtaskMap);
    }

    @Test
    public void shouldReturnHistoryListElements() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<List<Task>>() {}.getType();
        List<Task> historyList = httpTaskServer.getGson().fromJson(response.body(), type);

        List<Task> histList = httpTaskManager.getHistoryList();

        for (int i=0; i < historyList.size(); i++) {
            assertEquals(histList.get(i).getId(), historyList.get(i).getId());
            assertEquals(histList.get(i).getName(), historyList.get(i).getName());
            assertEquals(histList.get(i).getDescription(), historyList.get(i).getDescription());
            assertEquals(histList.get(i).getTaskStatus(), historyList.get(i).getTaskStatus());
            assertEquals(histList.get(i).getStartTime(), historyList.get(i).getStartTime());
            assertEquals(histList.get(i).getDuration(), historyList.get(i).getDuration());
        }
    }

    @Test
    public void shouldReturnPrioritizedListElements() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<List<Task>>() {}.getType();
        List<Task> priorServ = httpTaskServer.getGson().fromJson(response.body(), type);

        List<Task> priorBase = new ArrayList<Task>(httpTaskManager.getPrioritizedTasks());

        for (int i=0; i < priorBase.size(); i++) {
            assertEquals(priorBase.get(i).getId(), priorServ.get(i).getId());
            assertEquals(priorBase.get(i).getName(), priorServ.get(i).getName());
            assertEquals(priorBase.get(i).getDescription(), priorServ.get(i).getDescription());
            assertEquals(priorBase.get(i).getTaskStatus(), priorServ.get(i).getTaskStatus());
            assertEquals(priorBase.get(i).getStartTime(), priorServ.get(i).getStartTime());
            assertEquals(priorBase.get(i).getDuration(), priorServ.get(i).getDuration());
        }
    }


}











