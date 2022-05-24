
import Managers.HTTPTaskManager;
import HTTP.HttpTaskServer;
import HTTP.KVServer;

import java.io.IOException;

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