import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    String tasksFile;

    public FileBackedTasksManager (String tasksFile) {
        this.tasksFile = tasksFile;
    }

    private static FileBackedTasksManager loadFromFile(File file) {
        return new FileBackedTasksManager(file.toString());
    }

    public static void main(String[] args) {

        TaskManager manager = loadFromFile(new File("tasksFile.csv"));

        Task task1 = new Task(1, "Task1", "Description Task1", TaskStatus.NEW);
        manager.setTask(task1);
        Task task2 = new Task(2, "Task2", "Description Task2", TaskStatus.NEW);
        manager.setTask(task2);

        HashMap<Integer, SubTask> mapSubTasks1 = new HashMap<>();
        Epic epic1 = new Epic(3, "Epic1", "Description Epic1", TaskStatus.NEW, mapSubTasks1);
        manager.setEpic(epic1);

        SubTask subTask1 = new SubTask(4, "SubTask1", "Description SubTask1", TaskStatus.NEW, epic1);
        manager.setSubTasks(subTask1);
        SubTask subTask2 = new SubTask(5, "SubTask2", "Description SubTask2", TaskStatus.NEW, epic1);
        manager.setSubTasks(subTask2);
        SubTask subTask3 = new SubTask(6, "SubTask3", "Description SubTask3", TaskStatus.NEW, epic1);
        manager.setSubTasks(subTask3);

        HashMap<Integer, SubTask> mapSubTasks2 = new HashMap<>();
        Epic epic2 = new Epic(7, "Epic2", "Description Epic2", TaskStatus.NEW, mapSubTasks2);
        manager.setEpic(epic2);

        manager.getTask(1);
        manager.getEpic(3);
        manager.getSubTask(6);

        for (Task task: manager.history()) {
            System.out.println(task);
        }

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
    public void setTask(Task task) {
        super.setTask(task);
        save();
    }

    @Override
    public void setEpic(Epic epic) {
        super.setEpic(epic);
        save();
    }

    @Override
    public void setSubTasks(SubTask subTask) {
        super.setSubTasks(subTask);
        save();
    }

    @Override
    public List<Task> history() {
        return super.history();
    }

    public String taskToString(Task task) {
        return task.getId() + ", " + TaskType.TASK + ", " + task.getName() + ", " +
                task.getTaskStatus() + ", " + task.getDescription();
    }

    public String epicToString(Epic epic) {
        return epic.getId() + ", " + TaskType.EPIC + ", " + epic.getName() + ", " +
                epic.getTaskStatus() + ", " + epic.getDescription();
    }

    public String subTaskToString(SubTask subTask) {
        return subTask.getId() + ", " + TaskType.SUBTASK + ", " + subTask.getName() + ", " +
                subTask.getTaskStatus() + ", " + subTask.getDescription() +
                ", " + subTask.getEpic().getId();
    }

    public void save () {

        try (FileWriter fileWriter = new FileWriter("tasksFile.csv", StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task: super.getTasks().values()) {
                fileWriter.write(taskToString(task) + "\n");
            }
            for (Epic epic: super.getEpics().values()) {
                fileWriter.write(epicToString(epic) + "\n");
            }
            for (SubTask subTask: super.getSubTasks().values()) {
                fileWriter.write(subTaskToString(subTask) + "\n");
            }

            fileWriter.write("\n");

            for (Task task: history()) {
                fileWriter.write(task.getId() + ", ");
            }

        } catch (IOException e) {
            System.out.println("Запись не сделана.");
        }
    }
}
