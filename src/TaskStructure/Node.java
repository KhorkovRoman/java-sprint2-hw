package TaskStructure;

public class Node<Task> {
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
