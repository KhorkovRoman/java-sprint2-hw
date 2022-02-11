import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.util.HashMap;

public interface TaskManager {

    Integer generateId();

    void setTask(Task task);

    void setEpic(Epic epic);

    void setSubTasks(SubTask subTask);

    void getTask(int id, InMemoryHistoryManager historyManager);

    void getEpic(int id, InMemoryHistoryManager historyManager);

    void getSubTask(int id, InMemoryHistoryManager historyManager);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    TaskStatus findStatusEpic(HashMap<Integer, SubTask> SubTaskList);

    void setStatusEpic(Epic currentEpic);

    void removeTask(Task task);

    void removeEpic(Epic epic, HashMap<Integer, SubTask> SubTaskList);

    void removeSubTask(SubTask subTask);

    void printTasks();

    void printEpics();

    void printSubTasks(Epic epic);

    void deleteAllTasksAndEpics();
}
