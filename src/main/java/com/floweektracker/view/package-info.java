/**
 * View package contains the UI components of the application responsible for displaying and interacting with the user.
 * All components are Swing-based. This package should not contain any business logic or persistent-related code.
 * Components depend on controllers to handle logic and are updated by the service layer.
 * <p></p>
 * <p>Class overview:</p>
 * <ul>
 *     <li><b>CleanerView</b> - a dialog allowing the user to select the days of the week for a cleaning operation, e.g. delete all tasks or reset points; implemented as a singleton</li>
 *     <li><b>InfoView</b> - a {@code JPanel} displaying information about using the application</li>
 *     <li><b>MainPanelView</b> - a {@code JPanel} representing the main view of the application after launching; implemented as a singleton</li>
 *     <li><b>PlannerView</b> - a {@code JTable} representing a weekly planner; implemented as a singleton</li>
 *     <li><b>TaskAddingDialogView</b> - a dialog used to add a new task</li>
 *     <li><b>TaskDialogView</b> - a builder for dialogs used to add or edit a task</li>
 *     <li><b>TaskEditingDialogView</b> - a dialog used to edit an existing task</li>
 *     <li><b>TaskNamesDialogView</b> - a dialog used to add or remove task names; implemented as a singleton</li>
 *     <li><b>WeekdayPlannerView</b> - a {@code JPanel} displaying all tasks for a given day with their details</li>
 * </ul>
 *
 * @see com.floweektracker.controller
 * @see com.floweektracker.service
 */
package com.floweektracker.view;