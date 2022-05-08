import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class TaskManagerTest<T extends TaskManager> {

    FileBackedTasksManager fileManager = new FileBackedTasksManager("tasksFile.csv");
    InMemoryTaskManager taskManager = new InMemoryTaskManager();
    HashMap<Integer, Task> taskMap = taskManager.getTaskMap();
    HashMap<Integer, Epic> epicMap = taskManager.getEpicMap();
    HashMap<Integer, SubTask> subTaskMap = taskManager.getSubTaskMap();

    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    Map<Integer, Node<Task>> historyMap = historyManager.getHistoryMap();

    static Task task1 = new Task(1, "Task1", "Task1 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 26, 10, 00), 1);
    static Task task2 = new Task(2, "Task2", "Task2 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 26, 12, 00), 1);

    static Epic epic1 = new Epic(3, "Epic1", "Epic1 description", TaskStatus.NEW,
            null, 0);

    static SubTask subTask1 = new SubTask(4, "SubTask1", "SubTask1 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 27, 12, 00), 1, epic1);
    static SubTask subTask2 = new SubTask(5, "SubTask2", "SubTask2 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 28, 12, 00), 1, epic1);
    static SubTask subTask3 = new SubTask(6, "SubTask3", "SubTask3 description", TaskStatus.DONE,
            LocalDateTime.of(2022, Month.APRIL, 29, 12, 00), 1, epic1);

    static Epic epic2 = new Epic(7, "Epic2", "Epic2 description", TaskStatus.NEW,
            null, 0);

    @BeforeEach
    public void addAllTasksEpicsAndSubTasks() {
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);
        taskManager.addEpic(epic2);

        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubTask(subTask3.getId());
    }

    @AfterEach
    public void clearTaskMapEpicMapSubTaskMap() {
        taskManager.deleteTasksEpicsSubTasks();
    }

    @Test
    public void generateId3TimesMustReturn3() {
        Integer id = taskManager.getId();
        for (int i = 0; i < 3; i++ ) {
            id = taskManager.generateId();
        }
        assertEquals(3, id, "Id не совпадают.");
    }

    @Test
    public void getTaskTest() {
        Task savedTask = taskManager.getTask(task1.getId());

        Task wrongTask = taskManager.getTask(25);

        assertNull(wrongTask, "Такая задача есть");

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");

        assertNotNull(taskMap, "Map c задачами не возвращается.");
        assertEquals(2, taskMap.size(), "Неверное количество задач.");
        assertEquals(task1, taskMap.get(task1.getId()), "Задачи не совпадают.");
    }

    @Test
    public void getEpicTest() {
        Epic savedEpic = taskManager.getEpic(epic1.getId());

        Epic wrongEpic = taskManager.getEpic(25);

        assertNull(wrongEpic, "Такой эпик есть");

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic1, savedEpic, "Эпики не совпадают.");

        assertNotNull(taskMap, "Map c эпиками не возвращается.");
        assertEquals(2, epicMap.size(), "Неверное количество эпиков.");
        assertEquals(epic1, epicMap.get(epic1.getId()), "Эпики не совпадают.");
    }

    @Test
    public void getSubTaskTest() {
        SubTask savedSubTask = taskManager.getSubTask(subTask1.getId());

        SubTask wrongSubTask = taskManager.getSubTask(25);

        assertNull(wrongSubTask, "Такая задача есть");

        assertNotNull(savedSubTask, "Сабтаск не найден.");
        assertEquals(subTask1, savedSubTask, "Сабтаски не совпадают.");

        assertNotNull(subTaskMap, "Map c сабтасками не возвращается.");
        assertEquals(3, subTaskMap.size(), "Неверное количество сабтасков.");
        assertEquals(subTask1, subTaskMap.get(subTask1.getId()), "Сабтаски не совпадают.");
    }

    @Test
    public void updateTask() {
        taskManager.updateTask(task1);
        assertEquals(task1, taskMap.get(task1.getId()), "Задачи не совпадают.");
    }

    @Test
    public void updateEpic() {
        taskManager.updateEpic(epic1);
        assertEquals(epic1, epicMap.get(epic1.getId()), "Эпики не совпадают.");
    }

    @Test
    public void updateSubTask() {
        taskManager.updateSubTask(subTask1);
        assertEquals(subTask1, subTaskMap.get(subTask1.getId()), "Сабтаски не совпадают.");
    }

    @Test
    public void removeTask() {
        taskManager.removeTask(task1);
        assertNull(taskMap.get(task1.getId()), "Задача не удалена.");
    }

    @Test
    public void removeSubTask() {
        taskManager.removeSubTask(subTask1);
        assertNull(subTaskMap.get(subTask1.getId()), "Сабтаск не удален.");
    }

    @Test
    public void removeEpic() {
        taskManager.removeEpic(epic1);
        assertNull(epicMap.get(epic1.getId()), "Эпик не удален.");
    }

    @Test
    public void deleteTasksEpicsSubTasks() {
        taskManager.deleteTasksEpicsSubTasks();

        assertEquals(0, taskMap.size(), "Список задач не пустой.");
        assertEquals(0, epicMap.size(), "Список эпиков не пустой.");
        assertEquals(0, subTaskMap.size(), "Список сабтасков не пустой.");
    }

    @Test
    public void getHistoryList() {
        List<Task> historyList = taskManager.getHistoryList();

        assertNotNull(historyList, "Список истории не создан.");
        assertEquals(3, historyList.size(), "История сожержит не 3 задачи.");
    }
}
