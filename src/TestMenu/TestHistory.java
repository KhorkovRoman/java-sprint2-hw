package TestMenu;

import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.time.LocalDateTime;
import java.time.Month;

import Managers.Managers;
import Managers.InMemoryTaskManager;
import Exeptions.ManagerSaveException;

public class TestHistory {
    static InMemoryTaskManager manager = Managers.getDefault();

    public void testHistory() throws ManagerSaveException {

        Task task1 = new Task(1, "Таск1", "Описание Таск1", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 26, 10, 00), 1);
            manager.addTask(task1);
        Task task2 = new Task(2, "Таск2", "Описание Таск2", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 26, 12, 00), 1);
            manager.addTask(task2);

        Epic epic1 = new Epic(3, "Эпик1", "Описание Эпик1", TaskStatus.NEW,
                null, 0);
            manager.addEpic(epic1);

        SubTask subTask1 = new SubTask(4, "СабТаск1", "Описание СабТаск1", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 27, 12, 00), 1, epic1);
            manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask(5, "СабТаск2", "Описание СабТаск2", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 28, 12, 00), 1, epic1);
            manager.addSubTask(subTask2);
        SubTask subTask3 = new SubTask(6, "СабТаск3", "Описание СабТаск3", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 29, 12, 00), 1, epic1);
            manager.addSubTask(subTask3);

        Epic epic2 = new Epic(7, "Эпик2", "Описание Эпик2", TaskStatus.NEW,
                null, 0);
            manager.addEpic(epic2);
            System.out.println();

            manager.getTask(1);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

            manager.getTask(2);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

            manager.getEpic(3);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

            manager.getSubTask(4);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

            manager.getSubTask(5);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

            manager.getSubTask(6);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

            manager.getEpic(7);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

            manager.getTask(1);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

            manager.getSubTask(5);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

            manager.getTask(2);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

        Task task = manager.getTaskMap().get(1);
            manager.removeTask(task);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();

        Epic epic = manager.getEpicMap().get(3);
            manager.removeEpic(epic);
            System.out.println("История просмотров:");
        printHistory();
            System.out.println();
    }

    public static void printHistory() {
        for (Task task: manager.getHistoryList()) {
            System.out.println(task);
        }
    }
}