// BookDialog.java - диалог для добавления/редактирования книги
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class BookDialog extends JDialog {
    private JTextField titleField;
    private JTextField authorField;
    private JComboBox<String> genreCombo;
    private JTextField dateField;
    private JTextField pagesField;
    private JSlider ratingSlider;
    private JTextArea descriptionArea;
    private JLabel filePathLabel;

    private Book book;
    private boolean confirmed = false;
    private String selectedFilePath;

    public BookDialog(Frame parent, String title, Book existingBook) {
        super(parent, title, true);
        this.book = existingBook;
        initComponents();
        if (existingBook != null) {
            populateFields(existingBook);
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель с полями ввода
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Название
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Название*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);

        // Автор
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Автор*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        authorField = new JTextField(20);
        formPanel.add(authorField, gbc);

        // Жанр
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Жанр*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        genreCombo = new JComboBox<>();
        genreCombo.setEditable(true);
        formPanel.add(genreCombo, gbc);

        // Дата публикации
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(new JLabel("Дата (дд.мм.гггг):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        dateField = new JTextField(10);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        formPanel.add(dateField, gbc);

        // Количество страниц
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(new JLabel("Страниц:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        pagesField = new JTextField(10);
        formPanel.add(pagesField, gbc);

        // Рейтинг
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        formPanel.add(new JLabel("Рейтинг:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel ratingPanel = new JPanel(new BorderLayout(5, 0));
        ratingSlider = new JSlider(0, 50, 30);
        ratingSlider.setMajorTickSpacing(10);
        ratingSlider.setMinorTickSpacing(5);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingPanel.add(ratingSlider, BorderLayout.CENTER);
        JLabel ratingValue = new JLabel("3.0");
        ratingSlider.addChangeListener(e ->
                ratingValue.setText(String.format("%.1f", ratingSlider.getValue() / 10.0)));
        ratingPanel.add(ratingValue, BorderLayout.EAST);
        formPanel.add(ratingPanel, gbc);

        // Файл книги
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        formPanel.add(new JLabel("Файл книги:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        JButton selectFileButton = new JButton("Выбрать...");
        selectFileButton.addActionListener(e -> selectFile());
        filePanel.add(selectFileButton, BorderLayout.WEST);
        filePathLabel = new JLabel("Файл не выбран");
        filePanel.add(filePathLabel, BorderLayout.CENTER);
        formPanel.add(filePanel, gbc);

        // Описание
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0;
        formPanel.add(new JLabel("Описание:"), gbc);
        gbc.gridx = 1; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("Сохранить");
        okButton.addActionListener(e -> saveBook());
        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Настройка клавиш
        getRootPane().setDefaultButton(okButton);
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите файл книги");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "PDF файлы (*.pdf)", "pdf"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "EPUB файлы (*.epub)", "epub"));
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Текстовые файлы (*.txt)", "txt"));
        fileChooser.setAcceptAllFileFilterUsed(true);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
            filePathLabel.setText(fileChooser.getSelectedFile().getName());
        }
    }

    private void populateFields(Book book) {
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        genreCombo.setSelectedItem(book.getGenre());
        dateField.setText(book.getFormattedDate());
        pagesField.setText(String.valueOf(book.getPages()));
        ratingSlider.setValue((int)(book.getRating() * 10));
        descriptionArea.setText(book.getDescription());
        selectedFilePath = book.getFilePath();
        filePathLabel.setText(new java.io.File(book.getFilePath()).getName());
    }

    private void saveBook() {
        // Валидация
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите название книги", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authorField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите автора книги", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (genreCombo.getSelectedItem() == null || genreCombo.getSelectedItem().toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Выберите жанр книги", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Парсинг даты
        LocalDate publicationDate;
        try {
            publicationDate = LocalDate.parse(dateField.getText(),
                    DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Введите дату в формате дд.мм.гггг", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Парсинг количества страниц
        int pages;
        try {
            pages = pagesField.getText().isEmpty() ? 0 : Integer.parseInt(pagesField.getText());
            if (pages < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Введите корректное количество страниц", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Создание или обновление книги
        book = new Book(
                titleField.getText().trim(),
                authorField.getText().trim(),
                genreCombo.getSelectedItem().toString().trim(),
                publicationDate,
                selectedFilePath != null ? selectedFilePath : "",
                pages,
                ratingSlider.getValue() / 10.0,
                descriptionArea.getText().trim()
        );

        confirmed = true;
        dispose();
    }

    public Book getBook() {
        return book;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setGenres(List<String> genres) {
        genreCombo.removeAllItems();
        for (String genre : genres) {
            genreCombo.addItem(genre);
        }
    }
}