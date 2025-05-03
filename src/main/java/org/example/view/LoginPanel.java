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
        
        // Configura o layout
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        
        // Cria os componentes do formulário
        JLabel lblTitulo = SwingUtils.criarLabel("Login do Sistema", SwingUtils.FONTE_TITULO);
        txtEmail = SwingUtils.criarTextField(15);
        txtSenha = SwingUtils.criarPasswordField(15);
        btnLogin = SwingUtils.criarBotaoPrimario("Entrar");
        btnCadastrar = SwingUtils.criarBotao("Cadastrar-se");
        
        // Adiciona ação de Enter nos campos de texto
        SwingUtils.adicionarAcaoEnter(txtEmail, this::realizarLogin);
        SwingUtils.adicionarAcaoEnter(txtSenha, this::realizarLogin);
        
        // Configura os listeners dos botões
        btnLogin.addActionListener(e -> realizarLogin());
        btnCadastrar.addActionListener(e -> abrirTelaCadastro());
        
        // Painel para o título centralizado
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(lblTitulo);
        
        // Cria labels para os campos
        JLabel lblEmail = new JLabel("E-mail:");
        JLabel lblSenha = new JLabel("Senha:");
        
        // Painel para os campos de formulário com GridBagLayout para melhor controle
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Adiciona label E-mail
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblEmail, gbc);
        
        // Adiciona campo E-mail
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtEmail, gbc);
        
        // Adiciona label Senha
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(lblSenha, gbc);
        
        // Adiciona campo Senha
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(txtSenha, gbc);
        
        // Painel para os botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCadastrar);
        
        // Painel central com os componentes
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        // Adiciona espaço antes do formulário
        centerPanel.add(Box.createVerticalGlue());
        
        // Adiciona o formulário com alinhamento centralizado
        JPanel formWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        formWrapperPanel.add(formPanel);
        centerPanel.add(formWrapperPanel);
        
        // Adiciona os botões
        centerPanel.add(buttonPanel);
        
        // Adiciona espaço depois dos botões
        centerPanel.add(Box.createVerticalGlue());
        
        // Adiciona os componentes ao painel principal
        add(titlePanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void realizarLogin() {
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        
        // Validação simples
        if (email.isEmpty() || senha.isEmpty()) {
            SwingUtils.mostrarErro(this, "Por favor, preencha todos os campos.");
            return;
        }
        
        // Verifica se o e-mail é válido
        if (!SwingUtils.validarEmail(email)) {
            SwingUtils.mostrarErro(this, "Por favor, informe um e-mail válido.");
            txtEmail.requestFocus();
            return;
        }
        
        // Tenta realizar o login
        Optional<Usuario> usuarioOpt = controller.realizarLoginComEmail(email, senha);
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            SwingUtils.mostrarInfo(this, "Bem-vindo, " + usuario.getNome() + "!");
            
            // Abre a tela principal
            abrirTelaPrincipal(usuario);
        } else {
            SwingUtils.mostrarErro(this, "E-mail ou senha inválidos.");
            txtSenha.setText("");
            txtSenha.requestFocus();
        }
    }
    
    private void abrirTelaCadastro() {
        // Cria o painel de cadastro e substitui o painel atual
        CadastroUsuarioPanel cadastroPanel = new CadastroUsuarioPanel(parentFrame);
        parentFrame.setContentPane(cadastroPanel);
        parentFrame.revalidate();
        parentFrame.repaint();
    }
    
    private void abrirTelaPrincipal(Usuario usuario) {
        // Cria o painel principal e substitui o painel atual
        MainPanel mainPanel = new MainPanel(parentFrame, usuario);
        parentFrame.setContentPane(mainPanel);
        parentFrame.setTitle("Sistema - " + usuario.getNome());
        parentFrame.revalidate();
        parentFrame.repaint();
    }
} 