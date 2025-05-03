package org.example.controller;

import org.example.model.Usuario;
import org.example.service.UsuarioService;

import java.util.List;
import java.util.Optional;

public class UsuarioController {
    private final UsuarioService usuarioService;
    
    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioService.listarTodos();
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        return usuarioService.buscarPorId(id);
    }

    public boolean salvarUsuario(Usuario usuario) {
        if (usuario.getId() == null) {
            Optional<Usuario> usuarioOpt = usuarioService.cadastrarUsuario(
                    usuario.getNome(), 
                    usuario.getEmail(), 
                    usuario.getSenha());
            return usuarioOpt.isPresent();
        } else {
            return usuarioService.atualizarUsuario(usuario);
        }
    }

    public boolean excluirUsuario(Long id) {
        return usuarioService.excluirUsuario(id);
    }
    

    public boolean emailExiste(String email, Long idUsuarioAtual) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(email);

        if (!usuarioOpt.isPresent()) {
            return false;
        }

        Usuario usuarioEncontrado = usuarioOpt.get();
        return idUsuarioAtual == null || !idUsuarioAtual.equals(usuarioEncontrado.getId());
    }

    public Optional<Usuario> realizarLoginComEmail(String email, String senha) {
        return usuarioService.autenticarPorEmail(email, senha);
    }
} 