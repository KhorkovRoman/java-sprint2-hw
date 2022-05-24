package HTTP;

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

    public Gson getGson;
    private TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
    }

    private final String TASK = "task";
    private final String EPIC = "epic";
    private final String SUBTASK = "subtask";
    private final String HISTORY = "history";

    private final int PORT = 8080;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public Gson getGson() {
        return gson;
    }

    public void createHTTPServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler());

        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
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
                    if (splitPath[2].equals(TASK) & requestURI.getRawQuery() == null) {
                        new PostAddTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals(TASK) & requestURI.getRawQuery() != null) {
                        new PostUpdateTaskHandler().handle(httpExchange);
                    }

                    //epic
                    if (splitPath[2].equals(EPIC) & requestURI.getRawQuery() == null) {
                        new PostAddEpicHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals(EPIC) & requestURI.getRawQuery() != null) {
                        new PostUpdateEpicHandler().handle(httpExchange);
                    }

                    //subtask
                    if (splitPath[2].equals(SUBTASK) & requestURI.getRawQuery() == null) {
                        new PostAddSubTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals(SUBTASK) & requestURI.getRawQuery() != null) {
                        new PostUpdateSubTaskHandler().handle(httpExchange);
                    }
                    break;
                case "GET":
                    //task
                    if (splitPath[2].equals(TASK) & requestURI.getRawQuery() != null) {
                        new GetTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals(TASK) & requestURI.getRawQuery() == null) {
                        new GetTasksMapHandler().handle(httpExchange);
                    }

                    //epic
                    if (splitPath[2].equals(EPIC) & requestURI.getRawQuery() != null) {
                        new GetEpicHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals(EPIC) & requestURI.getRawQuery() == null) {
                        new GetEpicsMapHandler().handle(httpExchange);
                    }

                    //subTask
                    if (splitPath[2].equals(SUBTASK) & requestURI.getRawQuery() != null) {
                        new GetSubTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals(SUBTASK) & requestURI.getRawQuery() == null) {
                        new GetSubTasksMapHandler().handle(httpExchange);
                    }

                    //history
                    if (splitPath[2].equals(HISTORY) & requestURI.getRawQuery() == null) {
                        new GetHistoryHandler().handle(httpExchange);
                    }
                    break;
                case "DELETE":
                    //task
                    if (splitPath[2].equals(TASK) & requestURI.getRawQuery() != null) {
                        new DeleteTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals(TASK) & requestURI.getRawQuery() == null) {
                        new DeleteTasksEpicsSubTasksMapHandler().handle(httpExchange);
                    }

                    //epic
                    if (splitPath[2].equals(EPIC) & requestURI.getRawQuery() != null) {
                        new DeleteEpicHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals(EPIC) & requestURI.getRawQuery() == null) {
                        new DeleteTasksEpicsSubTasksMapHandler().handle(httpExchange);
                    }

                    //subtask
                    if (splitPath[2].equals(SUBTASK) & requestURI.getRawQuery() != null) {
                        new DeleteSubTaskHandler().handle(httpExchange);
                    }
                    if (splitPath[2].equals(SUBTASK) & requestURI.getRawQuery() == null) {
                        new DeleteTasksEpicsSubTasksMapHandler().handle(httpExchange);
                    }
                    break;
            }
        }

        void successWrite(HttpExchange httpExchange, String response) throws IOException {
            httpExchange.sendResponseHeaders(200, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        void failureWrite(HttpExchange httpExchange, String message) throws IOException {
            httpExchange.sendResponseHeaders(404, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(message).getBytes());
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
                    String message = "Отсортированный список задач не найден в базе.";
                    failureWrite(httpExchange, message);
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
                    successWrite(httpExchange, response);
                } else {
                    String message = "Задача с Id " + taskToAdd.getId() + " уже есть в базе.";
                    failureWrite(httpExchange, message);
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
                    successWrite(httpExchange, response);
                } else {
                    String message = "Эпик с Id " + epicToAdd.getId() + " уже есть в базе.";
                    failureWrite(httpExchange, message);
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
                        String response = "Создали новую подзадачу с Id " + subTaskToAdd.getId();
                        successWrite(httpExchange, response);
                    } else {
                        String message = "Подзадачи с Id " + subTaskToAdd.getEpicId() + " нет в базе.";
                        failureWrite(httpExchange, message);
                    }
                } else {
                    String message = "Подзадача с Id " + subTaskToAdd.getId() + " уже есть в базе.";
                    failureWrite(httpExchange, message);
                }

            }
        }

        int setId(HttpExchange httpExchange) {
            int id = Integer.parseInt(httpExchange.getRequestURI().toString()
                    .split("\\?")[1].split("=")[1]);
            return id;
        }

        class PostUpdateTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idTask = setId(httpExchange);
                InputStream inputStream = httpExchange.getRequestBody();
                String bodyUpdate = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Task taskToUpdate = gson.fromJson(bodyUpdate, Task.class);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    taskManager.updateTask(taskToUpdate);
                    String response = "Обновили задачу с Id "+ idTask;
                    successWrite(httpExchange, response);
                } else {
                    String message = "Задача с Id " + idTask + " нет в базе.";
                    failureWrite(httpExchange, message);
                }

            }
        }

        class PostUpdateEpicHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idEpic = setId(httpExchange);
                InputStream inputStream = httpExchange.getRequestBody();
                String bodyUpdateEpic = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                Epic epicToUpdate = gson.fromJson(bodyUpdateEpic, Epic.class);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    taskManager.updateEpic(epicToUpdate);
                    String response = "Обновили эпик с Id "+ idEpic;
                    successWrite(httpExchange, response);
                } else {
                    String message = "Эпика с Id " + idEpic + " нет в базе.";
                    failureWrite(httpExchange, message);
                }

            }
        }

        class PostUpdateSubTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idSubTask = setId(httpExchange);
                InputStream inputStream = httpExchange.getRequestBody();
                String bodyUpdateSubTask = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                SubTask subTaskToUpdate = gson.fromJson(bodyUpdateSubTask, SubTask.class);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    taskManager.updateSubTask(subTaskToUpdate);
                    String response = "Обновили подзадачу с Id "+ idSubTask;
                    successWrite(httpExchange, response);
                } else {
                    String message = "Подзадачи с Id " + idSubTask + " нет в базе.";
                    failureWrite(httpExchange, message);
                }

            }
        }

        class GetTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idTask = setId(httpExchange);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    Task task = taskManager.getTask(idTask);
                    String response = gson.toJson(task);
                    successWrite(httpExchange, response);
                } else {
                    String message = "Задача с Id " + idTask + " не найдена в базе.";
                    failureWrite(httpExchange, message);
                }
            }
        }

        class GetEpicHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idEpic = setId(httpExchange);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    Epic epic = taskManager.getEpic(idEpic);
                    String response = gson.toJson(epic);
                    successWrite(httpExchange, response);
                } else {
                    String message = "Эпик с Id " + idEpic + " не найден в базе.";
                    failureWrite(httpExchange, message);
                }
            }
        }

        class GetSubTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idSubTask = setId(httpExchange);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    SubTask subTask = taskManager.getSubTask(idSubTask);
                    String response = gson.toJson(subTask);
                    successWrite(httpExchange, response);
                } else {
                    String message = "Подзадача с Id " + idSubTask + " не найдена в базе.";
                    failureWrite(httpExchange, message);
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
                    String message = "Список задач не найден в базе.";
                    failureWrite(httpExchange, message);
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
                    String message = "Список эпиков не найден в базе.";
                    failureWrite(httpExchange, message);
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
                    String message = "Список подзадач не найден в базе.";
                    failureWrite(httpExchange, message);
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
                    String message = "Cписок просмотра задач пуст.";
                    failureWrite(httpExchange, message);
                }
            }
        }

        class DeleteSubTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idSubTask = setId(httpExchange);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    SubTask subTask = taskManager.getSubTaskMap().get(idSubTask);
                    taskManager.removeSubTask(subTask);
                    String response = "Удалили " + gson.toJson(subTask);
                    successWrite(httpExchange, response);
                } else {
                    String message = "Подзадача с Id " + idSubTask + " не найдена в базе.";
                    failureWrite(httpExchange, message);
                }
            }
        }

        class DeleteEpicHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idEpic = setId(httpExchange);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    Epic epic = taskManager.getEpicMap().get(idEpic);
                    taskManager.removeEpic(epic);
                    String response = "Удалили " + gson.toJson(epic);
                    successWrite(httpExchange, response);
                } else {
                    String message = "Эпик с Id " + idEpic + " не найден в базе.";
                    failureWrite(httpExchange, message);
                }
            }
        }

        class DeleteTaskHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                int idTask = setId(httpExchange);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    Task task = taskManager.getTaskMap().get(idTask);
                    taskManager.removeTask(task);
                    String response = "Удалили " + gson.toJson(task);
                    successWrite(httpExchange, response);
                } else {
                    String message = "Задача с Id " + idTask + " не найдена в базе.";
                    failureWrite(httpExchange, message);
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
                    String response = "Все задачи удалены.";
                    successWrite(httpExchange, response);
                } else {
                    String message = "Задач для удаления нет.";
                    failureWrite(httpExchange, message);
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