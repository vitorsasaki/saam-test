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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private LoginController loginController;

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
    @DisplayName("Deve realizar login com usuário e senha corretos")
    void realizarLogin_ComCredenciaisCorretas_DeveRetornarUsuario() {
        // Arrange
        when(usuarioService.autenticarPorEmail("usuario", "senha123"))
                .thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = loginController.realizarLoginComEmail("usuario", "senha123");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Usuário Teste", resultado.get().getNome());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).autenticarPorEmail("usuario", "senha123");
    }

    @Test
    @DisplayName("Deve retornar empty quando credenciais forem inválidas")
    void realizarLogin_ComCredenciaisIncorretas_DeveRetornarEmpty() {
        // Arrange
        when(usuarioService.autenticarPorEmail("usuario", "senhaErrada"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = loginController.realizarLoginComEmail("usuario", "senhaErrada");

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).autenticarPorEmail("usuario", "senhaErrada");
    }

    @Test
    @DisplayName("Deve realizar login com email e senha corretos")
    void realizarLoginComEmail_ComCredenciaisCorretas_DeveRetornarUsuario() {
        // Arrange
        when(usuarioService.autenticarPorEmail("teste@email.com", "senha123"))
                .thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = loginController.realizarLoginComEmail("teste@email.com", "senha123");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Usuário Teste", resultado.get().getNome());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).autenticarPorEmail("teste@email.com", "senha123");
    }

    @Test
    @DisplayName("Deve cadastrar novo usuário com sucesso")
    void cadastrarUsuario_ComDadosValidos_DeveRetornarUsuario() {
        // Arrange
        String nome = "Novo Usuário";
        String email = "novo@email.com";
        String senha = "senha456";
        
        when(usuarioService.cadastrarUsuario(nome, email, senha))
                .thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = loginController.cadastrarUsuarioSimplificado(nome, email, senha);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).cadastrarUsuario(nome, email, senha);
    }

    @Test
    @DisplayName("Deve falhar ao cadastrar usuário com email já existente")
    void cadastrarUsuario_ComEmailExistente_DeveRetornarEmpty() {
        // Arrange
        String nome = "Outro Usuário";
        String email = "existente@email.com";
        String senha = "senha789";
        
        when(usuarioService.cadastrarUsuario(nome, email, senha))
                .thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = loginController.cadastrarUsuarioSimplificado(nome, email, senha);

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).cadastrarUsuario(nome, email, senha);
    }

    @Test
    @DisplayName("Deve verificar se email existe")
    void emailExiste_ComEmailExistente_DeveRetornarTrue() {
        // Arrange
        String email = "existente@email.com";
        when(usuarioService.buscarPorEmail(email))
                .thenReturn(Optional.of(usuarioMock));

        // Act
        boolean resultado = loginController.emailExiste(email);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).buscarPorEmail(email);
    }

    @Test
    @DisplayName("Deve verificar se email não existe")
    void emailExiste_ComEmailNaoExistente_DeveRetornarFalse() {
        // Arrange
        String email = "naoexiste@email.com";
        when(usuarioService.buscarPorEmail(email))
                .thenReturn(Optional.empty());

        // Act
        boolean resultado = loginController.emailExiste(email);

        // Assert
        assertFalse(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(usuarioService).buscarPorEmail(email);
    }
} 