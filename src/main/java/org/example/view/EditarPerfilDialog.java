package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Usuario;
import org.example.service.UsuarioService;
import org.example.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class EditarPerfilDialog extends JDialog {
    private final UsuarioController controller;
    private final UsuarioService usuarioService;
    private final Usuario usuario;
    private boolean perfilAtualizado;
    
    private JTextField txtNome;
    private JTextField txtEmail;
    private JPasswordField txtSenhaAtual;
    private JPasswordField txtNovaSenha;
    private JPasswordField txtConfirmarSenha;
    
    public EditarPerfilDialog(JFrame parent, Usuario usuario) {
        super(parent, "Meu Perfil", true);
        this.controller = new UsuarioController();
        this.usuarioService = new UsuarioService();
        this.usuario = usuario;
        this.perfilAtualizado = false;
        
        inicializarComponentes();
        preencherCampos();

        setSize(400, 500);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void inicializarComponentes() {
        txtNome = SwingUtils.criarTextField(15);
        txtEmail = SwingUtils.criarTextField(15);
        txtSenhaAtual = SwingUtils.criarPasswordField(15);
        txtNovaSenha = SwingUtils.criarPasswordField(15);
        txtConfirmarSenha = SwingUtils.criarPasswordField(15);
        
        JButton btnSalvar = SwingUtils.criarBotaoPrimario("Salvar");
        JButton btnCancelar = SwingUtils.criarBotao("Cancelar");

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JPanel dadosPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        dadosPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel lblDadosPessoais = SwingUtils.criarLabel("Dados Pessoais", SwingUtils.FONTE_SUBTITULO);
        JPanel nomeCampo = SwingUtils.criarCampoFormulario("Nome Completo:", txtNome);
        JPanel emailCampo = SwingUtils.criarCampoFormulario("E-mail:", txtEmail);
        
        dadosPanel.add(lblDadosPessoais);
        dadosPanel.add(nomeCampo);
        dadosPanel.add(emailCampo);

        JPanel senhaHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblAlterarSenha = SwingUtils.criarLabel("Alterar Senha", SwingUtils.FONTE_SUBTITULO);
        senhaHeaderPanel.add(lblAlterarSenha);
        
        JPanel senhaPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        senhaPanel.add(SwingUtils.criarCampoFormulario("Senha Atual:", txtSenhaAtual));
        senhaPanel.add(SwingUtils.criarCampoFormulario("Nova Senha:", txtNovaSenha));
        senhaPanel.add(SwingUtils.criarCampoFormulario("Confirmar Nova Senha:", txtConfirmarSenha));
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblInfoSenha = SwingUtils.criarLabel("* Preencha apenas se desejar alterar sua senha", new Font(SwingUtils.FONTE_PADRAO.getName(), Font.ITALIC, 12));
        lblInfoSenha.setForeground(Color.GRAY);
        infoPanel.add(lblInfoSenha);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        mainPanel.add(dadosPanel);
        mainPanel.add(senhaHeaderPanel);
        mainPanel.add(senhaPanel);
        mainPanel.add(infoPanel);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(mainPanel, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void preencherCampos() {
        txtNome.setText(usuario.getNome());
        txtEmail.setText(usuario.getEmail());
    }
    
    private void salvar() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String senhaAtual = new String(txtSenhaAtual.getPassword());
        String novaSenha = new String(txtNovaSenha.getPassword());
        String confirmacaoSenha = new String(txtConfirmarSenha.getPassword());

        if (!validarCampos(nome, email, senhaAtual, novaSenha, confirmacaoSenha)) {
            return;
        }

        if (controller.emailExiste(email, usuario.getId())) {
            SwingUtils.mostrarErro(this, "Este e-mail já está em uso. Por favor, escolha outro.");
            txtEmail.requestFocus();
            return;
        }

        usuario.setNome(nome);
        usuario.setEmail(email);

        if (!novaSenha.isEmpty()) {
            String senhaCriptografada = usuarioService.criptografarSenhaTeste(novaSenha);
            usuario.setSenha(senhaCriptografada);
        }

        boolean sucesso = controller.salvarUsuario(usuario);
        
        if (sucesso) {
            perfilAtualizado = true;
            SwingUtils.mostrarInfo(this, "Perfil atualizado com sucesso!");
            dispose();
        } else {
            SwingUtils.mostrarErro(this, "Erro ao atualizar perfil. Por favor, tente novamente.");
        }
    }
    
    private boolean validarCampos(String nome, String email, String senhaAtual, String novaSenha, String confirmacaoSenha) {
        if (nome.isEmpty() || email.isEmpty()) {
            SwingUtils.mostrarErro(this, "Por favor, preencha todos os campos obrigatórios.");
            return false;
        }

        if (!SwingUtils.validarEmail(email)) {
            SwingUtils.mostrarErro(this, "Por favor, informe um e-mail válido.");
            txtEmail.requestFocus();
            return false;
        }

        if (!novaSenha.isEmpty() || !confirmacaoSenha.isEmpty()) {
            if (senhaAtual.isEmpty()) {
                SwingUtils.mostrarErro(this, "Por favor, informe sua senha atual para confirmar a alteração.");
                txtSenhaAtual.requestFocus();
                return false;
            }

            if (novaSenha.isEmpty() || confirmacaoSenha.isEmpty()) {
                SwingUtils.mostrarErro(this, "Por favor, informe a nova senha e a confirmação.");
                if (novaSenha.isEmpty()) txtNovaSenha.requestFocus();
                else txtConfirmarSenha.requestFocus();
                return false;
            }

            if (novaSenha.length() < 6) {
                SwingUtils.mostrarErro(this, "A nova senha deve ter pelo menos 6 caracteres.");
                txtNovaSenha.setText("");
                txtConfirmarSenha.setText("");
                txtNovaSenha.requestFocus();
                return false;
            }

            if (!novaSenha.equals(confirmacaoSenha)) {
                SwingUtils.mostrarErro(this, "As senhas não conferem.");
                txtNovaSenha.setText("");
                txtConfirmarSenha.setText("");
                txtNovaSenha.requestFocus();
                return false;
            }

            if (!controller.realizarLoginComEmail(usuario.getEmail(), senhaAtual).isPresent()) {
                SwingUtils.mostrarErro(this, "Senha atual incorreta.");
                txtSenhaAtual.setText("");
                txtSenhaAtual.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    public boolean isPerfilAtualizado() {
        return perfilAtualizado;
    }
} 