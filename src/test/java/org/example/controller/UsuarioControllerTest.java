package org.example.controller;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import org.example.util.H2DatabaseUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuarioMock;
    
    @BeforeAll
    static void inicializar() {
        // Inicializa o banco de dados de teste
        H2DatabaseUtil.limparBancoDados();
        H2DatabaseUtil.criarTabelaUsuarios();
    }
    
    @AfterAll
    static void finalizar() {
        // Limpa e fecha o banco de dados de teste
        H2DatabaseUtil.limparBancoDados();
        H2DatabaseUtil.fecharConexoes();
    }

    @BeforeEach
    void setUp() {
        // Configuração comum para todos os testes
        usuarioMock = new Usuario("Usuário Teste", "teste@email.com", "senha123");
        usuarioMock.setId(1L);
        
        // Limpar contadores e configurações prévias
        reset(usuarioService);
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void listarUsuarios_DeveRetornarListaDeUsuarios() {
        // Arrange
        List<Usuario> listaUsuarios = new ArrayList<>();
        listaUsuarios.add(usuarioMock);
        
        Usuario usuario2 = new Usuario("Outro Usuário", "outro@email.com", "senha456");
        usuario2.setId(2L);
        listaUsuarios.add(usuario2);
        
        when(usuarioService.listarTodos()).thenReturn(listaUsuarios);

        // Act
        List<Usuario> resultado = usuarioController.listarUsuarios();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).listarTodos();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID")
    void buscarUsuarioPorId_ComIdExistente_DeveRetornarUsuario() {
        // Arrange
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = usuarioController.buscarUsuarioPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Usuário Teste", resultado.get().getNome());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve retornar empty ao buscar usuário com ID inexistente")
    void buscarUsuarioPorId_ComIdInexistente_DeveRetornarEmpty() {
        // Arrange
        when(usuarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioController.buscarUsuarioPorId(99L);

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).buscarPorId(99L);
    }
    
    @Test
    @DisplayName("Deve cadastrar novo usuário com sucesso")
    void salvarUsuario_ComNovoUsuario_DeveRetornarTrue() {
        // Arrange
        Usuario novoUsuario = new Usuario("Novo Usuário", "novo@email.com", "senha456");
        // ID é null para indicar que é um novo usuário
        
        when(usuarioService.cadastrarUsuario(novoUsuario.getNome(), novoUsuario.getEmail(), novoUsuario.getSenha()))
                .thenReturn(Optional.of(usuarioMock));

        // Act
        boolean resultado = usuarioController.salvarUsuario(novoUsuario);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).cadastrarUsuario(novoUsuario.getNome(), novoUsuario.getEmail(), novoUsuario.getSenha());
    }
    
    @Test
    @DisplayName("Deve falhar ao cadastrar novo usuário com email já existente")
    void salvarUsuario_ComEmailJaExistente_DeveRetornarFalse() {
        // Arrange
        Usuario novoUsuario = new Usuario("Usuário Duplicado", "duplicado@email.com", "senha789");
        // ID é null para indicar que é um novo usuário
        
        when(usuarioService.cadastrarUsuario(novoUsuario.getNome(), novoUsuario.getEmail(), novoUsuario.getSenha()))
                .thenReturn(Optional.empty());

        // Act
        boolean resultado = usuarioController.salvarUsuario(novoUsuario);

        // Assert
        assertFalse(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).cadastrarUsuario(novoUsuario.getNome(), novoUsuario.getEmail(), novoUsuario.getSenha());
    }
    
    @Test
    @DisplayName("Deve atualizar usuário existente com sucesso")
    void salvarUsuario_ComUsuarioExistente_DeveRetornarTrue() {
        // Arrange
        Usuario usuarioExistente = new Usuario("Usuário Atualizado", "atualizado@email.com", "senha123");
        usuarioExistente.setId(1L); // ID existente
        
        when(usuarioService.atualizarUsuario(usuarioExistente)).thenReturn(true);

        // Act
        boolean resultado = usuarioController.salvarUsuario(usuarioExistente);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).atualizarUsuario(usuarioExistente);
    }
    
    @Test
    @DisplayName("Deve falhar ao atualizar usuário inexistente")
    void salvarUsuario_ComUsuarioInexistente_DeveRetornarFalse() {
        // Arrange
        Usuario usuarioInexistente = new Usuario("Usuário Inexistente", "inexistente@email.com", "senha123");
        usuarioInexistente.setId(99L); // ID inexistente
        
        when(usuarioService.atualizarUsuario(usuarioInexistente)).thenReturn(false);

        // Act
        boolean resultado = usuarioController.salvarUsuario(usuarioInexistente);

        // Assert
        assertFalse(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).atualizarUsuario(usuarioInexistente);
    }
    
    @Test
    @DisplayName("Deve excluir usuário existente com sucesso")
    void excluirUsuario_ComIdExistente_DeveRetornarTrue() {
        // Arrange
        when(usuarioService.excluirUsuario(1L)).thenReturn(true);

        // Act
        boolean resultado = usuarioController.excluirUsuario(1L);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).excluirUsuario(1L);
    }
    
    @Test
    @DisplayName("Deve falhar ao excluir usuário inexistente")
    void excluirUsuario_ComIdInexistente_DeveRetornarFalse() {
        // Arrange
        when(usuarioService.excluirUsuario(99L)).thenReturn(false);

        // Act
        boolean resultado = usuarioController.excluirUsuario(99L);

        // Assert
        assertFalse(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).excluirUsuario(99L);
    }
    
    @Test
    @DisplayName("Deve verificar se email existe (exceto para o usuário atual)")
    void emailExiste_ComEmailExistenteMasDoUsuarioAtual_DeveRetornarFalse() {
        // Arrange
        String email = "teste@email.com";
        Long idUsuarioAtual = 1L;
        
        when(usuarioService.buscarPorEmail(email)).thenReturn(Optional.of(usuarioMock));

        // Act
        boolean resultado = usuarioController.emailExiste(email, idUsuarioAtual);

        // Assert
        assertFalse(resultado); // False porque o email pertence ao usuário atual
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).buscarPorEmail(email);
    }
    
    @Test
    @DisplayName("Deve verificar se email pertence a outro usuário")
    void emailExiste_ComEmailPertenceAOutroUsuario_DeveRetornarTrue() {
        // Arrange
        String email = "outro@email.com";
        Long idUsuarioAtual = 2L;
        
        Usuario outroUsuario = new Usuario("Outro", "outro@email.com", "senha456");
        outroUsuario.setId(1L);
        
        when(usuarioService.buscarPorEmail(email)).thenReturn(Optional.of(outroUsuario));

        // Act
        boolean resultado = usuarioController.emailExiste(email, idUsuarioAtual);

        // Assert
        assertTrue(resultado); // True porque o email pertence a outro usuário
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).buscarPorEmail(email);
    }
    
    @Test
    @DisplayName("Deve retornar false para email inexistente")
    void emailExiste_ComEmailInexistente_DeveRetornarFalse() {
        // Arrange
        String email = "inexistente@email.com";
        Long idUsuarioAtual = 1L;
        
        when(usuarioService.buscarPorEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean resultado = usuarioController.emailExiste(email, idUsuarioAtual);

        // Assert
        assertFalse(resultado); // False porque o email não existe
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).buscarPorEmail(email);
    }
    
    @Test
    @DisplayName("Deve realizar login com email e senha corretos")
    void realizarLoginComEmail_ComCredenciaisCorretas_DeveRetornarUsuario() {
        // Arrange
        String email = "teste@email.com";
        String senha = "senha123";
        
        when(usuarioService.autenticarPorEmail(email, senha)).thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = usuarioController.realizarLoginComEmail(email, senha);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Usuário Teste", resultado.get().getNome());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).autenticarPorEmail(email, senha);
    }
    
    @Test
    @DisplayName("Deve falhar ao realizar login com credenciais incorretas")
    void realizarLoginComEmail_ComCredenciaisIncorretas_DeveRetornarEmpty() {
        // Arrange
        String email = "teste@email.com";
        String senha = "senhaErrada";
        
        when(usuarioService.autenticarPorEmail(email, senha)).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioController.realizarLoginComEmail(email, senha);

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).autenticarPorEmail(email, senha);
    }
} 