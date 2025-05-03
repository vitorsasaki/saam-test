# SAAM - Sistema de Administração e Aplicações Modulares

## Descrição

SAAM é um sistema Java Swing com PostgreSQL que segue o padrão MVC (Model-View-Controller), desenvolvido como teste para programador na SISAUDCON. O sistema possui módulos de gerenciamento de usuários e funcionários, com uma arquitetura robusta de tratamento de erros para garantir estabilidade em ambiente de produção.

## Características Principais

- Interface gráfica desenvolvida com Java Swing
- Banco de dados PostgreSQL
- Padrão de arquitetura MVC
- Tratamento avançado de erros de banco de dados
- Mecanismo de retry com backoff exponencial
- Gerenciamento de transações
- Pool de conexões eficiente com HikariCP

## Requisitos

- Java JDK 17 ou superior
- PostgreSQL 12 ou superior
- Maven 3.6 ou superior

## Configuração do Ambiente de Produção

### 1. Preparação do Banco de Dados PostgreSQL

1. Instale o PostgreSQL seguindo as instruções em [postgresql.org](https://www.postgresql.org/download/)

2. Crie um novo banco de dados para a aplicação:

```sql
CREATE DATABASE saam_db;
```

3. Crie um usuário para a aplicação (ou use um existente):

```sql
CREATE USER saam_user WITH PASSWORD 'senha_segura';
GRANT ALL PRIVILEGES ON DATABASE saam_db TO saam_user;
```

4. Conecte-se ao banco de dados criado e defina as permissões necessárias:

```sql
\c saam_db

-- Conceder permissões para o usuário criado
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO saam_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO saam_user;
```

### 2. Configuração da Aplicação

1. Clone o repositório:

```bash
git clone [URL_DO_REPOSITORIO]
cd SAAM-teste
```

2. Configure o arquivo de propriedades do banco de dados em `src/main/resources/database.properties`:

```properties
# Configurações de conexão com o PostgreSQL
db.url=jdbc:postgresql://localhost:5432/saam_db
db.user=saam_user
db.password=senha_segura
db.poolSize=10
```

**Importante**: Ajuste os valores acima de acordo com sua configuração de PostgreSQL.

3. Compile a aplicação:

```bash
mvn clean package
```

## Execução em Produção

### Modo de Execução Padrão

```bash
java -jar target/SAAM-teste-1.0-SNAPSHOT.jar
```

### Configurações Avançadas JVM

Para ambientes de produção, recomendamos configurar a JVM com parâmetros otimizados:

```bash
java -Xms512m -Xmx1024m -XX:+UseG1GC -jar target/SAAM-teste-1.0-SNAPSHOT.jar
```

### Execução como Serviço (Linux)

Para executar a aplicação como um serviço no Linux, crie um arquivo de serviço systemd:

1. Crie o arquivo `/etc/systemd/system/saam.service`:

```
[Unit]
Description=SAAM Service
After=network.target postgresql.service

[Service]
User=saam
WorkingDirectory=/opt/saam
ExecStart=/usr/bin/java -jar /opt/saam/SAAM-teste-1.0-SNAPSHOT.jar
SuccessExitStatus=143
TimeoutStopSec=10
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

2. Habilite e inicie o serviço:

```bash
sudo systemctl enable saam
sudo systemctl start saam
```

3. Verifique o status:

```bash
sudo systemctl status saam
```

## Manutenção e Monitoramento

### Logs da Aplicação

Os logs são gerados pelo SLF4J e podem ser encontrados no diretório de execução da aplicação.

Para configurar logs mais detalhados, ajuste o arquivo `src/main/resources/simplelogger.properties` (se não existir, crie-o):

```properties
# Nível de log padrão
org.slf4j.simpleLogger.defaultLogLevel=info

# Configurações específicas de pacotes
org.slf4j.simpleLogger.log.org.example.dao=debug
org.slf4j.simpleLogger.log.org.example.util=debug

# Formato de data/hora
org.slf4j.simpleLogger.showDateTime=true
org.slf4j.simpleLogger.dateTimeFormat=yyyy-MM-dd HH:mm:ss:SSS Z

# Mostrar o nome curto do logger (últimos elementos do pacote)
org.slf4j.simpleLogger.showShortLogName=true
```

### Backup do Banco de Dados

É recomendado configurar backups regulares do banco de dados PostgreSQL:

```bash
pg_dump -U postgres -d saam_db -F c -f /caminho/para/backup/saam_backup_$(date +%Y%m%d).dump
```

## Tratamento de Erros

A aplicação implementa um sistema avançado de tratamento de erros com:

1. Hierarquia de exceções personalizadas
2. Identificação precisa de problemas no banco de dados
3. Retry automático para falhas transitórias
4. Backoff exponencial para reduzir sobrecarga
5. Gerenciamento de transações para garantir atomicidade

## Segurança

Para ambientes de produção, recomendamos:

1. Usar senhas fortes para o banco de dados
2. Configurar SSL para a conexão com o banco de dados (ajuste a URL de conexão)
3. Limitar o acesso ao servidor de banco de dados
4. Executar a aplicação com um usuário com privilégios limitados

## Suporte

Para obter suporte, entre em contato com a equipe de desenvolvimento.

## Licença

Este software é proprietário e seu uso está sujeito aos termos e condições estabelecidos. 