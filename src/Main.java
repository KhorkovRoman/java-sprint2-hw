import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;

import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Manager manager = new Manager();
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
                        String status = manager.statusNew;
                        Task task = new Task(id, name, description, status);
                        manager.setTask(task);
                        break;
                    } else if (userInputSubMenu == 2) {   //эпик
                        int id = manager.generateId();
                        String name = userIn.epicName();
                        String description = userIn.epicDescritpion();
                        HashMap<Integer, SubTask> subTasks = manager.subTasks;
                        String status = manager.statusNew;
                        Epic epic = new Epic(id, name, description, status, subTasks);
                        manager.setEpic(epic);
                        break;
                    } else if (userInputSubMenu == 3) { //подзадача
                        int currentKey = userIn.epicId();
                        if (manager.epics.containsKey(currentKey)) {
                            int id = manager.generateId();
                            String name = userIn.subTaskName();
                            String description = userIn.subTaskDescritpion();
                            String status = manager.statusNew;
                            Epic currentEpic = manager.epics.get(currentKey);
                            SubTask subTask = new SubTask(id, name, description, status, currentEpic);
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
            } else if (userInput == 2) {  //обновить
                printSubMenu();
                int userInputSubMenu = scanner.nextInt();
                while (userInputSubMenu != 0) {
                    if (userInputSubMenu == 1) {     //задачу
                        int id = userIn.taskId();
                        if(manager.tasks.containsKey(id)) {
                            Task task = manager.tasks.get(id);
                            String name = userIn.taskName();
                            task.setName(name);
                            String description = userIn.taskDescritpion();
                            task.setDescription(description);
                            String status = userIn.taskStatus(manager);
                            task.setStatus(status);

                            manager.updateTask(task);
                        } else {
                            System.out.println("Задачи с таким номером нет в базе");
                        }
                        break;
                    } else if (userInputSubMenu == 2) {  // эпик
                        int id = userIn.epicId();
                        if(manager.epics.containsKey(id)) {
                            Epic epic = manager.epics.get(id);
                            String name = userIn.epicName();
                            epic.setName(name);
                            String description = userIn.epicDescritpion();
                            epic.setDescription(description);

                            manager.updateEpic(epic);
                        } else {
                            System.out.println("Эпика с таким номером нет в базе");
                        }
                        break;
                    } else if (userInputSubMenu == 3) { //подзадача
                        int id = userIn.subTaskId();
                        if(manager.subTasks.containsKey(id)) {
                            SubTask subTask = manager.subTasks.get(id);
                            String name = userIn.subTaskName();
                            subTask.setName(name);
                            String description = userIn.subTaskDescritpion();
                            subTask.setDescription(description);
                            String status = userIn.subTaskStatus(manager);
                            subTask.setStatus(status);

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
            } else if (userInput == 3) {          //Распечатать список задач
                printSubMenu();
                int userInputSubMenu = scanner.nextInt();
                while (userInputSubMenu != 0) {
                    if (userInputSubMenu == 1) {     //задачу
                        manager.printTasks();
                        break;
                    } else if (userInputSubMenu == 2) {  // эпик
                        manager.printEpics();
                        break;
                    } else if (userInputSubMenu == 3) { //подзадача
                        int id = userIn.epicId();
                        manager.printSubTasks(id);
                        break;
                    } else {
                        System.out.println("Извините, такой команды нет.");
                    }
                    printSubMenu();
                    userInputSubMenu = scanner.nextInt();
                }
            } else if (userInput == 4) {    //удалить
                printSubMenu();
                int userInputSubMenu = scanner.nextInt();
                while (userInputSubMenu != 0) {
                    if (userInputSubMenu == 1) {    //задачу
                        int id = userIn.taskId();
                        manager.removeTask(id);
                        break;
                    } else if (userInputSubMenu == 2) {   //эпик
                        int id = userIn.epicId();
                        manager.removeEpic(id);
                        break;
                    } else if (userInputSubMenu == 3) { //подзадача
                        int id = userIn.subTaskId();

                        if(manager.subTasks.containsKey(id)) {
                            SubTask currentSubTask = manager.subTasks.get(id);
                            manager.removeSubTask(currentSubTask);
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
            } else if (userInput == 5) {
                manager.deleteAllTasksAndEpics();
            } else {
                System.out.println("Извините, такой команды нет");
            }
            printMenu();
            userInput = scanner.nextInt();
        }
        System.out.println("Программа завершена");
    }

    private static void printMenu() {
        System.out.println("");
        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Ввести новую задачу");
        System.out.println("2 - Обновить задачу");
        System.out.println("3 - Распечатать список задач");
        System.out.println("4 - Удалить задачу");
        System.out.println("5 - Удалить все задачи");
        System.out.println("0 - Закрыть приложение");
    }

    private static void printSubMenu() {
        System.out.println("");
        System.out.println("Введите тип задачи:");
        System.out.println("1 - Отдельная задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        System.out.println("0 - Выйти");
    }

}