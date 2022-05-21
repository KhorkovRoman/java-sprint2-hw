package Managers;

import TaskStructure.Task;
import TaskStructure.Node;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    public Map<Integer, Node<Task>> getHistoryMap() {
        return historyMap;
    }

    private Node<Task> firstNode;
    private Node<Task> lastNode;

    private void addNode(Task task) {

        if (historyMap.containsKey(task.getId())) {
            Node<Task> node = historyMap.get(task.getId());
            removeNode(node);
            historyMap.remove(task.getId());
        }

        Node<Task> oldLastNode = lastNode;
        Node<Task> newNode = new Node<>(oldLastNode, task, null);
        lastNode = newNode;
        if (oldLastNode == null) {
            firstNode = newNode;
        } else {
            oldLastNode.nextNode = newNode;
        }

        historyMap.put(task.getId(), newNode);
    }

    private void removeNode(Node<Task> node) {

        final Node<Task> nextNode = node.nextNode;
        final Node<Task> prevNode = node.prevNode;

        if (prevNode == null) {
            firstNode = nextNode;
        } else {
            prevNode.nextNode = nextNode;
            node.prevNode = null;
        }

        if (nextNode == null) {
            lastNode = prevNode;
        } else {
            nextNode.prevNode = prevNode;
            node.nextNode = null;
        }

        node.task = null;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        Node<Task> node = firstNode;
        while (node != null) {
            historyList.add(node.task);
            node = node.nextNode;
        }
        return historyList;
    }

    @Override
    public void addToHistory(Task task) {
        addNode(task);
    }

    @Override
    public void removeFromHistory(int id) {
        if (historyMap.containsKey(id)) {
            Node<Task> node = historyMap.get(id);
            removeNode(node);
            historyMap.remove(id);
        }
    }

    @Override
    public void clearHistory() {
        for (Integer i: historyMap.keySet()) {
            Node<Task> node = historyMap.get(i);
            removeNode(node);
        }
        historyMap.clear();
    }
}
