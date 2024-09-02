import javax.swing.*;
import java.util.List;

public interface MainView {
    void showFileList(List<String> files);
    void showError(String message);
    void showSuccess(String message);

    default String showEditDialog(String content) {
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setText(content);
        int option = JOptionPane.showConfirmDialog(null, new JScrollPane(textArea), "Edit File", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            return textArea.getText();
        }
        return null;
    }
}
