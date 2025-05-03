package org.example.view;

import org.example.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {
    
    public MainFrame() {
        // Configura o título da janela
        setTitle("Login do Sistema");
        
        // Define o tamanho inicial
        setSize(800, 600);
        
        // Posiciona no centro da tela
        setLocationRelativeTo(null);
        
        // Define a operação padrão ao fechar (não encerra o aplicativo automaticamente)
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        // Adiciona um listener para o evento de fechamento da janela
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Pede confirmação antes de encerrar o aplicativo
                if (SwingUtils.confirmar(MainFrame.this, "Deseja realmente sair do sistema?")) {
                    // Encerra o aplicativo
                    System.exit(0);
                }
            }
        });
        
        // Define o ícone da aplicação (se existir)
        try {
            setIconImage(new ImageIcon(getClass().getResource("/icons/app_icon.png")).getImage());
        } catch (Exception e) {
            // Se não encontrar o ícone, apenas ignora
        }
        
        // Cria o painel de login como conteúdo inicial
        setContentPane(new LoginPanel(this));
    }
} 