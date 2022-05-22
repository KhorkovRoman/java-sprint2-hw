
import HTTP.HTTPTaskManager;
import HTTP.HttpTaskServer;
import HTTP.KVServer;
import HTTP.KVTaskClient;
import Managers.Managers;
import TaskStructure.Epic;
import TaskStructure.SubTask;
import TaskStructure.Task;
import TaskStructure.TaskStatus;
import TestMenu.TestHistory;
import TestMenu.TestMenu;
import TestMenu.TestFileBacked;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        new KVServer().start();

        new HttpTaskServer(new HTTPTaskManager("http://localhost:8078")).createHTTPServer();

        //Managers.getDefaultHTTPTaskManager();

//        try {
//            HttpTaskServer httpTaskServer = new HttpTaskServer();
//            httpTaskServer.createHTTPServer();
//        } catch (IOException exception) {
//            System.out.println("Ошибка создания сервера.");
//        }

//        TestHistory testHistory = new TestHistory();
//        testHistory.testHistory();

        ////or

//        TestMenu testMenu = new TestMenu();
//        testMenu.testMenu();

        ////or

//        TestFileBacked testFileBacked = new TestFileBacked();
//        testFileBacked.testFileBacked();


        ////or

//        public static void main(String[] args) throws IOException {
//            new KVServer().start();
    }

}