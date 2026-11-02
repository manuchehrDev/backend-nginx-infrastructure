package ShopAPI.ShopAPI.dto.response.ImageResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с данными об изображении")
public class ImageResponse {

    @Schema(
            description = "Уникальный идентификатор изображения",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    )
    private UUID id;

    @Schema(
            description = "Сообщение о результате операции",
            example = "Изображение успешно добавлено"
    )
    private String message;

    @Schema(
            description = "Массив байтов изображения",
            example = "bytes[]"
    )
    private byte[] image;

    public ImageResponse(){

    }
}
