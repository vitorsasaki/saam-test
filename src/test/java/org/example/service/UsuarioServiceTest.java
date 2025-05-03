package org.example.service;

import org.example.dao.UsuarioDAO;
import org.example.model.Usuario;
import org.example.util.H2DatabaseUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    @InjectMocks
    private UsuarioService usuarioService;

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
    }

    @Test
    @DisplayName("Deve criar a tabela no banco ao inicializar")
    void inicializacaoDoBancoDeDados() {
        // O teste precisa criar uma nova instância de UsuarioService manualmente
        // para que o construtor chame o método inicializarBancoDados()
        
        // Cria um novo mock para este teste específico
        UsuarioDAO daoMock = mock(UsuarioDAO.class);
        
        // Cria uma nova instância do service com o mock
        UsuarioService service = new UsuarioService(daoMock);
        
        // Verifica se o método do DAO foi chamado na inicialização
        verify(daoMock).criarTabela();
    }

    @Test
    @DisplayName("Deve cadastrar um novo usuário com sucesso")
    void cadastrarUsuario_ComDadosValidos_DeveRetornarUsuario() {
        // Arrange
        String nome = "Novo Usuário";
        String email = "novo@email.com";
        String senha = "senha456";
        
        // Configura mock para simular email não existente
        when(usuarioDAO.buscarPorEmail(email)).thenReturn(Optional.empty());
        
        // Configura mock para simular inserção com sucesso
        when(usuarioDAO.inserir(any(Usuario.class))).thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = usuarioService.cadastrarUsuario(nome, email, senha);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        
        // Verifica se os métodos do DAO foram chamados corretamente
        verify(usuarioDAO).buscarPorEmail(email);
        verify(usuarioDAO).inserir(any(Usuario.class));
    }

    @Test
    @DisplayName("Não deve cadastrar usuário com email já existente")
    void cadastrarUsuario_ComEmailJaExistente_DeveRetornarEmpty() {
        // Arrange
        String nome = "Outro Usuário";
        String email = "existente@email.com";
        String senha = "senha789";
        
        // Configura mock para simular email já existente
        when(usuarioDAO.buscarPorEmail(email)).thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = usuarioService.cadastrarUsuario(nome, email, senha);

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica que o método de inserção não foi chamado
        verify(usuarioDAO).buscarPorEmail(email);
        verify(usuarioDAO, never()).inserir(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar um usuário existente")
    void atualizarUsuario_ComUsuarioValido_DeveRetornarTrue() {
        // Arrange
        when(usuarioDAO.atualizar(any(Usuario.class))).thenReturn(true);

        // Act
        boolean resultado = usuarioService.atualizarUsuario(usuarioMock);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(usuarioDAO).atualizar(usuarioMock);
    }

    @Test
    @DisplayName("Deve excluir um usuário pelo ID")
    void excluirUsuario_ComIdValido_DeveRetornarTrue() {
        // Arrange
        when(usuarioDAO.excluir(1L)).thenReturn(true);

        // Act
        boolean resultado = usuarioService.excluirUsuario(1L);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(usuarioDAO).excluir(1L);
    }

    @Test
    @DisplayName("Deve buscar um usuário pelo ID")
    void buscarPorId_ComIdExistente_DeveRetornarUsuario() {
        // Arrange
        when(usuarioDAO.buscarPorId(1L)).thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(usuarioDAO).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve buscar um usuário pelo e-mail")
    void buscarPorEmail_ComEmailExistente_DeveRetornarUsuario() {
        // Arrange
        when(usuarioDAO.buscarPorEmail("teste@email.com")).thenReturn(Optional.of(usuarioMock));

        // Act
        Optional<Usuario> resultado = usuarioService.buscarPorEmail("teste@email.com");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("teste@email.com", resultado.get().getEmail());
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(usuarioDAO).buscarPorEmail("teste@email.com");
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void listarTodos_DeveRetornarListaDeUsuarios() {
        // Arrange
        List<Usuario> listaUsuarios = new ArrayList<>();
        listaUsuarios.add(usuarioMock);
        
        Usuario usuario2 = new Usuario("Outro Usuário", "outro@email.com", "senha456");
        usuario2.setId(2L);
        listaUsuarios.add(usuario2);
        
        when(usuarioDAO.listarTodos()).thenReturn(listaUsuarios);

        // Act
        List<Usuario> resultado = usuarioService.listarTodos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(usuarioDAO).listarTodos();
    }

    @Test
    @DisplayName("Deve autenticar usuário com email e senha corretos")
    void autenticarPorEmail_ComCredenciaisCorretas_DeveRetornarUsuario() {
        // Arrange
        // Criptografa a senha como o service faria
        String senhaCriptografada = usuarioService.criptografarSenhaTeste("senha123");
        
        // Cria um usuário com a senha criptografada
        Usuario usuarioComSenhaCriptografada = new Usuario(usuarioMock.getNome(), usuarioMock.getEmail(), senhaCriptografada);
        usuarioComSenhaCriptografada.setId(1L);
        
        when(usuarioDAO.buscarPorEmail("teste@email.com")).thenReturn(Optional.of(usuarioComSenhaCriptografada));

        // Act
        Optional<Usuario> resultado = usuarioService.autenticarPorEmail("teste@email.com", "senha123");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(usuarioDAO).buscarPorEmail("teste@email.com");
    }

    @Test
    @DisplayName("Não deve autenticar com senha incorreta")
    void autenticarPorEmail_ComSenhaIncorreta_DeveRetornarEmpty() {
        // Arrange
        // Criptografa a senha como o service faria
        String senhaCriptografada = usuarioService.criptografarSenhaTeste("senha123");
        
        // Cria um usuário com a senha criptografada
        Usuario usuarioComSenhaCriptografada = new Usuario(usuarioMock.getNome(), usuarioMock.getEmail(), senhaCriptografada);
        usuarioComSenhaCriptografada.setId(1L);
        
        when(usuarioDAO.buscarPorEmail("teste@email.com")).thenReturn(Optional.of(usuarioComSenhaCriptografada));

        // Act - Tenta autenticar com uma senha diferente
        Optional<Usuario> resultado = usuarioService.autenticarPorEmail("teste@email.com", "senha456");

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(usuarioDAO).buscarPorEmail("teste@email.com");
    }

    @Test
    @DisplayName("Não deve autenticar com email inexistente")
    void autenticarPorEmail_ComEmailInexistente_DeveRetornarEmpty() {
        // Arrange
        when(usuarioDAO.buscarPorEmail("inexistente@email.com")).thenReturn(Optional.empty());

        // Act
        Optional<Usuario> resultado = usuarioService.autenticarPorEmail("inexistente@email.com", "qualquersenha");

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica se o método do DAO foi chamado corretamente
        verify(usuarioDAO).buscarPorEmail("inexistente@email.com");
    }
} 