package TaskStructure;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private final HashMap<Integer, SubTask> subTaskList;

    public Epic(Integer id, String name, String description, TaskStatus taskStatus,
                LocalDateTime startTime, int duration) {
        super(id, name, description, taskStatus, startTime, duration);
        this.subTaskList = new HashMap<>();
    }

    public HashMap<Integer, SubTask> getSubTaskList() {
        return subTaskList;
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    @Override
    public int getDuration() {
        return super.getDuration();
    }

    @Override
    public LocalDateTime getEndTime() {
        return super.getEndTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Epic epic = (Epic) o;
        return Objects.equals(subTaskList, epic.subTaskList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTaskList);
    }

    @Override
    public String toString() {
        return  "Epic{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getTaskStatus() + '\'' +
                ", start='" + super.getStartTime() + '\'' +
                ", duration='" + super.getDuration() + '\'' +
                '}';
    }
}
