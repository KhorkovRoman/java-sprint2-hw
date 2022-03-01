import TaskStructure.Task;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> historyList = new ArrayList<>();

    private final Map<Integer, Node<Task>> mapHistory = new HashMap<>();

    private Node<Task> firstNode;
    private Node<Task> lastNode;

    private void linkLast(Task task) {

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

        mapHistory.put(task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {

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
        linkLast(task);
    }

    @Override
    public void removeHistory(int id) {
        if (mapHistory.containsKey(id)) {
            Node<Task> node = mapHistory.get(id);
            removeNode(node);
            mapHistory.remove(id);
        }
    }

    public void clearHistory() {
        for (Integer i: mapHistory.keySet()) {
            Node<Task> node = mapHistory.get(i);
            removeNode(node);
        }
        mapHistory.clear();
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "historyList=" + historyList +
                '}';
    }
}
