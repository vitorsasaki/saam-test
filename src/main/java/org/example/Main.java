package org.example;

import org.example.view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Configura o Look and Feel para parecer com o sistema operacional
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Inicia a janela principal
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}