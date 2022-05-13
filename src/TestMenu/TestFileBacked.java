package TestMenu;

import Managers.FileBackedTasksManager;
import Managers.TaskManager;
import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;

public class TestFileBacked {

    public void testFileBacked() {

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

        TaskManager taskManager = FileBackedTasksManager.loadFromFile(new File("tasksFile.csv"));

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

        TaskManager taskManager1 = FileBackedTasksManager.loadFromFile(new File("tasksFile.csv"));

        System.out.println("Вывод менеджер Тест:");
        for (Task task : taskManager1.getHistoryList()) {
            System.out.print(task.getId() + ",");
        }
    }


}
