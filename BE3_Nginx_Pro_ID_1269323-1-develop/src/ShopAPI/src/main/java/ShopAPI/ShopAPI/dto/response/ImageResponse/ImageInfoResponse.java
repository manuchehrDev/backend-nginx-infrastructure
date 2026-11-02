package ShopAPI.ShopAPI.dto.response.ImageResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Ответ с информацией об изображении (без бинарных данных)")
public class ImageInfoResponse {

    @Schema(
            description = "Уникальный идентификатор изображения",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    )
    private UUID uuid;

    @Schema(
            description = "Сообщение о результате операции",
            example = "Изображение успешно удалено"
    )
    private String message;
}
