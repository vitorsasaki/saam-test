# Documentação Técnica - SAAM

Este documento descreve a arquitetura e as funcionalidades do sistema SAAM (Sistema de Administração e Aplicações Modulares).

## Arquitetura do Sistema

O SAAM segue o padrão de arquitetura MVC (Model-View-Controller) e está organizado nas seguintes camadas:

1. **Model**: Entidades que representam os dados do sistema
2. **View**: Interfaces gráficas desenvolvidas com Java Swing
3. **Controller**: Classes que intermediam a comunicação entre View e Model
4. **Service**: Camada de serviço que implementa a lógica de negócio
5. **DAO** (Data Access Object): Classes responsáveis pelo acesso ao banco de dados
6. **Exception**: Hierarquia de exceções personalizadas
7. **Util**: Classes utilitárias
8. **Config**: Classes de configuração do sistema

## Descrição das Classes e Métodos

### Pacote `org.example.model`

#### Classe `Funcionario`
Representa a entidade Funcionário no sistema.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| id | Long | Identificador único do funcionário |
| nome | String | Nome do funcionário |
| dataAdmissao | LocalDate | Data de admissão do funcionário |
| salario | BigDecimal | Salário do funcionário |
| status | boolean | Status de atividade do funcionário |

**Métodos principais:**
- Getters e setters para todos os atributos
- `equals()`, `hashCode()` e `toString()` sobrescritos

#### Classe `Usuario`
Representa a entidade Usuário no sistema.

| Atributo | Tipo | Descrição |
|----------|------|-----------|
| id | Long | Identificador único do usuário |
| nome | String | Nome do usuário |
| email | String | Email do usuário (único) |
| senha | String | Senha do usuário |

**Métodos principais:**
- Getters e setters para todos os atributos
- `equals()`, `hashCode()` e `toString()` sobrescritos

### Pacote `org.example.dao`

#### Classe `FuncionarioDAO`
Responsável pelo acesso a dados de funcionários no banco de dados.

**Métodos principais:**
- `criarTabela()`: Cria a tabela de funcionários no banco de dados se não existir
- `inserir(Funcionario funcionario)`: Insere um novo funcionário no banco de dados
- `atualizar(Funcionario funcionario)`: Atualiza os dados de um funcionário existente
- `excluir(Long id)`: Remove um funcionário do banco de dados
- `buscarPorId(Long id)`: Busca um funcionário pelo ID
- `listarTodos()`: Retorna todos os funcionários cadastrados
- `montarFuncionarioDoResultSet(ResultSet rs)`: Converte um ResultSet em um objeto Funcionario

#### Classe `UsuarioDAO`
Responsável pelo acesso a dados de usuários no banco de dados.

**Métodos principais:**
- `criarTabela()`: Cria a tabela de usuários no banco de dados se não existir
- `inserir(Usuario usuario)`: Insere um novo usuário no banco de dados
- `atualizar(Usuario usuario)`: Atualiza os dados de um usuário existente
- `excluir(Long id)`: Remove um usuário do banco de dados
- `buscarPorId(Long id)`: Busca um usuário pelo ID
- `buscarPorEmail(String email)`: Busca um usuário pelo email
- `listarTodos()`: Retorna todos os usuários cadastrados
- `montarUsuarioDoResultSet`: Converte um ResultSet em um objeto

### Pacote `org.example.service`

#### Classe `FuncionarioService`
Implementa a lógica de negócio relacionada aos funcionários.

**Métodos principais:**
- `cadastrarFuncionario(String nome, LocalDate dataAdmissao, BigDecimal salario, boolean status)`: Valida e cadastra um novo funcionário
- `atualizarFuncionario(Funcionario funcionario)`: Valida e atualiza um funcionário existente
- `excluirFuncionario(Long id)`: Remove um funcionário
- `buscarPorId(Long id)`: Busca um funcionário pelo ID
- `listarTodos()`: Lista todos os funcionários cadastrados

#### Classe `UsuarioService`
Implementa a lógica de negócio relacionada aos usuários.

**Métodos principais:**
- `cadastrarUsuario(String nome, String email, String senha)`: Valida e cadastra um novo usuário
- `atualizarUsuario(Usuario usuario)`: Valida e atualiza um usuário existente
- `excluirUsuario(Long id)`: Remove um usuário
- `buscarPorId(Long id)`: Busca um usuário pelo ID
- `buscarPorEmail(String email)`: Busca um usuário pelo email
- `listarTodos()`: Lista todos os usuários cadastrados
- `criptografarSenha(String senha)`: Usado para criptografar a senha
- `verificarSenha(String senhaDigitada, String senhaCriptografada)`: Verifica a senha
- `criptografarSenhaTeste(String senha)`: Método para testar a criptografia de senha (apenas para fins de teste)

### Pacote `org.example.controller`

#### Classe `FuncionarioController`
Controla as operações relacionadas a funcionários, intermediando a comunicação entre view e service.

**Métodos principais:**
- `cadastrarFuncionario(String nome, String dataAdmissao, String salario, boolean status)`: Converte e valida parâmetros para cadastro
- `salvarFuncionario(Funcionario funcionario)`: Salva um funcionário (novo ou existente) verificando se já existe ID
- `excluirFuncionario(Long id)`: Solicita exclusão de um funcionário
- `buscarFuncionarioPorId(Long id)`: Busca um funcionário por ID
- `listarFuncionarios()`: Lista todos os funcionários
-`filtrarPorId(Long id)`: Filtra funcionários por ID
-`filtrarPorNome(String nome)`: Filtra funcionários por nome (busca parcial, não sensível a maiúsculas/minúsculas)
-`filtrarPorPeriodoAdmissao(LocalDate dataInicio, LocalDate dataFim)`: Filtra funcionários por período de admissão

