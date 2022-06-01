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
        public void handle(HttpExchange h) throws IOException {
            String methodRequest = h.getRequestMethod();
            URI requestURI = h.getRequestURI();
            String path = requestURI.getPath();
            String[] splitPath = path.split("/");

            if (splitPath.length == 2) {
                handleGetPrioritizedTasks(h);
            }

            switch (methodRequest) {
                case "POST":
                    if (splitPath[2].equals(TASK)) {
                        handlePostAddUpdateTask(h);
                    } else if (splitPath[2].equals(EPIC)) {
                        handlePostAddUpdateEpic(h);
                    } else if (splitPath[2].equals(SUBTASK)) {
                        handlePostAddUpdateSubTask(h);
                    } else {
                        outputStreamWrite(h, "Запрашиваемая страница не найдена", 404);
                    }
                    break;
                case "GET":
                    if (splitPath[2].equals(TASK)) {
                        handleGetTaskGetTasksMap(h);
                    } else if (splitPath[2].equals(EPIC)) {
                        handleGetEpicGetEpicsMap(h);
                    } else if (splitPath[2].equals(SUBTASK)) {
                        handleGetSubTaskGetSubTasksMap(h);
                    } else if (splitPath[2].equals(HISTORY)) {
                        handleGetHistory(h);
                    } else {
                        outputStreamWrite(h, "Запрашиваемая страница не найдена", 404);
                    }
                    break;
                case "DELETE":
                    if (splitPath[2].equals(TASK)) {
                        handleDeleteTask(h);
                    } else if (splitPath[2].equals(EPIC)) {
                        handleDeleteEpic(h);
                    } else if (splitPath[2].equals(SUBTASK)) {
                        handleDeleteSubTask(h);
                    } else {
                        outputStreamWrite(h, "Запрашиваемая страница не найдена", 404);
                    }
                    break;
                default:
                    outputStreamWrite(h, "Неизвестный HTTP запрос", 404);
            }
        }

        int setId(HttpExchange httpExchange) {
            int id = Integer.parseInt(httpExchange.getRequestURI().toString()
                    .split("\\?")[1].split("=")[1]);
            return id;
        }

        void outputStreamWrite(HttpExchange h, String response, int code) throws IOException {
            h.sendResponseHeaders(code, 0);
            try (OutputStream os = h.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        private String readText(HttpExchange h) throws IOException {
            return new String(h.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        }

        public void handleGetPrioritizedTasks(HttpExchange h) throws IOException {
            if (!taskManager.getPrioritizedTasks().isEmpty()) {
                outputStreamWrite(h, gson.toJson(taskManager.getPrioritizedTasks()), 200);
            } else {
                outputStreamWrite(h, "Отсортированный список задач не найден в базе.", 404);
            }
        }

        public void handlePostAddUpdateTask(HttpExchange h) throws IOException {
            String body = readText(h);
            if (body.isEmpty()) {
                outputStreamWrite(h, "Ничего не передано.", 404);
            }
            Task task = gson.fromJson(body, Task.class);
            if (h.getRequestURI().getQuery() == null) {
                taskManager.addTask(task);
                outputStreamWrite(h, "Создали новую задачу с Id " + task.getId(), 200);
            } else {
                int idTask = setId(h);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    taskManager.updateTask(task);
                    outputStreamWrite(h, "Обновили задачу с Id "+ idTask, 200);
                } else {
                    outputStreamWrite(h, "Задачи с Id " + idTask + " нет в базе.", 404);
                }
            }
        }

        public void handlePostAddUpdateEpic(HttpExchange h) throws IOException {
            String body = readText(h);
            if (body.isEmpty()) {
                outputStreamWrite(h, "Ничего не передано.", 404);
            }
            Epic epic = gson.fromJson(body, Epic.class);
            if (h.getRequestURI().getQuery() == null) {
                taskManager.addEpic(epic);
                outputStreamWrite(h, "Создали новый эпик с Id "+ epic.getId(), 200);
            } else {
                int idEpic = setId(h);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    taskManager.updateEpic(epic);
                    outputStreamWrite(h, "Обновили эпик с Id "+ idEpic, 200);
                } else {
                    outputStreamWrite(h, "Эпика с Id " + idEpic + " нет в базе.", 404);
                }
            }
        }

        public void handlePostAddUpdateSubTask(HttpExchange h) throws IOException {
            String body = readText(h);
            if (body.isEmpty()) {
                outputStreamWrite(h, "Ничего не передано.", 404);
            }
            SubTask subTask = gson.fromJson(body, SubTask.class);
            if (h.getRequestURI().getQuery() == null) {
                if (taskManager.getEpicMap().containsKey(subTask.getEpicId())) {
                    taskManager.addSubTask(subTask);
                    outputStreamWrite(h, "Создали новую подзадачу с Id " + subTask.getId(), 200);
                } else {
                    outputStreamWrite(h, "Эпика с Id " + subTask.getEpicId() + " нет в базе.", 404);
                }
            } else {
                int idSubTask = setId(h);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    taskManager.updateSubTask(subTask);
                    outputStreamWrite(h, "Обновили подзадачу с Id "+ idSubTask, 200);
                } else {
                    outputStreamWrite(h, "Подзадачи с Id " + idSubTask + " нет в базе.", 404);
                }
            }
        }

        public void handleGetTaskGetTasksMap(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idTask = setId(h);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    Task task = taskManager.getTask(idTask);
                    outputStreamWrite(h, gson.toJson(task), 200);
                } else {
                    outputStreamWrite(h, "Задача с Id " + idTask + " не найдена в базе.", 404);
                }
            } else {
                if (!taskManager.getTaskMap().isEmpty()) {
                    outputStreamWrite(h, gson.toJson(taskManager.getTaskMap()), 200);
                } else {
                    outputStreamWrite(h, "Список задач не найден в базе.", 404);
                }
            }
        }

        public void handleGetEpicGetEpicsMap(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idEpic = setId(h);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    Epic epic = taskManager.getEpic(idEpic);
                    outputStreamWrite(h, gson.toJson(epic), 200);
                } else {
                    outputStreamWrite(h, "Эпик с Id " + idEpic + " не найден в базе.", 404);
                }
            } else {
                if (!taskManager.getEpicMap().isEmpty()) {
                    outputStreamWrite(h, gson.toJson(taskManager.getEpicMap()), 200);
                } else {
                    String message = "Список эпиков не найден в базе.";
                    outputStreamWrite(h, message, 404);
                }
            }
        }

        public void handleGetSubTaskGetSubTasksMap(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idSubTask = setId(h);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    SubTask subTask = taskManager.getSubTask(idSubTask);
                    outputStreamWrite(h, gson.toJson(subTask), 200);
                } else {
                    outputStreamWrite(h, "Подзадача с Id " + idSubTask + " не найдена в базе.", 404);
                }
            } else {
                if (!taskManager.getSubTaskMap().isEmpty()) {
                    outputStreamWrite(h, gson.toJson(taskManager.getSubTaskMap()), 200);
                } else {
                    outputStreamWrite(h, "Список подзадач не найден в базе.", 404);
                }
            }
        }

        public void handleGetHistory(HttpExchange h) throws IOException {
            if (!taskManager.getHistoryList().isEmpty()) {
                outputStreamWrite(h, gson.toJson(taskManager.getHistoryList()), 200);
            } else {
                outputStreamWrite(h, "Cписок просмотра задач пуст.", 404);
            }
        }

        public void handleDeleteSubTask(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idSubTask = setId(h);
                if (taskManager.getSubTaskMap().containsKey(idSubTask)) {
                    SubTask subTask = taskManager.getSubTaskMap().get(idSubTask);
                    taskManager.removeSubTask(subTask);
                    outputStreamWrite(h, "Удалили " + gson.toJson(subTask), 200);
                } else {
                    outputStreamWrite(h, "Подзадача с Id " + idSubTask + " не найдена в базе.", 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(h);
            }
        }

        public void handleDeleteEpic(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idEpic = setId(h);
                if (taskManager.getEpicMap().containsKey(idEpic)) {
                    Epic epic = taskManager.getEpicMap().get(idEpic);
                    taskManager.removeEpic(epic);
                    outputStreamWrite(h, "Удалили " + gson.toJson(epic), 200);
                } else {
                    outputStreamWrite(h, "Эпик с Id " + idEpic + " не найден в базе.", 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(h);
            }
        }

        public void handleDeleteTask(HttpExchange h) throws IOException {
            if (h.getRequestURI().getQuery() != null) {
                int idTask = setId(h);
                if (taskManager.getTaskMap().containsKey(idTask)) {
                    Task task = taskManager.getTaskMap().get(idTask);
                    taskManager.removeTask(task);
                    outputStreamWrite(h, "Удалили " + gson.toJson(task), 200);
                } else {
                    outputStreamWrite(h, "Задача с Id " + idTask + " не найдена в базе.", 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(h);
            }
        }

        public void handleDeleteTasksEpicsSubTasksMap(HttpExchange h) throws IOException {
            if (!taskManager.getTaskMap().isEmpty() ||
                !taskManager.getEpicMap().isEmpty() ||
                !taskManager.getSubTaskMap().isEmpty()) {
                taskManager.deleteTasksEpicsSubTasks();
                outputStreamWrite(h, "Все задачи удалены.", 200);
            } else {
                outputStreamWrite(h, "Задач для удаления нет.", 404);
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