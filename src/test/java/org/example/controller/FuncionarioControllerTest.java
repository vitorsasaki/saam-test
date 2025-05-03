package org.example.controller;

import org.example.model.Funcionario;
import org.example.service.FuncionarioService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FuncionarioControllerTest {

    @Mock
    private FuncionarioService funcionarioService;

    @InjectMocks
    private FuncionarioController funcionarioController;

    private Funcionario funcionarioMock;
    private final LocalDate dataAdmissao = LocalDate.of(2023, 1, 15);
    private final BigDecimal salario = new BigDecimal("5000.00");
    
    @BeforeAll
    static void inicializar() {
        // Inicializa o banco de dados de teste
        H2DatabaseUtil.limparBancoDados();
        H2DatabaseUtil.criarTabelaFuncionarios();
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
        funcionarioMock = new Funcionario("Funcionário Teste", dataAdmissao, salario, true);
        funcionarioMock.setId(1L);
        
        // Limpar contadores e configurações prévias
        reset(funcionarioService);
    }

    @Test
    @DisplayName("Deve listar todos os funcionários")
    void listarFuncionarios_DeveRetornarListaDeFuncionarios() {
        // Arrange
        List<Funcionario> listaFuncionarios = new ArrayList<>();
        listaFuncionarios.add(funcionarioMock);
        
        Funcionario funcionario2 = new Funcionario("Outro Funcionário", dataAdmissao, salario, false);
        funcionario2.setId(2L);
        listaFuncionarios.add(funcionario2);
        
        when(funcionarioService.listarTodos()).thenReturn(listaFuncionarios);

        // Act
        List<Funcionario> resultado = funcionarioController.listarFuncionarios();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals(2L, resultado.get(1).getId());
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).listarTodos();
    }

    @Test
    @DisplayName("Deve buscar funcionário por ID")
    void buscarFuncionarioPorId_ComIdExistente_DeveRetornarFuncionario() {
        // Arrange
        when(funcionarioService.buscarPorId(1L)).thenReturn(Optional.of(funcionarioMock));

        // Act
        Optional<Funcionario> resultado = funcionarioController.buscarFuncionarioPorId(1L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Funcionário Teste", resultado.get().getNome());
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).buscarPorId(1L);
    }

    @Test
    @DisplayName("Deve retornar empty ao buscar funcionário com ID inexistente")
    void buscarFuncionarioPorId_ComIdInexistente_DeveRetornarEmpty() {
        // Arrange
        when(funcionarioService.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Funcionario> resultado = funcionarioController.buscarFuncionarioPorId(99L);

        // Assert
        assertFalse(resultado.isPresent());
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).buscarPorId(99L);
    }
    
    @Test
    @DisplayName("Deve cadastrar novo funcionário com sucesso")
    void salvarFuncionario_ComNovoFuncionario_DeveRetornarTrue() {
        // Arrange
        Funcionario novoFuncionario = new Funcionario("Novo Funcionário", dataAdmissao, salario, true);
        // ID é null para indicar que é um novo funcionário
        
        when(funcionarioService.cadastrarFuncionario(
                novoFuncionario.getNome(), 
                novoFuncionario.getDataAdmissao(), 
                novoFuncionario.getSalario(), 
                novoFuncionario.isStatus()))
                .thenReturn(Optional.of(funcionarioMock));

        // Act
        boolean resultado = funcionarioController.salvarFuncionario(novoFuncionario);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).cadastrarFuncionario(
                novoFuncionario.getNome(), 
                novoFuncionario.getDataAdmissao(), 
                novoFuncionario.getSalario(), 
                novoFuncionario.isStatus());
    }
    
    @Test
    @DisplayName("Deve falhar ao cadastrar novo funcionário")
    void salvarFuncionario_ComFalhaAoCadastrar_DeveRetornarFalse() {
        // Arrange
        Funcionario novoFuncionario = new Funcionario("Funcionário Inválido", dataAdmissao, salario, true);
        // ID é null para indicar que é um novo funcionário
        
        when(funcionarioService.cadastrarFuncionario(
                novoFuncionario.getNome(), 
                novoFuncionario.getDataAdmissao(), 
                novoFuncionario.getSalario(), 
                novoFuncionario.isStatus()))
                .thenReturn(Optional.empty());

        // Act
        boolean resultado = funcionarioController.salvarFuncionario(novoFuncionario);

        // Assert
        assertFalse(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).cadastrarFuncionario(
                novoFuncionario.getNome(), 
                novoFuncionario.getDataAdmissao(), 
                novoFuncionario.getSalario(), 
                novoFuncionario.isStatus());
    }
    
    @Test
    @DisplayName("Deve atualizar funcionário existente com sucesso")
    void salvarFuncionario_ComFuncionarioExistente_DeveRetornarTrue() {
        // Arrange
        Funcionario funcionarioExistente = new Funcionario("Funcionário Atualizado", dataAdmissao, salario, true);
        funcionarioExistente.setId(1L); // ID existente
        
        when(funcionarioService.atualizarFuncionario(funcionarioExistente)).thenReturn(true);

        // Act
        boolean resultado = funcionarioController.salvarFuncionario(funcionarioExistente);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).atualizarFuncionario(funcionarioExistente);
    }
    
    @Test
    @DisplayName("Deve falhar ao atualizar funcionário inexistente")
    void salvarFuncionario_ComFuncionarioInexistente_DeveRetornarFalse() {
        // Arrange
        Funcionario funcionarioInexistente = new Funcionario("Funcionário Inexistente", dataAdmissao, salario, true);
        funcionarioInexistente.setId(99L); // ID inexistente
        
        when(funcionarioService.atualizarFuncionario(funcionarioInexistente)).thenReturn(false);

        // Act
        boolean resultado = funcionarioController.salvarFuncionario(funcionarioInexistente);

        // Assert
        assertFalse(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).atualizarFuncionario(funcionarioInexistente);
    }
    
    @Test
    @DisplayName("Deve excluir funcionário existente com sucesso")
    void excluirFuncionario_ComIdExistente_DeveRetornarTrue() {
        // Arrange
        when(funcionarioService.excluirFuncionario(1L)).thenReturn(true);

        // Act
        boolean resultado = funcionarioController.excluirFuncionario(1L);

        // Assert
        assertTrue(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).excluirFuncionario(1L);
    }
    
    @Test
    @DisplayName("Deve falhar ao excluir funcionário inexistente")
    void excluirFuncionario_ComIdInexistente_DeveRetornarFalse() {
        // Arrange
        when(funcionarioService.excluirFuncionario(99L)).thenReturn(false);

        // Act
        boolean resultado = funcionarioController.excluirFuncionario(99L);

        // Assert
        assertFalse(resultado);
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).excluirFuncionario(99L);
    }
    
    @Test
    @DisplayName("Deve cadastrar novo funcionário com método de conveniência")
    void cadastrarFuncionario_ComDadosValidos_DeveRetornarFuncionario() {
        // Arrange
        String nome = "Novo Funcionário";
        
        when(funcionarioService.cadastrarFuncionario(nome, dataAdmissao, salario, true))
                .thenReturn(Optional.of(funcionarioMock));

        // Act
        Optional<Funcionario> resultado = funcionarioController.cadastrarFuncionario(nome, dataAdmissao, salario, true);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        
        // Verifica se o método do service foi chamado corretamente
        verify(funcionarioService).cadastrarFuncionario(nome, dataAdmissao, salario, true);
    }
} 