import TaskStructure.Task;

import java.util.List;

public interface HistoryManager {

    List<Task> getHistory();

    void addHistoryList(Task task);

}
