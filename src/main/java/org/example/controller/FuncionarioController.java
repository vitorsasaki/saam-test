package org.example.controller;

import org.example.model.Funcionario;
import org.example.service.FuncionarioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FuncionarioController {
    private final FuncionarioService funcionarioService;
    
    public FuncionarioController() {
        this.funcionarioService = new FuncionarioService();
    }

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }
    
    public List<Funcionario> listarFuncionarios() {
        return funcionarioService.listarTodos();
    }
    
    public Optional<Funcionario> buscarFuncionarioPorId(Long id) {
        return funcionarioService.buscarPorId(id);
    }
    
    public boolean salvarFuncionario(Funcionario funcionario) {
        if (funcionario.getId() == null) {
            Optional<Funcionario> funcionarioOpt = funcionarioService.cadastrarFuncionario(
                    funcionario.getNome(),
                    funcionario.getDataAdmissao(),
                    funcionario.getSalario(),
                    funcionario.isStatus());
            return funcionarioOpt.isPresent();
        } else {
            return funcionarioService.atualizarFuncionario(funcionario);
        }
    }
    
    public boolean excluirFuncionario(Long id) {
        return funcionarioService.excluirFuncionario(id);
    }

    public Optional<Funcionario> cadastrarFuncionario(String nome, LocalDate dataAdmissao, 
                                                    BigDecimal salario, boolean status) {
        return funcionarioService.cadastrarFuncionario(nome, dataAdmissao, salario, status);
    }
    

    public List<Funcionario> filtrarPorId(Long id) {
        if (id == null) {
            return listarFuncionarios();
        }
        
        return buscarFuncionarioPorId(id)
                .map(List::of)
                .orElse(List.of());
    }

    public List<Funcionario> filtrarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return listarFuncionarios();
        }
        
        String nomeBusca = nome.toLowerCase().trim();
        
        return listarFuncionarios().stream()
                .filter(f -> f.getNome() != null && f.getNome().toLowerCase().contains(nomeBusca))
                .collect(Collectors.toList());
    }

    public List<Funcionario> filtrarPorPeriodoAdmissao(LocalDate dataInicio, LocalDate dataFim) {
        List<Funcionario> todos = listarFuncionarios();
        
        if (dataInicio == null && dataFim == null) {
            return todos;
        }
        
        return todos.stream()
                .filter(f -> {
                    LocalDate dataAdmissao = f.getDataAdmissao();
                    if (dataAdmissao == null) return false;
                    
                    boolean aposDataInicio = dataInicio == null || !dataAdmissao.isBefore(dataInicio);
                    boolean antesDataFim = dataFim == null || !dataAdmissao.isAfter(dataFim);
                    
                    return aposDataInicio && antesDataFim;
                })
                .collect(Collectors.toList());
    }
} 