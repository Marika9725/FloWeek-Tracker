/**
 * Controller package contains classes responsible for handling user actions and coordinating communication between
 * views and services. Controllers add listeners to UI components and redirect user input to the appropriate service
 * methods.
 * <p></p>
 * <p>Class overview:</p>
 * <ul>
 *     <li><b>MainPanelController</b> - handles actions in {@link com.floweektracker.view.MainPanelView}</li>
 *     <li><b>PlannerController</b> - handles actions in {@link com.floweektracker.view.PlannerView} and delegates to {@link com.floweektracker.service.PlannerService}</li>
 *     <li><b>TaskAddingDialogController</b> - handles actions in {@link com.floweektracker.view.TaskAddingDialog}</li>
 *     <li><b>TaskEditingDialogController</b> - handles actions in {@link com.floweektracker.view.TaskEditingDialog}</li>
 *     <li><b>TaskNamesController</b> - handles actions in {@link com.floweektracker.view.TaskNamesDialog} and delegates to{@link com.floweektracker.service.TaskNamesService}</li>
 * </ul>
 *
 * @see com.floweektracker.view
 * @see com.floweektracker.service
 */

package com.floweektracker.controller;