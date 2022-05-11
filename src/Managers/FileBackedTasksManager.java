package Managers;

import Exeptions.*;

import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import Enums.TaskType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final String tasksFile;

    public FileBackedTasksManager(String tasksFile) {
        this.tasksFile = tasksFile;
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public void getTaskById(int id) {
        super.getTaskById(id);
        save();
    }

    @Override
    public Task getTask(int id) {
        super.getTask(id);
        save();
        return super.getTask(id);
    }

    @Override
    public Epic getEpic(int id) {
        super.getEpic(id);
        save();
        return super.getEpic(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        super.getSubTask(id);
        save();
        return super.getSubTask(id);
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    public static void main(String[] args) {

        Task task1 = new Task(1, "Task1", "Description Task1", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 30, 10, 00), 1);
        Task task2 = new Task(2, "Task2", "Description Task2", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 26, 12, 00), 1);
        Epic epic1 = new Epic(3, "Epic1", "Description Epic1", TaskStatus.NEW,
                null, 0);
        SubTask subTask1 = new SubTask(4, "SubTask1", "Description SubTask1", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 27, 12, 00), 1, epic1);
        SubTask subTask2 = new SubTask(5, "SubTask2", "Description SubTask2", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 28, 12, 00), 1, epic1);
        SubTask subTask3 = new SubTask(6, "SubTask3", "Description SubTask3", TaskStatus.DONE,
                LocalDateTime.of(2022, Month.APRIL, 29, 12, 00), 1, epic1);
        Epic epic2 = new Epic(7, "Epic2", "Description Epic2", TaskStatus.NEW,
                null, 0);

        TaskManager taskManager = loadFromFile(new File("tasksFile.csv"));

        System.out.println("Вывод менеджер:");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.addEpic(epic2);

        if (taskManager.getHistoryList().isEmpty()) {
            taskManager.getTask(1);
            taskManager.getEpic(3);
            taskManager.getSubTask(6);
        }

        for (Task task : taskManager.getHistoryList()) {
            System.out.print(task.getId() + ",");
        }

        System.out.println("\n");

        TaskManager taskManager1 = loadFromFile(new File("tasksFile.csv"));

        System.out.println("Вывод менеджер Тест:");
        for (Task task : taskManager1.getHistoryList()) {
            System.out.print(task.getId() + ",");
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager manager = new FileBackedTasksManager(file.toString());

        try (FileReader fr = new FileReader(file)) {
            BufferedReader br = new BufferedReader(fr);
            if (br.readLine() == null) {
                throw new IOException();
            }
            while (br.ready()) {
                String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }

                String[] lineArray = line.split(",");

                LocalDateTime taskDateTime;
                if (lineArray[1].equals(TaskType.TASK.toString())) {
                    if (lineArray[5].equals("null")) {
                        taskDateTime = null;
                    } else {
                        taskDateTime = LocalDateTime.parse(lineArray[5]);
                    }
                    manager.addTask(new Task(Integer.parseInt(lineArray[0]), lineArray[2], lineArray[4],
                            TaskStatus.valueOf(lineArray[3]),
                            taskDateTime, Integer.parseInt(lineArray[6])));
                }

                LocalDateTime epicDateTime;
                if (lineArray[1].equals(TaskType.EPIC.toString())) {
                    if (lineArray[5].equals("null")) {
                         epicDateTime = null;
                    } else {
                        epicDateTime = LocalDateTime.parse(lineArray[5]);
                    }
                    manager.addEpic(new Epic(Integer.parseInt(lineArray[0]), lineArray[2], lineArray[4],
                            TaskStatus.valueOf(lineArray[3]),
                            epicDateTime, Integer.parseInt(lineArray[6])));
                }

                LocalDateTime subTaskDateTime;
                if (lineArray[1].equals(TaskType.SUBTASK.toString())) {
                    if (lineArray[5].equals("null")) {
                        subTaskDateTime = null;
                    } else {
                        subTaskDateTime = LocalDateTime.parse(lineArray[5]);
                    }
                    Epic epic = manager.getEpicMap().get(Integer.parseInt(lineArray[7]));
                    manager.addSubTask(new SubTask(Integer.parseInt(lineArray[0]), lineArray[2], lineArray[4],
                            TaskStatus.valueOf(lineArray[3]),
                            subTaskDateTime, Integer.parseInt(lineArray[6]), epic));
                }
            }

            String historyLine = br.readLine();

            if (historyLine != null) {
                for (String idString : historyLine.split(",")) {
                    manager.getTaskById(Integer.parseInt(idString));
                }
            }

        } catch (IOException e) {
            System.out.println("Файл не прочитан. Будет создан пустой менеджер.");
        }

        return manager;
    }

    public void save() {

        try (FileWriter fr = new FileWriter("tasksFile.csv", StandardCharsets.UTF_8)) {

            fr.write("id,type,name,status,description,start-time,duration,epic\n");

            for (Task task : super.getTaskMap().values()) {
                fr.write(taskToString(task) + "\n");
            }
            for (Epic epic : super.getEpicMap().values()) {
                fr.write(epicToString(epic) + "\n");
            }
            for (SubTask subTask : super.getSubTaskMap().values()) {
                fr.write(subTaskToString(subTask) + "\n");
            }

            fr.write("\n");

            List<String> historyList = new ArrayList<>();
            List<Task> listHistory = getHistoryList();
            for (Task task : listHistory) {
                historyList.add(String.valueOf(task.getId()));
            }

            fr.write(String.join(",", historyList));

        } catch (IOException e) {
            throw new ManagerSaveException("Файл не записан.");
        }
    }

    public String taskToString(Task task) {
        return task.getId() + "," + TaskType.TASK + "," +
                task.getName() + "," +
                task.getTaskStatus() + "," + task.getDescription() + "," +
                task.getStartTime() + "," + task.getDuration();
    }

    public String epicToString(Epic epic) {
        return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + "," +
                epic.getTaskStatus() + "," + epic.getDescription() + "," +
                epic.getStartTime() + "," + epic.getDuration();
    }

    public String subTaskToString(SubTask subTask) {
        return subTask.getId() + "," + TaskType.SUBTASK + "," +
                subTask.getName() + "," + subTask.getTaskStatus() + "," + subTask.getDescription() + "," +
                subTask.getStartTime() + "," + subTask.getDuration() + "," +
                subTask.getEpic().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileBackedTasksManager manager = (FileBackedTasksManager) o;
        return Objects.equals(tasksFile, manager.tasksFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasksFile);
    }
}


