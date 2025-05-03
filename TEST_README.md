# Solução para Problemas de Testes com Maven

## Problema Encontrado

Ao executar `mvn install`, estão ocorrendo falhas nos testes com o seguinte erro:

```
java.sql.SQLException: HikariDataSource HikariDataSource (HikariPool-1) has been closed.
```

Este erro está relacionado ao gerenciamento do pool de conexões do banco de dados H2 usado nos testes.

## Causa do Problema

O problema ocorre porque:

1. O pool de conexões está sendo fechado prematuramente entre diferentes classes de teste
2. Não há um mecanismo adequado para reinicializar o pool quando necessário
3. A configuração do pool de conexões não está otimizada para ambiente de testes

## Solução Implementada

Foram realizadas as seguintes modificações para corrigir o problema:

1. Na classe `TestDatabaseConfig`:
   - Substituímos o bloco `static` por um método `initializeDataSource()` que verifica e recria o datasource quando necessário
   - Aumentamos os parâmetros do pool de conexões para suportar mais requisições simultâneas
   - Adicionamos configurações adicionais para evitar o fechamento prematuro de conexões
   - Configuramos o método `shutdown()` para definir o datasource como nulo após fechá-lo

2. Na classe `H2DatabaseUtil`:
   - Adicionamos um método `inicializarBancoDados()` para configurar corretamente o banco de testes
   - Aprimoramos o método `limparBancoDados()` para lidar corretamente com restrições de chave estrangeira
   - Adicionamos suporte para a coluna `departamento_id` na tabela de funcionários
   - Incluímos o drop da tabela `logs` que é usada no método `transferirParaDepartamento`

## Como Executar os Testes

Para executar os testes com as correções:

1. Certifique-se de que as modificações foram aplicadas às classes `TestDatabaseConfig` e `H2DatabaseUtil`

2. Execute os testes através do Maven:

```
mvn clean test
```

3. Ou execute os testes através da sua IDE, garantindo que:
   - Cada classe de teste chame `H2DatabaseUtil.inicializarBancoDados()` em seu método `@BeforeEach`
   - As classes de teste chamem `H2DatabaseUtil.fecharConexoes()` em seu método `@AfterAll` (método estático)

## Dicas Adicionais

- Se os testes continuarem falhando, verifique se há algum método `@BeforeAll` fechando o datasource sem reinicializá-lo
- Certifique-se de que todas as conexões são devidamente fechadas nos testes (usando try-with-resources)
- Para depurar problemas de conexão, adicione a configuração `config.setLeakDetectionThreshold(1000)` para detectar vazamentos de conexão 