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


