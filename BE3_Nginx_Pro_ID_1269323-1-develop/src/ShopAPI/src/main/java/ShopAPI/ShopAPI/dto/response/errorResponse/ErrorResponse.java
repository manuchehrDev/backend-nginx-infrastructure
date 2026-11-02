package ShopAPI.ShopAPI.dto.response.errorResponse;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @Schema(
            description = "Текст ошибки",
            example = "Ресурс не найден"
    )
    private String message;

    @Schema(
            description = "HTTP статус код",
            example = "404"
    )
    private int status;

    @Schema(
            description = "Время возникновения ошибки",
            example = "2026-07-30T15:30:00"
    )
    private LocalDateTime timestamp;
}