#### Classe `UsuarioController`
Controla as operações relacionadas a usuários.

**Métodos principais:**
- `listarUsuarios()`: Obtém a lista de todos os usuários cadastrados no sistema
- `buscarUsuarioPorId(Long id)`: Recupera um usuário específico pelo seu ID
- `salvarUsuario(Usuario usuario)`: Salva um usuário (novo ou existente) no sistema
- `excluirUsuario(Long id)`: Remove um usuário do sistema
- `emailExiste(String email)`: Verifica se já existe um usuário com o email informado
- `realizarLoginComEmail(String email, String senha)`: Autentica um usuário com as credenciais fornecidas

#### Classe `LoginController`
Controla as operações de autenticação e cadastro inicial.

**Métodos principais:**
- `realizarLoginComEmail(String email, String senha)`: Autentica um usuário com base no email e senha fornecidos
- `cadastrarUsuarioSimplificado(String nome, String email, String senha)`: Realiza o cadastro simplificado de novos usuários
- `emailExiste(String email)`: Verifica se já existe um usuário cadastrado com o email informado

### Pacote `org.example.exception`

#### Classe `DatabaseException`
Classe base para exceções relacionadas ao banco de dados.

**Subclasses:**
- `IntegrityConstraintViolationException`: Violações de restrições de integridade
- `DatabaseTimeoutException`: Timeouts de operações
- `ConnectionException`: Problemas de conexão
- `RecordNotFoundException`: Registros não encontrados
- `TransactionException`: Falhas em transações

### Pacote `org.example.util`

#### Classe `DatabaseExceptionHandler`
Utilitário para tratamento de exceções de banco de dados.

**Métodos principais:**
- `handleSQLException(SQLException e, String operationDescription)`: Converte SQLExceptions em exceções personalizadas
- `handleReadException(SQLException e, String operationDescription, T defaultValue)`: Trata exceções em operações de leitura

#### Classe `DatabaseRetryHandler`
Implementa mecanismo de retry para operações de banco de dados.

**Métodos principais:**
- `executeWithRetry(Callable<T> operation, String description)`: Executa operação com retry automático
- `executeWithRetry(Callable<T> operation, String description, int maxRetries, long initialBackoffMs, double backoffMultiplier, Predicate<Throwable> retryableExceptions)`: Executa operação com parâmetros de retry personalizados
- `executeWithRetry(Runnable operation, String description)`: Versão para operações sem retorno

#### Classe `TransactionManager`
Gerencia transações de banco de dados.

**Métodos principais:**
- `executeInTransaction(TransactionCallable<T> operation, String description)`: Executa operação dentro de uma transação
- `executeInTransaction(TransactionRunnable operation, String description)`: Versão para operações sem retorno

#### Classe `SwingUtils`
Contém utilidades para a interface gráfica.

**Métodos principais:**
- `showError(Component parent, String message)`: Exibe mensagem de erro
- `showInfo(Component parent, String message)`: Exibe mensagem informativa
- `showConfirmDialog(Component parent, String message)`: Exibe diálogo de confirmação
- `centerOnScreen(Window window)`: Centraliza janela na tela

### Pacote `org.example.config`

#### Classe `DatabaseConfig`
Configuração do banco de dados PostgreSQL.

**Métodos principais:**
- `getConnection()`: Obtém uma conexão com o banco de dados
- `getDataSource()`: Obtém o pool de conexões
- `closeDataSource()`: Fecha o pool de conexões
- `initDataSource()`: Inicializa o pool de conexões a partir das configurações

### Pacote `org.example.view`

O pacote contém as classes de interface gráfica desenvolvidas com Java Swing, incluindo:

- `LoginView`: Tela de login
- `MainView`: Tela principal do sistema
- `FuncionarioView`: Tela de gerenciamento de funcionários
- `UsuarioView`: Tela de gerenciamento de usuários

## Fluxo de Tratamento de Erros

O sistema implementa um sofisticado mecanismo de tratamento de erros que segue este fluxo:

1. Uma SQLException é lançada durante operação de banco de dados
2. `DatabaseExceptionHandler` identifica o tipo específico de erro e converte para um tipo de exceção personalizada
3. `DatabaseRetryHandler` tenta novamente a operação em caso de erros transitórios
4. As camadas superiores do sistema tratam as exceções de forma adequada para o usuário

## Operações Transacionais

As operações transacionais do sistema seguem este fluxo:

1. A camada service solicita uma operação transacional
2. `TransactionManager` inicia uma transação
3. As operações são executadas dentro do escopo da transação
4. Em caso de sucesso, a transação é comitada; em caso de falha, é realizado rollback
5. O resultado da operação é retornado para a camada superior

## Considerações de Segurança

- Senhas não são armazenadas em texto plano (implementação abstrata)
- Validações de entrada são realizadas em múltiplas camadas
- Conexões com o banco de dados são gerenciadas por pool seguro
- Tratamento adequado de exceções previne vazamento de informações sensíveis