import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.Month;


public class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    @Test
    public void fileManagerNotNullWhithoutFile() {
        fileManager = FileBackedTasksManager.loadFromFile(new File("tasksFile.csv"));
        System.out.println(fileManager);
        assertNotNull(fileManager, "Менеджер не создан.");
    }

    @Test
    public void fileManagerEpicWithoutSubTasks() {
        Epic epic1 = new Epic(3, "Epic1", "Epic1 description", TaskStatus.NEW,
                null, 0);
        fileManager.addEpic(epic1);
        fileManager.getEpic(epic1.getId());

        fileManager = FileBackedTasksManager.loadFromFile(new File("tasksFile.csv"));

        FileBackedTasksManager fileManagerTest =
                FileBackedTasksManager.loadFromFile(new File("tasksFile.csv"));

        assertEquals(fileManager, fileManagerTest, "Менеджеры не равны.");
    }

    @Test
    public void fileManagerTasksWithHistory() {
        Task task1 = new Task(1, "Task1", "Task1 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 26, 10, 00), 1);
        Task task2 = new Task(2, "Task2", "Task2 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 26, 12, 00), 1);

        Epic epic1 = new Epic(3, "Epic1", "Epic1 description", TaskStatus.NEW,
                null, 0);

        SubTask subTask1 = new SubTask(4, "SubTask1", "SubTask1 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 27, 12, 00), 1, epic1);
        SubTask subTask2 = new SubTask(5, "SubTask2", "SubTask2 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 28, 12, 00), 1, epic1);
        SubTask subTask3 = new SubTask(6, "SubTask3", "SubTask3 description", TaskStatus.DONE,
                LocalDateTime.of(2022, Month.APRIL, 29, 12, 00), 1, epic1);

        Epic epic2 = new Epic(7, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);

        fileManager.addTask(task1);
        fileManager.addTask(task2);
        fileManager.addEpic(epic1);
        fileManager.addSubTask(subTask1);
        fileManager.addSubTask(subTask2);
        fileManager.addSubTask(subTask3);
        fileManager.addEpic(epic2);

        fileManager.getTask(1);
        fileManager.getEpic(3);
        fileManager.getSubTask(6);

        fileManager = FileBackedTasksManager.loadFromFile(new File("tasksFile.csv"));

        FileBackedTasksManager fileManagerTest =
                FileBackedTasksManager.loadFromFile(new File("tasksFile.csv"));

        assertEquals(fileManager, fileManagerTest, "Менеджеры не равны.");
    }

    @Test
    public void fileManagerTasksWighoutHistory() {
        Task task1 = new Task(1, "Task1", "Task1 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 26, 10, 00), 1);
        Task task2 = new Task(2, "Task2", "Task2 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 26, 12, 00), 1);

        Epic epic1 = new Epic(3, "Epic1", "Epic1 description", TaskStatus.NEW,
                null, 0);

        SubTask subTask1 = new SubTask(4, "SubTask1", "SubTask1 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 27, 12, 00), 1, epic1);
        SubTask subTask2 = new SubTask(5, "SubTask2", "SubTask2 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.APRIL, 28, 12, 00), 1, epic1);
        SubTask subTask3 = new SubTask(6, "SubTask3", "SubTask3 description", TaskStatus.DONE,
                LocalDateTime.of(2022, Month.APRIL, 29, 12, 00), 1, epic1);

        Epic epic2 = new Epic(7, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);

        fileManager.addTask(task1);
        fileManager.addTask(task2);
        fileManager.addEpic(epic1);
        fileManager.addSubTask(subTask1);
        fileManager.addSubTask(subTask2);
        fileManager.addSubTask(subTask3);
        fileManager.addEpic(epic2);

        fileManager = FileBackedTasksManager.loadFromFile(new File("tasksFile.csv"));

        FileBackedTasksManager fileManagerTest =
                FileBackedTasksManager.loadFromFile(new File("tasksFile.csv"));

        assertEquals(fileManager, fileManagerTest, "Менеджеры не равны.");
    }

}
