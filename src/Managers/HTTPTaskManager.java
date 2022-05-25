package Managers;

import HTTP.KVTaskClient;
import com.google.gson.Gson;
import java.util.ArrayList;


public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HTTPTaskManager(String url) {
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public void save(String key) {

        String jsonTasks = gson.toJson(new ArrayList<>(super.getTaskMap().values()));
        kvTaskClient.put("tasks", jsonTasks);

        String jsonEpics = gson.toJson(new ArrayList<>(super.getEpicMap().values()));
        kvTaskClient.put("epics", jsonEpics);

        String jsonSubTasks = gson.toJson(new ArrayList<>(super.getSubTaskMap().values()));
        kvTaskClient.put("subtasks", jsonSubTasks);

        String jsonHistory = gson.toJson(new ArrayList<>(super.getHistoryList()));
        kvTaskClient.put("history", jsonHistory);
    }
}