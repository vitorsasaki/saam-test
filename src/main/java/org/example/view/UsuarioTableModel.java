package org.example.view;

import org.example.model.Usuario;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class UsuarioTableModel extends AbstractTableModel {
    private List<Usuario> usuarios;
    private final String[] colunas = {"ID", "Nome", "E-mail"};
    
    public UsuarioTableModel() {
        this.usuarios = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return usuarios.size();
    }
    
    @Override
    public int getColumnCount() {
        return colunas.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: return Long.class;    // ID
            default: return String.class; // Nome, E-mail
        }
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Usuario usuario = usuarios.get(rowIndex);
        
        switch (columnIndex) {
            case 0: return usuario.getId();
            case 1: return usuario.getNome();
            case 2: return usuario.getEmail();
            default: return null;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Não permite edição direta na tabela
    }
    
    /**
     * Atualiza os dados da tabela
     * 
     * @param usuarios Lista de usuários para exibir na tabela
     */
    public void atualizarDados(List<Usuario> usuarios) {
        this.usuarios = usuarios;
        fireTableDataChanged();
    }
    
    /**
     * Retorna o usuário em uma determinada linha
     * 
     * @param rowIndex Índice da linha
     * @return Usuário na linha especificada
     */
    public Usuario getUsuario(int rowIndex) {
        return usuarios.get(rowIndex);
    }
} 