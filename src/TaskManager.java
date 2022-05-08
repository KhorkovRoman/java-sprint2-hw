import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    Integer generateId();

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    TaskStatus findStatusEpic(HashMap<Integer, SubTask> SubTaskList);

    void setStatusEpic(Epic currentEpic);

    void removeTask(Task task);

    void removeEpic(Epic epic);

    void removeSubTask(SubTask subTask);

    void deleteTasksEpicsSubTasks();

    void printTasks();

    void printEpics();

    void printSubTasks(Epic epic);

    List<Task> getHistoryList();

}
