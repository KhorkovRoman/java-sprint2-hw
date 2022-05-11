import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import Managers.Managers;
import Managers.InMemoryTaskManager;
import Exeptions.ManagerSaveException;
import TestMenu.UserIn;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static InMemoryTaskManager manager = Managers.getDefault();

//    public static void main(String[] args) {
//        testHistory();
//    }

    private static void testHistory() throws ManagerSaveException {
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

    private static void printHistory() {
        for (Task task: manager.getHistoryList()) {
            System.out.println(task);
        }
    }

    private static void testMenu() throws ManagerSaveException {
        Scanner scanner = new Scanner(System.in);
        UserIn userIn = new UserIn();

        System.out.println("Менеджер задач.");
        printMenu();
        int userInput = scanner.nextInt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy_HH:mm");

        while (userInput != 0) {
            if (userInput == 1) {  //новая
                printSubMenu();
                int userInputSubMenu = scanner.nextInt();
                while (userInputSubMenu != 0) {
                    if (userInputSubMenu == 1) {         // задача
                        int id = manager.generateId();
                        String name = userIn.taskName();
                        String description = userIn.taskDescritpion();
                        TaskStatus taskStatus = TaskStatus.NEW;
                        LocalDateTime startTime = LocalDateTime.parse(scanner.next(), formatter);
                        int duration = scanner.nextInt();
                        Task task = new Task(id, name, description, taskStatus, startTime, duration);
                        manager.addTask(task);
                        break;
                    } else if (userInputSubMenu == 2) {   //эпик
                        int id = manager.generateId();
                        String name = userIn.epicName();
                        String description = userIn.epicDescritpion();
                        HashMap<Integer, SubTask> subTasks = new HashMap<>();
                        TaskStatus taskStatus = TaskStatus.NEW;
                        LocalDateTime startTime = null;
                                //LocalDateTime.of(1990,10,10,10,10);
                        int duration = 0;
                        Epic epic = new Epic(id, name, description, taskStatus, startTime, duration);
                        manager.addEpic(epic);
                        break;
                    } else if (userInputSubMenu == 3) { //подзадача
                        int idEpic = userIn.epicId();
                        if (manager.getEpicMap().containsKey(idEpic)) {
                            int id = manager.generateId();
                            String name = userIn.subTaskName();
                            String description = userIn.subTaskDescritpion();
                            TaskStatus taskStatus = TaskStatus.NEW;
                            LocalDateTime startTime = LocalDateTime.parse(scanner.next(), formatter);
                            int duration = scanner.nextInt();
                            Epic epic = manager.getEpicMap().get(idEpic);
                            SubTask subTask = new SubTask(id, name, description, taskStatus, startTime, duration, epic);
                            manager.addSubTask(subTask);
                            break;
                        } else {
                            System.out.println("Эпика с таким номером нет в базе");
                        }
                    } else {
                        System.out.println("Извините, такой команды нет.");
                    }
                    printSubMenu();
                    userInputSubMenu = scanner.nextInt();
                }
            } else if (userInput == 2) {                //Просмотр
                printSubMenu();
                int userInputSubMenu = scanner.nextInt();
                while (userInputSubMenu != 0) {
                    if (userInputSubMenu == 1) {        //задача
                        int id = userIn.taskId();
                        manager.getTask(id);
                        break;
                    } else if (userInputSubMenu == 2) {  // эпик
                        int id = userIn.epicId();
                        manager.getEpic(id);
                        break;
                    } else if (userInputSubMenu == 3) { //подзадача
                        int id = userIn.subTaskId();
                        manager.getSubTask(id);
                        break;
                    } else {
                        System.out.println("Извините, такой команды нет.");
                    }
                    printSubMenu();
                    userInputSubMenu = scanner.nextInt();
                }
            } else if (userInput == 3) {  //обновить
                printSubMenu();
                int userInputSubMenu = scanner.nextInt();
                while (userInputSubMenu != 0) {
                    if (userInputSubMenu == 1) {     //задачу
                        int id = userIn.taskId();
                        if(manager.getTaskMap().containsKey(id)) {
                            Task task = manager.getTaskMap().get(id);
                            String name = userIn.taskName();
                            task.setName(name);
                            String description = userIn.taskDescritpion();
                            task.setDescription(description);
                            TaskStatus taskStatus = userIn.taskStatus();
                            task.setTaskStatus(taskStatus);

                            manager.updateTask(task);
                        } else {
                            System.out.println("Задачи с таким номером нет в базе");
                        }
                        break;
                    } else if (userInputSubMenu == 2) {  // эпик
                        int id = userIn.epicId();
                        if(manager.getEpicMap().containsKey(id)) {
                            Epic epic = manager.getEpicMap().get(id);
                            String name = userIn.epicName();
                            epic.setName(name);
                            String description = userIn.epicDescritpion();
                            epic.setDescription(description);

                            manager.updateEpic(epic);
                        } else {
                            System.out.println("Эпика с таким номером нет в базе");
                        }
                        break;
                    } else if (userInputSubMenu == 3) { //подзадачу
                        int id = userIn.subTaskId();
                        if(manager.getSubTaskMap().containsKey(id)) {
                            SubTask subTask = manager.getSubTaskMap().get(id);
                            String name = userIn.subTaskName();
                            subTask.setName(name);
                            String description = userIn.subTaskDescritpion();
                            subTask.setDescription(description);
                            TaskStatus taskStatus = userIn.subTaskStatus();
                            subTask.setTaskStatus(taskStatus);

                            manager.updateSubTask(subTask);
                        } else {
                            System.out.println("Подзадачи с таким номером нет в базе");
                        }
                        break;
                    } else {
                        System.out.println("Извините, такой команды нет.");
                    }
                    printSubMenu();
                    userInputSubMenu = scanner.nextInt();
                }
            } else if (userInput == 4) {          //Распечатать список
                printSubMenu();
                int userInputSubMenu = scanner.nextInt();
                while (userInputSubMenu != 0) {
                    if (userInputSubMenu == 1) {     //задач
                        manager.printTasks();
                        break;
                    } else if (userInputSubMenu == 2) {  // эпиков
                        manager.printEpics();
                        break;
                    } else if (userInputSubMenu == 3) { //подзадач
                        int id = userIn.epicId();
                        if (manager.getEpicMap().containsKey(id)) {
                            Epic epic = manager.getEpicMap().get(id);
                            manager.printSubTasks(epic);
                        } else {
                            System.out.println("Эпика с таким номером нет в базе");
                        }
                        break;
                    } else {
                        System.out.println("Извините, такой команды нет.");
                    }
                    printSubMenu();
                    userInputSubMenu = scanner.nextInt();
                }
            } else if (userInput == 5) {    //удалить
                printSubMenu();
                int userInputSubMenu = scanner.nextInt();
                while (userInputSubMenu != 0) {
                    if (userInputSubMenu == 1) {    //задачу
                        int id = userIn.taskId();
                        if (manager.getTaskMap().containsKey(id)) {
                            Task task = manager.getTaskMap().get(id);
                            manager.removeTask(task);
                        } else {
                            System.out.println("Задачи с таким номером нет в базе");
                        }
                        break;
                    } else if (userInputSubMenu == 2) {   //эпик
                        int id = userIn.epicId();
                        if (manager.getEpicMap().containsKey(id)) {
                            Epic epic = manager.getEpicMap().get(id);
                            manager.removeEpic(epic);
                        } else {
                            System.out.println("Эпика с таким номером нет в базе");
                        }
                        break;
                    } else if (userInputSubMenu == 3) { //подзадачу
                        int id = userIn.subTaskId();

                        if(manager.getSubTaskMap().containsKey(id)) {
                            SubTask subTask = manager.getSubTaskMap().get(id);
                            manager.removeSubTask(subTask);
                        } else {
                            System.out.println("Подзадачи с таким номером нет в базе");
                        }
                        break;
                    } else {
                        System.out.println("Извините, такой команды нет.");
                    }
                    printSubMenu();
                    userInputSubMenu = scanner.nextInt();
                }
            } else if (userInput == 6) {         //удалить все задачи, эпики и подзадачи
                manager.deleteTasksEpicsSubTasks();
            } else if (userInput == 7) {         //показать историю просмотров задач
                printHistory();
            } else {
                System.out.println("Извините, такой команды нет");
            }
            printMenu();
            userInput = scanner.nextInt();
        }
        System.out.println("Программа завершена");
    }



    private static void printMenu() {
        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Ввести новую задачу");
        System.out.println("2 - Просмотр задачи");
        System.out.println("3 - Обновить задачу");
        System.out.println("4 - Распечатать список задач");
        System.out.println("5 - Удалить задачу");
        System.out.println("6 - Удалить все задачи");
        System.out.println("7 - Показать историю просмотров задач");
        System.out.println("0 - Закрыть приложение");
    }

    private static void printSubMenu() {
        System.out.println("Введите тип задачи:");
        System.out.println("1 - Отдельная задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        System.out.println("0 - Выйти");
    }
}