package TaskStructure;

import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private HashMap<Integer, SubTask> subTaskList;

    public Epic(Integer id, String name, String description, TaskStatus taskStatus, HashMap<Integer, SubTask> subTaskList) {
        super(id, name, description, taskStatus);
        this.subTaskList = subTaskList;
    }

    public HashMap<Integer, SubTask> getSubTaskList() {
        return subTaskList;
    }

    public void setSubTaskList(HashMap<Integer, SubTask> subTaskList) {
        this.subTaskList = subTaskList;
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
        return "Epic{" +
                "id=" + super.getId() +
//                ", name='" + super.getName() + '\'' +
//                ", description='" + super.getDescription() + '\'' +
//                ", status='" + super.getTaskStatus() + '\'' +
//                ", subTaskList =" + subTaskList.toString() +
                '}';
    }
}
