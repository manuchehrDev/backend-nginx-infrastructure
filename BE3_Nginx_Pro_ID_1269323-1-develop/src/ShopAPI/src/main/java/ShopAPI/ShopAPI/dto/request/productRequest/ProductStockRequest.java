package ShopAPI.ShopAPI.dto.request.productRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на уменьшение количества товара")
public class ProductStockRequest {

    @Schema(
            description = "Количество, на которое нужно уменьшить товар",
            example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1"
    )
    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество должно быть больше 0")
    private Integer quantity;
}
