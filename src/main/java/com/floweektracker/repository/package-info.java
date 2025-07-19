/**
 * Repositories package contains the data access layer responsible for connecting to the JSON-based storage system. Each
 * repository provides methods for saving and loading structured data models. JSON serialization and deserialization are
 * responsible for data exchange.
 * <p>Class overview:</p>
 * <ul>
 *     <li><b>LocalTimeAdapter</b> - a {@code GSON} adapter for {@code LocalTime} which is used for JSON serialization</li>
 *     <li><b>PlannerRepository</b> - handles reading and writing planner data from/to a JSON file</li>
 *     <li><b>RepositoryConfigurator</b> - a configurator for all repositories</li>
 *     <li><b>TaskNamesRepository</b> - handles reading and writing the list of task names in a JSON file</li>
 * </ul>
 *
 * @see com.floweektracker.service
 */

package com.floweektracker.repository;