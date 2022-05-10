import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.TreeSet;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public void setManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void prioritizedTasksNotEmpty() {
        TreeSet<Task> listTasks = taskManager.getPrioritizedTasks();

        assertNotNull(listTasks, "Список отсортированных задач не создан.");
    }

    @Test
    public void addTaskWithWrongStartTimeMustBeExeption() {
        Task task20 = new Task(20, "Task20", "Task20 description", TaskStatus.NEW,
                LocalDateTime.of(2021, Month.APRIL, 26, 10, 00), 100);
        Task task21 = new Task(21, "Task21", "Task21 description", TaskStatus.NEW,
                LocalDateTime.of(2021, Month.APRIL, 27, 12, 00), 1);

        taskManager.addTask(task20);

        final RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> taskManager.addTask(task21));

        assertEquals("Задача Task{id=21, name='Task21', description='Task21 description', status=NEW, " +
                "start=2021-04-27T12:00, duration=1} пересекается c задачей Task{id=20, name='Task20', " +
                "description='Task20 description', status=NEW, start=2021-04-26T10:00, duration=100}",
                e.getMessage());
    }

    @Test
    public void addSubTaskWithWrongStartTimeMustBeExeption() {
        Epic epic20 = new Epic(20, "Epic20", "Epic20 description", TaskStatus.NEW,
                null, 0);
       SubTask subTask21 = new SubTask(21, "SubTask21", "SubTask21 description", TaskStatus.NEW,
                LocalDateTime.of(2010, Month.APRIL, 27, 12, 00), 100, epic1);
        SubTask subTask22 = new SubTask(22, "SubTask22", "SubTask22 description", TaskStatus.NEW,
                LocalDateTime.of(2010, Month.APRIL, 28, 12, 00), 1, epic1);

        taskManager.addEpic(epic20);
        taskManager.addSubTask(subTask21);

        final RuntimeException e = assertThrows(
                RuntimeException.class,
                () -> taskManager.addSubTask(subTask22));

        assertEquals("Подзадача SubTask{id=22, name='SubTask22', description='SubTask22 description', " +
                        "status='NEW', start='2010-04-28T12:00', duration='1', epic=3} пересекается c задачей " +
                        "SubTask{id=21, name='SubTask21', description='SubTask21 description', status='NEW', " +
                        "start='2010-04-27T12:00', duration='100', epic=3}",
                        e.getMessage());
    }


    @Test
    public void findStatusEpic1MustReturnIn_Progress() {
        TaskStatus status = TaskStatus.IN_PROGRESS;
        TaskStatus savedEpicStatus = epic1.getTaskStatus();

        assertEquals(status, savedEpicStatus, "Статусы не совпадают.");
    }

    @Test
    public void findStatusEpicWithoutSubTasks_MustReturnStatus_NEW() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        assertEquals(TaskStatus.IN_PROGRESS, epic3.getTaskStatus(), "Статусы не совпадают.");
        taskManager.removeSubTask(subTask4);
        assertEquals(TaskStatus.NEW, epic3.getTaskStatus(), "Статусы не совпадают.");
    }

    @Test
    public void findStatusEpicWithSubTasksNEW_MustReturnStatus_NEW() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        SubTask subTask5 = new SubTask(10, "SubTask5", "SubTask5 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00), 1, epic3);
        SubTask subTask6 = new SubTask(11, "SubTask6", "SubTask6 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.MAY, 22, 12, 00), 1, epic3);
        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);

        assertEquals(TaskStatus.NEW, epic3.getTaskStatus(), "Статусы не совпадают.");
    }

    @Test
    public void findStatusEpicWithSubTasksDONE_MustReturnStatus_DONE() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.DONE,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        SubTask subTask5 = new SubTask(10, "SubTask5", "SubTask5 description", TaskStatus.DONE,
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00), 1, epic3);
        SubTask subTask6 = new SubTask(11, "SubTask6", "SubTask6 description", TaskStatus.DONE,
                LocalDateTime.of(2022, Month.MAY, 22, 12, 00), 1, epic3);

        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);

        assertEquals(TaskStatus.DONE, epic3.getTaskStatus(), "Статусы не совпадают.");
    }

    @Test
    public void findStatusEpicWithSubTasksNEW_DONE_MustReturnStatus_IN_PROGRESS() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        SubTask subTask5 = new SubTask(10, "SubTask5", "SubTask5 description", TaskStatus.DONE,
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00), 1, epic3);
        SubTask subTask6 = new SubTask(11, "SubTask6", "SubTask6 description", TaskStatus.DONE,
                LocalDateTime.of(2022, Month.MAY, 22, 12, 00), 1, epic3);

        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);

        assertEquals(TaskStatus.IN_PROGRESS, epic3.getTaskStatus(), "Статусы не совпадают.");
    }

    @Test
    public void findStatusEpicWithSubTasksIN_PROGRESS_MustReturnStatus_IN_PROGRESS() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        SubTask subTask5 = new SubTask(10, "SubTask5", "SubTask5 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00), 1, epic3);
        SubTask subTask6 = new SubTask(11, "SubTask6", "SubTask6 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 22, 12, 00), 1, epic3);

        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);

        assertEquals(TaskStatus.IN_PROGRESS, epic3.getTaskStatus(), "Статусы не совпадают.");
    }

    @Test
    public void findStatusEpicWhenSubTasks4_Delete_MustReturnStatus_NEW() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        SubTask subTask5 = new SubTask(10, "SubTask5", "SubTask5 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00), 1, epic3);
        SubTask subTask6 = new SubTask(11, "SubTask6", "SubTask6 description", TaskStatus.NEW,
                LocalDateTime.of(2022, Month.MAY, 22, 12, 00), 1, epic3);

        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);
        assertEquals(TaskStatus.IN_PROGRESS, epic3.getTaskStatus(), "Статусы не совпадают.");

        taskManager.removeSubTask(subTask4);
        assertEquals(TaskStatus.NEW, epic3.getTaskStatus(), "Статусы не совпадают.");
    }


    @Test
    public void findEpicStartTimeWithSubTasks_Must200520222() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        SubTask subTask5 = new SubTask(10, "SubTask5", "SubTask5 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00), 1, epic3);
        SubTask subTask6 = new SubTask(11, "SubTask6", "SubTask6 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 22, 12, 00), 1, epic3);

        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);

        LocalDateTime startTimeEpic = LocalDateTime.of(2022, Month.MAY, 20, 12, 00);

        assertEquals(startTimeEpic, epic3.getStartTime(), "Время не совпадает.");
    }

    @Test
    public void findEpicDurationWithSubTasks_Must3() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        SubTask subTask5 = new SubTask(10, "SubTask5", "SubTask5 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00), 1, epic3);
        SubTask subTask6 = new SubTask(11, "SubTask6", "SubTask6 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 22, 12, 00), 1, epic3);

        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);

        assertEquals(3, epic3.getDuration(), "Продолжительность не совпадает.");
    }

    @Test
    public void findEpicStartTimeWenDelSubTask4_Must210520222() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        SubTask subTask5 = new SubTask(10, "SubTask5", "SubTask5 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00), 1, epic3);
        SubTask subTask6 = new SubTask(11, "SubTask6", "SubTask6 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 22, 12, 00), 1, epic3);

        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);

        LocalDateTime startTimeEpic = LocalDateTime.of(2022, Month.MAY, 20, 12, 00);
        assertEquals(startTimeEpic, epic3.getStartTime(), "Время не совпадает.");

        taskManager.removeSubTask(subTask4);

        LocalDateTime startTimeEpicAfterRemoveSubTask4 =
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00);
        assertEquals(startTimeEpicAfterRemoveSubTask4, epic3.getStartTime(), "Время не совпадает.");
    }

    @Test
    public void findEpicDurationWenDelSubTask4_Must2() {
        Epic epic3 = new Epic(8, "Epic2", "Epic2 description", TaskStatus.NEW,
                null, 0);
        SubTask subTask4 = new SubTask(9, "SubTask4", "SubTask4 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 20, 12, 00), 1, epic3);
        SubTask subTask5 = new SubTask(10, "SubTask5", "SubTask5 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 21, 12, 00), 1, epic3);
        SubTask subTask6 = new SubTask(11, "SubTask6", "SubTask6 description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2022, Month.MAY, 22, 12, 00), 1, epic3);

        taskManager.addEpic(epic3);
        taskManager.addSubTask(subTask4);
        taskManager.addSubTask(subTask5);
        taskManager.addSubTask(subTask6);

        assertEquals(3, epic3.getDuration(), "Время не совпадает.");

        taskManager.removeSubTask(subTask4);
        assertEquals(2, epic3.getDuration(), "Время не совпадает.");
    }

    @Test
    public void subTaskMustHaveEpic() {
        assertEquals(epic1, subTask1.getEpic(), "Эпики не совпадают.");
    }

    @Test
    public void getHistoryList() {
        List<Task> historyList = taskManager.getHistoryList();

        assertNotNull(historyList, "Список истории не создан.");
        assertEquals(3, historyList.size(), "История сожержит не 3 задачи.");
    }
}













