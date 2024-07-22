<img src="pictures-readme/app-pictures/logo.png" alt="logo" style="width: 100%">

FloWeekTracker is a Windows application designed in Polish to organize weekly tasks and improve productivity.

## Table of contents 📜

* [Main features](#main-features)
* [Built with](#built-with)
* [Installation and running](#installation-and-running)
    * [For Developers](#for-developers-)
        * [Requirements](#requirements)
        * [Installation](#installation)
        * [Running](#running)
    * [For end users](#for-end-users-windows)
* [Code example/issues](#code-exampleissues)
* [Documentation](#documentation)
* [Licenses](#licenses)
  * [For FloWeek Tracker](#for-floweek-tracker)
  * [For Libraries](#for-used-libraries)
* [Sources](#sources-ℹ-)
* [Contact](#contact)

## Main features✅

### Database📂

<img src="pictures-readme/app-pictures/database.png" width="400" alt="database">
<details><summary>Click to get more information</summary>

Contains and manages task names to prevent users from entering the same task names. Users can check the task names
list, and also:

<details><summary><b>add task name</b></summary>

![database-addTaskName](pictures-readme/features/database-addTaskName.gif)
</details>
<details><summary><b>remove task name</b></summary>

![database-removeTaskName](pictures-readme/features/database-removeTaskName.gif)
</details>

</details>

### Planner 📆

<img src="pictures-readme/app-pictures/planner-main.png" width="400" alt="planner">
<details><summary>Click to get more information</summary>

Allows creating a personal weekly calendar where each task includes a description, time, weekday, and points. Users can:

<details><summary><b>add task</b></summary>

![planner-addTask.gif](pictures-readme/features/planner-addTask.gif)
</details>
<details><summary><b>edit task</b></summary>

![planner-editTask.gif](pictures-readme/features/planner-editTask.gif)
</details>
<details><summary><b>remove task</b></summary>

![planner-removeTask](pictures-readme/features/planner-removeTask.gif)
</details>
<details><summary><b>check the daily schedule</b></summary>

![planner-showSchedule](pictures-readme/features/planner-showSchedule.gif)
</details>
<details><summary><b>mark task as done</b></summary>

![planer-markAsDone](pictures-readme/features/planner-markAsDone.gif)
</details>
<details><summary><b>delete multiple tasks</b></summary>

![planner-clearCalendar](pictures-readme/features/planner-clearCalendar.gif)
</details>
<details><summary><b>reset points</b></summary>

![planner-resetPoints](pictures-readme/features/planner-resetPoints.gif)
</details>

Users can also check the total points earned for each day as well as the achieved points.
</details>

## Built with🔧

<p>
<img src="pictures-readme/logos/Java.png" width="80" alt="Java" style="margin-right: 10px">
<img src="pictures-readme/logos/Maven.png" width="80" alt="Maven" style="margin-right: 10px">
<img src="pictures-readme/logos/Gson.png" width="80" alt="Gson">
</p>

## Installation and running⚙️

### For developers 💻

#### Requirements❗

<details><summary>Click to get information</summary>

* <b>Java 22 or newer</b>: to compile and runt the project<br><br>
* <b>Gson 2.10.1 or newer</b>: to convert Java objects to JSON and vice versa<br><br>
* <b>Apache Maven 3.9.6 or newer</b>: to manage projects and built the application<br><br>
* <b>Maven plugins</b>:
    * <u>maven-assembly-plugin 3.6.0</u>: to create app packages with dependencies.
    * <u>maven-compiler-plugin 3.11.0</u>: to compile source code
    * <u>maven-javadoc-plugin 3.6.2</u>: to generate documentation for the project

Make sure that you've got Java 22 and Apache Maven 3.9.6.

You can check your Java version in terminal:

```bash
java -version
```

You can also check your Maven version in terminal:

```bash
mvn -v
```

If you have got an older version, you can download a newer one from:

* <a href="https://www.oracle.com/java/technologies/downloads/">Oracle website</a> (for Java)
* <a href="https://maven.apache.org/download.cgi">Apache Maven website</a> (for Maven)

</details>

#### Installation⚙️

<details><summary>Click to get information</summary>

Clone the repository

```bash
git clone https://github.com/Marika9725/FloWeek-Tracker.git
```

Go to the app folder

```bash
cd FloWeekTracker
```

Build the project

```bash
mvn clean install
```

</details>

#### Running▶️

<details><summary>Click to get information</summary>

```bash
java -jar target/FloWeekTracker-1.0-jar-with-dependencies.jar
```

</details>

### For end users (Windows)🪟

<details><summary>Click to get information</summary>

1. Download FloWeekTracker.exe from the <a href="https://github.com/Marika9725/FloWeek-Tracker/releases">
   releases</a>.<br><br>
2. After downloading the file, run it and follow the displayed instructions to install the application on your
   desktop.<br><br>
3. After installing an application, double-click on the app icon.<br><br>
4. Enjoy using the application.

</details>

## Code Example/Issues🔎

If you have any issues, please let me know in the <a href="https://github.com/Marika9725/FloWeek-Tracker/issues">issues
section</a> or directly at <a href="mailto:m.szewczyk2@o2.pl">m.szewczyk2@o2.pl</a>

## Documentation📑

Full documentation of the FloWeek Tracker is
available <a href="https://marika9725.github.io/FloWeek-Tracker/FloWeekTracker/module-summary.html">here</a>. It
contains detailed information about the constructed packages, classes, methods and fields.

## Licenses🔱

For complete license terms, see the [LICENSE](LICENSE.md) file.

### For FloWeek Tracker🔱

This software is licensed under a custom license with the following terms:

1. **Usage:** For personal use and recruitment purposes only. Commercial use is prohibited.
2. **Modification:** No modifications are allowed. The software must remain unchanged.
3. **Redistribution:** Redistribution, sale, or offering of the software is not permitted.
4. **Attribution:** Proper credit must be given to the original author.
5. **Privacy:** Data is stored locally on your device. Compliance with data protection laws is your responsibility.
6. **Dependencies:** Includes libraries licensed under GNU GPL-2.0 and Apache License 2.0. These licenses apply to the
   libraries but not to my own code.
7. **Contact:** For issues, contact <a href="mailto:m.szewczyk2@o2.pl">email</a>
   or <a href="www.linkedin.com/in/maria-szewczyk/">LinkedIn</a>
   8**Disclaimer:** Provided "as if" without warranties. The Licensor is not liable for any damages.

### For used libraries🔱

* **Maven**: Apache 2.0 license
* **Gson**: Apache 2.0 license
* **Java**: GPL 2.0 license

## Sources ℹ️ ️

The application was created to practice the knowledge gained from:

* "Java: A Beginner's Guide. Eighth Edition" by Herbert Schildt,
* <a href="https://www.udemy.com/course/kurs-java-od-podstaw-od-zera-do-mastera-zbuduj-wlasne-aplikacje/learn/lecture/24333546?start=0#overview"> Java course from Udemy, created by Kuba Wąsikowski</a>,
* <a href="https://www.youtube.com/watch?v=tvHVafvw16Y&list=PLj-pbEqbjo6AKsJ8oE2pvIqsb15mxdrxs&ab_channel=Zaprogramuj%C5%BBycie">
  Git course created by Mateusz</a>

## Contact📞

<a href="www.linkedin.com/in/maria-szewczyk/">
<img src="pictures-readme/logos/linkedin-logo.png" width="80" alt="linked-in" style="right-margin: 10px"></a>

<a href="mailto:m.szewczyk2@o2.pl">
<img src="pictures-readme/logos/mail.png" alt="e-mail" width="120"></a>
