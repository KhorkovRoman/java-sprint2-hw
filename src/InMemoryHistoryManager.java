import TaskStructure.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    List<Task> historyList = new ArrayList<>();

    @Override
    public void history() {
        int i = 0;
        for (Task task: historyList) {
            i++;
            System.out.println("Запись " + i + " " + task);
        }
    }

    @Override
    public void addHistoryList(Task task) {
        if (historyList.size() < 10) {
            historyList.add(task);
        } else {
            historyList.remove(0);
            historyList.add(task);
        }
    }
}
