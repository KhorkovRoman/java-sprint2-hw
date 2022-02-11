import TaskStructure.TaskStatus;

import java.util.Scanner;

public class UserIn {
    Scanner scanner = new Scanner(System.in);
    TaskStatus taskStatus;

    public Integer taskId() {
        System.out.println("Введите номер задачи");
        return scanner.nextInt();
    }

    public Integer epicId() {
        System.out.println("Введите номер эпика");
        return scanner.nextInt();
    }

    public Integer subTaskId() {
        System.out.println("Введите номер подзадачи");
        return scanner.nextInt();
    }

    public String taskName() {
        System.out.println("Введите название задачи.");
        return scanner.nextLine();
    }

    public String taskDescritpion() {
        System.out.println("Введите описание задачи.");
        return scanner.nextLine();
    }

    public String epicName() {
        System.out.println("Введите название эпика.");
        return scanner.nextLine();
    }

    public String epicDescritpion() {
        System.out.println("Введите описание эпика.");
        return scanner.nextLine();
    }

    public String subTaskName() {
        System.out.println("Введите название подзадачи.");
        return scanner.nextLine();
    }

    public String subTaskDescritpion() {
        System.out.println("Введите описание подзадачи.");
        return scanner.nextLine();
    }

    public TaskStatus taskStatus() {
        System.out.println("Введите статус задачи:");
        return inputStatus();
    }

    public TaskStatus subTaskStatus() {
        System.out.println("Введите статус подзадачи:");
        return inputStatus();
    }

    public TaskStatus inputStatus() {
        printStatusMenu();
        int userInput = scanner.nextInt();
        while (userInput != 0) {
            if (userInput == 1) {
                taskStatus = TaskStatus.NEW;
                break;
            } else if (userInput == 2) {
                taskStatus = TaskStatus.IN_PROGRESS;
                break;
            } else if (userInput == 3) {
                taskStatus = TaskStatus.DONE;
                break;
            } else {
                System.out.println("Извините, такой команды нет.");
            }
            printStatusMenu();
            userInput = scanner.nextInt();
        }
        return taskStatus;
    }

    private static void printStatusMenu() {
        System.out.println("1 - Новая задача");
        System.out.println("2 - Задача в работе");
        System.out.println("3 - Задача выполнена");
        System.out.println("0 - Выйти");
    }
}



