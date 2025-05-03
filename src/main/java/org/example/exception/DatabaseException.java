package org.example.exception;

public class DatabaseException extends RuntimeException {

    //Cria uma nova exceção de banco de dados com a mensagem especificada.
    public DatabaseException(String message) {
        super(message);
    }

    //Cria uma nova exceção de banco de dados com a mensagem e causa especificadas.
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    //Exceção lançada quando uma restrição de integridade é violada (ex: duplicação de chave única).
    public static class IntegrityConstraintViolationException extends DatabaseException {
        public IntegrityConstraintViolationException(String message) {
            super(message);
        }

        public IntegrityConstraintViolationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    //Exceção lançada quando ocorre um timeout na conexão ou operação de banco de dados.
    public static class DatabaseTimeoutException extends DatabaseException {
        public DatabaseTimeoutException(String message) {
            super(message);
        }

        public DatabaseTimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    //Exceção lançada quando uma conexão com o banco de dados não pode ser estabelecida.
    public static class ConnectionException extends DatabaseException {
        public ConnectionException(String message) {
            super(message);
        }

        public ConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
