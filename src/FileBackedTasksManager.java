import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;

import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private String tasksFile;

    public FileBackedTasksManager(String tasksFile) {
        this.tasksFile = tasksFile;
    }

    public String getTasksFile() {
        return tasksFile;
    }

    public void setTasksFile(String tasksFile) {
        this.tasksFile = tasksFile;
    }

    @Override
    public void getTaskById(int id) {
        super.getTaskById(id);
        save();
    }

    @Override
    public void getTask(int id) {
        super.getTask(id);
        save();
    }

    @Override
    public void getEpic(int id) {
        super.getEpic(id);
        save();
    }

    @Override
    public void getSubTask(int id) {
        super.getSubTask(id);
        save();
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

        System.out.println("Вывод менеджер:");
        TaskManager manager = loadFromFile(new File("tasksFile.csv"));

        Task task1 = new Task(1, "Task1", "Description Task1", TaskStatus.NEW);
        manager.addTask(task1);
        Task task2 = new Task(2, "Task2", "Description Task2", TaskStatus.NEW);
        manager.addTask(task2);

        Epic epic1 = new Epic(3, "Epic1", "Description Epic1", TaskStatus.NEW);
        manager.addEpic(epic1);

        SubTask subTask1 = new SubTask(4, "SubTask1", "Description SubTask1", TaskStatus.NEW, epic1);
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask(5, "SubTask2", "Description SubTask2", TaskStatus.NEW, epic1);
        manager.addSubTask(subTask2);
        SubTask subTask3 = new SubTask(6, "SubTask3", "Description SubTask3", TaskStatus.DONE, epic1);
        manager.addSubTask(subTask3);

        Epic epic2 = new Epic(7, "Epic2", "Description Epic2", TaskStatus.NEW);
        manager.addEpic(epic2);

        if (manager.history().isEmpty()) {
            manager.getTask(1);
            manager.getEpic(3);
            manager.getSubTask(6);
        }

        for (Task task : manager.history()) {
            System.out.print(task.getId() + ",");
        }

        System.out.println("\n");

        System.out.println("Вывод менеджер1:");
        TaskManager manager1 = loadFromFile(new File("tasksFile.csv"));
        for (Task task : manager1.history()) {
            System.out.print(task.getId() + ",");
        }
    }

    private static FileBackedTasksManager loadFromFile(File file) {

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

                if (lineArray[1].equals(TaskType.TASK.toString())) {
                    manager.addTask(new Task(Integer.parseInt(lineArray[0]), lineArray[2], lineArray[4],
                            TaskStatus.valueOf(lineArray[3])));
                }
                if (lineArray[1].equals(TaskType.EPIC.toString())) {
                    manager.addEpic(new Epic(Integer.parseInt(lineArray[0]), lineArray[2], lineArray[4],
                            TaskStatus.valueOf(lineArray[3])));
                }
                if (lineArray[1].equals(TaskType.SUBTASK.toString())) {
                    Epic epic = manager.getEpicMap().get(Integer.parseInt(lineArray[5]));
                    manager.addSubTask(new SubTask(Integer.parseInt(lineArray[0]), lineArray[2], lineArray[4],
                            TaskStatus.valueOf(lineArray[3]), epic));
                }
            }

            String historyLine = br.readLine();

            for (String idString : historyLine.split(",")) {
                manager.getTaskById(Integer.parseInt(idString));
            }

        } catch (IOException e) {
            System.out.println("Файл не прочитан. Будет создан пустой менеджер.");
        }

        return manager;
    }

    public String taskToString(Task task) {
        return task.getId() + "," + TaskType.TASK + "," + task.getName() + "," +
                task.getTaskStatus() + "," + task.getDescription();
    }

    public String epicToString(Epic epic) {
        return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + "," +
                epic.getTaskStatus() + "," + epic.getDescription();
    }

    public String subTaskToString(SubTask subTask) {
        return subTask.getId() + "," + TaskType.SUBTASK + "," + subTask.getName() + "," +
                subTask.getTaskStatus() + "," + subTask.getDescription() +
                "," + subTask.getEpic().getId();
    }

    public void save() {

        try (FileWriter fileWriter = new FileWriter("tasksFile.csv", StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : super.getTaskMap().values()) {
                fileWriter.write(taskToString(task) + "\n");
            }
            for (Epic epic : super.getEpicMap().values()) {
                fileWriter.write(epicToString(epic) + "\n");
            }
            for (SubTask subTask : super.getSubTaskMap().values()) {
                fileWriter.write(subTaskToString(subTask) + "\n");
            }

            fileWriter.write("\n");

            List<String> stringId = new ArrayList<>();
            List<Task> listHistory = history();
            for (Task task : listHistory) {
                stringId.add(String.valueOf(task.getId()));
            }

            fileWriter.write(String.join(",", stringId));

        } catch (IOException e) {
            throw new ManagerSaveException("Файл не записан.");
        }
    }
}
