package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Usuario;
import org.example.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {
    private final UsuarioController controller;
    private final JFrame parentFrame;
    private final Usuario usuarioLogado;
    private JTabbedPane tabbedPane;
    
    // Componentes do painel de usuários - removidos
    // private JTable tblUsuarios;
    // private JButton btnAdicionar;
    // private JButton btnEditar;
    // private JButton btnExcluir;
    // private JButton btnAtualizar;
    // private UsuarioTableModel usuarioTableModel;

    public MainPanel(JFrame parentFrame, Usuario usuarioLogado) {
        this.parentFrame = parentFrame;
        this.usuarioLogado = usuarioLogado;
        this.controller = new UsuarioController();
        
        // Configura o layout
        setLayout(new BorderLayout());
        
        // Cria a barra de menu
        JMenuBar menuBar = criarMenuBar();
        parentFrame.setJMenuBar(menuBar);
        
        // Cria as abas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(SwingUtils.FONTE_PADRAO);
        
        // Adiciona o painel de boas-vindas
        tabbedPane.addTab("Início", criarPainelInicio());
        
        // Remove a aba de usuários
        // tabbedPane.addTab("Usuários", criarPainelUsuarios());
        
        // Adiciona o painel de funcionários
        tabbedPane.addTab("Funcionários", criarPainelFuncionarios());
        
        // Adiciona o painel principal ao frame
        add(tabbedPane, BorderLayout.CENTER);
        
        // Remove carregamento de dados dos usuários
        // atualizarTabelaUsuarios();
    }
    
    private JMenuBar criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Arquivo
        JMenu menuArquivo = new JMenu("Arquivo");
        menuArquivo.setFont(SwingUtils.FONTE_PADRAO);
        
        JMenuItem itemMeuPerfil = new JMenuItem("Meu Perfil");
        itemMeuPerfil.setFont(SwingUtils.FONTE_PADRAO);
        itemMeuPerfil.addActionListener(e -> abrirMeuPerfil());
        
        JMenuItem itemLogout = new JMenuItem("Logout");
        itemLogout.setFont(SwingUtils.FONTE_PADRAO);
        itemLogout.addActionListener(e -> realizarLogout());
        
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.setFont(SwingUtils.FONTE_PADRAO);
        itemSair.addActionListener(e -> sair());
        
        menuArquivo.add(itemMeuPerfil);
        menuArquivo.add(itemLogout);
        menuArquivo.addSeparator();
        menuArquivo.add(itemSair);
        
        // Menu Cadastros
        JMenu menuCadastros = new JMenu("Cadastros");
        menuCadastros.setFont(SwingUtils.FONTE_PADRAO);
        
        JMenuItem itemFuncionarios = new JMenuItem("Funcionários");
        itemFuncionarios.setFont(SwingUtils.FONTE_PADRAO);
        itemFuncionarios.addActionListener(e -> abrirCadastroFuncionarios());
        
        menuCadastros.add(itemFuncionarios);
        
        // Menu Ajuda
        JMenu menuAjuda = new JMenu("Ajuda");
        menuAjuda.setFont(SwingUtils.FONTE_PADRAO);
        
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.setFont(SwingUtils.FONTE_PADRAO);
        itemSobre.addActionListener(e -> exibirSobre());
        
        menuAjuda.add(itemSobre);
        
        // Adiciona os menus à barra
        menuBar.add(menuArquivo);
        menuBar.add(menuCadastros);
        menuBar.add(menuAjuda);
        
        return menuBar;
    }
    
    private JPanel criarPainelInicio() {
        JPanel panel = SwingUtils.criarPainel(15, 15, 15, 15);
        
        JLabel lblBemVindo = SwingUtils.criarLabel("Bem-vindo ao Sistema, " + usuarioLogado.getNome() + "!", SwingUtils.FONTE_TITULO);
        
        // Reduzindo o espaçamento entre os campos (de 5 para 2)
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        
        // Criando os campos com um layout mais compacto
        JLabel lblNome = SwingUtils.criarLabel("Nome: " + usuarioLogado.getNome());
        JLabel lblEmail = SwingUtils.criarLabel("E-mail: " + usuarioLogado.getEmail());
        
        // Ajustando margens para evitar espaço excessivo
        lblNome.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 5));
        lblEmail.setBorder(BorderFactory.createEmptyBorder(0, 5, 2, 5));
        
        infoPanel.add(lblNome);
        infoPanel.add(lblEmail);
        
        // Centralizando melhor os componentes
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        
        panel.add(lblBemVindo, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void abrirMeuPerfil() {
        // Abre o diálogo de edição do perfil do usuário logado
        controller.buscarUsuarioPorId(usuarioLogado.getId()).ifPresent(usuario -> {
            // Como o perfil pode ter sido atualizado no banco, usamos a versão mais recente
            EditarPerfilDialog dialog = new EditarPerfilDialog(parentFrame, usuario);
            dialog.setVisible(true);
            
            // Se o perfil foi atualizado, atualiza o usuário logado e a tela de boas-vindas
            if (dialog.isPerfilAtualizado()) {
                controller.buscarUsuarioPorId(usuarioLogado.getId()).ifPresent(usuarioAtualizado -> {
                    // Atualiza os dados do usuário logado
                    usuarioLogado.setNome(usuarioAtualizado.getNome());
                    usuarioLogado.setEmail(usuarioAtualizado.getEmail());
                    
                    // Atualiza a tela
                    tabbedPane.setComponentAt(0, criarPainelInicio());
                    parentFrame.setTitle("Sistema - " + usuarioLogado.getNome());
                    // Removido: atualizarTabelaUsuarios();
                });
            }
        });
    }
    
    private void realizarLogout() {
        // Pede confirmação antes de sair
        if (SwingUtils.confirmar(this, "Deseja realmente sair?")) {
            // Remove a barra de menus
            parentFrame.setJMenuBar(null);
            
            // Volta para a tela de login
            LoginPanel loginPanel = new LoginPanel(parentFrame);
            parentFrame.setContentPane(loginPanel);
            parentFrame.setTitle("Login do Sistema");
            parentFrame.revalidate();
            parentFrame.repaint();
        }
    }
    
    private void exibirSobre() {
        JOptionPane.showMessageDialog(this,
                "Sistema de Gerenciamento de Funcionários\n" +
                "Versão 1.0\n" +
                "© 2023 - Todos os direitos reservados",
                "Sobre o Sistema",
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void sair() {
        // Pede confirmação antes de encerrar o aplicativo
        if (SwingUtils.confirmar(this, "Deseja realmente encerrar o aplicativo?")) {
            System.exit(0);
        }
    }

    /**
     * Cria o painel de cadastro de funcionários
     */
    private JPanel criarPainelFuncionarios() {
        return new FuncionarioPanel();
    }
    
    /**
     * Abre a aba de cadastro de funcionários
     */
    private void abrirCadastroFuncionarios() {
        // Seleciona a aba de funcionários no tabbedPane
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals("Funcionários")) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }
} 