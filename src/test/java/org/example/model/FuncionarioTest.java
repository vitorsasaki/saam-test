package org.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FuncionarioTest {
    
    private Funcionario funcionario;
    private final LocalDate dataAdmissao = LocalDate.of(2023, 1, 15);
    private final BigDecimal salario = new BigDecimal("5000.00");
    
    @BeforeEach
    void setUp() {
        funcionario = new Funcionario("João Silva", dataAdmissao, salario, true);
    }
    
    @Test
    @DisplayName("Deve criar um funcionário com valores corretos")
    void deveCriarFuncionarioComValoresCorretos() {
        // Assert
        assertEquals("João Silva", funcionario.getNome());
        assertEquals(dataAdmissao, funcionario.getDataAdmissao());
        assertEquals(salario, funcionario.getSalario());
        assertTrue(funcionario.isStatus());
        assertNull(funcionario.getId());
    }
    
    @Test
    @DisplayName("Deve definir e obter o ID corretamente")
    void deveDefinirEObterIdCorretamente() {
        // Act
        funcionario.setId(1L);
        
        // Assert
        assertEquals(1L, funcionario.getId());
    }
    
    @Test
    @DisplayName("Deve definir e obter o nome corretamente")
    void deveDefinirEObterNomeCorretamente() {
        // Act
        funcionario.setNome("Maria Oliveira");
        
        // Assert
        assertEquals("Maria Oliveira", funcionario.getNome());
    }
    
    @Test
    @DisplayName("Deve definir e obter a data de admissão corretamente")
    void deveDefinirEObterDataAdmissaoCorretamente() {
        // Act
        LocalDate novaData = LocalDate.of(2022, 5, 10);
        funcionario.setDataAdmissao(novaData);
        
        // Assert
        assertEquals(novaData, funcionario.getDataAdmissao());
    }
    
    @Test
    @DisplayName("Deve definir e obter o salário corretamente")
    void deveDefinirEObterSalarioCorretamente() {
        // Act
        BigDecimal novoSalario = new BigDecimal("6500.00");
        funcionario.setSalario(novoSalario);
        
        // Assert
        assertEquals(novoSalario, funcionario.getSalario());
    }
    
    @Test
    @DisplayName("Deve definir e obter o status corretamente")
    void deveDefinirEObterStatusCorretamente() {
        // Act
        funcionario.setStatus(false);
        
        // Assert
        assertFalse(funcionario.isStatus());
    }
    
    @Test
    @DisplayName("Deve implementar equals e hashCode corretamente")
    void deveImplementarEqualsEHashCodeCorretamente() {
        // Arrange
        Funcionario funcionario1 = new Funcionario("João Silva", dataAdmissao, salario, true);
        funcionario1.setId(1L);
        
        Funcionario funcionario2 = new Funcionario("João Silva", dataAdmissao, salario, true);
        funcionario2.setId(1L);
        
        Funcionario funcionario3 = new Funcionario("João Silva", dataAdmissao, salario, true);
        funcionario3.setId(2L);
        
        // Assert
        assertEquals(funcionario1, funcionario2);
        assertEquals(funcionario1.hashCode(), funcionario2.hashCode());
        
        assertNotEquals(funcionario1, funcionario3);
        assertNotEquals(funcionario1.hashCode(), funcionario3.hashCode());
    }
    
    @Test
    @DisplayName("Deve implementar toString corretamente")
    void deveImplementarToStringCorretamente() {
        // Arrange
        funcionario.setId(1L);
        
        // Act
        String resultado = funcionario.toString();
        
        // Assert
        assertTrue(resultado.contains("id=1"));
        assertTrue(resultado.contains("nome='João Silva'"));
        assertTrue(resultado.contains("dataAdmissao=" + dataAdmissao));
        assertTrue(resultado.contains("salario=" + salario));
        assertTrue(resultado.contains("status=true"));
    }
} 