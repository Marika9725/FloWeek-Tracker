package com.floweektracker.service;

import com.floweektracker.MainFrame;
import com.floweektracker.controller.MainPanelController;
import com.floweektracker.model.SingleTask;
import com.floweektracker.util.DialogUtils;
import com.floweektracker.view.WeekdayPlannerView;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.util.List;
import java.util.*;
import java.util.stream.*;

/**
 * Singleton service for the {@link WeekdayPlannerView}es. It is used to manage tasks in the weekday planner view.
 * <br><br>
 * FIELDS: {@link #service}
 * <br><br>
 * CRUD METHODS: {@link #addTask(SingleTask)}, {@link #deleteTask(SingleTask)},
 * {@link #editTask(SingleTask, SingleTask)}
 * <br><br>
 * GETTER METHODS: {@link #getWeekdayPlannerView(String)},
 * <br><br>
 * OTHER METHODS: {@link #findIndexForNewTask(List, LocalTime)}, {@link #extractTimeFromTaskPanel(Component)},
 * {@link #isTaskInWeekdayPlanner(SingleTask)}, {@link #isTaskInWeekdayPlanner(WeekdayPlannerView, SingleTask)},
 * {@link #isComponentNameSame(JPanel, SingleTask)}, {@link #isComponentHasSameData(JPanel, SingleTask)}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeekdayPlannerService {
    @Getter
    private static final WeekdayPlannerService service = new WeekdayPlannerService();

    //region addTask() methods

    /**
     * Adds a given task to the compatible weekday planner view. Checks if the given task is null - if it is, it returns
     * false. Then it creates a new task panel based on the given task name and then adds it to the weekday view's
     * content panel. If there is any task, it localizes suitable index for the new task panel in content panel and
     * inserts it at the correct position.
     *
     * @param task a given task which should be added to the appropriate weekday planner view
     * @return true if the given task is successfully added, otherwise false
     * @see #getWeekdayPlannerView(String)
     * @see #isTaskInWeekdayPlanner(WeekdayPlannerView, SingleTask)
     * @see com.floweektracker.controller.TaskAddingDialogController#addTask(SingleTask)
     * @see #editTask(SingleTask, SingleTask)
     */
    public boolean addTask(SingleTask task) {
        if (task == null || isTaskInWeekdayPlanner(task)) return false;
        var plannerView = getWeekdayPlannerView(task.getWeekday().toString());

        var newTaskPanel = plannerView.createTaskPanel(task);
        var contentPanel = plannerView.getContentPanel();
        var taskPanels = Arrays.stream(contentPanel.getComponents()).toList();

        if (taskPanels.isEmpty()) contentPanel.add(newTaskPanel);
        else {
            var newTime = task.getTime();
            contentPanel.add(newTaskPanel, findIndexForNewTask(taskPanels, newTime));
        }

        return isTaskInWeekdayPlanner(plannerView, task);
    }

    /**
     * Finds a suitable index in a given {@code taskPanels} for a given {@code newTime}. If any of the given parameters
     * is null, it returns 0.
     *
     * @param taskPanels a given list of task panels
     * @param newTime    a given time which should be inserted in the {@code taskPanels} at the correct position
     * @return an index where the new task panel should be inserted.
     * @see #extractTimeFromTaskPanel(Component)
     * @see #addTask(SingleTask)
     */
    private int findIndexForNewTask(List<Component> taskPanels, LocalTime newTime) {
        if (taskPanels == null || newTime == null) return 0;
        return IntStream.range(0, taskPanels.size())
                .filter(i -> extractTimeFromTaskPanel(taskPanels.get(i)).isAfter(newTime))
                .findFirst()
                .orElse(taskPanels.size());
    }

    /**
     * @param component a given component whose name contains the time that should be extracted
     * @return a time which is extracted from the {@code component}'s name
     * @see #findIndexForNewTask(List, LocalTime)
     */
    @NotNull
    private LocalTime extractTimeFromTaskPanel(@NotNull Component component) {
        return LocalTime.parse(component.getName().substring(0, 5));
    }
    //endregion

    /**
     * Deletes a given task in the compatible weekday planner view. Checks if the given task is null or is not in the
     * view - if it is, it returns false. Then it get content panel with task panels from the weekday planner view,
     * finds the given {@code task} and removes it.
     *
     * @param task a given task which should be removes from the appropriate weekday planner view
     * @return true if the given task is successfully remove, otherwise false
     * @see #isTaskInWeekdayPlanner(WeekdayPlannerView, SingleTask)
     * @see #getWeekdayPlannerView(String)
     * @see #isComponentNameSame(JPanel, SingleTask)
     * @see MainPanelController#deleteTask()
     * @see MainPanelController#deleteTasksForWeekdays(List)
     * @see #editTask(SingleTask, SingleTask)
     */
    public boolean deleteTask(SingleTask task) {
        if (task == null || !isTaskInWeekdayPlanner(task)) return false;
        var plannerView = getWeekdayPlannerView(task.getWeekday().toString());

        var contentPanel = plannerView.getContentPanel();

        Arrays.stream(contentPanel.getComponents())
                .map(JPanel.class::cast)
                .filter(panel -> isComponentNameSame(panel, task))
                .findFirst().ifPresent(contentPanel::remove);

        return !isTaskInWeekdayPlanner(plannerView, task);
    }

    /**
     * Edits a given task in the compatible weekday planner view. Checks if a given {@code task} and {@code editedTask}
     * are null, {@code task} is not in the view or {@code editedTask} is in the view - if it is, it returns false. Then
     * it deletes the {@code task} and adds the {@code editedTask} to the weekday planner view. If it fails, it
     * rollbacks the changes.
     *
     * @param task       a given task which should be deleted from the appropriate weekday planner view
     * @param editedTask a given task which should be added to the appropriate weekday planner view
     * @return true if the given task is successfully edited, otherwise false
     * @see #isTaskInWeekdayPlanner(SingleTask)
     * @see #deleteTask(SingleTask)
     * @see #addTask(SingleTask)
     * @see MainPanelController#resetPoints(List)
     * @see com.floweektracker.controller.TaskEditingDialogController#editTask(SingleTask, SingleTask)
     */
    public boolean editTask(SingleTask task, SingleTask editedTask) {
        if ((task == null) || (editedTask == null) || task.equals(editedTask)) return false;

        if (!deleteTask(task)) return false;
        if (!addTask(editedTask)) DialogUtils.rollback(task, this::addTask, this::isTaskInWeekdayPlanner);

        return !isTaskInWeekdayPlanner(task) && isTaskInWeekdayPlanner(editedTask);
    }

    /**
     * @param weekdayName a given weekday name which should be used to find the weekday planner view in the
     *                    {@link MainFrame#cardPanel}
     * @return a weekday planner view with the given {@code weekdayName} or null
     * @see #addTask(SingleTask)
     * @see #deleteTask(SingleTask)
     * @see #isTaskInWeekdayPlanner(SingleTask)
     */
    public WeekdayPlannerView getWeekdayPlannerView(String weekdayName) {
        return Arrays.stream(MainFrame.getMAIN_FRAME().getCardPanel().getComponents())
                .filter(weekdayPanel -> weekdayPanel.getName().contains(weekdayName))
                .map(WeekdayPlannerView.class::cast)
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if a given {@code task} is in a given {@code view}. If any of the given parameters is null, it returns
     * false. Then it gets task panels from the {@code view}'s content panel and check if any of them has the same name
     * and data as the given {@code task}.
     *
     * @param view a given weekday planner view whose task panels from its content panel should be checked
     * @param task a given task name which should be found in the {@code view}
     * @return true if the given {@code task} is in the given {@code view}, otherwise false
     * @see #isComponentNameSame(JPanel, SingleTask)
     * @see #isComponentHasSameData(JPanel, SingleTask)
     * @see #addTask(SingleTask)
     * @see #deleteTask(SingleTask)
     * @see #isTaskInWeekdayPlanner(SingleTask)
     */
    public boolean isTaskInWeekdayPlanner(WeekdayPlannerView view, SingleTask task) {
        if (view == null || task == null) return false;

        return Arrays.stream(view.getContentPanel().getComponents())
                .map(JPanel.class::cast)
                .filter(taskPanel -> isComponentNameSame(taskPanel, task))
                .anyMatch(taskPanel -> isComponentHasSameData(taskPanel, task));
    }

    /**
     * Checks if a given {@code task} is in a weekday planner view. If the given task is null, it returns false. Then it
     * gets a weekday planner view based on the weekday's name of the {@code task} and then calls the
     * {@link #isTaskInWeekdayPlanner(WeekdayPlannerView, SingleTask)} method.
     *
     * @param task a given task which should be checked
     * @return true if the given {@code task} is in a weekday planner view, otherwise false
     * @see com.floweektracker.controller.TaskAddingDialogController#isTaskAdded(SingleTask)
     * @see com.floweektracker.controller.TaskEditingDialogController#isTaskEdited(SingleTask, SingleTask)
     * @see #deleteTask(SingleTask)
     * @see #editTask(SingleTask, SingleTask)
     */
    public boolean isTaskInWeekdayPlanner(SingleTask task) {
        if (task == null) return false;
        var weekdayPlannerView = getWeekdayPlannerView(task.getWeekday().toString());

        return isTaskInWeekdayPlanner(weekdayPlannerView, task);
    }

    //region helpful methods

    /**
     * Checks if a {@code component}'s name have the {@code baseTask}'s time and task name. If any of the given
     * parameters is null, it returns false.
     *
     * @param component a given component which name should be checked
     * @param baseTask  a given task which time and name should be compared
     * @return true if the given {@code component}'s name have the {@code baseTask}'s time and task name, otherwise
     * false
     * @see #deleteTask(SingleTask)
     * @see #isTaskInWeekdayPlanner(WeekdayPlannerView, SingleTask)
     * @see #isComponentHasSameData(JPanel, SingleTask)
     */
    private boolean isComponentNameSame(@NotNull JPanel component, @NotNull SingleTask baseTask) {
        return component.getName().equals("%s_%s".formatted(baseTask.getTime(), baseTask.getTaskName()));
    }

    /**
     * Check if the given {@code taskPanel} has same information as a given {@code task}. It creates expected data based
     * on the given {@code task} and compares them with the given {@code taskPanel}'s data.
     *
     * @param taskPanel a given task panel which data should be same as the given {@code task}
     * @param task      a given task which data should be same as the given {@code taskPanel}
     * @return true if the given {@code taskPanel} has same information as the given {@code task}, otherwise false
     * @see #isComponentNameSame(JPanel, SingleTask)
     * @see #isTaskInWeekdayPlanner(WeekdayPlannerView, SingleTask)
     */
    private boolean isComponentHasSameData(@NotNull JPanel taskPanel, @NotNull SingleTask task) {
        var expectedTime = task.getTime().toString();
        var expectedTaskName = "%s(%d/%d)".formatted(task.getTaskName(), task.calculatePoints(), task.getPriority());
        var expectedDescription = task.getDescription() == null ? "" : task.getDescription();

        var labels = Arrays.stream(taskPanel.getComponents())
                .filter(JLabel.class::isInstance)
                .map(JLabel.class::cast)
                .collect(Collectors.toMap(JLabel::getName, JLabel::getText, (existing, _) -> existing));

        var description = Arrays.stream(taskPanel.getComponents())
                .filter(JScrollPane.class::isInstance)
                .map(JScrollPane.class::cast)
                .map(descriptionPane -> ((JTextArea) descriptionPane.getViewport().getView()).getText())
                .findFirst()
                .orElse("");

        var isTimeLabelSame = labels.get("timeLabel").equals(expectedTime);
        var isTaskNameLabelSame = labels.get("taskNameLabel").equals(expectedTaskName);
        var isDescriptionSame = description.equals(expectedDescription);

        return isComponentNameSame(taskPanel, task) && isTimeLabelSame && isTaskNameLabelSame && isDescriptionSame;
    }
    //endregion
}