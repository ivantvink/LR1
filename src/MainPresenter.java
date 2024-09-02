import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter {
    private MainView view;
    private File currentDirectory;
    private boolean showHiddenFiles = false;

    public MainPresenter(MainView view) {
        this.view = view;
        // Устанавливаем начальную директорию как диск C:
        this.currentDirectory = new File("C:\\");
    }

    public void setShowHiddenFiles(boolean showHiddenFiles) {
        this.showHiddenFiles = showHiddenFiles;
        updateFileList(); // Обновляем список файлов с новым параметром
    }

    public void updateFileList() {
        List<String> files = new ArrayList<>();
        File[] fileList = currentDirectory.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (showHiddenFiles || !file.isHidden()) {
                    files.add(file.getAbsolutePath());
                }
            }
        }
        view.showFileList(files);
    }

    public void onCreateFile(String filePath) {
        File newFile = new File(currentDirectory, filePath);
        try {
            if (newFile.createNewFile()) {
                view.showSuccess("File created successfully.");
                updateFileList();
            } else {
                view.showError("File already exists.");
            }
        } catch (IOException e) {
            view.showError("Error creating file.");
        }
    }

    public void onDeleteFile(String filePath) {
        File fileToDelete = new File(filePath);
        if (fileToDelete.delete()) {
            view.showSuccess("File deleted successfully.");
            updateFileList();
        } else {
            view.showError("Error deleting file.");
        }
    }

    public void onRenameFile(String oldPath, String newPath) {
        File oldFile = new File(oldPath);
        File newFile = new File(currentDirectory, newPath);
        if (oldFile.renameTo(newFile)) {
            view.showSuccess("File renamed successfully.");
            updateFileList();
        } else {
            view.showError("Error renaming file.");
        }
    }

    public void onOpenDirectory(String directoryPath) {
        File newDirectory = new File(directoryPath);
        if (newDirectory.isDirectory()) {
            currentDirectory = newDirectory;
            updateFileList();
        } else {
            view.showError("Selected path is not a directory.");
        }
    }

    public void onGoUp() {
        File parentDir = currentDirectory.getParentFile();
        if (parentDir != null) {
            currentDirectory = parentDir;
            updateFileList();
        } else {
            view.showError("No parent directory.");
        }
    }

    public void onEditFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile()) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                String newContent = view.showEditDialog(content);
                if (newContent != null) {
                    java.nio.file.Files.write(file.toPath(), newContent.getBytes());
                    view.showSuccess("File edited successfully.");
                }
            } catch (IOException e) {
                view.showError("Error reading or writing file.");
            }
        } else {
            view.showError("Selected file is not a text file.");
        }
    }
}
