import TaskStructure.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new ArrayList<>();

    Map<Integer, Node<Task>> mapHistory = new HashMap<>();

    private Node<Task> firstNode;
    private Node<Task> lastNode;
    private int size = 0;

    public void linkLast(Task task) {

        if (mapHistory.containsKey(task.getId())) {
            Node<Task> node = mapHistory.get(task.getId());
            removeNode(node);
            mapHistory.remove(task.getId());
        }

        Node<Task> oldLastNode = lastNode;
        Node<Task> newNode = new Node<>(oldLastNode, task, null);
        lastNode = newNode;
        if (oldLastNode == null) {
            firstNode = newNode;
        } else {
            oldLastNode.nextNode = newNode;
        }

        size++;

        mapHistory.put(task.getId(), newNode);
    }

    public void removeNode(Node<Task> node) {

        if (node.prevNode == null) {
            firstNode = node.nextNode;
        } else {
            node.prevNode.nextNode = node.nextNode;
            node.prevNode = null;
        }

        if (node.nextNode == null) {
            lastNode = node.prevNode;
        } else {
            node.nextNode.prevNode = node.prevNode;
            node.nextNode = null;
        }

        node.task = null;

        size--;
    }

    public List<Task> getTasks() {
        historyList.clear();
        Node<Task> node = firstNode;
        while (node != null) {
            historyList.add(node.task);
            node = node.nextNode;
        }
        return historyList;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void addHistory(Task task) {

    }

    @Override
    public void removeHistory(int id) {

    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "historyList=" + historyList +
                '}';
    }
}
