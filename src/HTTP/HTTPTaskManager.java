package HTTP;

import Managers.FileBackedTasksManager;
import java.io.IOException;

public class HTTPTaskManager extends FileBackedTasksManager {

    String url;
    KVTaskClient kvTaskClient;

    public HTTPTaskManager(String url) throws IOException, InterruptedException {
        this.url = url;
        kvTaskClient = new KVTaskClient(url);
    }

    @Override
    public void save() {
        super.save();
    }
}