package org.example.view;

import org.example.controller.LoginController;
import org.example.model.Usuario;
import org.example.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class CadastroUsuarioPanel extends JPanel {
    private final LoginController controller;
    private final JTextField txtNome;
    private final JTextField txtEmail;
    private final JPasswordField txtSenha;
    private final JPasswordField txtConfirmarSenha;
    private final JButton btnCadastrar;
    private final JButton btnVoltar;
    private final JFrame parentFrame;

    public CadastroUsuarioPanel(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.controller = new LoginController();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JLabel lblTitulo = SwingUtils.criarLabel("Cadastro de Usuário", SwingUtils.FONTE_TITULO);
        txtNome = SwingUtils.criarTextField(15);
        txtEmail = SwingUtils.criarTextField(15);
        txtSenha = SwingUtils.criarPasswordField(15);
        txtConfirmarSenha = SwingUtils.criarPasswordField(15);
        btnCadastrar = SwingUtils.criarBotaoPrimario("Cadastrar");
        btnVoltar = SwingUtils.criarBotao("Voltar");

        btnCadastrar.addActionListener(e -> realizarCadastro());
        btnVoltar.addActionListener(e -> voltarParaLogin());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(lblTitulo);

        JLabel lblNome = new JLabel("Nome Completo:");
        JLabel lblEmail = new JLabel("E-mail:");
        JLabel lblSenha = new JLabel("Senha:");
        JLabel lblConfirmarSenha = new JLabel("Confirmar Senha:");

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblNome, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtNome, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblEmail, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblSenha, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtSenha, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblConfirmarSenha, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtConfirmarSenha, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnCadastrar);
        buttonPanel.add(btnVoltar);

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
    
    private void realizarCadastro() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        String confirmacaoSenha = new String(txtConfirmarSenha.getPassword());

        if (!validarCampos(nome, email, senha, confirmacaoSenha)) {
            return;
        }

        if (controller.emailExiste(email)) {
            SwingUtils.mostrarErro(this, "Este e-mail já está em uso. Por favor, escolha outro.");
            txtEmail.requestFocus();
            return;
        }

        Optional<Usuario> usuarioOpt = controller.cadastrarUsuarioSimplificado(nome, email, senha);
        
        if (usuarioOpt.isPresent()) {
            SwingUtils.mostrarInfo(this, "Usuário cadastrado com sucesso!");
            voltarParaLogin();
        } else {
            SwingUtils.mostrarErro(this, "Erro ao cadastrar usuário. Por favor, tente novamente.");
        }
    }
    
    private boolean validarCampos(String nome, String email, String senha, String confirmacaoSenha) {
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmacaoSenha.isEmpty()) {
            SwingUtils.mostrarErro(this, "Por favor, preencha todos os campos.");
            return false;
        }

        if (!SwingUtils.validarEmail(email)) {
            SwingUtils.mostrarErro(this, "Por favor, informe um e-mail válido.");
            txtEmail.requestFocus();
            return false;
        }

        if (!senha.equals(confirmacaoSenha)) {
            SwingUtils.mostrarErro(this, "As senhas não conferem.");
            txtSenha.setText("");
            txtConfirmarSenha.setText("");
            txtSenha.requestFocus();
            return false;
        }

        if (senha.length() < 6) {
            SwingUtils.mostrarErro(this, "A senha deve ter pelo menos 6 caracteres.");
            txtSenha.setText("");
            txtConfirmarSenha.setText("");
            txtSenha.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void voltarParaLogin() {
        LoginPanel loginPanel = new LoginPanel(parentFrame);
        parentFrame.setContentPane(loginPanel);
        parentFrame.revalidate();
        parentFrame.repaint();
    }
} 