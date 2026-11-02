package ShopAPI.ShopAPI.dto.response.productResponse;

import ShopAPI.ShopAPI.dto.response.ImageResponse.ImageInfoResponse;
import ShopAPI.ShopAPI.dto.response.supplierResponse.SupplierResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Schema(description = "Краткая информация о товаре (для списка)")
public class ProductInfoResponse {

    @Schema(description = "UUID товара", example = "987fcdeb-51a2-43d7-9e56-426614174000")
    private UUID id;

    @Schema(description = "Название товара", example = "Смартфон Samsung Galaxy S24")
    private String name;

    @Schema(description = "Категория товара", example = "Смартфоны")
    private String category;

    @Schema(description = "Цена товара", example = "89999.99")
    private BigDecimal price;

    @Schema(description = "Количество на складе", example = "47")
    private Integer availableStock;
    private LocalDateTime lastUpdateDate;
    private SupplierResponse supplier;
    private ImageInfoResponse Image;

    public ProductInfoResponse(){

    }

}
