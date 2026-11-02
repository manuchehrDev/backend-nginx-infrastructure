package ShopAPI.ShopAPI.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OperationNotExcepted extends RuntimeException {
    public OperationNotExcepted(String message) {
        super(message);
    }
}
