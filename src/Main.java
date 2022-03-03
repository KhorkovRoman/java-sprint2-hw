import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static InMemoryTaskManager manager = Managers.getDefault();
    
    public static void main(String[] args) {
        testHistory(manager);
    }

    private static void testHistory(InMemoryTaskManager manager) {
        Task task1 = new Task(1, "Таск1", "Описание Таск1", TaskStatus.NEW);
        manager.setTask(task1);
        Task task2 = new Task(2, "Таск1", "Описание Таск1", TaskStatus.NEW);
        manager.setTask(task2);

        HashMap<Integer, SubTask> subTasks1 = new HashMap<>();
        Epic epic1 = new Epic(3, "Эпик1", "Описание Эпик1", TaskStatus.NEW, subTasks1);
        manager.setEpic(epic1);

        SubTask subTask1 = new SubTask(4, "СабТаск1", "Описание СабТаск1", TaskStatus.NEW, epic1);
        manager.setSubTasks(subTask1);
        SubTask subTask2 = new SubTask(5, "СабТаск2", "Описание СабТаск2", TaskStatus.NEW, epic1);
        manager.setSubTasks(subTask2);
        SubTask subTask3 = new SubTask(6, "СабТаск3", "Описание СабТаск3", TaskStatus.NEW, epic1);
        manager.setSubTasks(subTask3);

        HashMap<Integer, SubTask> subTasks2 = new HashMap<>();
        Epic epic2 = new Epic(7, "Эпик2", "Описание Эпик2", TaskStatus.NEW, subTasks2);
        manager.setEpic(epic2);
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

        Task task = manager.getTasks().get(1);
        manager.removeTask(task);
        System.out.println("История просмотров:");
        printHistory();
        System.out.println();

        Epic epic = manager.getEpics().get(3);
        manager.removeEpic(epic);
        System.out.println("История просмотров:");
        printHistory();
        System.out.println();
    }

    private static void testMenu(InMemoryTaskManager manager) {
        Scanner scanner = new Scanner(System.in);
        UserIn userIn = new UserIn();

        System.out.println("Менеджер задач.");
        printMenu();
        int userInput = scanner.nextInt();

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
                        Task task = new Task(id, name, description, taskStatus);
                        manager.setTask(task);
                        break;
                    } else if (userInputSubMenu == 2) {   //эпик
                        int id = manager.generateId();
                        String name = userIn.epicName();
                        String description = userIn.epicDescritpion();
                        //HashMap<Integer, SubTask> subTasks = manager.getSubTasks();
                        HashMap<Integer, SubTask> subTasks = new HashMap<>();
                        TaskStatus taskStatus = TaskStatus.NEW;
                        Epic epic = new Epic(id, name, description, taskStatus, subTasks);
                        manager.setEpic(epic);
                        break;
                    } else if (userInputSubMenu == 3) { //подзадача
                        int currentKey = userIn.epicId();
                        if (manager.getEpics().containsKey(currentKey)) {
                            int id = manager.generateId();
                            String name = userIn.subTaskName();
                            String description = userIn.subTaskDescritpion();
                            TaskStatus taskStatus = TaskStatus.NEW;
                            Epic currentEpic = manager.getEpics().get(currentKey);
                            SubTask subTask = new SubTask(id, name, description, taskStatus, currentEpic);
                            manager.setSubTasks(subTask);
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
                        if(manager.getTasks().containsKey(id)) {
                            Task task = manager.getTasks().get(id);
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
                        if(manager.getEpics().containsKey(id)) {
                            Epic epic = manager.getEpics().get(id);
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
                        if(manager.getSubTasks().containsKey(id)) {
                            SubTask subTask = manager.getSubTasks().get(id);
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
                        if (manager.getEpics().containsKey(id)) {
                            Epic epic = manager.getEpics().get(id);
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
                        if (manager.getTasks().containsKey(id)) {
                            Task task = manager.getTasks().get(id);
                            manager.removeTask(task);
                        } else {
                            System.out.println("Задачи с таким номером нет в базе");
                        }
                        break;
                    } else if (userInputSubMenu == 2) {   //эпик
                        int id = userIn.epicId();
                        if (manager.getEpics().containsKey(id)) {
                            Epic epic = manager.getEpics().get(id);
                            manager.removeEpic(epic);
                        } else {
                            System.out.println("Эпика с таким номером нет в базе");
                        }
                        break;
                    } else if (userInputSubMenu == 3) { //подзадачу
                        int id = userIn.subTaskId();

                        if(manager.getSubTasks().containsKey(id)) {
                            SubTask subTask = manager.getSubTasks().get(id);
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
                manager.deleteAllTasksAndEpics();
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

    private static void printHistory() {
        for (Task task: manager.history()) {
            System.out.println(task);
        }
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