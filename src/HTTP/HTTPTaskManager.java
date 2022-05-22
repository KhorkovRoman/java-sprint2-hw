package HTTP;

import Managers.FileBackedTasksManager;
import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;

    public HTTPTaskManager(String url) throws IOException, InterruptedException {
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public void save(String key) {

            StringBuilder builder = new StringBuilder();

            if (builder != null) {
                for (Task task : super.getTaskMap().values()) {
                    builder.append(taskToString(task));
                    builder.append("/");
                }
                for (Epic epic : super.getEpicMap().values()) {
                    builder.append(epicToString(epic));
                    builder.append("/");
                }
                for (SubTask subTask : super.getSubTaskMap().values()) {
                    builder.append(subTaskToString(subTask));
                    builder.append("/");
                }

                builder.append(" /");

                List<String> historyList = new ArrayList<>();
                List<Task> listHistory = getHistoryList();
                for (Task task : listHistory) {
                    historyList.add(String.valueOf(task.getId()));
                }

                builder.append(String.join(",", historyList));
            }

            System.out.println(builder);

            kvTaskClient.put("manager", builder.toString());
    }

    public String load(String key) {
        return kvTaskClient.load("manager");
    }
}