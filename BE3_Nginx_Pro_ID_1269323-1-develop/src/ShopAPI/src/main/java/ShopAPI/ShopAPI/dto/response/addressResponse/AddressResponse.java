package ShopAPI.ShopAPI.dto.response.addressResponse;

import ShopAPI.ShopAPI.dto.request.addressRequest.AddressRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Setter
@Getter
@Schema(description = "Ответ с данными об адресе")
public class AddressResponse {

    @Schema(
            description = "UUID адреса",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String country;

    @Schema(
            description = "Название страны",
            example = "Россия"
    )
    private String city;

    @Schema(
            description = "Улица и номер дома",
            example = "ул. Ленина, д. 5"
    )
    private String street;

    public AddressResponse() {

    }

    public AddressResponse(AddressRequest request) {
        this.country = request.getCountry();
        this.city = request.getCity();
        this.street = request.getStreet();
    }
}
