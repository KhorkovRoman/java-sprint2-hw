import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private HashMap<Integer, SubTask> subTaskMap = new HashMap<>();

    private InMemoryHistoryManager historyManager = Managers.getDefaultHistory();

    private Integer id = 0;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public void setTaskMap(HashMap<Integer, Task> taskMap) {
        this.taskMap = taskMap;
    }

    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    public void setEpicMap(HashMap<Integer, Epic> epicMap) {
        this.epicMap = epicMap;
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    public void setSubTaskMap(HashMap<Integer, SubTask> subTaskMap) {
        this.subTaskMap = subTaskMap;
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
    public void addTask(Task task) {
        if (!taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
            System.out.println("Создали: " + task);
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (!epicMap.containsKey(epic.getId())) {
            epicMap.put(epic.getId(), epic);
            System.out.println("Создали: " + epic);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (!subTaskMap.containsKey(subTask.getId())) {
            subTaskMap.put(subTask.getId(), subTask);
            System.out.println("Создали: " + subTask);

            Epic epic = subTask.getEpic();
            HashMap<Integer, SubTask> subTasksEpic = epic.getSubTaskList();
            subTasksEpic.put(subTask.getId(), subTask);

            setStatusEpic(epic);
        }
    }

    public void getTaskById(int id) {
        if(taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            historyManager.addToHistory(task);
            System.out.println("Просмотр: " + task);
        }
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            historyManager.addToHistory(epic);
            System.out.println("Просмотр: " + epic);
        }
        if (subTaskMap.containsKey(id)) {
            SubTask subTask = subTaskMap.get(id);
            historyManager.addToHistory(subTask);
            System.out.println("Просмотр: " + subTask);
        }
    }

    @Override
    public void getTask(int id) {
        if(taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            historyManager.addToHistory(task);
            System.out.println("Просмотр: " + task);
        } else {
            System.out.println("Задачи с таким номером нет в базе");
        }
    }

    @Override
    public void getEpic(int id) {
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            historyManager.addToHistory(epic);
            System.out.println("Просмотр: " + epic);
        } else {
            System.out.println("Эпика с таким номером нет в базе");
        }
    }

    @Override
    public void getSubTask(int id) {
        if (subTaskMap.containsKey(id)) {
            SubTask subTask = subTaskMap.get(id);
            historyManager.addToHistory(subTask);
            System.out.println("Просмотр: " + subTask);
        } else {
            System.out.println("Подзадачи с таким номером нет в базе");
        }
    }

    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
        System.out.println("Обновили задачу " + task);
    }

    @Override
    public void updateEpic(Epic epic) {
        taskMap.put(epic.getId(), epic);
        System.out.println("Обновили эпик " + epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        taskMap.put(subTask.getId(), subTask);
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
        taskMap.remove(task.getId());
        historyManager.removeFromHistory(task.getId());
        System.out.println("Удалили задачу " + task);
    }

    @Override
    public void removeEpic(Epic epic) {
        HashMap<Integer, SubTask> subTasksToDel = epic.getSubTaskList();
        for (Integer i: subTasksToDel.keySet()) {
            System.out.println("Удалили подзадачу " + subTasksToDel.get(i));
            subTaskMap.remove(i);
            historyManager.removeFromHistory(i);
        }

        subTasksToDel.clear();
        System.out.println("Удалили эпик " + epic);
        epicMap.remove(epic.getId());
        historyManager.removeFromHistory(epic.getId());
    }

    @Override
    public void removeSubTask(SubTask subTask) {
        subTaskMap.remove(subTask.getId());
        historyManager.removeFromHistory(subTask.getId());
        System.out.println("Удалили подзадачу " + subTask);

        Epic epic = subTask.getEpic();
        setStatusEpic(epic);
    }

    @Override
    public void deleteTasksEpicsSubTasks() {
        taskMap.clear();
        epicMap.clear();
        subTaskMap.clear();
        historyManager.clearHistory();
        System.out.println("Удалили все задачи, эпики и подзадачи");
    }

    @Override
    public void printTasks() {
        System.out.println("Список задач.");
        for (Integer i : taskMap.keySet()) {
            System.out.println(taskMap.get(i));
        }
    }

    @Override
    public void printEpics() {
        System.out.println("Список эпиков.");
        for (Integer i : epicMap.keySet()) {
            System.out.println(epicMap.get(i));
        }
    }

    @Override
    public void printSubTasks(Epic epic) {
        System.out.println("Список подзадач.");
        for (Integer i : epic.getSubTaskList().keySet()) {
            SubTask subTask = subTaskMap.get(i);
            if (subTask.getEpic().equals(epic)) {
                System.out.println(subTask);
            }
        }
    }

    @Override
    public List<Task> history() {
        return historyManager.getHistory();
    }

}