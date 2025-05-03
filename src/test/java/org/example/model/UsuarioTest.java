package org.example.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    @Test
    @DisplayName("Deve criar um usuário com construtor vazio")
    void construtorVazio() {
        Usuario usuario = new Usuario();
        
        assertNull(usuario.getId());
        assertNull(usuario.getNome());
        assertNull(usuario.getEmail());
        assertNull(usuario.getSenha());
    }
    
    @Test
    @DisplayName("Deve criar um usuário com construtor completo")
    void construtorCompleto() {
        Usuario usuario = new Usuario("João Silva", "joao@email.com", "senha123");
        
        assertNull(usuario.getId());
        assertEquals("João Silva", usuario.getNome());
        assertEquals("joao@email.com", usuario.getEmail());
        assertEquals("senha123", usuario.getSenha());
    }
    
    @Test
    @DisplayName("Deve funcionar corretamente os getters e setters")
    void gettersESetters() {
        Usuario usuario = new Usuario();
        
        usuario.setId(1L);
        usuario.setNome("Maria Santos");
        usuario.setEmail("maria@email.com");
        usuario.setSenha("senha456");
        
        assertEquals(1L, usuario.getId());
        assertEquals("Maria Santos", usuario.getNome());
        assertEquals("maria@email.com", usuario.getEmail());
        assertEquals("senha456", usuario.getSenha());
    }
    
    @Test
    @DisplayName("Deve considerar usuários iguais se tiverem o mesmo ID")
    void equals_ComMesmoId_DeveSerIgual() {
        Usuario usuario1 = new Usuario("Teste", "teste@email.com", "senha");
        usuario1.setId(1L);
        
        Usuario usuario2 = new Usuario("Outro", "outro@email.com", "outra");
        usuario2.setId(1L);
        
        // Dois usuários com o mesmo ID são considerados iguais, independente dos outros atributos
        assertEquals(usuario1, usuario2);
        assertEquals(usuario1.hashCode(), usuario2.hashCode());
    }
    
    @Test
    @DisplayName("Deve considerar usuários diferentes se tiverem IDs diferentes")
    void equals_ComIdsDiferentes_DeveSerDiferente() {
        Usuario usuario1 = new Usuario("Teste", "teste@email.com", "senha");
        usuario1.setId(1L);
        
        Usuario usuario2 = new Usuario("Teste", "teste@email.com", "senha");
        usuario2.setId(2L);
        
        // Mesmo com atributos iguais, os IDs são diferentes, então os usuários são diferentes
        assertNotEquals(usuario1, usuario2);
        assertNotEquals(usuario1.hashCode(), usuario2.hashCode());
    }
    
    @Test
    @DisplayName("Não deve ser igual a null ou a outro tipo de objeto")
    void equals_ComOutroTipoOuNull_DeveSerDiferente() {
        Usuario usuario = new Usuario("Teste", "teste@email.com", "senha");
        usuario.setId(1L);
        
        // Um usuário não deve ser igual a null
        assertNotEquals(usuario, null);
        
        // Um usuário não deve ser igual a outro tipo de objeto
        assertNotEquals(usuario, new Object());
        
        // Um usuário deve ser igual a si mesmo
        assertEquals(usuario, usuario);
    }
    
    @Test
    @DisplayName("Deve gerar toString corretamente")
    void toString_DeveConterInformacoesImportantes() {
        Usuario usuario = new Usuario("Carlos Souza", "carlos@email.com", "senha789");
        usuario.setId(3L);
        
        String toString = usuario.toString();
        
        // O toString deve conter as informações importantes do usuário
        assertTrue(toString.contains("id=3"));
        assertTrue(toString.contains("nome='Carlos Souza'"));
        assertTrue(toString.contains("email='carlos@email.com'"));
        
        // A senha não deve aparecer no toString por questões de segurança
        assertFalse(toString.contains("senha789"));
    }
} 