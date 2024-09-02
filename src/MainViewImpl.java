import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;

public class MainViewImpl extends JFrame implements MainView {
    private JList<String> fileList;
    private JButton createButton;
    private JButton deleteButton;
    private JButton renameButton;
    private JButton editButton;
    private JButton toggleHiddenButton;
    private boolean showHiddenFiles = false;

    private MainPresenter presenter;
    private JPopupMenu contextMenu;

    public MainViewImpl() {
        presenter = new MainPresenter(this);
        initUI();
        presenter.updateFileList(); // Загрузка начальной директории (диск C:)
    }

    private void initUI() {
        // Устанавливаем общую цветовую схему
        Color backgroundColor = new Color(30,30,40);
        Color foregroundColor = Color.WHITE;
        Color buttonColor = new Color(50, 50, 70);

        setLayout(new BorderLayout());

        fileList = new JList<>();
        fileList.setCellRenderer(new FileListCellRenderer());
        fileList.setBackground(backgroundColor);
        fileList.setForeground(foregroundColor);
        fileList.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.getViewport().setBackground(backgroundColor);

        createButton = new JButton("Create File");
        deleteButton = new JButton("Delete File");
        renameButton = new JButton("Rename File");
        editButton = new JButton("Edit File");
        toggleHiddenButton = new JButton("Toggle Hidden Files");

        JButton[] buttons = {createButton, deleteButton, renameButton, editButton, toggleHiddenButton};

        for (JButton button : buttons) {
            button.setBackground(buttonColor);
            button.setForeground(foregroundColor);
            button.setFont(new Font("Arial", Font.BOLD, 14));
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 5));
        buttonPanel.setBackground(backgroundColor); // Устанавливаем цвет панели кнопок
        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(renameButton);
        buttonPanel.add(editButton);
        buttonPanel.add(toggleHiddenButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().setBackground(backgroundColor); // Устанавливаем цвет фона главного окна
        setTitle("File Manager");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Создаем контекстное меню
        contextMenu = new JPopupMenu();
        JMenuItem upMenuItem = new JMenuItem("Up");
        contextMenu.add(upMenuItem);

        // Обработчики событий для кнопок
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = JOptionPane.showInputDialog("Enter the name for the new file:");
                if (filePath != null && !filePath.trim().isEmpty()) {
                    presenter.onCreateFile(filePath);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile != null) {
                    presenter.onDeleteFile(selectedFile);
                } else {
                    showError("Please select a file to delete.");
                }
            }
        });

        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile != null) {
                    String newFilePath = JOptionPane.showInputDialog("Enter the new name for the file:");
                    if (newFilePath != null && !newFilePath.trim().isEmpty()) {
                        presenter.onRenameFile(selectedFile, newFilePath);
                    }
                } else {
                    showError("Please select a file to rename.");
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedFile = fileList.getSelectedValue();
                if (selectedFile != null) {
                    presenter.onEditFile(selectedFile);
                } else {
                    showError("Please select a text file to edit.");
                }
            }
        });

        // Обработчик кнопки для переключения скрытых файлов
        toggleHiddenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHiddenFiles = !showHiddenFiles; // Переключаем состояние
                presenter.setShowHiddenFiles(showHiddenFiles);
            }
        });

        // Добавляем обработчик двойного щелчка
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedFile = fileList.getSelectedValue();
                    if (selectedFile != null) {
                        File file = new File(selectedFile);
                        if (file.isDirectory()) {
                            presenter.onOpenDirectory(selectedFile);
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = fileList.locationToIndex(e.getPoint());
                    fileList.setSelectedIndex(index);
                    if (index >= 0) {
                        contextMenu.show(fileList, e.getX(), e.getY());
                    }
                }
            }
        });

        // Обработка события выбора пункта "Up" из контекстного меню
        upMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presenter.onGoUp();
            }
        });
    }

    @Override
    public void showFileList(List<String> files) {
        fileList.setListData(files.toArray(new String[0]));
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public String showEditDialog(String content) {
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setText(content);
        int option = JOptionPane.showConfirmDialog(null, new JScrollPane(textArea), "Edit File", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            return textArea.getText();
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainViewImpl mainView = new MainViewImpl();
            mainView.setVisible(true);
        });
    }

    private static class FileListCellRenderer extends DefaultListCellRenderer {
        private final Icon folderIcon;
        private final Icon fileIcon;

        public FileListCellRenderer() {
            folderIcon = UIManager.getIcon("FileView.directoryIcon");
            fileIcon = UIManager.getIcon("FileView.fileIcon");
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            String fileName = value.toString();
            File file = new File(fileName);

            if (file.isDirectory()) {
                label.setIcon(folderIcon);
            } else {
                label.setIcon(fileIcon);
            }

            label.setBackground(isSelected ? new Color(50,50,70) : new Color(30,30,40));
            label.setForeground(Color.WHITE);

            return label;
        }
    }
}
