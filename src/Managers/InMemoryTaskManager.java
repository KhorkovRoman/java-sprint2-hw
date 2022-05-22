package Managers;

import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.util.TreeSet;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, Epic> epicMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();

    private final TreeSet<Task> taskTreeSet = new TreeSet<Task>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));

    private final InMemoryHistoryManager historyManager = Managers.getDefaultHistory();

    private Integer id = 0;

    public Integer getId() {
        return id;
    }

    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public HashMap<Integer, Epic> getEpicMap() {
        return epicMap;
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return taskTreeSet;
    }

    @Override
    public List<Task> getHistoryList() {
        return historyManager.getHistory();
    }

    @Override
    public Integer generateId() {
        return id = id + 1;
    }


    public void addTaskToTreeSet(Task task) {
        Task lowerTask = taskTreeSet.lower(task);

        if (lowerTask != null) {
            if (lowerTask.getEndTime().isAfter(task.getStartTime())) {
                throw new RuntimeException("Задача " + task +
                        " пересекается c задачей " + lowerTask);
            } else {
                taskTreeSet.add(task);
            }
        } else {
            taskTreeSet.add(task);
        }
    }

    public void addSubTaskToTreeSet(SubTask subTask) {
        Task lowerTask = taskTreeSet.lower(subTask);

        if (lowerTask != null) {
            if (lowerTask.getEndTime().isAfter(subTask.getStartTime())) {
                throw new RuntimeException("Подзадача " + subTask +
                        " пересекается c задачей " + lowerTask);
            } else {
                taskTreeSet.add(subTask);
            }
        } else {
            taskTreeSet.add(subTask);
        }
    }

    @Override
    public void addTask(Task task) {
        if (!taskMap.containsKey(task.getId())) {
            taskMap.put(task.getId(), task);
            System.out.println("Создали: " + task);
        }

        addTaskToTreeSet(task);
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
            if (epicMap.containsKey(subTask.getEpicId())) {
                subTaskMap.put(subTask.getId(), subTask);
                System.out.println("Создали: " + subTask);

                Epic epic = epicMap.get(subTask.getEpicId());
                if (epic.getSubTaskList() == null) {
                    epic.setSubTaskList(new HashMap<Integer, SubTask>());
                }

                HashMap<Integer, SubTask> subTasksEpic = epic.getSubTaskList();
                subTasksEpic.put(subTask.getId(), subTask);

                addSubTaskToTreeSet(subTask);

                setStatusEpic(epic);
                setEpicStartTime(epic);
                setEpicDuration(epic);
            } else {
                System.out.println("В базе нет эпика с Id " + subTask.getEpicId());
            }
        }
    }

    public void getTaskById(int id) {
        if(taskMap.containsKey(id)) {
            getTask(id);
        } else if (epicMap.containsKey(id)) {
            getEpic(id);
        } else if (subTaskMap.containsKey(id)) {
            getSubTask(id);
        }
    }

    @Override
    public Task getTask(int id) {
        if(taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            historyManager.addToHistory(task);
            System.out.println("Просмотр: " + task);
            return task;
        } else {
            System.out.println("Задачи с таким номером нет в базе");
            return null;
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            historyManager.addToHistory(epic);
            System.out.println("Просмотр: " + epic);
            return epic;
        } else {
            System.out.println("Эпика с таким номером нет в базе");
            return null;
        }
    }

    @Override
    public SubTask getSubTask(int id) {
        if (subTaskMap.containsKey(id)) {
            SubTask subTask = subTaskMap.get(id);
            historyManager.addToHistory(subTask);
            System.out.println("Просмотр: " + subTask);
            return subTask;
        } else {
            System.out.println("Подзадачи с таким номером нет в базе");
            return null;
        }
    }

    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
        System.out.println("Обновили " + task);

        taskTreeSet.remove(task);
        addTaskToTreeSet(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epicMap.get(epic.getId());
        //subTaskList
        HashMap<Integer, SubTask> oldSubTaskList = oldEpic.getSubTaskList();
        if (oldSubTaskList == null) {
            epic.setSubTaskList(new HashMap<Integer, SubTask>());
            oldSubTaskList = epic.getSubTaskList();
        }
        HashMap<Integer, SubTask> newSubTaskList = epic.getSubTaskList();
        if (newSubTaskList == null) {
            epic.setSubTaskList(new HashMap<Integer, SubTask>());
            newSubTaskList = epic.getSubTaskList();
        }
        newSubTaskList.putAll(oldSubTaskList);

        //taskStatus, startTime, duration
        epic.setTaskStatus(oldEpic.getTaskStatus());
        epic.setStartTime(oldEpic.getStartTime());
        epic.setDuration(oldEpic.getDuration());


        epicMap.put(epic.getId(), epic);
        System.out.println("Обновили " + epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        System.out.println("Обновили " + subTask);

        taskTreeSet.remove(subTask);
        addSubTaskToTreeSet(subTask);

        Epic epic = epicMap.get(subTask.getEpicId());
        HashMap<Integer, SubTask> subTasksEpic = epic.getSubTaskList();
        subTasksEpic.put(subTask.getId(), subTask);

        setStatusEpic(epic);
        setEpicStartTime(epic);
        setEpicDuration(epic);
    }

    private LocalDateTime findEpicStartTime(HashMap<Integer, SubTask> subTaskList) {
        LocalDateTime epicStartTime = LocalDateTime.of(3990,10,10,10,10);
        for (Integer i : subTaskList.keySet()) {
            LocalDateTime subTaskStartTime = subTaskList.get(i).getStartTime();
            if (subTaskStartTime.isBefore(epicStartTime)) {
                epicStartTime = subTaskStartTime;
            }
        }
        return epicStartTime;
    }

    private void setEpicStartTime(Epic epic) {
        HashMap<Integer, SubTask> subTaskList = epic.getSubTaskList();
        LocalDateTime epicStartTime = findEpicStartTime(subTaskList);
        epic.setStartTime(epicStartTime);
    }

    private int findEpicDuration(HashMap<Integer, SubTask> subTaskList) {
        int epicDuration = 0;
        for (Integer i : subTaskList.keySet()) {
            epicDuration += subTaskList.get(i).getDuration();
        }
        return epicDuration;
    }

    private void setEpicDuration(Epic epic) {
        HashMap<Integer, SubTask> subTaskList = epic.getSubTaskList();
        int epicDuration = findEpicDuration(subTaskList);
        epic.setDuration(epicDuration);
    }

    private TaskStatus findStatusEpic(HashMap<Integer, SubTask> subTaskList) {
        boolean containsNew = false;
        boolean containsInProgress = false;
        boolean containsDone = false;

        TaskStatus epicStatus;

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
            epicStatus = TaskStatus.NEW;
        } else if (containsNew && !containsInProgress && !containsDone) {
            epicStatus = TaskStatus.NEW;
        } else if (!containsNew && !containsInProgress && containsDone) {
            epicStatus = TaskStatus.DONE;
        } else {
            epicStatus = TaskStatus.IN_PROGRESS;
        }
        return epicStatus;
    }

    private void setStatusEpic(Epic epic) {
        HashMap<Integer, SubTask> subTaskList = epic.getSubTaskList();
        TaskStatus statusEpic = findStatusEpic(subTaskList);
        epic.setTaskStatus(statusEpic);
    }

    @Override
    public void removeTask(Task task) {
        taskMap.remove(task.getId());
        historyManager.removeFromHistory(task.getId());
        taskTreeSet.remove(task);
        System.out.println("Удалили " + task);
    }

    @Override
    public void removeEpic(Epic epic) {
        HashMap<Integer, SubTask> subTaskList = epic.getSubTaskList();
        try {
            for (Integer i : subTaskList.keySet()) {
                subTaskMap.remove(i);
                historyManager.removeFromHistory(i);
                taskTreeSet.remove(subTaskList.get(i));
                System.out.println("Удалили " + subTaskList.get(i));
            }
            subTaskList.clear();

        } catch (NullPointerException e) {
            System.out.println("Список пуст.");
        }
        epicMap.remove(epic.getId());
        historyManager.removeFromHistory(epic.getId());
        System.out.println("Удалили " + epic);
    }

    @Override
    public void removeSubTask(SubTask subTask) {

        Epic epic = epicMap.get(subTask.getEpicId());
        HashMap<Integer, SubTask> subTaskList = epic.getSubTaskList();
        subTaskList.remove(subTask.getId());

        subTaskMap.remove(subTask.getId());
        historyManager.removeFromHistory(subTask.getId());
        taskTreeSet.remove(subTask);
        System.out.println("Удалили " + subTask);

        setStatusEpic(epic);
        setEpicStartTime(epic);
        setEpicDuration(epic);
    }

    @Override
    public void deleteTasksEpicsSubTasks() {
        taskMap.clear();
        epicMap.clear();
        subTaskMap.clear();
        historyManager.clearHistory();
        taskTreeSet.clear();
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
            if (epicMap.get(subTask.getEpicId()).equals(epic)) {
                System.out.println(subTask);
            }
        }
    }
}