package org.example.view;

import org.example.controller.FuncionarioController;
import org.example.model.Funcionario;
import org.example.view.model.FuncionarioTableModel;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class FuncionarioPanel extends JPanel {
    private final FuncionarioController funcionarioController;
    private FuncionarioTableModel tableModel;
    
    private JTextField txtNome;
    private JFormattedTextField txtDataAdmissao;
    private JFormattedTextField txtSalario;
    private JCheckBox chkStatus;
    private JTable tblFuncionarios;
    private JLabel lblModoEdicao;

    private JTextField txtFiltroId;
    private JTextField txtFiltroNome;
    private JFormattedTextField txtFiltroDataInicio;
    private JFormattedTextField txtFiltroDataFim;
    private JRadioButton rdbFiltroId;
    private JRadioButton rdbFiltroNome;
    private JRadioButton rdbFiltroPeriodo;
    private JRadioButton rdbSemFiltro;
    
    private Funcionario funcionarioSelecionado;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FuncionarioPanel() {
        this.funcionarioController = new FuncionarioController();
        inicializarComponentes();
        carregarFuncionarios();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Dados do Funcionário"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblNome = new JLabel("Nome:");
        txtNome = new JTextField(30);
        
        JLabel lblDataAdmissao = new JLabel("Data de Admissão:");
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtDataAdmissao = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            txtDataAdmissao = new JFormattedTextField();
        }
        txtDataAdmissao.setColumns(10);
        
        JLabel lblSalario = new JLabel("Salário:");
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        NumberFormatter formatter = new NumberFormatter(decimalFormat);
        formatter.setValueClass(BigDecimal.class);
        formatter.setMinimum(BigDecimal.ZERO);
        txtSalario = new JFormattedTextField(new DefaultFormatterFactory(formatter));
        txtSalario.setColumns(10);
        
        JLabel lblStatus = new JLabel("Status:");
        chkStatus = new JCheckBox();
        chkStatus.setSelected(true);

        lblModoEdicao = new JLabel("Modo: Novo registro");
        lblModoEdicao.setFont(new Font(lblModoEdicao.getFont().getName(), Font.BOLD, 12));
        lblModoEdicao.setForeground(new Color(0, 102, 204)); // Azul

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.gridwidth = 2;
        formPanel.add(lblModoEdicao, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(lblNome, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(txtNome, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(lblDataAdmissao, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(txtDataAdmissao, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(lblSalario, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(txtSalario, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        formPanel.add(lblStatus, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(chkStatus, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(this::salvarFuncionario);
        
        JButton btnNovo = new JButton("Novo");
        btnNovo.addActionListener(e -> limparFormulario());
        
        JButton btnAlterar = new JButton("Alterar");
        btnAlterar.addActionListener(this::alterarFuncionario);
        
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(this::excluirFuncionario);
        
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnNovo);
        buttonPanel.add(btnAlterar);
        buttonPanel.add(btnExcluir);

        JPanel filterPanel = criarPainelFiltros();

        tableModel = new FuncionarioTableModel();
        tblFuncionarios = new JTable(tableModel);
        tblFuncionarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblFuncionarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tblFuncionarios.getSelectedRow();
                    if (selectedRow >= 0) {
                        selecionarFuncionario(selectedRow);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblFuncionarios);
        scrollPane.setPreferredSize(new Dimension(600, 300));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel criarPainelFiltros() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtros de Busca"));

        ButtonGroup grupoBotoes = new ButtonGroup();
        
        rdbSemFiltro = new JRadioButton("Sem filtro");
        rdbSemFiltro.setSelected(true);
        rdbFiltroId = new JRadioButton("ID:");
        rdbFiltroNome = new JRadioButton("Nome:");
        rdbFiltroPeriodo = new JRadioButton("Período:");
        
        grupoBotoes.add(rdbSemFiltro);
        grupoBotoes.add(rdbFiltroId);
        grupoBotoes.add(rdbFiltroNome);
        grupoBotoes.add(rdbFiltroPeriodo);

        txtFiltroId = new JTextField(5);
        txtFiltroNome = new JTextField(15);
        
        try {
            MaskFormatter dateMask = new MaskFormatter("##/##/####");
            dateMask.setPlaceholderCharacter('_');
            txtFiltroDataInicio = new JFormattedTextField(dateMask);
            txtFiltroDataFim = new JFormattedTextField(dateMask);
        } catch (ParseException e) {
            txtFiltroDataInicio = new JFormattedTextField();
            txtFiltroDataFim = new JFormattedTextField();
        }
        
        txtFiltroDataInicio.setColumns(8);
        txtFiltroDataFim.setColumns(8);

        JButton btnFiltrar = new JButton("Aplicar Filtro");
        btnFiltrar.addActionListener(this::aplicarFiltro);

        filterPanel.add(rdbSemFiltro);

        JPanel idPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        idPanel.add(rdbFiltroId);
        idPanel.add(txtFiltroId);
        filterPanel.add(idPanel);

        JPanel nomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        nomePanel.add(rdbFiltroNome);
        nomePanel.add(txtFiltroNome);
        filterPanel.add(nomePanel);

        JPanel periodoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        periodoPanel.add(rdbFiltroPeriodo);
        periodoPanel.add(new JLabel("De:"));
        periodoPanel.add(txtFiltroDataInicio);
        periodoPanel.add(new JLabel("Até:"));
        periodoPanel.add(txtFiltroDataFim);
        filterPanel.add(periodoPanel);

        filterPanel.add(btnFiltrar);
        
        return filterPanel;
    }

    private void aplicarFiltro(ActionEvent e) {
        List<Funcionario> funcionariosFiltrados;
        
        if (rdbSemFiltro.isSelected()) {
            funcionariosFiltrados = funcionarioController.listarFuncionarios();
        } else if (rdbFiltroId.isSelected()) {
            try {
                Long id = Long.parseLong(txtFiltroId.getText().trim());
                funcionariosFiltrados = funcionarioController.filtrarPorId(id);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "ID inválido. Digite um número válido.", 
                    "Erro de Filtro", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (rdbFiltroNome.isSelected()) {
            String nome = txtFiltroNome.getText();
            funcionariosFiltrados = funcionarioController.filtrarPorNome(nome);
        } else if (rdbFiltroPeriodo.isSelected()) {
            LocalDate dataInicio = null;
            LocalDate dataFim = null;
            
            try {
                String txtDataInicio = txtFiltroDataInicio.getText();
                if (!txtDataInicio.equals("__/__/____") && !txtDataInicio.trim().isEmpty()) {
                    dataInicio = LocalDate.parse(txtDataInicio, dateFormatter);
                }
                
                String txtDataFim = txtFiltroDataFim.getText();
                if (!txtDataFim.equals("__/__/____") && !txtDataFim.trim().isEmpty()) {
                    dataFim = LocalDate.parse(txtDataFim, dateFormatter);
                }
                
                if (dataInicio == null && dataFim == null) {
                    JOptionPane.showMessageDialog(this, 
                        "Por favor, informe pelo menos uma data.", 
                        "Filtro incompleto", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                funcionariosFiltrados = funcionarioController.filtrarPorPeriodoAdmissao(dataInicio, dataFim);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Formato de data inválido. Use DD/MM/AAAA.", 
                    "Erro de Filtro", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            funcionariosFiltrados = funcionarioController.listarFuncionarios();
        }
        
        tableModel.setFuncionarios(funcionariosFiltrados);
        
        if (funcionariosFiltrados.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Nenhum funcionário encontrado com os filtros informados.", 
                "Resultado da Busca", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void carregarFuncionarios() {
        List<Funcionario> funcionarios = funcionarioController.listarFuncionarios();
        tableModel.setFuncionarios(funcionarios);
    }
    
    private void limparFormulario() {
        txtNome.setText("");
        txtDataAdmissao.setText("");
        txtSalario.setValue(null);
        chkStatus.setSelected(true);
        funcionarioSelecionado = null;
        lblModoEdicao.setText("Modo: Novo registro");
        lblModoEdicao.setForeground(new Color(0, 102, 204)); // Azul
    }
    
    private void selecionarFuncionario(int rowIndex) {
        Funcionario funcionario = tableModel.getFuncionario(rowIndex);
        if (funcionario != null) {
            funcionarioSelecionado = funcionario;
            txtNome.setText(funcionario.getNome());
            txtDataAdmissao.setText(funcionario.getDataAdmissao().format(dateFormatter));
            txtSalario.setValue(funcionario.getSalario());
            chkStatus.setSelected(funcionario.isStatus());
            
            lblModoEdicao.setText("Modo: Editando registro ID " + funcionario.getId());
            lblModoEdicao.setForeground(new Color(204, 102, 0)); // Laranja
        }
    }
    
    private void salvarFuncionario(ActionEvent e) {
        try {
            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "O nome é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate dataAdmissao;
            try {
                String dataText = txtDataAdmissao.getText().replace("_", "");
                dataAdmissao = LocalDate.parse(dataText, dateFormatter);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Data de admissão inválida. Use o formato DD/MM/AAAA.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            BigDecimal salario;
            try {
                salario = (BigDecimal) txtSalario.getValue();
                if (salario == null || salario.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "O salário deve ser maior que zero.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Salário inválido.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean status = chkStatus.isSelected();
            
            boolean resultado;
            String mensagem;
            
            if (funcionarioSelecionado == null) {
                Optional<Funcionario> novoFuncionario = funcionarioController.cadastrarFuncionario(nome, dataAdmissao, salario, status);
                resultado = novoFuncionario.isPresent();
                mensagem = "Funcionário cadastrado com sucesso!";
            } else {
                funcionarioSelecionado.setNome(nome);
                funcionarioSelecionado.setDataAdmissao(dataAdmissao);
                funcionarioSelecionado.setSalario(salario);
                funcionarioSelecionado.setStatus(status);
                
                resultado = funcionarioController.salvarFuncionario(funcionarioSelecionado);
                mensagem = "Funcionário alterado com sucesso!";
            }
            
            if (resultado) {
                JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarFuncionarios();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao salvar funcionário. Verifique os dados e tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarFuncionario(ActionEvent e) {
        int selectedRow = tblFuncionarios.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                    "Selecione um funcionário na tabela para alterar.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        selecionarFuncionario(selectedRow);
    }
    
    private void excluirFuncionario(ActionEvent e) {
        int selectedRow = tblFuncionarios.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um funcionário para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Funcionario funcionario = tableModel.getFuncionario(selectedRow);
        if (funcionario == null || funcionario.getId() == null) {
            JOptionPane.showMessageDialog(this, "Funcionário inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacao = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja excluir o funcionário " + funcionario.getNome() + "?", 
                "Confirmar Exclusão", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            boolean resultado = funcionarioController.excluirFuncionario(funcionario.getId());
            
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Funcionário excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarFuncionarios();
            } else {
                JOptionPane.showMessageDialog(this, "Erro ao excluir funcionário.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 