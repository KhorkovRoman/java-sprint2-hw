package TaskStructure;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
//    private Epic epic;
//
//    public SubTask(Integer id,
//                   String name,
//                   String description,
//                   TaskStatus taskStatus,
//                   LocalDateTime startTime,
//                   int duration,
//                   Epic epic) {
//        super(id, name, description, taskStatus, startTime, duration);
//        this.epic = epic;
//    }

    private Integer epicId;

    public SubTask(Integer id,
                   String name,
                   String description,
                   TaskStatus taskStatus,
                   LocalDateTime startTime,
                   int duration,
                   Integer epicId) {
        super(id, name, description, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
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
        SubTask subTask = (SubTask) o;
        return Objects.equals(epicId, subTask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return  "SubTask{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getTaskStatus() + '\'' +
                ", start='" + super.getStartTime() + '\'' +
                ", duration='" + super.getDuration() + '\'' +
                ", epic=" + epicId +
                '}';
    }
}
