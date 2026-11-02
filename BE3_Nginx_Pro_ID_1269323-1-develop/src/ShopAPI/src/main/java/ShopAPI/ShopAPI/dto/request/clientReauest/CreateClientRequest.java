package ShopAPI.ShopAPI.dto.request.clientReauest;

import ShopAPI.ShopAPI.dto.request.addressRequest.AddressRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name = "CreateClientRequest",
        description = "Запрос на создание нового клиента"
)
public class CreateClientRequest {

    @Schema(
            description = "Уникальный идентификатор клиента (генерируется автоматически, не передавать при создании)",
            example = "550e8400-e29b-41d4-a716-446655440000",
            accessMode = Schema.AccessMode.READ_ONLY,
            hidden = true
    )
    private UUID id;

    @Schema(
            description = "Имя клиента",
            example = "Иван",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 100
    )
    @NotBlank(message = "Имя клиента обязательно")
    @Size(max = 100, message = "Имя не может превышать 100 символов")
    private String clientName;

    @Schema(
            description = "Фамилия клиента",
            example = "Петров",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 1,
            maxLength = 100
    )
    @NotBlank(message = "Фамилия клиента обязательна")
    @Size(max = 100, message = "Фамилия не может превышать 100 символов")
    private String clientSurname;

    @Schema(
            description = "Дата рождения клиента в формате ISO-8601",
            example = "1990-05-15",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "date"
    )
    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthday;

    @Schema(
            description = "Пол клиента (M - мужской, F - женский)",
            example = "M",
            requiredMode = Schema.RequiredMode.REQUIRED,
            allowableValues = {"M", "F"},
            minLength = 1,
            maxLength = 1
    )
    @NotBlank(message = "Пол обязателен")
    @Pattern(regexp = "^[MF]$", message = "Пол должен быть 'M' или 'F'")
    @Size(min = 1, max = 1, message = "Пол должен быть 'M' или 'F'")
    private String gender;

    @Schema(
            description = "Дата и время регистрации (генерируется автоматически на сервере)",
            example = "2026-07-30T15:30:00",
            accessMode = Schema.AccessMode.READ_ONLY,
            hidden = true
    )
    private LocalDateTime registrationDate;

    @Schema(
            description = "Адрес клиента",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Valid
    @NotNull(message = "Адрес обязателен")
    private AddressRequest address;
}