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
        if (!tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            System.out.println("Создали: " + task);
        }
    }

    @Override
    public void setEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            System.out.println("Создали: " + epic);
        }
    }

    @Override
    public void setSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            System.out.println("Создали: " + subTask);

            Epic epic = subTask.getEpic();
            setStatusEpic(epic);

            HashMap<Integer, SubTask> subTasksEpic = epic.getSubTaskList();
            subTasksEpic.put(subTask.getId(), subTask);
        }
    }

    public void getTaskById(int id) {
        if(tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.addHistory(task);
            System.out.println("Просмотр: " + task);
        }
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.addHistory(epic);
            System.out.println("Просмотр: " + epic);
        }
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            historyManager.addHistory(subTask);
            System.out.println("Просмотр: " + subTask);
        }
    }

    @Override
    public void getTask(int id) {
        if(tasks.containsKey(id)) {
            Task task = tasks.get(id);
            historyManager.addHistory(task);
            System.out.println("Просмотр: " + task);
        } else {
            System.out.println("Задачи с таким номером нет в базе");
        }
    }

    @Override
    public void getEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            historyManager.addHistory(epic);
            System.out.println("Просмотр: " + epic);
        } else {
            System.out.println("Эпика с таким номером нет в базе");
        }
    }

    @Override
    public void getSubTask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            historyManager.addHistory(subTask);
            System.out.println("Просмотр: " + subTask);
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
        historyManager.removeHistory(task.getId());
        System.out.println("Удалили задачу " + task);
    }

    @Override
    public void removeEpic(Epic epic) {
        HashMap<Integer, SubTask> subTasksToDel = epic.getSubTaskList();
        for (Integer i: subTasksToDel.keySet()) {
            System.out.println("Удалили подзадачу " + subTasksToDel.get(i));
            subTasks.remove(i);
            historyManager.removeHistory(i);
        }

        subTasksToDel.clear();
        System.out.println("Удалили эпик " + epic);
        epics.remove(epic.getId());
        historyManager.removeHistory(epic.getId());
    }

    @Override
    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask.getId());
        historyManager.removeHistory(subTask.getId());
        System.out.println("Удалили подзадачу " + subTask);

        Epic currentEpic = subTask.getEpic();
        setStatusEpic(currentEpic);
    }

    @Override
    public void deleteAllTasksAndEpics() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
        historyManager.clearHistory();
        System.out.println("Удалили все задачи, эпики и подзадачи");
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

}