import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    Integer generateId();

    void setTask(Task task);

    void setEpic(Epic epic);

    void setSubTask(SubTask subTask);

    void getTask(int id);

    void getEpic(int id);

    void getSubTask(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    TaskStatus findStatusEpic(HashMap<Integer, SubTask> SubTaskList);

    void setStatusEpic(Epic currentEpic);

    void removeTask(Task task);

    void removeEpic(Epic epic);

    void removeSubTask(SubTask subTask);

    void printTasks();

    void printEpics();

    void printSubTasks(Epic epic);

    List<Task> history();

    void deleteAllTasksAndEpics();
}
