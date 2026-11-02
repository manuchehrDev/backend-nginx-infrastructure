package ShopAPI.ShopAPI.dto.request.supplierRequest;


import ShopAPI.ShopAPI.dto.response.addressResponse.AddressResponse;
import ShopAPI.ShopAPI.dto.response.supplierResponse.SupplierResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SupplierRequest {

    @Schema(
            description = "Название поставщика",
            example = "ООО Электроника",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Название поставщика обязательно")
    private String name;


    @Schema(
            description = "Номер телефона поставщика",
            example = "+7 (495) 123-45-67",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank(message = "Номер телефона обязателен")
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{10,15}$", message = "Номер телефона должен быть в формате +7 (XXX) XXX-XX-XX")
    private String phoneNumber;


    @Schema(
            description = "Адрес поставщика",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Valid
    @NotNull(message = "Адрес обязателен")
    private AddressResponse address;

    public SupplierRequest() {
    }
}
