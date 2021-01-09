package model.security.error;

import java.util.HashMap;
import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, String> ERRORS = new HashMap<>();

    public ValidationException(String message) {
        super(message);
    }

    public Map<String, String> getERRORS() {
        return ERRORS;
    }

    public void addError(String fieldName, String errorMessage) {
        this.ERRORS.put(fieldName, errorMessage);
    }
}
