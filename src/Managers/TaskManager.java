package Managers;

import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    Integer generateId();

    Integer getId();

    HashMap<Integer, Task> getTaskMap();

    HashMap<Integer, Epic> getEpicMap();

    HashMap<Integer, SubTask> getSubTaskMap();

    TreeSet<Task> getPrioritizedTasks();

    List<Task> getHistoryList();

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubTask(SubTask subTask);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubTask(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTask(Task task);

    void removeEpic(Epic epic);

    void removeSubTask(SubTask subTask);

    void deleteTasksEpicsSubTasks();

    void printTasks();

    void printEpics();

    void printSubTasks(Epic epic);
}
