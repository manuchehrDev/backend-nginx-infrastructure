package ShopAPI.ShopAPI.dto.request.productRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "StockRequest",
        description = "Запрос на уменьшение количества товара на складе"
)
public class StockRequest {

    @Schema(
            description = "Уникальный идентификатор товара",
            example = "987fcdeb-51a2-43d7-9e56-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "ID товара обязателен")
    private UUID id;

    @Schema(
            description = "Количество единиц товара для списания со склада",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1",
            type = "integer",
            format = "int32"
    )
    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть больше 0")
    private Integer quantity;
}