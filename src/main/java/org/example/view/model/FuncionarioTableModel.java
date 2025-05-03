package org.example.view.model;

import org.example.model.Funcionario;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FuncionarioTableModel extends AbstractTableModel {
    private List<Funcionario> funcionarios;
    private final String[] colunas = {"ID", "Nome", "Data de Admissão", "Salário", "Status"};
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    
    public FuncionarioTableModel() {
        this.funcionarios = new ArrayList<>();
    }
    
    public FuncionarioTableModel(List<Funcionario> funcionarios) {
        this.funcionarios = funcionarios;
    }
    
    public void setFuncionarios(List<Funcionario> funcionarios) {
        this.funcionarios = funcionarios;
        fireTableDataChanged();
    }
    
    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }
    
    public Funcionario getFuncionario(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < funcionarios.size()) {
            return funcionarios.get(rowIndex);
        }
        return null;
    }
    
    public void adicionarFuncionario(Funcionario funcionario) {
        funcionarios.add(funcionario);
        fireTableRowsInserted(funcionarios.size() - 1, funcionarios.size() - 1);
    }
    
    public void atualizarFuncionario(Funcionario funcionario, int rowIndex) {
        if (rowIndex >= 0 && rowIndex < funcionarios.size()) {
            funcionarios.set(rowIndex, funcionario);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
    
    public void removerFuncionario(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < funcionarios.size()) {
            funcionarios.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    @Override
    public int getRowCount() {
        return funcionarios.size();
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
            case 0: return Long.class;   // ID
            case 1: return String.class; // Nome
            case 2: return String.class; // Data de Admissão (formatada)
            case 3: return String.class; // Salário (formatado como moeda)
            case 4: return Boolean.class; // Status
            default: return Object.class;
        }
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Apenas a coluna "Status" pode ser editada diretamente na tabela
        return columnIndex == 4;
    }
    
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < funcionarios.size()) {
            Funcionario funcionario = funcionarios.get(rowIndex);
            
            if (columnIndex == 4 && value instanceof Boolean) {
                funcionario.setStatus((Boolean) value);
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= 0 && rowIndex < funcionarios.size()) {
            Funcionario funcionario = funcionarios.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return funcionario.getId();
                case 1: return funcionario.getNome();
                case 2: 
                    LocalDate dataAdmissao = funcionario.getDataAdmissao();
                    return dataAdmissao != null ? dataAdmissao.format(dateFormatter) : "";
                case 3: 
                    BigDecimal salario = funcionario.getSalario();
                    return salario != null ? currencyFormatter.format(salario) : "";
                case 4: return funcionario.isStatus();
                default: return null;
            }
        }
        
        return null;
    }
} 