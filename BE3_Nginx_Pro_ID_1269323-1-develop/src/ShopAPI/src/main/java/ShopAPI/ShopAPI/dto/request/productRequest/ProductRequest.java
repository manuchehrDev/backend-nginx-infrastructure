package ShopAPI.ShopAPI.dto.request.productRequest;

import ShopAPI.ShopAPI.dto.request.supplierRequest.SupplierRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@Schema(description = "Запрос на создание товара")
public class ProductRequest {

    @Schema(
            description = "Название товара",
            example = "Смартфон Samsung Galaxy S24",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Название товара обязательно")
    private String name;

    @Schema(
            description = "Категория товара",
            example = "Смартфоны",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Категория обязательна")
    private String category;


    @Schema(
            description = "Цена товара",
            example = "89999.99",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть больше 0")
    private BigDecimal price;


    @Schema(
            description = "Количество товара на складе",
            example = "50",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "Количество на складе обязателен")
    @Min(value = 0, message = "Количество не может быт отрицательным")
    private Integer availableStock;

    @Schema(
            description = "UUID поставщика",
            example = "123e4567-e89b-12d3-a456-426614174000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "ID поставщика обязательна")
    private UUID supplierId;
}
