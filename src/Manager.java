import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;

import java.util.HashMap;

public class Manager {

    String statusNew = "NEW";
    String statusInProgress = "IN_PROGRESS";
    String statusDone = "DONE";

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, SubTask> subTasks = new HashMap<>();

    Integer id = 0;

    public Integer generateId() {
        return id = id + 1;
    }

    public void setTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Создали задачу.");
    }

    public void setEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        System.out.println("Создали эпик.");
    }

    public void setSubTasks(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
        System.out.println("Создали подзадачу.");

        Epic currentEpic = subTask.getEpic();
        setStatusEpic(currentEpic);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        System.out.println("Обновили задачу.");
    }

    public void updateEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
        System.out.println("Обновили эпик.");
    }

    public String findStatusEpic(HashMap<Integer, SubTask> currentSubTaskList) {
        boolean containsNewSubTask = false;
        boolean containsInProgressSubTask = false;
        boolean containsDoneSubTask = false;

        String statusEpic;

        for (Integer i : currentSubTaskList.keySet()) {
            String statusSubTask = currentSubTaskList.get(i).getStatus();
            if (statusSubTask.equals(statusNew)) {
                containsNewSubTask = true;
            } else if (statusSubTask.equals(statusInProgress)) {
                containsInProgressSubTask = true;
            } else if (statusSubTask.equals(statusDone)) {
                containsDoneSubTask = true;
            }
        }

        if (currentSubTaskList.isEmpty()) {
            statusEpic = statusNew;
        } else if (containsNewSubTask && !containsInProgressSubTask && !containsDoneSubTask) {
            statusEpic = statusNew;
        } else if (!containsNewSubTask && !containsInProgressSubTask && containsDoneSubTask) {
            statusEpic = statusDone;
        } else {
            statusEpic = statusInProgress;
        }
        return statusEpic;
    }

    public void setStatusEpic(Epic currentEpic) {
        HashMap<Integer, SubTask> currentSubTaskList = currentEpic.getSubTaskList();
        String statusEpic = findStatusEpic(currentSubTaskList);
        currentEpic.setStatus(statusEpic);
    }

    public void updateSubTask(SubTask subTask) {
        tasks.put(subTask.getId(), subTask);
        System.out.println("Обновили подзадачу.");

        Epic currentEpic = subTask.getEpic();
        setStatusEpic(currentEpic);
    }

    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            Task currentTask = tasks.remove(id);
            System.out.println("Удалили задачу" + currentTask);
        } else {
            System.out.println("Задачи с таким номером нет в базе");
        }
    }

    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            Epic currentEpic = epics.get(id);
            HashMap<Integer, SubTask> SubTaskList = currentEpic.getSubTaskList();
            for (Integer i : SubTaskList.keySet()) {
                System.out.println("Удалили подзадачу " + SubTaskList.get(i));
                SubTaskList.remove(i);
            }
            epics.remove(id);
            System.out.println("Удалили эпик " + currentEpic);
        } else {
            System.out.println("Эпика с таким номером нет в базе");
        }
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask.getId());
        System.out.println("Удалили подзадачу " + subTask);

        Epic currentEpic = subTask.getEpic();
        setStatusEpic(currentEpic);
    }

    public void printTasks() {
        System.out.println("Список задач.");
        for (Integer i : tasks.keySet()) {
            System.out.println(tasks.get(i));
        }
    }

    public void printEpics() {
        System.out.println("Список эпиков.");
        for (Integer i : epics.keySet()) {
            System.out.println(epics.get(i));
        }
    }

    public void printSubTasks(int id) {
        if (epics.containsKey(id)) {
            Epic currentEpic = epics.get(id);
            System.out.println("Список подзадач.");

            for (Integer i : subTasks.keySet()) {
                SubTask currentSubTask = subTasks.get(i);
                if (currentSubTask.getEpic().equals(currentEpic)) {
                    System.out.println(currentSubTask);
                }
            }
        } else {
            System.out.println("Эпика с таким номером нет в базе");
        }
    }

    public void deleteAllTasksAndEpics() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
        System.out.println("Удалили все задачи, эпики и подзадачи");
    }
}
