package org.example.view;

import org.example.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    
    public MainFrame() {
        setTitle("Login do Sistema");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (SwingUtils.confirmar(MainFrame.this, "Deseja realmente sair do sistema?")) {
                    System.exit(0);
                }
            }
        });

        try {
            setIconImage(new ImageIcon(getClass().getResource("/icons/app_icon.png")).getImage());
        } catch (Exception e) {
            // Se não encontrar o ícone, apenas ignora
        }

        setContentPane(new LoginPanel(this));
    }
} 