/**
 * Service package contains the business logic of the application responsible for managing user data and application
 * state. Each service class provides CRUD operations. Services act as a bridge between controllers, views and
 * repositories. Controllers call the service methods, services work with the data shown in the views and repositories
 * provide access to the database.
 * <p></p>
 * <p>Class overview:</p>
 * <ul>
 *     <li><b>CleanerService</b> - handles logic for {@link com.floweektracker.view.CleanerView}; implemented as a singleton</li>
 *     <li><b>PlannerService</b> - handles logic for the {@link com.floweektracker.view.PlannerView}; implemented as a singleton</li>
 *     <li><b>TaskNamesService</b> - manages the list of task names and connects to the database via {@link com.floweektracker.repository.TaskNamesRepository}; implemented as a singleton</li>
 *     <li><b>TasksService</b> - manages the tasks using a {@code Map} structure and connects to the database via {@link com.floweektracker.repository.PlannerRepository}; implemented as a singleton</li>
 *     <li><b>WeekdayPlannerService</b> - handles logic for the {@link com.floweektracker.view.WeekdayPlannerView}; implemented as a singleton</li>
 * </ul>
 *
 * @see com.floweektracker.controller
 * @see com.floweektracker.view
 * @see com.floweektracker.repository
 */
package com.floweektracker.service;