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

}



