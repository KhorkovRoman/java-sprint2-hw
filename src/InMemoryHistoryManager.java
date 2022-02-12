import TaskStructure.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> historyList = new ArrayList<>();

    public List<Task> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<Task> historyList) {
        this.historyList = historyList;
    }

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void addHistoryList(Task task) {
        if (historyList.size() >= 10) {
            historyList.remove(0);
        }
        historyList.add(task);
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "historyList=" + historyList +
                '}';
    }
}
