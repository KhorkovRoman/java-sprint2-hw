import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.util.Arrays;
import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        manager.setSubTask(subTask1);
        SubTask subTask2 = new SubTask(5, "SubTask2", "Description SubTask2", TaskStatus.NEW, epic1);
        manager.setSubTask(subTask2);
        SubTask subTask3 = new SubTask(6, "SubTask3", "Description SubTask3", TaskStatus.NEW, epic1);
        manager.setSubTask(subTask3);

        HashMap<Integer, SubTask> mapSubTasks2 = new HashMap<>();
        Epic epic2 = new Epic(7, "Epic2", "Description Epic2", TaskStatus.NEW, mapSubTasks2);
        manager.setEpic(epic2);

        manager.getTask(1);
        manager.getEpic(3);
        manager.getSubTask(6);

        System.out.println("Вывод менеджер:");
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

        int id = 0;
        TaskType taskType = TaskType.TASK;
        String name = "";
        String description = "";
        TaskStatus taskStatus = TaskStatus.NEW;
        int epicId = 0;
        HashMap<Integer, SubTask> subTaskList = new HashMap<>();
        Epic epic = null;

        FileBackedTasksManager manager = new FileBackedTasksManager(file.toString());
        try(FileReader fr = new FileReader(file)) {
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

                int numberElement = 0;
                for (String lineElement: lineArray) {
                    if (numberElement == 0) {
                        id = Integer.parseInt(lineElement);
                    } else if (numberElement == 1) {
                        if (lineElement.equals(TaskType.TASK.toString())) {
                            taskType = TaskType.TASK;
                        } else if (lineElement.equals(TaskType.EPIC.toString())) {
                            taskType = TaskType.EPIC;
                            subTaskList = new HashMap<>();
                        } else if (lineElement.equals(TaskType.SUBTASK.toString())) {
                            taskType = TaskType.SUBTASK;
                        }
                    } else if (numberElement == 2) {
                        name = lineElement;
                    } else if (numberElement == 3) {
                        if (line.equals(TaskStatus.NEW.toString())) {
                            taskStatus = TaskStatus.NEW;
                        } else if (line.equals(TaskStatus.DONE.toString())) {
                            taskStatus = TaskStatus.DONE;
                        } else if (line.equals(TaskStatus.IN_PROGRESS.toString())) {
                            taskStatus = TaskStatus.IN_PROGRESS;
                        }
                    } else if (numberElement == 4) {
                        description = lineElement;
                    } else if (numberElement == 5) {
                        epicId = Integer.parseInt(lineElement);
                    }
                    numberElement += 1;
                }

                if (taskType == TaskType.TASK) {
                    manager.setTask(new Task(id, name, description, taskStatus));
                } else if (taskType == TaskType.EPIC) {
                    manager.setEpic(new Epic(id, name, description, taskStatus, subTaskList));
                } else if (taskType == TaskType.SUBTASK) {
                    epic = manager.getEpics().get(epicId);
                    manager.setSubTask(new SubTask(id, name, description, taskStatus, epic));
                }
            }

            String historyLine = br.readLine();

            for (String idString: historyLine.split(",")) {
                manager.getTaskById(Integer.parseInt(idString));
            }


        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл. Будет создан пустой менеджер.");
        }

        return manager;
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
    public void setSubTask(SubTask subTask) {
        super.setSubTask(subTask);
        save();
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
            for (Task task : super.getTasks().values()) {
                fileWriter.write(taskToString(task) + "\n");
            }
            for (Epic epic : super.getEpics().values()) {
                fileWriter.write(epicToString(epic) + "\n");
            }
            for (SubTask subTask : super.getSubTasks().values()) {
                fileWriter.write(subTaskToString(subTask) + "\n");
            }

            fileWriter.write("\n");

            List<String> stringId = new ArrayList<>();
            for (Task task : history()) {
                stringId.add(String.valueOf(task.getId()));
            }

            fileWriter.write(String.join(",", stringId));

        } catch (IOException e) {
            System.out.println("Запись не сделана.");
        }
    }
}
