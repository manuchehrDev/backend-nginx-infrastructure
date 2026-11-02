package ShopAPI.ShopAPI.dto.response.supplierResponse;


import ShopAPI.ShopAPI.dto.response.addressResponse.AddressResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.resilience.annotation.EnableResilientMethods;

import java.util.UUID;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с данными о поставщике")
public class SupplierResponse {

    @Schema(
            description = "UUID поставщика",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID id;

    @Schema(
            description = "Название поставщика",
            example = "ООО Электроника"
    )
    private String name;

    @Schema(
            description = "Номер телефона поставщика",
            example = "+7 (495) 123-45-67"
    )
    private String phoneNumber;

    @Schema(description = "Адрес поставщика")
    private AddressResponse address;

    public SupplierResponse(){

    }
}
