package ShopAPI.ShopAPI.dto.request.imageRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "ImageRequest",
        description = "Запрос на загрузку или обновление изображения товара"
)
public class ImageRequest {

    @Schema(
            description = "Уникальный идентификатор товара, к которому привязывается изображение",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "ID товара обязателен")
    private UUID productId;

    @Schema(
            description = "Файл изображения (поддерживаемые форматы: JPEG, PNG, GIF, WebP)",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "binary",
            type = "string"
    )
    @NotNull(message = "Файл изображения обязателен")
    private MultipartFile file;
}