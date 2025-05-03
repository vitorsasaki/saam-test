package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Usuario;
import org.example.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class EditarUsuarioDialog extends JDialog {
    private final UsuarioController controller;
    private final Usuario usuario;
    private boolean usuarioSalvo;
    
    private JTextField txtNome;
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmarSenha;
    
    public EditarUsuarioDialog(JFrame parent, Usuario usuario) {
        super(parent, usuario == null ? "Novo Usuário" : "Editar Usuário", true);
        this.controller = new UsuarioController();
        this.usuario = usuario == null ? new Usuario() : usuario;
        this.usuarioSalvo = false;
        
        inicializarComponentes();
        preencherCampos();
        
        // Configura a janela
        setSize(400, 350);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void inicializarComponentes() {
        // Cria os componentes com tamanho reduzido
        txtNome = SwingUtils.criarTextField(15);
        txtEmail = SwingUtils.criarTextField(15);
        txtSenha = SwingUtils.criarPasswordField(15);
        txtConfirmarSenha = SwingUtils.criarPasswordField(15);
        
        JButton btnSalvar = SwingUtils.criarBotaoPrimario("Salvar");
        JButton btnCancelar = SwingUtils.criarBotao("Cancelar");
        
        // Configura os listeners
        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());
        
        // Organiza os componentes em painéis com espaçamento reduzido
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Cria labels e campos com GridBagLayout para melhor controle
        JPanel campoNome = new JPanel(new GridBagLayout());
        JPanel campoEmail = new JPanel(new GridBagLayout());
        JPanel campoSenha = new JPanel(new GridBagLayout());
        JPanel campoConfirmarSenha = new JPanel(new GridBagLayout());
        
        // Adiciona componentes com GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.EAST;
        
        // Adiciona os rótulos e campos
        formPanel.add(SwingUtils.criarCampoFormulario("Nome Completo:", txtNome));
        formPanel.add(SwingUtils.criarCampoFormulario("E-mail:", txtEmail));
        formPanel.add(SwingUtils.criarCampoFormulario("Senha:", txtSenha));
        formPanel.add(SwingUtils.criarCampoFormulario("Confirmar Senha:", txtConfirmarSenha));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        
        // Adiciona os painéis ao diálogo
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        // Cria um painel centralizado
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.add(formPanel, BorderLayout.NORTH);
        centerWrapper.add(Box.createVerticalGlue(), BorderLayout.CENTER);
        
        contentPane.add(centerWrapper, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void preencherCampos() {
        // Se for um usuário existente, preenche os campos com os dados
        if (usuario.getId() != null) {
            txtNome.setText(usuario.getNome());
            txtEmail.setText(usuario.getEmail());
            txtSenha.setText(""); // Não exibe a senha atual por segurança
            txtConfirmarSenha.setText("");
        }
    }
    
    private void salvar() {
        // Recupera os dados do formulário
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String senha = new String(txtSenha.getPassword());
        String confirmacaoSenha = new String(txtConfirmarSenha.getPassword());
        
        // Validação dos campos
        if (!validarCampos(nome, email, senha, confirmacaoSenha)) {
            return;
        }
        
        // Verifica se o email já existe (para outro usuário)
        if (controller.emailExiste(email, usuario.getId())) {
            SwingUtils.mostrarErro(this, "Este e-mail já está em uso. Por favor, escolha outro.");
            txtEmail.requestFocus();
            return;
        }
        
        // Atualiza os dados do usuário
        usuario.setNome(nome);
        usuario.setEmail(email);
        
        // Só atualiza a senha se for informada
        if (!senha.isEmpty()) {
            usuario.setSenha(senha);
        }
        
        // Salva o usuário
        boolean sucesso = controller.salvarUsuario(usuario);
        
        if (sucesso) {
            usuarioSalvo = true;
            SwingUtils.mostrarInfo(this, "Usuário salvo com sucesso!");
            dispose();
        } else {
            SwingUtils.mostrarErro(this, "Erro ao salvar usuário. Por favor, tente novamente.");
        }
    }
    
    private boolean validarCampos(String nome, String email, String senha, String confirmacaoSenha) {
        // Verifica campos obrigatórios
        if (nome.isEmpty() || email.isEmpty()) {
            SwingUtils.mostrarErro(this, "Por favor, preencha os campos obrigatórios (Nome e E-mail).");
            return false;
        }
        
        // Verifica o e-mail
        if (!SwingUtils.validarEmail(email)) {
            SwingUtils.mostrarErro(this, "Por favor, informe um e-mail válido.");
            txtEmail.requestFocus();
            return false;
        }
        
        // Se for um novo usuário ou se a senha foi informada, valida a senha
        if (usuario.getId() == null || !senha.isEmpty()) {
            // Verifica se a senha foi informada
            if (senha.isEmpty()) {
                SwingUtils.mostrarErro(this, "Por favor, informe a senha.");
                txtSenha.requestFocus();
                return false;
            }
            
            // Verifica o tamanho mínimo da senha
            if (senha.length() < 6) {
                SwingUtils.mostrarErro(this, "A senha deve ter pelo menos 6 caracteres.");
                txtSenha.setText("");
                txtConfirmarSenha.setText("");
                txtSenha.requestFocus();
                return false;
            }
            
            // Verifica se as senhas conferem
            if (!senha.equals(confirmacaoSenha)) {
                SwingUtils.mostrarErro(this, "As senhas não conferem.");
                txtSenha.setText("");
                txtConfirmarSenha.setText("");
                txtSenha.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isUsuarioSalvo() {
        return usuarioSalvo;
    }
} 