package HTTP;

import Managers.Managers;
import Managers.TaskManager;
import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonReader;

public class HttpTaskServer {

    HTTPTaskManager taskManager;
    HttpServer httpServer;

    public HttpTaskServer(HTTPTaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
    }

    private final int PORT = 8080;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            // любые другие методы билдера
            .create();



    public void createHTTPServer() throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler());

        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        //httpServer.stop(1); //остановили HTTP-сервер (программу)
    }

    public void stopHttpServer() {
        httpServer.stop(1);
    }

    class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String methodRequest = httpExchange.getRequestMethod();
            URI requestURI = httpExchange.getRequestURI();
            String path = requestURI.getPath();
            String[] splitPath = path.split("/");

            if (splitPath.length == 2) {
                new GetPrioritizedTasksHandler().handle(httpExchange);
            }

            switch (methodRequest) {
                case "POST":
                    //task
                    if (splitPath[2].equals("task") & requestURI.getRawQuery() == null) {
                        new PostAddTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals("task") & requestURI.getRawQuery() != null) {
                        new PostUpdateTaskHandler().handle(httpExchange);
                    }

                    //epic
                    if (splitPath[2].equals("epic") & requestURI.getRawQuery() == null) {
                        new PostAddEpicHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals("epic") & requestURI.getRawQuery() != null) {
                        new PostUpdateEpicHandler().handle(httpExchange);
                    }

                    //subtask
                    if (splitPath[2].equals("subtask") & requestURI.getRawQuery() == null) {
                        new PostAddSubTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals("subtask") & requestURI.getRawQuery() != null) {
                        new PostUpdateSubTaskHandler().handle(httpExchange);
                    }
                    break;
                case "GET":
                    //task
                    if (splitPath[2].equals("task") & requestURI.getRawQuery() != null & splitPath.length >= 3) {
                        new GetTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals("task") & requestURI.getRawQuery() == null & splitPath.length >= 3) {
                        new GetTasksMapHandler().handle(httpExchange);
                    }

                    //epic
                    if (splitPath[2].equals("epic") & requestURI.getRawQuery() != null & splitPath.length >= 3) {
                        new GetEpicHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals("epic") & requestURI.getRawQuery() == null & splitPath.length >= 3) {
                        new GetEpicsMapHandler().handle(httpExchange);
                    }

                    //subTask
                    if (splitPath[2].equals("subtask") & requestURI.getRawQuery() != null & splitPath.length >= 3) {
                        new GetSubTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals("subtask") & requestURI.getRawQuery() == null & splitPath.length >= 3) {
                        new GetSubTasksMapHandler().handle(httpExchange);
                    }

                    //history
                    if (splitPath[2].equals("history") & requestURI.getRawQuery() == null & splitPath.length >= 3) {
                        new GetHistoryHandler().handle(httpExchange);
                    }
                    break;
                case "DELETE":
                    //task
                    if (splitPath[2].equals("task") & requestURI.getRawQuery() != null & splitPath.length >= 3) {
                        new DeleteTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals("task") & requestURI.getRawQuery() == null & splitPath.length >= 3) {
                        new DeleteTasksEpicsSubTasksMapHandler().handle(httpExchange);
                    }

                    //epic
                    if (splitPath[2].equals("epic") & requestURI.getRawQuery() != null & splitPath.length >= 3) {
                        new DeleteEpicHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals("epic") & requestURI.getRawQuery() == null & splitPath.length >= 3) {
                        new DeleteTasksEpicsSubTasksMapHandler().handle(httpExchange);
                    }

                    //subtask
                    if (splitPath[2].equals("subtask") & requestURI.getRawQuery() != null & splitPath.length >= 3) {
                        new DeleteSubTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals("subtask") & requestURI.getRawQuery() == null & splitPath.length >= 3) {
                        new DeleteTasksEpicsSubTasksMapHandler().handle(httpExchange);
                    }
                    break;
            }
        }

        class GetPrioritizedTasksHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if (!taskManager.getPrioritizedTasks().isEmpty()) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson(taskManager.getPrioritizedTasks()).getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Отсортированный список задач " +
                                "по началу не найден в базе.").getBytes());
                    }
                }
            }
        }

        class PostAddTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                InputStream inputStream = httpExchange.getRequestBody();
                String bodyAdd = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Task taskToAdd = gson.fromJson(bodyAdd, Task.class);
                if (!taskManager.getTaskMap().containsKey(taskToAdd.getId())) {
                    taskManager.addTask(taskToAdd);

                    String response = "Создали новую задачу с Id "+ taskToAdd.getId();
                    httpExchange.sendResponseHeaders(201, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Задача с Id "
                                + taskToAdd.getId() + " уже есть в базе.").getBytes());
                    }
                }

            }
        }

        class PostAddEpicHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                InputStream inputStream = httpExchange.getRequestBody();
                String bodyAddEpic = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Epic epicToAdd = gson.fromJson(bodyAddEpic, Epic.class);
                if (!taskManager.getEpicMap().containsKey(epicToAdd.getId())) {
                    taskManager.addEpic(epicToAdd);
                    String response = "Создали новый эпик с Id "+ epicToAdd.getId();
                    httpExchange.sendResponseHeaders(201, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Эпик с Id "
                                + epicToAdd.getId() + " уже есть в базе.").getBytes());
                    }
                }

            }
        }

        class PostAddSubTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                InputStream inputStream = httpExchange.getRequestBody();
                String bodyAddSubTask = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                SubTask subTaskToAdd = gson.fromJson(bodyAddSubTask, SubTask.class);
                if (!taskManager.getSubTaskMap().containsKey(subTaskToAdd.getId())) {
                    if (taskManager.getEpicMap().containsKey(subTaskToAdd.getEpicId())) {
                        taskManager.addSubTask(subTaskToAdd);

                        httpExchange.sendResponseHeaders(201, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(gson.toJson("Создали новую подзадачу с Id "
                                    + subTaskToAdd.getId()).getBytes());
                        }
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(gson.toJson("Эпика с Id "
                                    + subTaskToAdd.getEpicId() + " нет в базе.").getBytes());
                        }
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Подзадача с Id "
                                + subTaskToAdd.getId() + " уже есть в базе.").getBytes());
                    }
                }

            }
        }

        class PostUpdateTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idTask = Integer.parseInt(httpExchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);
                InputStream inputStream = httpExchange.getRequestBody();
                String bodyUpdate = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Task taskToUpdate = gson.fromJson(bodyUpdate, Task.class);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    taskManager.updateTask(taskToUpdate);
                    String response = "Обновили задачу с Id "+ idTask;
                    httpExchange.sendResponseHeaders(201, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Задача с Id "
                                + idTask + " нет в базе.").getBytes());
                    }
                }

            }
        }

        class PostUpdateEpicHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idEpic = Integer.parseInt(httpExchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);
                InputStream inputStream = httpExchange.getRequestBody();
                String bodyUpdateEpic = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Epic epicToUpdate = gson.fromJson(bodyUpdateEpic, Epic.class);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    taskManager.updateEpic(epicToUpdate);
                    String response = "Обновили эпик с Id "+ idEpic;
                    httpExchange.sendResponseHeaders(201, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Эпика с Id "
                                + idEpic + " нет в базе.").getBytes());
                    }
                }

            }
        }

        class PostUpdateSubTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idSubTask = Integer.parseInt(httpExchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);
                InputStream inputStream = httpExchange.getRequestBody();
                String bodyUpdateSubTask = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                SubTask subTaskToUpdate = gson.fromJson(bodyUpdateSubTask, SubTask.class);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    taskManager.updateSubTask(subTaskToUpdate);
                    String response = "Обновили подзадачу с Id "+ idSubTask;
                    httpExchange.sendResponseHeaders(201, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Подзадачи с Id "
                                + idSubTask + " нет в базе.").getBytes());
                    }
                }

            }
        }

        class GetTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idTask = Integer.parseInt(httpExchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);

                if (taskManager.getTaskMap().containsKey(idTask)) {
                    Task task = taskManager.getTask(idTask);
                    String response = gson.toJson(task);

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Задача с Id "
                                + idTask + " не найдена в базе.").getBytes());
                    }
                }
            }
        }

        class GetEpicHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idEpic = Integer.parseInt(httpExchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);

                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    Epic epic = taskManager.getEpic(idEpic);
                    String response = gson.toJson(epic);

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Эпик с Id "
                                + idEpic + " не найден в базе.").getBytes());
                    }
                }
            }
        }

        class GetSubTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idSubTask = Integer.parseInt(httpExchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);

                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    SubTask subTask = taskManager.getSubTask(idSubTask);
                    String response = gson.toJson(subTask);

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Подзадача с Id "
                                + idSubTask + " не найдена в базе.").getBytes());
                    }
                }
            }
        }

        class GetTasksMapHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if (!taskManager.getTaskMap().isEmpty()) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson(taskManager.getTaskMap()).getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Список задач не найден в базе.").getBytes());
                    }
                }
            }
        }

        class GetEpicsMapHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if (!taskManager.getEpicMap().isEmpty()) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson(taskManager.getEpicMap()).getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Список эпиков не найден в базе.").getBytes());
                    }
                }
            }
        }

        class GetSubTasksMapHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if (!taskManager.getSubTaskMap().isEmpty()) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson(taskManager.getSubTaskMap()).getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Список подзадач не найден в базе.").getBytes());
                    }
                }
            }
        }

        class GetHistoryHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if (!taskManager.getHistoryList().isEmpty()) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson(taskManager.getHistoryList()).getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Cписок просмотра задач пуст.").getBytes());
                    }
                }
            }
        }

        class DeleteSubTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idSubTask = Integer.parseInt(httpExchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);

                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    SubTask subTask = taskManager.getSubTaskMap().get(idSubTask);
                    taskManager.removeSubTask(subTask);
                    String response = "Удалили " + gson.toJson(subTask);

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Подзадача с Id "
                                + idSubTask + " не найдена в базе.").getBytes());
                    }
                }
            }
        }

        class DeleteEpicHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idEpic = Integer.parseInt(httpExchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);

                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    Epic epic = taskManager.getEpicMap().get(idEpic);
                    taskManager.removeEpic(epic);
                    String response = "Удалили " + gson.toJson(epic);

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Эпик с Id "
                                + idEpic + " не найден в базе.").getBytes());
                    }
                }
            }
        }

        class DeleteTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idTask = Integer.parseInt(httpExchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);

                if (taskManager.getTaskMap().containsKey(idTask)) {
                    Task task = taskManager.getTaskMap().get(idTask);
                    taskManager.removeTask(task);
                    String response = "Удалили " + gson.toJson(task);

                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Задача с Id "
                                + idTask + " не найдена в базе.").getBytes());
                    }
                }
            }
        }

        class DeleteTasksEpicsSubTasksMapHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                if (!taskManager.getTaskMap().isEmpty() ||
                    !taskManager.getEpicMap().isEmpty() ||
                    !taskManager.getSubTaskMap().isEmpty()) {
                    taskManager.deleteTasksEpicsSubTasks();
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Все задачи удалены.").getBytes());
                    }
                } else {
                    httpExchange.sendResponseHeaders(404, 0);

                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Задач для удаления нет.").getBytes());
                    }
                }
            }
        }
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