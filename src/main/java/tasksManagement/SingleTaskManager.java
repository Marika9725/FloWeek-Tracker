package tasksManagement;

import calendarManagement.WeekDays;

public class SingleTaskManager {
    private String taskName;
    private boolean isDone;
    private WeekDays weekDay;
    private byte priority;
    private String description;

    public SingleTaskManager() {
        this.taskName = "unknown";
        this.isDone = false;
        this.weekDay = WeekDays.MONDAY;
        this.priority = 1;
        this.description = "unknown";
    }

    public String getTaskName() {return this.taskName;}
    public boolean getIsDone() {return this.isDone;}
    public WeekDays getWeekDay() {return weekDay;}
    public byte getPriority() {return priority;}
    public String getDescription() {return description;}

    public void setTaskName(String taskName) {this.taskName = taskName;}
    public void setDone(boolean done) {this.isDone = done;}
    public void setWeekDay(WeekDays weekDay) {this.weekDay = weekDay;}
    public void setPriority(byte priority) {this.priority = priority;}
    public void setDescription(String description) {this.description = description;}

    @Override
    public String toString() {
        return "taskName='" + taskName + '\'' +
                ", isDone=" + isDone +
                ", weekDay=" + weekDay +
                ", priority=" + priority +
                ", describe='" + description + '\'';
    }
}
