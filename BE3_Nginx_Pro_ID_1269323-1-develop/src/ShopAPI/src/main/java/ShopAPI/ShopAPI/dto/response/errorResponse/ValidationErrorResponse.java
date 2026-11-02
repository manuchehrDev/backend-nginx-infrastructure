package ShopAPI.ShopAPI.dto.response.errorResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Builder
@Schema(
        name = "ValidationErrorResponse",
        description = "Ответ с ошибками валидации данных"
)
public class ValidationErrorResponse {

    @Schema(
            description = "Общее сообщение об ошибке",
            example = "Ошибка валидации данных"
    )
    private String errorMessage;

    @Schema(
            description = "HTTP статус код ошибки",
            example = "400"
    )
    private int errorCode;

    @Schema(
            description = "Время возникновения ошибки",
            example = "2026-08-04T15:30:00"
    )
    private LocalDateTime timestamp;

    @Schema(
            description = "Детали ошибок по полям",
            example = "{\"clientName\": [\"Имя клиента обязательно\"], \"birthday\": [\"Дата рождения обязательна\"]}"
    )
    private Map<String, List<String>> errors;

    public ValidationErrorResponse(String errorMessage, int errorCode,
                                   LocalDateTime timestamp, Map<String, List<String>> errors) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public ValidationErrorResponse(String errorMessage, int errorCode, LocalDateTime timestamp) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }

    public static ValidationErrorResponse of(String errorMessage, int errorCode,
                                             LocalDateTime timestamp, Map<String, List<String>> errors) {
        return new ValidationErrorResponse(errorMessage, errorCode, timestamp, errors);
    }
}