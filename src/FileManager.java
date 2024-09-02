import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    public void createFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            Files.createFile(Paths.get(path));
        } else {
            throw new IOException("File already exists.");
        }
    }

    public void deleteFile(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            Files.delete(Paths.get(path));
        } else {
            throw new IOException("File not found.");
        }
    }

    public void renameFile(String oldPath, String newPath) throws IOException {
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        if (oldFile.exists()) {
            if (!newFile.exists()) {
                Files.move(Paths.get(oldPath), Paths.get(newPath));
            } else {
                throw new IOException("New file name already exists.");
            }
        } else {
            throw new IOException("File not found.");
        }
    }

    public List<String> listFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            String[] files = directory.list();
            if (files != null) {
                return Arrays.asList(files);
            }
        }
        return Arrays.asList();
    }

    public String readFile(String path) throws IOException {
        StringBuilder content = new StringBuilder();
        try (FileReader reader = new FileReader(path)) {
            int c;
            while ((c = reader.read()) != -1) {
                content.append((char) c);
            }
        }
        return content.toString();
    }

    public void writeFile(String path, String content) throws IOException {
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(content);
        }
    }
}
