import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    Integer generateId();

    void setTask(Task task) throws ManagerSaveException;

    void setEpic(Epic epic) throws ManagerSaveException;

    void setSubTask(SubTask subTask) throws ManagerSaveException;

    void getTask(int id) throws ManagerSaveException;

    void getEpic(int id) throws ManagerSaveException;

    void getSubTask(int id) throws ManagerSaveException;

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
