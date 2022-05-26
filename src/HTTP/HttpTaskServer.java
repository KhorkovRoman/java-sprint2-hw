package HTTP;

import Managers.TaskManager;
import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
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

    private TaskManager taskManager;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    private final String TASK = "task";
    private final String EPIC = "epic";
    private final String SUBTASK = "subtask";
    private final String HISTORY = "history";

    private final int PORT = 8080;
    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private Gson gson = new GsonBuilder()
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
                handleGetPrioritizedTasks(httpExchange);
            }

            switch (methodRequest) {
                case "POST":
                    if (splitPath[2].equals(TASK)) {
                        handlePostAddUpdateTask(httpExchange);
                    } else if (splitPath[2].equals(EPIC)) {
                        handlePostAddUpdateEpic(httpExchange);
                    } else if (splitPath[2].equals(SUBTASK)) {
                        handlePostAddUpdateSubTask(httpExchange);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(gson.toJson("Запрашиваемая страница не найдена").getBytes());
                        }
                    }
                    break;
                case "GET":
                    if (splitPath[2].equals(TASK)) {
                        handleGetTaskGetTasksMap(httpExchange);
                    } else if (splitPath[2].equals(EPIC)) {
                        handleGetEpicGetEpicsMap(httpExchange);
                    } else if (splitPath[2].equals(SUBTASK)) {
                        handleGetSubTaskGetSubTasksMap(httpExchange);
                    } else if (splitPath[2].equals(HISTORY)) {
                        handleGetHistory(httpExchange);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(gson.toJson("Запрашиваемая страница не найдена").getBytes());
                        }
                    }
                    break;
                case "DELETE":
                    if (splitPath[2].equals(TASK)) {
                        handleDeleteTask(httpExchange);
                    } else if (splitPath[2].equals(EPIC)) {
                        handleDeleteEpic(httpExchange);
                    } else if (splitPath[2].equals(SUBTASK)) {
                        handleDeleteSubTask(httpExchange);
                    } else {
                        httpExchange.sendResponseHeaders(404, 0);
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(gson.toJson("Запрашиваемая страница не найдена").getBytes());
                        }
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(405, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson("Неизвестный HTTP запрос").getBytes());
                    }
            }
        }

        int setId(HttpExchange httpExchange) {
            int id = Integer.parseInt(httpExchange.getRequestURI().toString()
                    .split("\\?")[1].split("=")[1]);
            return id;
        }

        void successWrite(HttpExchange httpExchange, String response, int code) throws IOException {
            httpExchange.sendResponseHeaders(code, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        void failureWrite(HttpExchange httpExchange, String message, int code) throws IOException {
            httpExchange.sendResponseHeaders(code, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(gson.toJson(message).getBytes());
            }
        }

        private String readText(HttpExchange h) throws IOException {
            return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        }

        public void handleGetPrioritizedTasks(HttpExchange httpExchange) throws IOException {
            if (!taskManager.getPrioritizedTasks().isEmpty()) {
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(gson.toJson(taskManager.getPrioritizedTasks()).getBytes());
                }
            } else {
                String message = "Отсортированный список задач не найден в базе.";
                failureWrite(httpExchange, message, 404);
            }
        }

        public void handlePostAddUpdateTask(HttpExchange h) throws IOException {
            String body = readText(h);
            if (body.isEmpty()) {
                h.sendResponseHeaders(400, 0);
            }
            Task task = gson.fromJson(body, Task.class);
            if (h.getRequestURI().getQuery() == null) {
                taskManager.addTask(task);
                successWrite(h, "Создали новую задачу с Id " + task.getId(), 200);
            } else {
                int idTask = setId(h);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    taskManager.updateTask(task);
                    successWrite(h, "Обновили задачу с Id "+ idTask, 200);
                } else {
                    failureWrite(h, "Задачи с Id " + idTask + " нет в базе.", 404);
                }
            }
        }

        public void handlePostAddUpdateEpic(HttpExchange h) throws IOException {
            String body = readText(h);
            if (body.isEmpty()) {
                h.sendResponseHeaders(400, 0);
            }
            Epic epic = gson.fromJson(body, Epic.class);
            if (h.getRequestURI().getQuery() == null) {
                taskManager.addEpic(epic);
                successWrite(h, "Создали новый эпик с Id "+ epic.getId(), 200);
            } else {
                int idEpic = setId(h);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    taskManager.updateEpic(epic);
                    successWrite(h, "Обновили эпик с Id "+ idEpic, 200);
                } else {
                    failureWrite(h, "Эпика с Id " + idEpic + " нет в базе.", 404);
                }
            }
        }

        public void handlePostAddUpdateSubTask(HttpExchange h) throws IOException {
            String body = readText(h);
            if (body.isEmpty()) {
                h.sendResponseHeaders(400, 0);
            }
            SubTask subTask = gson.fromJson(body, SubTask.class);
            if (h.getRequestURI().getQuery() == null) {
                if (taskManager.getEpicMap().containsKey(subTask.getEpicId())) {
                    taskManager.addSubTask(subTask);
                    successWrite(h, "Создали новую подзадачу с Id " + subTask.getId(), 200);
                } else {
                    failureWrite(h, "Эпика с Id " + subTask.getEpicId() + " нет в базе.", 404);
                }
            } else {
                int idSubTask = setId(h);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    taskManager.updateSubTask(subTask);
                    successWrite(h, "Обновили подзадачу с Id "+ idSubTask, 200);
                } else {
                    failureWrite(h, "Подзадачи с Id " + idSubTask + " нет в базе.", 404);
                }
            }
        }

        public void handleGetTaskGetTasksMap(HttpExchange httpExchange) throws IOException {
            if (httpExchange.getRequestURI().getQuery() != null) {
                int idTask = setId(httpExchange);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    Task task = taskManager.getTask(idTask);
                    String response = gson.toJson(task);
                    successWrite(httpExchange, response, 200);
                } else {
                    String message = "Задача с Id " + idTask + " не найдена в базе.";
                    failureWrite(httpExchange, message, 404);
                }
            } else {
                if (!taskManager.getTaskMap().isEmpty()) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson(taskManager.getTaskMap()).getBytes());
                    }
                } else {
                    String message = "Список задач не найден в базе.";
                    failureWrite(httpExchange, message, 404);
                }
            }
        }

        public void handleGetEpicGetEpicsMap(HttpExchange httpExchange) throws IOException {
            if (httpExchange.getRequestURI().getQuery() != null) {
                int idEpic = setId(httpExchange);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    Epic epic = taskManager.getEpic(idEpic);
                    String response = gson.toJson(epic);
                    successWrite(httpExchange, response, 200);
                } else {
                    String message = "Эпик с Id " + idEpic + " не найден в базе.";
                    failureWrite(httpExchange, message, 404);
                }
            } else {
                if (!taskManager.getEpicMap().isEmpty()) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson(taskManager.getEpicMap()).getBytes());
                    }
                } else {
                    String message = "Список эпиков не найден в базе.";
                    failureWrite(httpExchange, message, 404);
                }
            }
        }

        public void handleGetSubTaskGetSubTasksMap(HttpExchange httpExchange) throws IOException {
            if (httpExchange.getRequestURI().getQuery() != null) {
                int idSubTask = setId(httpExchange);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    SubTask subTask = taskManager.getSubTask(idSubTask);
                    String response = gson.toJson(subTask);
                    successWrite(httpExchange, response, 200);
                } else {
                    String message = "Подзадача с Id " + idSubTask + " не найдена в базе.";
                    failureWrite(httpExchange, message, 404);
                }
            } else {
                if (!taskManager.getSubTaskMap().isEmpty()) {
                    httpExchange.sendResponseHeaders(200, 0);
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(gson.toJson(taskManager.getSubTaskMap()).getBytes());
                    }
                } else {
                    String message = "Список подзадач не найден в базе.";
                    failureWrite(httpExchange, message, 404);
                }
            }
        }

        public void handleGetHistory(HttpExchange httpExchange) throws IOException {
            if (!taskManager.getHistoryList().isEmpty()) {
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(gson.toJson(taskManager.getHistoryList()).getBytes());
                }
            } else {
                String message = "Cписок просмотра задач пуст.";
                failureWrite(httpExchange, message, 404);
            }
        }

        public void handleDeleteSubTask(HttpExchange httpExchange) throws IOException {
            if (httpExchange.getRequestURI().getQuery() != null) {
                int idSubTask = setId(httpExchange);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    SubTask subTask = taskManager.getSubTaskMap().get(idSubTask);
                    taskManager.removeSubTask(subTask);
                    String response = "Удалили " + gson.toJson(subTask);
                    successWrite(httpExchange, response, 200);
                } else {
                    String message = "Подзадача с Id " + idSubTask + " не найдена в базе.";
                    failureWrite(httpExchange, message, 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(httpExchange);
            }
        }

        public void handleDeleteEpic(HttpExchange httpExchange) throws IOException {
            if (httpExchange.getRequestURI().getQuery() != null) {
                int idEpic = setId(httpExchange);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    Epic epic = taskManager.getEpicMap().get(idEpic);
                    taskManager.removeEpic(epic);
                    String response = "Удалили " + gson.toJson(epic);
                    successWrite(httpExchange, response, 200);
                } else {
                    String message = "Эпик с Id " + idEpic + " не найден в базе.";
                    failureWrite(httpExchange, message, 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(httpExchange);
            }
        }

        public void handleDeleteTask(HttpExchange httpExchange) throws IOException {
            if (httpExchange.getRequestURI().getQuery() != null) {
                int idTask = setId(httpExchange);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    Task task = taskManager.getTaskMap().get(idTask);
                    taskManager.removeTask(task);
                    String response = "Удалили " + gson.toJson(task);
                    successWrite(httpExchange, response, 200);
                } else {
                    String message = "Задача с Id " + idTask + " не найдена в базе.";
                    failureWrite(httpExchange, message, 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(httpExchange);
            }
        }

        public void handleDeleteTasksEpicsSubTasksMap(HttpExchange httpExchange) throws IOException {
            if (!taskManager.getTaskMap().isEmpty() ||
                !taskManager.getEpicMap().isEmpty() ||
                !taskManager.getSubTaskMap().isEmpty()) {
                taskManager.deleteTasksEpicsSubTasks();
                String response = "Все задачи удалены.";
                successWrite(httpExchange, response, 200);
            } else {
                String message = "Задач для удаления нет.";
                failureWrite(httpExchange, message, 404);
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