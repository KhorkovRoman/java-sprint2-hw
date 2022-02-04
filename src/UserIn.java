import java.util.Scanner;

public class UserIn {
    Scanner scanner = new Scanner(System.in);

    public Integer taskId() {
        System.out.println("Введите номер задачи");
        int taskId = scanner.nextInt();
        return taskId;
    }

    public Integer epicId() {
        System.out.println("Введите номер эпика");
        int epicId = scanner.nextInt();
        return epicId;
    }

    public Integer subTaskId() {
        System.out.println("Введите номер подзадачи");
        int subTaskId = scanner.nextInt();
        return subTaskId;
    }

    public String taskName() {
        System.out.println("Введите название задачи.");
        String taskName = scanner.nextLine();
        return taskName;
    }

    public String taskDescritpion() {
        System.out.println("Введите описание задачи.");
        String taskDescription = scanner.nextLine();
        return taskDescription;
    }

    public String taskStatus(Manager manager) {
        System.out.println("Введите статус задачи:");
        String taskStatus = "";
        printStatusMenu();
        int userInput = scanner.nextInt();
        while (userInput != 0) {
            if (userInput == 1) {    //новая
                taskStatus = manager.statusNew;
                break;
            } else if (userInput == 2) {   //в работе
                taskStatus = manager.statusInProgress;
                break;
            } else if (userInput == 3) { //сделана
                taskStatus = manager.statusDone;
                break;
            } else {
                System.out.println("Извините, такой команды нет.");
            }
            printStatusMenu();
            userInput = scanner.nextInt();
        }
        return taskStatus;
    }

    public String epicName() {
        System.out.println("Введите название эпика.");
        String epicName = scanner.nextLine();
        return epicName;
    }

    public String epicDescritpion() {
        System.out.println("Введите описание эпика.");
        String epicDescritpion = scanner.nextLine();
        return epicDescritpion;
    }

    public String subTaskName() {
        String subTaskName = scanner.nextLine();
        System.out.println("Введите название подзадачи.");
        subTaskName = scanner.nextLine();
        return subTaskName;
    }

    public String subTaskDescritpion() {
        System.out.println("Введите описание подзадачи.");
        String subTaskDescritpion = scanner.nextLine();
        return subTaskDescritpion;
    }

    public String subTaskStatus(Manager manager) {
        System.out.println("Введите статус подзадачи:");
        String subTaskStatus = "";
        printStatusMenu();
        int userInput = scanner.nextInt();
        while (userInput != 0) {
            if (userInput == 1) {    //новая
                subTaskStatus = manager.statusNew;
                break;
            } else if (userInput == 2) {   //в работе
                subTaskStatus = manager.statusInProgress;
                break;
            } else if (userInput == 3) { //сделана
                subTaskStatus = manager.statusDone;
                break;
            } else {
                System.out.println("Извините, такой команды нет.");
            }
            printStatusMenu();
            userInput = scanner.nextInt();
        }
        return subTaskStatus;
    }

    private static void printStatusMenu() {
        System.out.println("1 - Новая задача");
        System.out.println("2 - Задача в работе");
        System.out.println("3 - Задача выполнена");
        System.out.println("0 - Выйти");
    }
}



