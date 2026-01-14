// BookTableModel.java - модель данных для таблицы
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class BookTableModel extends AbstractTableModel{

    private final List<Book> books;
    private final String[] columnNames = {
            "Название", "Автор", "Жанр", "Дата публикации", "Страниц", "Рейтинг"
    };

    public BookTableModel(List<Book> books) {
        this.books = books;
    }

    @Override
    public int getRowCount() {
        return books.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Book book = books.get(rowIndex);
        switch (columnIndex) {
            case 0: return book.getTitle();
            case 1: return book.getAuthor();
            case 2: return book.getGenre();
            case 3: return book.getFormattedDate();
            case 4: return book.getPages();
            case 5: return String.format("%.1f", book.getRating());
            default: return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 4: return Integer.class;
            case 5: return String.class;
            default: return String.class;
        }
    }

    public Book getBookAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < books.size()) {
            return books.get(rowIndex);
        }
        return null;
    }

    public void updateData(List<Book> newBooks) {
        books.clear();
        books.addAll(newBooks);
        fireTableDataChanged();
    }
}
