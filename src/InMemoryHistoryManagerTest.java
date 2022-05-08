import TaskStructure.Task;
import TaskStructure.TaskStatus;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManagerTest {

    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

    Map<Integer, Node<Task>> historyMap = historyManager.getHistoryMap();

    Task task1 = new Task(1,"Task1", "Task1 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 20, 10, 00), 1);
    Task task2 = new Task(2,"Task2", "Task2 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 21, 10, 00), 1);
    Task task3 = new Task(3,"Task3", "Task3 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 22, 10, 00), 1);
    Task task4 = new Task(4,"Task4", "Task4 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 23, 10, 00), 1);
    Task task5 = new Task(5,"Task5", "Task5 description", TaskStatus.NEW,
            LocalDateTime.of(2022, Month.APRIL, 24, 10, 00), 1);

    @BeforeEach
    void addToHistory5TasksWithDuplicationMustBe5TasksInMap() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.addToHistory(task4);
        historyManager.addToHistory(task5);

        //duplication
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);
        historyManager.addToHistory(task4);
        historyManager.addToHistory(task5);

        assertNotNull(historyMap, "История пустая.");
        assertEquals(5, historyMap.size(), "История сожержит не 5 задач.");
    }

    @Test
    void getHistoryMustBe5TasksInList() {
        List<Task> historyList = historyManager.getHistory();
        assertNotNull(historyList, "Список истории не создан.");
        assertEquals(5, historyList.size(), "История сожержит не 5 задач.");
    }

    @Test
    void removeFromHistoryFistTask() {
        Task task = task1;
        historyManager.removeFromHistory(task.getId());
        assertEquals(4, historyMap.size(), "История сожержит не 4 задачи.");
    }

    @Test
    void removeFromHistoryMiddleTask() {
        Task task = task3;
        historyManager.removeFromHistory(task.getId());
        assertEquals(4, historyMap.size(), "История сожержит не 4 задачи.");
    }

    @Test
    void removeFromHistoryLastTask() {
        Task task = task5;
        historyManager.removeFromHistory(task.getId());
        assertEquals(4, historyMap.size(), "История сожержит не 4 задачи.");
    }

    @Test
    void clearHistoryMustBeZeroTasksInMap() {
        historyManager.clearHistory();
        assertNotNull(historyMap, "Список истории не создан.");
        assertEquals(0, historyMap.size(), "История не пустая.");
    }
}
