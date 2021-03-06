package Tests;

import Managers.TaskManager;

import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

abstract public class TaskManagerTest<T extends TaskManager> {

    T taskManager;

    protected abstract void setManager();

    static Task task1 = new Task(1, "Task1", "Task1 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 26, 10, 00), 1);
    static Task task2 = new Task(2, "Task2", "Task2 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 26, 12, 00), 1);

    static Epic epic1 = new Epic(3, "Epic1", "Epic1 description", TaskStatus.NEW,
            null, 0);

    static SubTask subTask1 = new SubTask(4, "SubTask1", "SubTask1 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 27, 12, 00), 1, 3);
    static SubTask subTask2 = new SubTask(5, "SubTask2", "SubTask2 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 28, 12, 00), 1, 3);
    static SubTask subTask3 = new SubTask(6, "SubTask3", "SubTask3 description", TaskStatus.DONE,
            LocalDateTime.of(2022, Month.APRIL, 29, 12, 00), 1, 3);

    static Epic epic2 = new Epic(7, "Epic2", "Epic2 description", TaskStatus.NEW,
            null, 0);

    @BeforeEach
    public void addAllTasksEpicsAndSubTasks() {
        setManager();

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
        assertEquals(3, id, "Id ???? ??????????????????.");
    }

    @Test
    public void getTaskTest() {
        Task savedTask = taskManager.getTask(task1.getId());

        Task wrongTask = taskManager.getTask(25);

        assertNull(wrongTask, "?????????? ???????????? ????????");

        assertNotNull(savedTask, "???????????? ???? ??????????????.");
        assertEquals(task1, savedTask, "???????????? ???? ??????????????????.");

        HashMap<Integer, Task> taskMap = taskManager.getTaskMap();

        assertNotNull(taskMap, "Map c ???????????????? ???? ????????????????????????.");
        assertEquals(2, taskMap.size(), "???????????????? ???????????????????? ??????????.");
        assertEquals(task1, taskMap.get(task1.getId()), "???????????? ???? ??????????????????.");
    }

    @Test
    public void getEpicTest() {
        Epic savedEpic = taskManager.getEpic(epic1.getId());

        Epic wrongEpic = taskManager.getEpic(25);

        assertNull(wrongEpic, "?????????? ???????? ????????");

        assertNotNull(savedEpic, "???????? ???? ????????????.");
        assertEquals(epic1, savedEpic, "?????????? ???? ??????????????????.");

        HashMap<Integer, Epic> epicMap = taskManager.getEpicMap();

        assertNotNull(epicMap, "Map c ?????????????? ???? ????????????????????????.");
        assertEquals(2, epicMap.size(), "???????????????? ???????????????????? ????????????.");
        assertEquals(epic1, epicMap.get(epic1.getId()), "?????????? ???? ??????????????????.");
    }

    @Test
    public void getSubTaskTest() {
        SubTask savedSubTask = taskManager.getSubTask(subTask1.getId());

        SubTask wrongSubTask = taskManager.getSubTask(25);

        assertNull(wrongSubTask, "?????????? ???????????? ????????");

        assertNotNull(savedSubTask, "?????????????? ???? ????????????.");
        assertEquals(subTask1, savedSubTask, "???????????????? ???? ??????????????????.");

        HashMap<Integer, SubTask> subTaskMap = taskManager.getSubTaskMap();

        assertNotNull(subTaskMap, "Map c ???????????????????? ???? ????????????????????????.");
        assertEquals(3, subTaskMap.size(), "???????????????? ???????????????????? ??????????????????.");
        assertEquals(subTask1, subTaskMap.get(subTask1.getId()), "???????????????? ???? ??????????????????.");
    }

    @Test
    public void updateTask() {
        taskManager.updateTask(task1);
        HashMap<Integer, Task> taskMap = taskManager.getTaskMap();
        assertEquals(task1, taskMap.get(task1.getId()), "???????????? ???? ??????????????????.");
    }

    @Test
    public void updateEpic() {
        taskManager.updateEpic(epic1);
        HashMap<Integer, Epic> epicMap = taskManager.getEpicMap();
        assertEquals(epic1, epicMap.get(epic1.getId()), "?????????? ???? ??????????????????.");
    }

    @Test
    public void updateSubTask() {
        taskManager.updateSubTask(subTask1);
        HashMap<Integer, SubTask> subTaskMap = taskManager.getSubTaskMap();
        assertEquals(subTask1, subTaskMap.get(subTask1.getId()), "???????????????? ???? ??????????????????.");
    }

    @Test
    public void removeTask() {
        taskManager.removeTask(task1);
        HashMap<Integer, Task> taskMap = taskManager.getTaskMap();
        assertNull(taskMap.get(task1.getId()), "???????????? ???? ??????????????.");
    }

    @Test
    public void removeSubTask() {
        taskManager.removeSubTask(subTask1);
        HashMap<Integer, SubTask> subTaskMap = taskManager.getSubTaskMap();
        assertNull(subTaskMap.get(subTask1.getId()), "?????????????? ???? ????????????.");
    }

    @Test
    public void removeEpic() {
        taskManager.removeEpic(epic1);
        HashMap<Integer, Epic> epicMap = taskManager.getEpicMap();
        assertNull(epicMap.get(epic1.getId()), "???????? ???? ????????????.");
    }

    @Test
    public void deleteTasksEpicsSubTasks() {
        taskManager.deleteTasksEpicsSubTasks();
        HashMap<Integer, Task> taskMap = taskManager.getTaskMap();
        HashMap<Integer, Epic> epicMap = taskManager.getEpicMap();
        HashMap<Integer, SubTask> subTaskMap = taskManager.getSubTaskMap();

        assertEquals(0, taskMap.size(), "???????????? ?????????? ???? ????????????.");
        assertEquals(0, epicMap.size(), "???????????? ???????????? ???? ????????????.");
        assertEquals(0, subTaskMap.size(), "???????????? ?????????????????? ???? ????????????.");
    }
}
