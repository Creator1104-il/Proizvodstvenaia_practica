// MainFrame.java - главное окно приложения
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

public class MainFrame extends JFrame {
    private Library library;
    private BookTableModel tableModel;
    private JTable booksTable;
    private JTextArea detailsArea;
    private JLabel statsLabel;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private TableRowSorter<BookTableModel> sorter;

    public MainFrame() {
        super("Библиотека электронных книг");
        library = new Library();
        initComponents();
        setupMenu();
        setupTable();
        updateStatistics();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // Основная панель с BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Верхняя панель с поиском
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("Поиск:"));
        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterTable(); }
        });
        searchPanel.add(searchField);

        searchTypeCombo = new JComboBox<>(new String[]{"По названию", "По автору", "По жанру"});
        searchPanel.add(searchTypeCombo);

        JButton clearSearchButton = new JButton("Очистить");
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            filterTable();
        });
        searchPanel.add(clearSearchButton);

        topPanel.add(searchPanel, BorderLayout.WEST);

        statsLabel = new JLabel();
        statsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        topPanel.add(statsLabel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Центральная панель с таблицей и деталями
        JSplitPane centerSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        centerSplitPane.setDividerLocation(600);

        // Панель с таблицей
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Книги"));

        tableModel = new BookTableModel(library.getAllBooks());
        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.getSelectionModel().addListSelectionListener(e -> showBookDetails());
        booksTable.setRowHeight(25);

        sorter = new TableRowSorter<>(tableModel);
        booksTable.setRowSorter(sorter);

        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Панель кнопок для таблицы
        JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Добавить книгу");
        addButton.addActionListener(e -> addBook());
        JButton editButton = new JButton("Редактировать");
        editButton.addActionListener(e -> editBook());
        JButton deleteButton = new JButton("Удалить");
        deleteButton.addActionListener(e -> deleteBook());
        JButton openButton = new JButton("Открыть книгу");
        openButton.addActionListener(e -> openBook());

        tableButtonsPanel.add(addButton);
        tableButtonsPanel.add(editButton);
        tableButtonsPanel.add(deleteButton);
        tableButtonsPanel.add(openButton);

        tablePanel.add(tableButtonsPanel, BorderLayout.SOUTH);

        // Панель с деталями
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Детали книги"));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        centerSplitPane.setLeftComponent(tablePanel);
        centerSplitPane.setRightComponent(detailsPanel);

        mainPanel.add(centerSplitPane, BorderLayout.CENTER);

        // Нижняя панель с информацией
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("Всего книг: " + library.getAllBooks().size()));

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void setupMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Меню Файл
        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem importItem = new JMenuItem("Импорт книги...");
        importItem.addActionListener(e -> importBook());
        fileMenu.add(importItem);

        JMenuItem exportItem = new JMenuItem("Экспорт библиотеки...");
        exportItem.addActionListener(e -> exportLibrary());
        fileMenu.add(exportItem);

        fileMenu.addSeparator();

