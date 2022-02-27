import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private InMemoryHistoryManager historyManager = Managers.getDefaultHistory();

    private Integer id = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void setEpics(HashMap<Integer, Epic> epics) {
        this.epics = epics;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(HashMap<Integer, SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public InMemoryHistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setHistoryManager(InMemoryHistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Integer generateId() {
        return id = id + 1;
    }

    @Override
    public void setTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Создали задачу " + task);
    }

    @Override
    public void setEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        System.out.println("Создали эпик " + epic);
    }

    @Override
    public void setSubTasks(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        System.out.println("Создали подзадачу " + subTask);

        Epic epic = subTask.getEpic();
        setStatusEpic(epic);
    }

    @Override
    public void getTask(int id) {
        if(tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.linkLast(task);
            System.out.println(task);
        } else {
            System.out.println("Задачи с таким номером нет в базе");
        }
    }

    @Override
    public void getEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.linkLast(epic);
            System.out.println(epic);
        } else {
            System.out.println("Эпика с таким номером нет в базе");
        }
    }

    @Override
    public void getSubTask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            historyManager.linkLast(subTask);
            System.out.println(subTask);
        } else {
            System.out.println("Подзадачи с таким номером нет в базе");
        }
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Обновили задачу " + task);
    }

    @Override
    public void updateEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
        System.out.println("Обновили эпик " + epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        tasks.put(subTask.getId(), subTask);
        System.out.println("Обновили подзадачу " + subTask);

        Epic epic = subTask.getEpic();
        setStatusEpic(epic);
    }

    @Override
    public TaskStatus findStatusEpic(HashMap<Integer, SubTask> subTaskList) {
        boolean containsNew = false;
        boolean containsInProgress = false;
        boolean containsDone = false;

        TaskStatus statusEpic;

        for (Integer i : subTaskList.keySet()) {
            TaskStatus statusSubTask = subTaskList.get(i).getTaskStatus();
            if (statusSubTask.equals(TaskStatus.NEW)) {
                containsNew = true;
            } else if (statusSubTask.equals(TaskStatus.IN_PROGRESS)) {
                containsInProgress = true;
            } else if (statusSubTask.equals(TaskStatus.DONE)) {
                containsDone = true;
            }
        }

        if (subTaskList.isEmpty()) {
            statusEpic = TaskStatus.NEW;
        } else if (containsNew && !containsInProgress && !containsDone) {
            statusEpic = TaskStatus.NEW;
        } else if (!containsNew && !containsInProgress && containsDone) {
            statusEpic = TaskStatus.DONE;
        } else {
            statusEpic = TaskStatus.IN_PROGRESS;
        }
        return statusEpic;
    }

    @Override
    public void setStatusEpic(Epic epic) {
        HashMap<Integer, SubTask> subTaskList = epic.getSubTaskList();
        TaskStatus taskStatus = findStatusEpic(subTaskList);
        epic.setTaskStatus(taskStatus);
    }

    @Override
    public void removeTask(Task task) {
        tasks.remove(task.getId());
        System.out.println("Удалили задачу" + task);
    }

    @Override
    public void removeEpic(Epic epic, HashMap<Integer, SubTask> subTaskList) {
            for (Integer i : subTaskList.keySet()) {
                subTaskList.remove(i);
                System.out.println("Удалили подзадачу " + subTaskList.get(i));
            }
            epics.remove(epic.getId());
            System.out.println("Удалили эпик " + epic);
    }

    @Override
    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask.getId());
        System.out.println("Удалили подзадачу " + subTask);

        Epic currentEpic = subTask.getEpic();
        setStatusEpic(currentEpic);
    }

    @Override
    public void printTasks() {
        System.out.println("Список задач.");
        for (Integer i : tasks.keySet()) {
            System.out.println(tasks.get(i));
        }
    }

    @Override
    public void printEpics() {
        System.out.println("Список эпиков.");
        for (Integer i : epics.keySet()) {
            System.out.println(epics.get(i));
        }
    }

    @Override
    public void printSubTasks(Epic epic) {
        System.out.println("Список подзадач.");
        for (Integer i : epic.getSubTaskList().keySet()) {
            SubTask subTask = subTasks.get(i);
            if (subTask.getEpic().equals(epic)) {
                System.out.println(subTask);
            }
        }
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

    @Override
    public void deleteAllTasksAndEpics() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
        System.out.println("Удалили все задачи, эпики и подзадачи");
    }
}