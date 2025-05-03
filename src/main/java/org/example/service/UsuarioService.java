package org.example.service;

import org.example.dao.UsuarioDAO;
import org.example.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class UsuarioService {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
        inicializarBancoDados();
    }

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
        inicializarBancoDados();
    }

    private void inicializarBancoDados() {
        usuarioDAO.criarTabela();
    }

    public Optional<Usuario> cadastrarUsuario(String nome, String email, String senha) {
        if (usuarioDAO.buscarPorEmail(email).isPresent()) {
            logger.warn("Tentativa de cadastro com email já existente: {}", email);
            return Optional.empty();
        }

        Usuario novoUsuario = new Usuario(nome, email, criptografarSenha(senha));
        return usuarioDAO.inserir(novoUsuario);
    }

    public boolean atualizarUsuario(Usuario usuario) {
        return usuarioDAO.atualizar(usuario);
    }

    public boolean excluirUsuario(Long id) {
        return usuarioDAO.excluir(id);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDAO.buscarPorId(id);
    }
    
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.buscarPorEmail(email);
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }
    
    public Optional<Usuario> autenticarPorEmail(String email, String senha) {
        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verifica a senha
            if (verificarSenha(senha, usuario.getSenha())) {
                return Optional.of(usuario);
            }
        }

        logger.warn("Falha na autenticação para o email: {}", email);
        return Optional.empty();
    }

    private String criptografarSenha(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(senha.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Erro ao criptografar senha", e);
            return senha; // Cuidado: isso é apenas para evitar erros, não é seguro
        }
    }

    private boolean verificarSenha(String senhaDigitada, String senhaCriptografada) {
        String senhaDigitadaCriptografada = criptografarSenha(senhaDigitada);
        return senhaDigitadaCriptografada.equals(senhaCriptografada);
    }

    public String criptografarSenhaTeste(String senha) {
        return criptografarSenha(senha);
    }
} 