/**
 * <p>This module delivers packages needed to create a FloWeekTracker application.</p>
 * <br>
 * <p><b>FloWeekTracker description</b></p>
 * <p>FloWeekTracker is an application in Polish designed to improve weekly productivity. This application has two parts:</p>
 * <ul>
 *     <li><u>Database</u>: Contains and manages task names to prevent the user from entering the same task names.
 *     Users can:
 *     <ul>
 *         <li>add or remove task name in the database,</li>
 *         <li>check the task list</li>
 *        </ul>
 *    </li>
 *    <li><u>Planner</u>: Allows creating a personal weekly calendar, where each task includes a description,
 *        time, weekday, and points. Users can:
 *        <ul>
 *            <li>add, edit and remove a task in the planner,</li>
 *            <li>check the daily plan for each day of the week,</li>
 *            <li>mark task as done,</li>
 *            <li>check the total points earned for each day as well as the achieved points.</li>
 *        </ul>
 *    </li>
 * </ul>
 * <p>FloWeekTracker is designed to help users organize their weekly tasks efficiently, offering tools to plan, track,
 * and evaluate their productivity.</p>
 * <br>
 * <p><b>System requirements</b></p>
 * <p>For detailed system requirements, please refer to the
 * <a href="https://github.com/Marika9725/FloWeek-Tracker">READ.ME</a> file.</p>
 * <br>
 * <p><b>Installation and running</b></p>
 * <p>For installation instructions and how to run the application, please refer to the
 * <a href="https://github.com/Marika9725/FloWeek-Tracker">READ.ME</a> file.</p>
 * <br>
 * <p><b>License</b></p>
 * <p>This software is freeware. You are allowed to use it for personal use only, and redistribution is prohibited. For
 * detailed license, pleas refer to the <a href="https://github.com/Marika9725/FloWeek-Tracker">READ.ME</a> file.</p>
 * <br>
 * <p><b>Contact and support</b><br>
 * For support, please contact the author:</p>
 * <ul>
 *     <li><b>m.szewczyk2@o2.pl</b></li>
 *     <li><b><a href="https://github.com/Marika9725">GitHub</a></b></li>
 *     <li><b><a href="https://www.linkedin.com/in/maria-szewczyk/">LinkedIn</a></b></li>
 * </ul>
 * <br><br>
 * @author Maria Szewczyk
 * @since 2024
  */
module FloWeekTracker {
    requires com.google.gson;
    requires java.datatransfer;
    requires java.desktop;

    opens com.floweektracker.tasksDatabase to com.google.gson;
    opens com.floweektracker.planner to com.google.gson;
}