package ShopAPI.ShopAPI.dto.request.addressRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на создание/обновление адреса")
public class AddressRequest {

    @Schema(
            description = "Название страны",
            example = "Россия",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Страна обязательна")
    private String country;

    @Schema(
            description = "Название города",
            example = "Москва",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Город обязателен")
    private String city;

    @Schema(
            description = "Улица и номер дома",
            example = "ул. Ленина, д. 5",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Улица обязательна")
    private String street;
}
