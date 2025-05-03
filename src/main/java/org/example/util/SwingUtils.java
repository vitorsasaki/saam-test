package org.example.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Classe utilitária para criação e configuração de componentes Swing
 */
public class SwingUtils {
    
    // Cores padrão
    public static final Color COR_PRIMARIA = new Color(0, 120, 215);
    public static final Color COR_SECUNDARIA = new Color(230, 230, 230);
    public static final Color COR_TEXTO = new Color(51, 51, 51);
    public static final Color COR_ERRO = new Color(220, 53, 69);
    public static final Color COR_SUCESSO = new Color(40, 167, 69);
    public static final Color COR_AVISO = new Color(255, 193, 7);
    
    // Fonte padrão
    public static final Font FONTE_PADRAO = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 16);
    
    /**
     * Cria um painel com layout BorderLayout e margens
     */
    public static JPanel criarPainel(int top, int left, int bottom, int right) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(new EmptyBorder(top, left, bottom, right));
        return painel;
    }
    
    /**
     * Cria um painel com o layout especificado e margens
     */
    public static JPanel criarPainel(LayoutManager layout, int top, int left, int bottom, int right) {
        JPanel painel = new JPanel(layout);
        painel.setBorder(new EmptyBorder(top, left, bottom, right));
        return painel;
    }
    
    /**
     * Cria um label com fonte e cor padrão
     */
    public static JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FONTE_PADRAO);
        label.setForeground(COR_TEXTO);
        return label;
    }
    
    /**
     * Cria um label com fonte personalizada
     */
    public static JLabel criarLabel(String texto, Font fonte) {
        JLabel label = criarLabel(texto);
        label.setFont(fonte);
        return label;
    }
    
    /**
     * Cria um campo de texto com fonte padrão
     */
    public static JTextField criarTextField(int colunas) {
        JTextField textField = new JTextField(colunas);
        textField.setFont(FONTE_PADRAO);
        return textField;
    }
    
    /**
     * Cria um campo de senha com fonte padrão
     */
    public static JPasswordField criarPasswordField(int colunas) {
        JPasswordField passwordField = new JPasswordField(colunas);
        passwordField.setFont(FONTE_PADRAO);
        return passwordField;
    }
    
    /**
     * Cria um botão com estilo padrão
     */
    public static JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(FONTE_PADRAO);
        botao.setFocusPainted(false);
        return botao;
    }
    
    /**
     * Cria um botão com estilo primário (destacado)
     */
    public static JButton criarBotaoPrimario(String texto) {
        JButton botao = criarBotao(texto);
        botao.setBackground(COR_PRIMARIA);
        botao.setForeground(Color.WHITE);
        return botao;
    }
    
    /**
     * Adiciona um listener para quando o usuário pressionar Enter em um campo de texto
     */
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
    
    /**
     * Centraliza um JFrame na tela
     */
    public static void centralizarJanela(JFrame janela) {
        janela.setLocationRelativeTo(null);
    }
    
    /**
     * Exibe uma mensagem de erro
     */
    public static void mostrarErro(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Exibe uma mensagem de informação
     */
    public static void mostrarInfo(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Exibe uma mensagem de alerta
     */
    public static void mostrarAlerta(Component pai, String mensagem) {
        JOptionPane.showMessageDialog(pai, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Pede confirmação ao usuário
     */
    public static boolean confirmar(Component pai, String mensagem) {
        int resposta = JOptionPane.showConfirmDialog(
                pai, mensagem, "Confirmação", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return resposta == JOptionPane.YES_OPTION;
    }
    
    /**
     * Configura uma tabela com aparência melhorada
     */
    public static void configurarTabela(JTable tabela) {
        tabela.setRowHeight(25);
        tabela.setFont(FONTE_PADRAO);
        tabela.getTableHeader().setFont(new Font(FONTE_PADRAO.getName(), Font.BOLD, FONTE_PADRAO.getSize()));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);
    }
    
    /**
     * Cria um campo de formulário com label e componente
     */
    public static JPanel criarCampoFormulario(String labelText, JComponent componente) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.add(criarLabel(labelText), BorderLayout.NORTH);
        panel.add(componente, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Valida se um campo de texto não está vazio
     */
    public static boolean validarCampoObrigatorio(JTextComponent campo, String nomeCampo, Component pai) {
        if (campo.getText().trim().isEmpty()) {
            mostrarErro(pai, "O campo " + nomeCampo + " é obrigatório.");
            campo.requestFocus();
            return false;
        }
        return true;
    }
    
    /**
     * Valida um email (verificação simples)
     */
    public static boolean validarEmail(String email) {
        return email != null && email.matches(".+@.+\\..+");
    }
} 