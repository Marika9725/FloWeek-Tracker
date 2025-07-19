package com.floweektracker.util;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Locale;

/**
 * Represents the manager of the application's directory, where necessary files are stored.
 * <br><br>
 * Fields: {@link #instance}, {@link #dataFolder}, {@link #folderChooser}
 * <br><br>
 * Methods: {@link #createDirectory()}, {@link #askUserAboutCreatingFolder()}, {@link #createDefaultDirectory()},
 * {@link #createUserDirectory()}, {@link #createFolderChooser()}
 */
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppDirectoryManager {
    /**
     * A singleton instance of the {@link AppDirectoryManager} class.
     *
     * @see com.floweektracker.Main
     */
    @Getter
    private static final AppDirectoryManager instance = new AppDirectoryManager();
    /**
     * Contains a reference to the application's directory, where necessary data files are stored. It can be modified by
     * the {@link #createUserDirectory()} method.
     *
     * @see com.floweektracker.repository.PlannerRepository
     * @see com.floweektracker.repository.TaskNamesRepository
     */
    @Getter
    private File dataFolder = new File(System.getProperty("user.dir"), "data");
    private JFileChooser folderChooser = createFolderChooser();

    /**
     * Creates the application's directory, where necessary data files are stored. Checks if the directory for data
     * exists. If it doesn't, asks the user about creating directory. When user clicks YES_OPTION, creates the directory
     * in default place. When user clicks NO_OPTION, creates the directory in user's place. When user clicks
     * CANCEL_OPTION, the application is terminated.
     *
     * @return a data directory for the application's data
     * @see com.floweektracker.Main
     * @see #askUserAboutCreatingFolder()
     * @see #createDefaultDirectory()
     * @see #createUserDirectory()
     */
    public File createDirectory() {
        while (!dataFolder.exists()) {
            switch (askUserAboutCreatingFolder()) {
                case JOptionPane.YES_OPTION -> createDefaultDirectory();
                case JOptionPane.NO_OPTION -> createUserDirectory();
                case JOptionPane.CANCEL_OPTION -> System.exit(0);
            }
        }

        return dataFolder;
    }

    /**
     * Asks the user about creating the directory in the default localization by showing a confirm dialog.
     *
     * @return a value compatible with the user's choice
     * @see #createDirectory()
     */
    private int askUserAboutCreatingFolder() {
        var title = "Tworzenie folderu FloWeekTracker";
        var message = """
                <html>
                Program chce utworzyć folder <b>data</b>, w którym będą przechowywane pliki niezbędne do prawidłowego działania aplikacji.
                <br><br>Czy wyrażasz zgodę na utworzenie folderu w domyślnej lokalizacji: <u>%s</u>?
                <br><b>Wybierz \\"Tak\\"</b>, jeśli wyrażasz zgodę.
                <br><b>Wybierz \\"Nie\\"</b>, jeśli chcesz wybrać inną lokalizację lub wczytać już istniejący folder.
                <br><b>Wybierz \\"Anuluj\\"</b>, aby przerwać działanie programu bez tworzenia lub wybierania folderu.
                </html>
                """.formatted(dataFolder.getParent());

        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_CANCEL_OPTION);
    }

    /**
     * Creates the {@link #dataFolder}. When the directory can't be created, it shows a message dialog to the user.
     *
     * @see #createDirectory()
     * @see #createUserDirectory()
     */
    private void createDefaultDirectory() {
        try {
            Files.createDirectory(dataFolder.toPath());
        } catch (IOException e) {
            DialogUtils.showMessageDialog(
                    "Błąd tworzenia folderu",
                    "Błąd w trakcie tworzenia folderu we wskazanej lokalizacji.\n" +
                            "Proszę o wskazanie nowej lokalizacji dla folderu FloWeekTracker lub wybranie już istniejącego."
            );
        }
    }

    /**
     * Creates a directory in the user's place. It calls {@link #createFolderChooser()} method, and then asks the user
     * to choose a directory. If APPROVE_OPTION is clicked, the directory is created, otherwise do nothing.
     *
     * @see #createFolderChooser()
     * @see #createDefaultDirectory()
     * @see #createDirectory()
     */
    private void createUserDirectory() {
        if (folderChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;

        var selectedPath = folderChooser.getSelectedFile();
        var isCorrectPath = !(selectedPath.toString().endsWith("data") || selectedPath.getParent().endsWith("data"));

        if (isCorrectPath) {
            dataFolder = new File(selectedPath + "/data");
            createDefaultDirectory();
        }
    }

    /**
     * @return a folder chooser, where user can choose only a directory
     * @see #createUserDirectory()
     */
    @NotNull
    private JFileChooser createFolderChooser() {
        var folderChooser = new JFileChooser();
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderChooser.setLocale(Locale.of("pl", "PL"));

        return folderChooser;
    }
}