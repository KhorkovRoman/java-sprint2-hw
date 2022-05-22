package Managers;

import HTTP.HTTPTaskManager;

import java.io.IOException;

public abstract class Managers implements TaskManager{

    public static InMemoryTaskManager getDefaultInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager InMemoryHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFileBackedTasksManager() {
        return new FileBackedTasksManager("tasksFile.csv");
    }

    public static HTTPTaskManager getDefaultHTTPTaskManager() throws IOException, InterruptedException {
        return new HTTPTaskManager("http://localhost:8078");
    }
}
