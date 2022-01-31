import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Manager {

    Scanner scanner = new Scanner(System.in);

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
    }

    public void updateTask(Task task) {
        if (task.getStatus().equals("NEW")){
            task.setStatus("DONE");
        }
        tasks.put(task.getId(), task);
        System.out.println("Обновили задачу.");
    }

    public void updateSubTask(SubTask subTask) {
        if (subTask.getStatus().equals("NEW")){
            subTask.setStatus("DONE");
        }
        tasks.put(subTask.getId(), subTask);
        System.out.println("Обновили подзадачу.");

        Epic currentEpic = subTask.getEpic();
        for (Integer i: subTasks.keySet()) {
            SubTask currentSubTask = subTasks.get(i);
            if (currentSubTask.getEpic().equals(currentEpic)){
                String currentStatus = currentSubTask.getStatus();
                if (currentStatus.equals("DONE")){
                    currentEpic.setStatus("DONE");
                } else if (currentStatus.equals("NEW")) {
                    currentEpic.setStatus("IN_PROGRESS");
                    break;
                }
            }
        }
    }

    public void removeTask(UserIn userIn) {
        int id = userIn.taskId();
        if(tasks.containsKey(id)) {
            Task currentTask = tasks.remove(id);
            System.out.println("Удалили задачу" + currentTask);
        } else {
            System.out.println("Задачи с таким номером нет в базе");
        }
    }

    public void removeEpic(UserIn userIn) {
        int id = userIn.epicId();
        if(epics.containsKey(id)) {
            Epic currentEpic = epics.get(id);
            for (Integer i: subTasks.keySet()) {
                SubTask currentSubTask = subTasks.get(i);
                if (currentSubTask.getEpic().equals(currentEpic)){
                    int idSubTask = currentSubTask.getId();
                    subTasks.remove(idSubTask);
                    System.out.println("Удалили подзадачу " + currentSubTask);
                }
            }
            epics.remove(id);
            System.out.println("Удалили эпик " + currentEpic);
        } else {
            System.out.println("Эпика с таким номером нет в базе");
        }
    }

    public void removeSubTask(UserIn userIn) {
        int id = userIn.subTaskId();
        if(subTasks.containsKey(id)) {
            SubTask currentSubTask = subTasks.get(id);
            subTasks.remove(id);
            System.out.println("Удалили подзадачу" + currentSubTask);
        } else {
            System.out.println("Подзадачи с таким номером нет в базе");
        }
    }

    public void printTasks() {
        System.out.println("Список задач.");
        for(Integer i: tasks.keySet()) {
            System.out.println(tasks.get(i));
        }
    }

    public void printEpics() {
        System.out.println("Список эпиков.");
        for(Integer i: epics.keySet()) {
            System.out.println(epics.get(i));
        }
    }

    public void printSubTasks(UserIn userIn) {
        int id = userIn.epicId();
        if (epics.containsKey(id)) {
            Epic currentEpic = epics.get(id);
            System.out.println("Список подзадач.");
            for(Integer i: subTasks.keySet()) {
                SubTask currentSubTask = subTasks.get(i);
                if (currentSubTask.getEpic().equals(currentEpic)){
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
