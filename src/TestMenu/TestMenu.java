package TestMenu;

import Exeptions.ManagerSaveException;
import Managers.InMemoryTaskManager;
import Managers.Managers;
import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class TestMenu {

    InMemoryTaskManager manager = Managers.getDefaultInMemoryTaskManager();

    public void testMenu() throws ManagerSaveException {
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
                            SubTask subTask = new SubTask(id, name, description, taskStatus, startTime, duration, epic.getId());
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
                        if (manager.getTaskMap().containsKey(id)) {
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
                        if (manager.getEpicMap().containsKey(id)) {
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
                        if (manager.getSubTaskMap().containsKey(id)) {
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

                        if (manager.getSubTaskMap().containsKey(id)) {
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
                TestHistory.printHistory();
            } else {
                System.out.println("Извините, такой команды нет");
            }
            printMenu();
            userInput = scanner.nextInt();
        }
        System.out.println("Программа завершена");
    }

    public void printMenu() {
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

    public void printSubMenu() {
        System.out.println("Введите тип задачи:");
        System.out.println("1 - Отдельная задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        System.out.println("0 - Выйти");
    }
}