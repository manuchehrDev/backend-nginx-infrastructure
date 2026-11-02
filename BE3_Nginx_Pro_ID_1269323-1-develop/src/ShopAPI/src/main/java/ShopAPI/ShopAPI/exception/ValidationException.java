package ShopAPI.ShopAPI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    private Map<String, List<String>> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = null;
    }

    public ValidationException(String message, Map<String, List<String>> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }

    ;
}
