package TaskStructure;

import java.util.Objects;

public class Task {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus taskStatus;

    public Task(Integer id, String name, String description, TaskStatus taskStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        return Objects.equals(id, task.id) &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description)
                && Objects.equals(taskStatus, task.taskStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, taskStatus);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }
}
