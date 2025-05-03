package org.example.service;

import org.example.dao.FuncionarioDAO;
import org.example.model.Funcionario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FuncionarioService {
    private static final Logger logger = LoggerFactory.getLogger(FuncionarioService.class);
    private final FuncionarioDAO funcionarioDAO;
    
    public FuncionarioService() {
        this.funcionarioDAO = new FuncionarioDAO();
        inicializarBancoDados();
    }

    public FuncionarioService(FuncionarioDAO funcionarioDAO) {
        this.funcionarioDAO = funcionarioDAO;
        inicializarBancoDados();
    }
    
    private void inicializarBancoDados() {
        funcionarioDAO.criarTabela();
    }
    
    public Optional<Funcionario> cadastrarFuncionario(String nome, LocalDate dataAdmissao, 
                                                    BigDecimal salario, boolean status) {
        if (nome == null || nome.trim().isEmpty()) {
            logger.warn("Tentativa de cadastro com nome vazio");
            return Optional.empty();
        }
        
        if (dataAdmissao == null) {
            logger.warn("Tentativa de cadastro com data de admissão nula");
            return Optional.empty();
        }
        
        if (salario == null || salario.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Tentativa de cadastro com salário inválido: {}", salario);
            return Optional.empty();
        }
        
        Funcionario novoFuncionario = new Funcionario(nome, dataAdmissao, salario, status);
        return funcionarioDAO.inserir(novoFuncionario);
    }
    
    public boolean atualizarFuncionario(Funcionario funcionario) {
        if (funcionario == null || funcionario.getId() == null) {
            logger.warn("Tentativa de atualização com funcionário nulo ou sem ID");
            return false;
        }
        
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            logger.warn("Tentativa de atualização com nome vazio");
            return false;
        }
        
        if (funcionario.getDataAdmissao() == null) {
            logger.warn("Tentativa de atualização com data de admissão nula");
            return false;
        }
        
        if (funcionario.getSalario() == null || funcionario.getSalario().compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Tentativa de atualização com salário inválido: {}", funcionario.getSalario());
            return false;
        }
        
        return funcionarioDAO.atualizar(funcionario);
    }
    
    public boolean excluirFuncionario(Long id) {
        if (id == null) {
            logger.warn("Tentativa de exclusão com ID nulo");
            return false;
        }
        
        return funcionarioDAO.excluir(id);
    }
    
    public Optional<Funcionario> buscarPorId(Long id) {
        if (id == null) {
            logger.warn("Tentativa de busca com ID nulo");
            return Optional.empty();
        }
        
        return funcionarioDAO.buscarPorId(id);
    }
    
    public List<Funcionario> listarTodos() {
        return funcionarioDAO.listarTodos();
    }
} 