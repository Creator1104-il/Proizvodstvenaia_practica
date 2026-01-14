// Main.java - точка входа в приложение

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Установка внешнего вида системы
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Запуск приложения в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}