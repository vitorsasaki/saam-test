package org.example.controller;

import org.example.model.Usuario;
import org.example.service.UsuarioService;

import java.util.Optional;

public class LoginController {
    private final UsuarioService usuarioService;
    
    public LoginController() {
        this.usuarioService = new UsuarioService();
    }

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Optional<Usuario> realizarLoginComEmail(String email, String senha) {
        return usuarioService.autenticarPorEmail(email, senha);
    }

    public Optional<Usuario> cadastrarUsuarioSimplificado(String nome, String email, String senha) {
        return usuarioService.cadastrarUsuario(nome, email, senha);
    }

    public boolean emailExiste(String email) {
        return usuarioService.buscarPorEmail(email).isPresent();
    }
} 