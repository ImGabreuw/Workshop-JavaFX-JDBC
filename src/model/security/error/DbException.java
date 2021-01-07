package model.security.error;

public class DbException extends RuntimeException {
    public DbException(String message) {
        super(message);
    }
}
