package tasksManagement;

import java.util.Comparator;

public class TasksListComparator implements Comparator<String> {
    @Override
    public int compare(String task1, String task2) {
        return task1.compareToIgnoreCase(task2);
    }
}
