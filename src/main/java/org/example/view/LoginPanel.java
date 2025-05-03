package org.example.view;

import org.example.controller.LoginController;
import org.example.model.Usuario;
import org.example.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginPanel extends JPanel {
    private final LoginController controller;
    private final JTextField txtEmail;
    private final JPasswordField txtSenha;
    private final JButton btnLogin;
    private final JButton btnCadastrar;
    private final JFrame parentFrame;

    public LoginPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.controller = new LoginController();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JLabel lblTitulo = SwingUtils.criarLabel("Login do Sistema", SwingUtils.FONTE_TITULO);
        txtEmail = SwingUtils.criarTextField(15);
        txtSenha = SwingUtils.criarPasswordField(15);
        btnLogin = SwingUtils.criarBotaoPrimario("Entrar");
        btnCadastrar = SwingUtils.criarBotao("Cadastrar-se");

        SwingUtils.adicionarAcaoEnter(txtEmail, this::realizarLogin);
        SwingUtils.adicionarAcaoEnter(txtSenha, this::realizarLogin);

        btnLogin.addActionListener(e -> realizarLogin());
        btnCadastrar.addActionListener(e -> abrirTelaCadastro());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(lblTitulo);

        JLabel lblEmail = new JLabel("E-mail:");
        JLabel lblSenha = new JLabel("Senha:");

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblSenha, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtSenha, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCadastrar);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        centerPanel.add(Box.createVerticalGlue());

        JPanel formWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formWrapperPanel.add(formPanel);
        centerPanel.add(formWrapperPanel);

        centerPanel.add(buttonPanel);

        centerPanel.add(Box.createVerticalGlue());

        add(titlePanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void realizarLogin() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (email.isEmpty() || senha.isEmpty()) {
            SwingUtils.mostrarErro(this, "Por favor, preencha todos os campos.");
            return;
        }

        if (!SwingUtils.validarEmail(email)) {
            SwingUtils.mostrarErro(this, "Por favor, informe um e-mail válido.");
            txtEmail.requestFocus();
            return;
        }

        Optional<Usuario> usuarioOpt = controller.realizarLoginComEmail(email, senha);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            SwingUtils.mostrarInfo(this, "Bem-vindo, " + usuario.getNome() + "!");

            abrirTelaPrincipal(usuario);
        } else {
            SwingUtils.mostrarErro(this, "E-mail ou senha inválidos.");
            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }
    
    private void abrirTelaCadastro() {
        CadastroUsuarioPanel cadastroPanel = new CadastroUsuarioPanel(parentFrame);
        parentFrame.setContentPane(cadastroPanel);
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    private void abrirTelaPrincipal(Usuario usuario) {
        MainPanel mainPanel = new MainPanel(parentFrame, usuario);
        parentFrame.setContentPane(mainPanel);
        parentFrame.setTitle("Sistema - " + usuario.getNome());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
} 