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


    public MainPanel(JFrame parentFrame, Usuario usuarioLogado) {
        this.parentFrame = parentFrame;
        this.usuarioLogado = usuarioLogado;
        this.controller = new UsuarioController();

        setLayout(new BorderLayout());

        JMenuBar menuBar = criarMenuBar();
        parentFrame.setJMenuBar(menuBar);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(SwingUtils.FONTE_PADRAO);

        tabbedPane.addTab("Início", criarPainelInicio());

        tabbedPane.addTab("Funcionários", criarPainelFuncionarios());

        add(tabbedPane, BorderLayout.CENTER);

    }
    
    private JMenuBar criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

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

        JMenu menuCadastros = new JMenu("Cadastros");
        menuCadastros.setFont(SwingUtils.FONTE_PADRAO);
        
        JMenuItem itemFuncionarios = new JMenuItem("Funcionários");
        itemFuncionarios.setFont(SwingUtils.FONTE_PADRAO);
        itemFuncionarios.addActionListener(e -> abrirCadastroFuncionarios());
        
        menuCadastros.add(itemFuncionarios);

        JMenu menuAjuda = new JMenu("Ajuda");
        menuAjuda.setFont(SwingUtils.FONTE_PADRAO);
        
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.setFont(SwingUtils.FONTE_PADRAO);
        itemSobre.addActionListener(e -> exibirSobre());
        
        menuAjuda.add(itemSobre);

        menuBar.add(menuArquivo);
        menuBar.add(menuCadastros);
        menuBar.add(menuAjuda);
        
        return menuBar;
    }
    
    private JPanel criarPainelInicio() {
        JPanel panel = SwingUtils.criarPainel(15, 15, 15, 15);
        
        JLabel lblBemVindo = SwingUtils.criarLabel("Bem-vindo ao Sistema, " + usuarioLogado.getNome() + "!", SwingUtils.FONTE_TITULO);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 2));

        JLabel lblNome = SwingUtils.criarLabel("Nome: " + usuarioLogado.getNome());
        JLabel lblEmail = SwingUtils.criarLabel("E-mail: " + usuarioLogado.getEmail());

        lblNome.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 5));
        lblEmail.setBorder(BorderFactory.createEmptyBorder(0, 5, 2, 5));
        
        infoPanel.add(lblNome);
        infoPanel.add(lblEmail);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        
        panel.add(lblBemVindo, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void abrirMeuPerfil() {
        controller.buscarUsuarioPorId(usuarioLogado.getId()).ifPresent(usuario -> {
            EditarPerfilDialog dialog = new EditarPerfilDialog(parentFrame, usuario);
            dialog.setVisible(true);

            if (dialog.isPerfilAtualizado()) {
                controller.buscarUsuarioPorId(usuarioLogado.getId()).ifPresent(usuarioAtualizado -> {
                    usuarioLogado.setNome(usuarioAtualizado.getNome());
                    usuarioLogado.setEmail(usuarioAtualizado.getEmail());
                    tabbedPane.setComponentAt(0, criarPainelInicio());
                    parentFrame.setTitle("Sistema - " + usuarioLogado.getNome());
                });
            }
        });
    }
    
    private void realizarLogout() {
        if (SwingUtils.confirmar(this, "Deseja realmente sair?")) {
            parentFrame.setJMenuBar(null);
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
        if (SwingUtils.confirmar(this, "Deseja realmente encerrar o aplicativo?")) {
            System.exit(0);
        }
    }

    private JPanel criarPainelFuncionarios() {
        return new FuncionarioPanel();
    }

    private void abrirCadastroFuncionarios() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals("Funcionários")) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }
} 