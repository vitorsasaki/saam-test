package org.example.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class SwingUtils {

    public static final Color COR_PRIMARIA = new Color(0, 120, 215);
    public static final Color COR_SECUNDARIA = new Color(230, 230, 230);
    public static final Color COR_TEXTO = new Color(51, 51, 51);
    public static final Color COR_ERRO = new Color(220, 53, 69);
    public static final Color COR_SUCESSO = new Color(40, 167, 69);
    public static final Color COR_AVISO = new Color(255, 193, 7);

    public static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);

    public static JPanel criarPainel(int top, int left, int bottom, int right) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(new EmptyBorder(top, left, bottom, right));
        return painel;
    }

    public static JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_PADRAO);
        label.setForeground(COR_TEXTO);
        return label;
    }

    public static JLabel criarLabel(String texto, Font fonte) {
        JLabel label = criarLabel(texto);
        label.setFont(fonte);
        return label;
    }

    public static JTextField criarTextField(int colunas) {
        JTextField textField = new JTextField(colunas);
        textField.setFont(FONTE_PADRAO);
        return textField;
    }

    public static JPasswordField criarPasswordField(int colunas) {
        JPasswordField passwordField = new JPasswordField(colunas);
        passwordField.setFont(FONTE_PADRAO);
        return passwordField;
    }

    public static JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_PADRAO);
        botao.setFocusPainted(false);
        return botao;
    }

    public static JButton criarBotaoPrimario(String texto) {
        JButton botao = criarBotao(texto);
        botao.setBackground(COR_PRIMARIA);
        botao.setForeground(Color.BLACK);
        return botao;
    }

    public static void adicionarAcaoEnter(JTextComponent textComponent, Runnable acao) {
        textComponent.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    acao.run();
                }
            }
        });
    }

    public static void mostrarErro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarInfo(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component pai, String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(
                pai, mensagem, "Confirmação", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resposta == JOptionPane.YES_OPTION;
    }

    public static JPanel criarCampoFormulario(String labelText, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.add(criarLabel(labelText), BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }

    public static boolean validarEmail(String email) {
        return email != null && email.matches(".+@.+\\..+");
    }
} 