//        JMenuItem exitItem = new JMenuItem("Выход");
//        exitItem.addActionListener(e -> System.exit(0));
//        fileMenu.add(exitItem);

        // Меню Книги
        JMenu bookMenu = new JMenu("Книги");
        bookMenu.setMnemonic(KeyEvent.VK_B);

        JMenuItem addBookItem = new JMenuItem("Добавить книгу");
        addBookItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        addBookItem.addActionListener(e -> addBook());
        bookMenu.add(addBookItem);

        JMenuItem editBookItem = new JMenuItem("Редактировать книгу");
        editBookItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        editBookItem.addActionListener(e -> editBook());
        bookMenu.add(editBookItem);

        JMenuItem deleteBookItem = new JMenuItem("Удалить книгу");
        deleteBookItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        deleteBookItem.addActionListener(e -> deleteBook());
        bookMenu.add(deleteBookItem);

        bookMenu.addSeparator();

        JMenuItem openBookItem = new JMenuItem("Открыть книгу");
        openBookItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openBookItem.addActionListener(e -> openBook());
        bookMenu.add(openBookItem);

        // Меню Вид
        JMenu viewMenu = new JMenu("Вид");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        JMenuItem refreshItem = new JMenuItem("Обновить");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        refreshItem.addActionListener(e -> refreshLibrary());
        viewMenu.add(refreshItem);

        JMenuItem statsItem = new JMenuItem("Статистика");
        statsItem.addActionListener(e -> showStatistics());
        viewMenu.add(statsItem);

        // Меню Помощь
        JMenu helpMenu = new JMenu("Помощь");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAbout());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(bookMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void setupTable() {
        // Настройка колонок
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        booksTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        booksTable.getColumnModel().getColumn(5).setPreferredWidth(80);
    }

    private void filterTable() {
        String searchText = searchField.getText().trim();
        String searchType = (String) searchTypeCombo.getSelectedItem();

        if (searchText.isEmpty()) {
            tableModel.updateData(library.getAllBooks());
            return;
        }

        List<Book> filteredBooks;
        switch (searchType) {
            case "По названию":
                filteredBooks = library.searchByTitle(searchText);
                break;
            case "По автору":
                filteredBooks = library.searchByAuthor(searchText);
                break;
            case "По жанру":
                filteredBooks = library.searchByGenre(searchText);
                break;
            default:
                filteredBooks = library.getAllBooks();
        }

        tableModel.updateData(filteredBooks);
    }

    private void showBookDetails() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = booksTable.convertRowIndexToModel(selectedRow);
            Book book = tableModel.getBookAt(modelRow);
            if (book != null) {
                detailsArea.setText(String.format(
                        "Название: %s\n" +
                                "Автор: %s\n" +
                                "Жанр: %s\n" +
                                "Дата публикации: %s\n" +
                                "Количество страниц: %d\n" +
                                "Рейтинг: %.1f/5.0\n\n" +
                                "Описание:\n%s\n\n" +
                                "Файл: %s",
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getFormattedDate(),
                        book.getPages(),
                        book.getRating(),
                        book.getDescription(),
                        new File(book.getFilePath()).getName()
                ));
            }
        }
    }

    private void addBook() {
        BookDialog dialog = new BookDialog(this, "Добавить книгу", null);
        dialog.setGenres(library.getAllGenres());
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            library.addBook(dialog.getBook());
            tableModel.updateData(library.getAllBooks());
            updateStatistics();
            JOptionPane.showMessageDialog(this, "Книга успешно добавлена", "Успех", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = booksTable.convertRowIndexToModel(selectedRow);
            Book book = tableModel.getBookAt(modelRow);

            BookDialog dialog = new BookDialog(this, "Редактировать книгу", book);
            dialog.setGenres(library.getAllGenres());
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                library.updateBook(modelRow, dialog.getBook());
                tableModel.updateData(library.getAllBooks());
                updateStatistics();
                JOptionPane.showMessageDialog(this, "Книга успешно обновлена", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите книгу для редактирования", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = booksTable.convertRowIndexToModel(selectedRow);
            Book book = tableModel.getBookAt(modelRow);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите удалить книгу:\n\"" + book.getTitle() + "\"?",
                    "Подтверждение удаления",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                library.removeBook(modelRow);
                tableModel.updateData(library.getAllBooks());
                updateStatistics();
                detailsArea.setText("");
                JOptionPane.showMessageDialog(this, "Книга успешно удалена", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите книгу для удаления", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = booksTable.convertRowIndexToModel(selectedRow);
            Book book = tableModel.getBookAt(modelRow);

            File bookFile = new File(book.getFilePath());
            if (bookFile.exists()) {
                try {
                    Desktop.getDesktop().open(bookFile);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Не удалось открыть файл: " + e.getMessage(),
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Файл книги не найден по указанному пути",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Выберите книгу для открытия",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importBook() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Импорт книги");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Здесь можно добавить логику парсинга метаданных из файла
            BookDialog dialog = new BookDialog(this, "Импорт книги", null);
            dialog.setGenres(library.getAllGenres());
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                library.addBook(dialog.getBook());
                tableModel.updateData(library.getAllBooks());
                updateStatistics();
            }
        }
    }

    private void exportLibrary() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Экспорт библиотеки");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            // Здесь можно добавить логику экспорта
            JOptionPane.showMessageDialog(this,
                    "Библиотека будет экспортирована в: " + selectedDir.getAbsolutePath(),
                    "Экспорт",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshLibrary() {
        tableModel.updateData(library.getAllBooks());
        updateStatistics();
    }

    private void updateStatistics() {
        statsLabel.setText(library.getStatistics());
    }

    private void showStatistics() {
        JOptionPane.showMessageDialog(this,
                library.getStatistics().replace("<html>", "").replace("</html>", ""),
                "Статистика библиотеки",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "Библиотека электронных книг v1.0\n\n" +
                        "Приложение для управления коллекцией\n" +
                        "электронных книг на языке Java.\n\n" +
                        "© 2026 Все права защищены",
                "О программе",
                JOptionPane.INFORMATION_MESSAGE);
    }
}