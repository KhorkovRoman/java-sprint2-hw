package Utils;

import TaskStructure.Task;

public class Node<T> {
    public Node<Task> prevNode;
    public Task task;
    public Node<Task> nextNode;

    public Node(Node<Task> prevNode, Task task, Node<Task> nextNode) {
        this.prevNode = prevNode;
        this.task = task;
        this.nextNode = nextNode;
    }

    public Task getTask() {
        return task;
    }
}
