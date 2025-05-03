package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Funcionario {
    private Long id;
    private String nome;
    private LocalDate dataAdmissao;
    private BigDecimal salario;
    private boolean status;

    public Funcionario() {
    }

    public Funcionario(String nome, LocalDate dataAdmissao, BigDecimal salario, boolean status) {
        this.nome = nome;
        this.dataAdmissao = dataAdmissao;
        this.salario = salario;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionario that = (Funcionario) o;
        return status == that.status &&
                Objects.equals(id, that.id) &&
                Objects.equals(nome, that.nome) &&
                Objects.equals(dataAdmissao, that.dataAdmissao) &&
                Objects.equals(salario, that.salario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, dataAdmissao, salario, status);
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataAdmissao=" + dataAdmissao +
                ", salario=" + salario +
                ", status=" + status +
                '}';
    }
} 